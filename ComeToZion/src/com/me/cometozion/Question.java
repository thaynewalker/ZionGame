package com.me.cometozion;

public class Question{
	public String text;
	public Scripture scripture;
	public Question(String text)
	{
		this.text = text;
	}
	public void setScripture(Scripture s)
	{
		this.scripture = s;
	}
}
