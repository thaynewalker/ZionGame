package com.me.cometozion;

import java.util.HashMap;

public class ConversationTopic {

  public String title = "";
  public String question = "";
  public float threshold = 0.5f;
  public HashMap<String, ConversationTopic> topics;

  ConversationTopic(String q, float threshold, ConversationTopic[] c)
  {
    this.question = q;
    this.threshold = threshold;
    this.topics = new HashMap<String, ConversationTopic>();
    for(ConversationTopic cc: c)
    {
      this.topics.put(cc.title,cc);
    }
  }
}
