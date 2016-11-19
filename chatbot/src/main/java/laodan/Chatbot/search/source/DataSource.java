/**
* @author hzyuyongmao
* @version 创建时间：2016年11月10日 下午5:38:54
* 类说明
*/
package laodan.Chatbot.search.source;

import laodan.Chatbot.search.domain.Question;

public interface DataSource {
	public Question getAnswerAndQuestion(String questionStr);

}
