package com.me.cometozion;

import java.util.HashMap;

public class Scripture {
	public String ref; // short format
	public String reference; // expanded format
	public String text; // Map of Version --> Scripture text
	public Question question;
	public String keyword;
	
	public Scripture(String ref, String reference, Question question, String keyword, String text)
	{
		this.ref = ref;
		this.reference = reference;
		this.question = question;
		this.question.setScripture(this);
		this.keyword = keyword;
		this.text = text;
	}
}
