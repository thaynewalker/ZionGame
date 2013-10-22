package com.me.cometozion;

public class StringCondition extends Condition {

	String a;
	String b;
	String op;

	StringCondition(String a, String b, String op)
	{
		
			this.a = a;
			this.b = b;
			this.op = op;
		
	}

	String a()
	{
		try {
			return (String)getField(a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	String b()
	{
		try {
			return (String)getField(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String toString()
	{
		return a + "("+a()+") " + op + " " + b + "("+b()+ ") = "+evaluate();
	}

	boolean evaluate()
	{
		if(op == "lt")
		{
			return a().compareTo(b()) < 0;
		}
		else if(op == "lte")
		{
			return a().compareTo(b()) <= 0;
		}
		else if(op == "gte")
		{
			return a().compareTo(b()) >= 0;
		}
		else if(op == "gt")
		{
			return a().compareTo(b()) > 0;
		}
		else if(op == "eq")
		{
			return a().compareTo(b()) == 0;
		}
		return false;
	}
}