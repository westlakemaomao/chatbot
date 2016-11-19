package laodan.Chatbot.aiml;


import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.io.ByteArrayOutputStream;

import laodan.Chatbot.aiml.bitoflife.chatterbean.AliceBot;
import laodan.Chatbot.aiml.bitoflife.chatterbean.Context;
import laodan.Chatbot.aiml.bitoflife.chatterbean.parser.AliceBotParser;
import laodan.Chatbot.aiml.bitoflife.chatterbean.util.Searcher;

public class AliceBotMother
{
  
  private ByteArrayOutputStream gossip;
  
  
  public void setUp()
  {
    gossip = new ByteArrayOutputStream();
  }
  
  public String gossip()
  {
    return gossip.toString();
  }

//  public AliceBot newInstance() throws Exception
//  {
//    Searcher searcher = new Searcher();
//    AliceBotParser parser = new AliceBotParser();
//    
//    AliceBot bot = parser.parse(new FileInputStream("Bots/context.xml"),
//                                new FileInputStream("Bots/splitters.xml"),
//                                new FileInputStream("Bots/substitutions.xml"),
//                                searcher.search("Bots/mydomain", ".*\\.aiml"));
//
//    Context context = bot.getContext(); 
//    context.outputStream(gossip);
//    return bot;
//  }
  
  public AliceBot newInstance() throws Exception
  {
    Searcher searcher = new Searcher();
    AliceBotParser parser = new AliceBotParser();
    ClassLoader classLoader = test.class.getClassLoader();
	URL resource = classLoader.getResource("Bots");
	String path = resource.getPath();
	
    AliceBot bot = parser.parse(new FileInputStream(path+"/context.xml"),
                                new FileInputStream(path+"/splitters.xml"),
                                new FileInputStream(path+"/substitutions.xml"),
                                searcher.search(path+"/mydomain", ".*\\.aiml"));

    Context context = bot.getContext(); 
    context.outputStream(gossip);
    return bot;
  }
}
