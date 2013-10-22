package com.me.cometozion;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class NpcDialog {
	Npc npc;
	String speaker;
	String text;
	Condition condition;
	Kekka result;
	public Set<NpcDialog> choices;
	public boolean didit = false;
	public NpcDialog(String s, String t, Condition c, Kekka r)
	{
		speaker = s;
		text = t;
		condition = c;
		result = r;
		choices = new HashSet<NpcDialog>();
	}
	
	public void setNpc(Npc n)
	{
		if(n == npc)
			return;
		didit = false;
		npc = n;
		if(condition != null)
			condition.setNpc(n);
		if(result != null)
			result.setNpc(n);
	}
	
	public String toString()
	{
		String v = text + "; " + condition + "; " + result + " Choices("+choices.size()+")";
		//for(Object o: choices.toArray())
		//{
			//v += "\n\t"+(NpcDialog)o;
		//}
		return v;
	}
	
	public void addDialog(NpcDialog n)
	{
		choices.add(n);
	}
	
	void setKekka(Kekka k)
	{
		result = k;
	}
	
	void setSpeaker(String s)
	{
		speaker = s;
	}
	
}
