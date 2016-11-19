package laodan.Chatbot.aiml.bitoflife.chatterbean.aiml;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.Attributes;

import laodan.Chatbot.aiml.bitoflife.chatterbean.AliceBot;
import laodan.Chatbot.aiml.bitoflife.chatterbean.Context;
import laodan.Chatbot.aiml.bitoflife.chatterbean.Match;
import laodan.Chatbot.aiml.bitoflife.chatterbean.Graphmaster;

public class Br extends TemplateElement
{
  /*
  Constructors
  */

  public Br(Attributes attributes)
  {
  }

  public Br(Object... children)
  {
    super(children);
  }

  /*
  Methods
  */

  public String process(Match match)
  {
    return "";
  }
}