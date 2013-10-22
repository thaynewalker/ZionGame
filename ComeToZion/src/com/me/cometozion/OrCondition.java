package com.me.cometozion;

public class OrCondition extends Condition{
	Condition a;
	Condition b;

	OrCondition(Condition a, Condition b)
	{
		this.a = a;
		this.b = b;
	}
	
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
			return "(" +a + ") OR ("+b+") = "+evaluate();
		else
			return "(" +a + ") OR ("+b+") = ?";
	}

	@Override
	boolean evaluate()
	{
		return a.evaluate() || b.evaluate();
	}
}
