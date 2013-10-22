package com.me.cometozion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Condition {
	Npc npc;
	NpcDialog dialog;

	boolean evaluate()
	{
		return true;
	}
	
	void setNpc(Npc n)
	{
		this.npc = n;
	}
	
	void setDialog(NpcDialog n)
	{
		this.dialog = n;
	}
	
	public Method getMethod(String varname) throws Exception
	{
		try
		{
			if(varname.contains("npc"))
			{
				if(null == npc)
					throw new Exception("Can't access npc without setting it first.");
				return npc.getClass().getMethod(varname.split(".")[1]);
			}
			if(varname.contains("player"))
			{
				return GlobalData.class.getMethod(varname.split(".")[1]);
			}
		}
		catch(NoSuchMethodException e)
		{
			throw new Exception("Cannot evaluate expression, method of object does not exist: "+varname);
		}

		throw new Exception("Cannot evaluate expression, object does not exist: "+varname);
	}
	
	public Object getField(String varname) throws Exception
	{
		try
		{
			if(varname.contains("npc."))
			{
				if(null == npc)
					throw new Exception("Can't access npc without setting it first.");
				System.out.println("Getting "+varname);
				String fname = varname.split("\\.")[1];
				return npc.getClass().getField(fname).get(npc);//varname.split("\\.")[1]);
			}
			if(varname.contains("player."))
			{
				return GlobalData.class.getField(varname.split("\\.")[1]).get(GlobalData.class);
			}
			if(varname.contains("self."))
			{
				return dialog.getClass().getField(varname.split("\\.")[1]).get(dialog);
			}
		}
		catch(NoSuchFieldException e)
		{
			throw new Exception("Cannot evaluate expression, field of object does not exist: "+varname+e);
		}

		throw new Exception("Cannot evaluate expression, object does not exist: "+varname.split("\\.")[1]);
	}
}
