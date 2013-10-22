package com.me.cometozion;

public class BoolCondition extends Condition{
	String a;
	boolean negate = true;

	BoolCondition(String a)
	{
		if(a.contains("!"))
		{
			this.negate = true;
			a.replaceAll("!", "");
		}
		else
			this.negate = false;
		this.a = a;
		
	}
	
	boolean a()
	{
		try {
			return (Boolean)getField(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		if(this.npc != null)
			return a + "(" + a() + ") = "+evaluate();
		else
			return a + " = ?";
	}

	@Override
	boolean evaluate()
	{
		if(this.negate)
			return !a();
		return a();
	}
}