/**
* @author hzyuyongmao
* @version 创建时间：2016年11月10日 下午7:01:31
* 类说明
*/
package laodan.Chatbot.search.domain;

import java.util.ArrayList;
import java.util.List;



public class Question {
	private String question;// 问题
	private final List<CandidateAnswer> candidateAnswerList = new ArrayList<>();// 相关问题-》答案

	

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<CandidateAnswer> getCandidateAnswerList() {
		return candidateAnswerList;
	}

	public void addCandidatAnswer(CandidateAnswer candidateAnswer) {
		this.candidateAnswerList.add(candidateAnswer);
	}

	public void addCandidatAnswer(List<CandidateAnswer> candidateAnswerList) {
		this.candidateAnswerList.addAll(candidateAnswerList);
	}

	public void removeCandidatAnswer(CandidateAnswer candidateAnswer) {
		this.candidateAnswerList.remove(candidateAnswer);
	}

	public void removeCandidatAnswer(List<CandidateAnswer> candidateAnswer) {
		this.candidateAnswerList.removeAll(candidateAnswer);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (CandidateAnswer ca : candidateAnswerList) {
			sb.append(ca.getTitle());
			sb.append("->" + ca.getAnswer()+"\n");
		}
		return sb.toString();

	}
}
