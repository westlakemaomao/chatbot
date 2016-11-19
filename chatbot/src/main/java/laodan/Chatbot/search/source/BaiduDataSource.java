/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package laodan.Chatbot.search.source;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import laodan.Chatbot.search.domain.CandidateAnswer;
import laodan.Chatbot.search.domain.Question;
import laodan.Chatbot.search.util.MySQLUtils;


/**
 * 从Baidu搜索问题的答案
 *
 * @author yym
 */
public class BaiduDataSource implements DataSource {

	private static final Logger logger = Logger.getLogger(BaiduDataSource.class);

	private static final String ACCEPT = "text/html, */*; q=0.01";
	private static final String ENCODING = "gzip, deflate";
	private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
	private static final String CONNECTION = "keep-alive";
	private static final String HOST = "www.baidu.com";
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0";

	// 获取多少页
	private static final int PAGE = 1;
	private static final int PAGESIZE = 10;
	// 使用摘要还是全文
	// 使用摘要
	private static final boolean SUMMARY = true;
	// 使用全文
	// private static final boolean SUMMARY = false;
	private final List<String> files = new ArrayList<String>();

	public BaiduDataSource() {
	}

	public BaiduDataSource(String file) {
		this.files.add(file);
	}

	public BaiduDataSource(List<String> files) {
		this.files.addAll(files);
	}

	public Question getAnswerAndQuestion(String questionStr) {
		// 1、先从本地缓存里面找
		Question question = MySQLUtils.getQuestionFromDatabase("baidu:", questionStr);
		if (question != null) {
			// 数据库中存在
			logger.info("从数据库中查询到Question：" + question.getQuestion());
			return question;
		}
		// 2、本地缓存里面没有再查询baidu
		question = new Question();
		question.setQuestion(questionStr);

		String query = "";
		try {
			query = URLEncoder.encode(question.getQuestion(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("url构造失败", e);
			return null;
		}
		String referer = "http://www.baidu.com/";
		for (int i = 0; i < PAGE; i++) {
			query = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + query + "&oq=" + query + "&usm=3&f=8&bs="
					+ query + "&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * PAGESIZE;
			logger.debug(query);
			List<CandidateAnswer> candidateAnswerList = searchBaidu(query, referer);
			referer = query;
			if (candidateAnswerList != null && candidateAnswerList.size() > 0) {
				question.addCandidatAnswer(candidateAnswerList);
			} else {
				logger.error("结果页 " + (i + 1) + " 没有搜索到结果");
				break;
			}
		}
		logger.info("Question：" + question.getQuestion() + " 搜索到CandidateAnswer " + question.getCandidateAnswerList().size()
				+ " 条");
		if (question.getCandidateAnswerList().isEmpty()) {
			return null;
		}
		// 3、将baidu查询结果加入本地缓存
		if (question.getCandidateAnswerList().size() > 7) {
			logger.info("将Question：" + question.getQuestion() + " 加入MySQL数据库");
			MySQLUtils.saveQuestionToDatabase("baidu:", question);
		}

		return question;
	}

	private List<CandidateAnswer> searchBaidu(String url, String referer) {
		List<CandidateAnswer> candidateAnswerList = new ArrayList<>();
		try {
			Document document = Jsoup.connect(url).header("Accept", ACCEPT).header("Accept-Encoding", ENCODING)
					.header("Accept-Language", LANGUAGE).header("Connection", CONNECTION)
					.header("User-Agent", USER_AGENT).header("Host", HOST).header("Referer", referer).get();

			String resultCssQuery = "html > body > div > div > div > div > div";
			Elements elements = document.select(resultCssQuery);
			for (Element element : elements) {
				Elements subElements = element.select("h3 > a");
				if (subElements.size() != 1) {
					logger.debug("没有找到标题");
					continue;
				}
				String title = subElements.get(0).text();
				if (title == null || "".equals(title.trim())) {
					logger.debug("标题为空");
					continue;
				}
				subElements = element.select("div.c-abstract");
				if (subElements.size() != 1) {
					logger.debug("没有找到摘要");
					continue;
				}
				String snippet = subElements.get(0).text();
				if (snippet == null || "".equals(snippet.trim())) {
					logger.debug("摘要为空");
					continue;
				}
				CandidateAnswer candidateAnswer = new CandidateAnswer();

				candidateAnswer.setTitle(title);
				// System.out.println("elements.html():"+elements.html());
				// 匹配原文url：
				String elementshtml = element.html();
				int start = elementshtml.indexOf("href=\"http://www.baidu.com/link?url=");
				int end = elementshtml.indexOf("\" target", start);
				if (end > start && start > 0) {
					String originUrl = elementshtml.substring(start + "href=\"".length(), end);
					candidateAnswer.setAnswer(snippet + "原文链接：" + originUrl);
				} else {
					candidateAnswer.setAnswer(snippet);
				}

				candidateAnswerList.add(candidateAnswer);
			}
		} catch (Exception ex) {
			logger.error("搜索出错", ex);
		}
		return candidateAnswerList;
	}

	public static void main(String args[]) {
		Question question = new BaiduDataSource().getAnswerAndQuestion("老聃科技");
		logger.info(question.toString());
		System.out.println(question.toString());
	}
}