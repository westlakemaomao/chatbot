/**
* @author hzyuyongmao
* @version 创建时间：2016年11月11日 上午11:07:54
* 类说明
*/
package laodan.Chatbot.system;

import java.util.List;

import laodan.Chatbot.domain.CandidateAnswer;
import laodan.Chatbot.domain.Question;
import laodan.Chatbot.source.BaiduDataSource;
import laodan.Chatbot.source.DataSource;

public class ChatBotSystem {

	public static Question getChatBotSystem(DataSource dataSource, String questionStr) {
		Question question = dataSource.getAnswerAndQuestion(questionStr);
		return question;
	}

	public static Question getChatBotSystem(String questionStr) {
		DataSource dataSource = new BaiduDataSource();
		return getChatBotSystem(dataSource, questionStr);
	}

	public static void main(String[] args) {
		Question question = getChatBotSystem("老聃科技");
		List<CandidateAnswer> list = question.getCandidateAnswerList();
		for (CandidateAnswer ca : list) {
         System.out.println(ca.getTitle());
         System.out.println(ca.getAnswer());
		}
	}
}
