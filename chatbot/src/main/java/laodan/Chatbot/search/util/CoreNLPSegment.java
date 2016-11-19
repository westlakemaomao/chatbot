/**
* @author hzyuyongmao
* @version 创建时间：2016年11月11日 下午7:38:49
* stanford 分词
*/
package laodan.Chatbot.search.util;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;

public class CoreNLPSegment {

	private static CoreNLPSegment instance;
	private CRFClassifier classifier;

	private CoreNLPSegment() {
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", "data");
		props.setProperty("serDictionary", "data/dict-chris6.ser.gz");
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
		classifier = new CRFClassifier(props);
		classifier.loadClassifierNoExceptions("data/ctb.gz", props);
		classifier.flags.setProperties(props);
	}

	public static CoreNLPSegment getInstance() {
		if (instance == null) {
			instance = new CoreNLPSegment();
		}

		return instance;
	}

	public List<String> doSegment(String data) {
		return classifier.segmentString(data);

	}

	public static void main(String[] args) {

		String sentence = "他和我在学校里常打桌球。";
		List<String> ret = CoreNLPSegment.getInstance().doSegment(sentence);
		for (String str : ret) {
			System.out.println(str);
		}

	}

}