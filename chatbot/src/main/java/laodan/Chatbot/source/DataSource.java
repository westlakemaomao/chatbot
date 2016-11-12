/**
* @author hzyuyongmao
* @version 创建时间：2016年11月10日 下午5:38:54
* 类说明
*/
package laodan.Chatbot.source;

import laodan.Chatbot.domain.Question;

public interface DataSource {
	public Question getAnswerAndQuestion(String questionStr);

}
