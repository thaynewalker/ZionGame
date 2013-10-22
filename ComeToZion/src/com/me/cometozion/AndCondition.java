package com.me.cometozion;

public class AndCondition extends Condition{
	Condition a;
	Condition b;
	
	@Override
	void setNpc(Npc n)
	{
		this.npc = n;
		this.a.setNpc(n);
		this.b.setNpc(n);
	}
	
	@Override
	void setDialog(NpcDialog n)
	{
		this.dialog = n;
		this.a.setDialog(n);
		this.b.setDialog(n);
	}
	
	@Override
	public String toString()
	{
		if(a.npc != null && b.npc != null)
			return "(" +a + ") AND ("+b+") = "+evaluate();
		else
			return "(" +a + ") AND ("+b+") = ?";
	}

	AndCondition(Condition a, Condition b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	boolean evaluate()
	{
		return a.evaluate() && b.evaluate();
	}
}