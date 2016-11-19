/**
* @author hzyuyongmao
* @version 创建时间：2016年11月11日 下午4:35:53
* 类说明
*/
package laodan.Chatbot.search.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseGrammaticalStructure;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;

public class StandfordNLPUtil {
	private static final Logger logger = Logger.getLogger(StandfordNLPUtil.class);
	private static final LexicalizedParser LP;
	private static final GrammaticalStructureFactory GSF;

	static {
		// 模型
		String models = "models/chineseFactored.ser.gz";
		logger.info("模型：" + models);
		LP = LexicalizedParser.loadModel(models);
		// 汉语
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GSF = tlp.grammaticalStructureFactory();
	}

	/**
	 * 获取句子的主谓宾
	 *
	 * @param question
	 *            问题
	 * @param words
	 *            HasWord列表
	 * @return 问题结构
	 */
	public QuestionStructure getMainPart(String question, List<edu.stanford.nlp.ling.Word> words) {
		QuestionStructure questionStructure = new QuestionStructure();
		questionStructure.setQuestion(question);

		Tree tree = LP.apply(words);
		logger.info("句法树: ");
		tree.pennPrint();
		questionStructure.setTree(tree);

		GrammaticalStructure gs = GSF.newGrammaticalStructure(tree);
		if (gs == null) {
			return null;
		}
		// 获取依存关系
		Collection<TypedDependency> tdls = gs.typedDependenciesCCprocessed(true);
		questionStructure.setTdls(tdls);
		Map<String, String> map = new HashMap<>();
		String top = null;
		String root = null;
		logger.info("句子依存关系：");
		// 依存关系
		List<String> dependencies = new ArrayList<>();
		for (TypedDependency tdl : tdls) {
			String item = tdl.toString();
			dependencies.add(item);
			logger.info("\t" + item);
			if (item.startsWith("top")) {
				top = item;
			}
			if (item.startsWith("root")) {
				root = item;
			}
			int start = item.indexOf("(");
			int end = item.lastIndexOf(")");
			item = item.substring(start + 1, end);
			String[] attr = item.split(",");
			String k = attr[0].trim();
			String v = attr[1].trim();
			String value = map.get(k);
			if (value == null) {
				map.put(k, v);
			} else {
				// 有值
				value += ":";
				value += v;
				map.put(k, value);
			}
		}
		questionStructure.setDependencies(dependencies);

		String mainPartForTop = null;
		String mainPartForRoot = null;
		if (top != null) {
			mainPartForTop = topPattern(top, map);
		}
		if (root != null) {
			mainPartForRoot = rootPattern(root, map);
		}
		questionStructure.setMainPartForTop(mainPartForTop);
		questionStructure.setMainPartForRoot(mainPartForRoot);

		if (questionStructure.getMainPart() == null) {
			logger.error("未能识别主谓宾：" + question);
		} else {
			logger.info("主谓宾：" + questionStructure.getMainPart());
		}
		return questionStructure;
	}

	private String rootPattern(String pattern, Map<String, String> map) {
		// root识别模式
		int start = pattern.indexOf("(");
		int end = pattern.lastIndexOf(")");
		pattern = pattern.substring(start + 1, end);
		String[] attr = pattern.split(",");
		String v = attr[1].trim();
		String first = null;
		// 临时谓语
		String second = v.split("-")[0];
		int secondIndex = Integer.parseInt(v.split("-")[1]);
		String third = "";

		String value = map.get(v);
		if (value == null) {
			return null;
		}
		// 判断是否是多值
		String[] values = value.split(":");
		if (values != null && values.length > 0) {
			if (values.length > 1) {
				first = values[0].split("-")[0];
				third = values[values.length - 1].split("-")[0];
			} else {
				String k = values[0];
				String t = k.split("-")[0];
				int tIndex = Integer.parseInt(k.split("-")[1]);
				if (secondIndex < tIndex) {
					// 谓语 调整为 主语
					first = second;
					second = t;
				} else {
					first = t;
				}
				// 没有宾语，再次查找
				String val = map.get(k);
				if (val != null) {
					// 找到宾语
					String[] vals = val.split(":");
					if (vals != null && vals.length > 0) {
						third = vals[vals.length - 1].split("-")[0];
					} else {
						logger.info("宾语获取失败: " + first + " " + second);
					}
				} else {
					// 找不到宾语，降级为主谓结构
					third = "";
				}
			}
		} else {
			logger.error("root模式未找到主语和宾语, " + v + " 只有依赖：" + value);
		}
		// 支持主谓宾和主谓结构
		if (first != null && second != null) {
			String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
			mainPart = mainPart.trim();
			return mainPart;
		}
		return null;
	}

	private String topPattern(String pattern, Map<String, String> map) {
		// top识别模式
		int start = pattern.indexOf("(");
		int end = pattern.lastIndexOf(")");
		pattern = pattern.substring(start + 1, end);
		String[] attr = pattern.split(",");
		String k = attr[0].trim();
		String v = attr[1].trim();
		String first = v.split("-")[0];
		String second = k.split("-")[0];
		String value = map.get(k);
		// 判断是否是多值
		String[] values = value.split(":");
		String candidate;
		if (values != null && values.length > 0) {
			candidate = values[values.length - 1];
		} else {
			candidate = value;
		}
		String third = candidate.split("-")[0];
		String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
		mainPart = mainPart.trim();
		return mainPart;
	}

	/**
	 * 获取句子的主谓宾
	 *
	 * @param question
	 *            问题
	 * @return 问题结构
	 */
	public QuestionStructure getMainPart(String question) {
		question = question.replace("\\s+", "");
		String questionWords = questionParse(question);
		return getMainPart(question, questionWords);
	}

	/**
	 * 对问题进行分词 如：APDPlat的发起人是谁？ 分词之后返回：apdplat 的 发起人 是 谁 ？
	 *
	 * @param question
	 *            问题
	 * @return 分词之后的用空格顺序连接的结果
	 */
	private String questionParse(String question) {
		// 分词
		logger.info("对问题进行分词：" + question);
		List<String> words = CoreNLPSegment.getInstance().doSegment(question);
		StringBuilder wordStr = new StringBuilder();
		for (String word : words) {
			wordStr.append(word).append(" ");
		}
		logger.info("分词结果为：" + wordStr.toString().trim());
		return wordStr.toString().trim();
	}

	/**
	 * 获取句子的主谓宾
	 *
	 * @param question
	 *            问题
	 * @param questionWords
	 *            问题词序，相互之间以空格分割
	 * @return 问题结构
	 */
	public QuestionStructure getMainPart(String question, String questionWords) {
		List<edu.stanford.nlp.ling.Word> words = new ArrayList<>();
		String[] qw = questionWords.split("\\s+");
		for (String item : qw) {
			item = item.trim();
			if ("".equals(item)) {
				continue;
			}
			words.add(new edu.stanford.nlp.ling.Word(item));
		}
		return getMainPart(question, words);
	}

	public static void main(String[] args) {
		StandfordNLPUtil sfp = new StandfordNLPUtil();
		QuestionStructure qs = sfp.getMainPart("你叫什么名字？");
		logger.info(qs.getQuestion());
		logger.info(qs.getMainPart());
		for (String d : qs.getDependencies()) {
			logger.info("\t" + d);
			System.out.println(d);
		}
	}
}
