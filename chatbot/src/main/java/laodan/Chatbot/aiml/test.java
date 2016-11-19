/**
* @author hzyuyongmao
* @version 创建时间：2016年11月18日 上午10:23:22
* 类说明
*/
package laodan.Chatbot.aiml;

import java.net.URL;
import java.util.Properties;

public class test {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("context", "Bots/context.xml");
		props.setProperty("splitters", "Bots/splitters.xml");
		String context = props.getProperty("context");
		System.out.println(context);
		System.out.println("test:" + System.getProperty("user.dir"));

		ClassLoader classLoader = test.class.getClassLoader();
		URL resource = classLoader.getResource("Bots");
		String path = resource.getPath();
		System.out.println(path);
		// InputStream resourceAsStream =
		// classLoader.getResourceAsStream("test.xml");
		
		String url="http://dmr.nosdn.127.net/qNgU2hcxMo856CpFA1wR8g==/6896093022708897730.gif ";
		System.out.println(url.trim());
		System.out.println(url.trim().endsWith(".gif"));
		  
	}
}
