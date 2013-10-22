package com.me.cometozion;

import java.lang.reflect.Method;
import java.util.Vector;

public class Kekka {
	Npc npc;
	private Vector<String> methods = new Vector<String>();
	private Vector<Object[]> params = new Vector<Object[]>();
	private Vector<Vector<Class<?>>> signature = new Vector<Vector<Class<?>>>();
	
	Kekka(String action)
	{
		String[] tmpmethods;
		tmpmethods = action.split("\\\\n");
		for(String method: tmpmethods)
		{
			this.methods.add(method.split("\\(")[0]);
			Object[] m = null;
			Vector<Class<?>> cls = null;
			if(method.contains("("))
			{
				m = method.replaceAll("[^\\(]*\\(([^\\)]*)\\).*","$1").split(",");
				cls = new Vector<Class<?>>();
				for(Object mm: m)
					cls.add(String.class);
			}
			this.params.add(m);
			this.signature.add(cls);
		}
	}

	void execute()
	{
		int ix = 0;
		for(String action: methods)
		{
			Object obj;
		
			if(action.split("\\.")[0].equals("npc"))
				obj = this.npc;
			else
				obj = this;

			try
			{
				if(signature.get(ix) != null)
				{
					Class<?>[] sig = new Class<?>[params.get(ix).length];
					String[] param = new String[params.get(ix).length];
					int i = 0;
					for(Object p: params.get(ix))
					{
						sig[i] = String.class;
						if(((String)p).contains("npc.")||((String)p).contains("player."))
						{
							param[i] = getField((String)p).toString();
						}
						else
							param[i] = p.toString();
						++i;
					}
					Method m = getMethodByName(action,sig);
					
					m.invoke(obj, (String[])param);
				}
				else
					getMethodByName(action,null).invoke(obj, params.get(ix));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ix++;
		}
	}
	
	void setNpc(Npc n)
	{
		this.npc = n;
	}
	
	public String toString()
	{
		String result = methods+"(";
		for(Object o: params)
			result +=o;
		return result+")";
	}
	
	public Method getMethodByName(String name, Class<?>[] sig) throws Exception
	{
		try
		{
			if(name.contains("npc"))
			{
				if(null == npc)
					throw new Exception("Can't access npc without setting it first.");
				//Method[] mms = npc.getClass().getMethods();
				//String nn = name.split("\\.")[1];
				//Method mmmm = npc.getClass().getMethod(name.split("\\.")[1], sig);
				return npc.getClass().getMethod(name.split("\\.")[1],sig);
			}
			if(name.contains("player"))
			{
				return this.getClass().getMethod(name.split("\\.")[1],sig);
			}
		}
		catch(NoSuchMethodException e)
		{
			throw new Exception("Cannot evaluate expression, method of object does not exist: "+name+" " +e);
		}
		catch(Exception e)
		{
			throw new Exception("Cannot evaluate expression, error occurred: "+name+" "+e);
		}

		throw new Exception("Cannot evaluate expression, object does not exist: "+name);
	}
	
	public Object getField(String varname) throws Exception
	{
		try
		{
			if(varname.contains("npc"))
			{
				if(null == npc)
					throw new Exception("Can't access npc without setting it first.");
				return npc.getClass().getField(varname.split("\\.")[1]).get(npc);
			}
			if(varname.contains("player"))
			{
				return GlobalData.class.getField(varname.split("\\.")[1]).get(GlobalData.class);
			}
		}
		catch(NoSuchFieldException e)
		{
			throw new Exception("Cannot evaluate expression, field of object does not exist: "+varname);
		}

		throw new Exception("Cannot evaluate expression, object does not exist: "+varname);
	}
	
	public static void addCash(String g)
	{
		Assets.playSound("cash");
		GlobalData.cash += Integer.valueOf(g);
	}
	public static void removeCash(String g)
	{
		Assets.playSound("cash");
		GlobalData.cash -= Integer.valueOf(g);
	}
	public static void addInven(String type, String amount)
	{
		Item i = GlobalData.inventory.get(type);
		if(i == null)
		{
			GlobalData.inventory.put(type, new Item(Integer.parseInt(amount)));
		}
		else
		{
			i.quantity += Integer.parseInt(amount);
		}
	}
}
