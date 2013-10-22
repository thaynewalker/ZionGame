package com.me.cometozion;

public class FloatCondition extends Condition {

	String a;
	String b;
	String op;
	FloatCondition(String a, String b, String op)
	{
		try{
			this.a = a;
			this.b = b;
			this.op = op;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	float a()
	{
		try
		{
			return Float.parseFloat(a);
		}
		catch(Exception f)
		{
			try
			{
				return (Float)getField(a);
			}
			catch (Exception e)
			{
				try
				{
					return ((Integer)getField(a)).floatValue();
				}
				catch (Exception g)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					f.printStackTrace();
					g.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	float b()
	{
		try {
			return Float.parseFloat(b);
		}
		catch(Exception f)
		{
			try {
				return (Float)getField(b);
			} catch (Exception e) {
				try
				{
					return ((Integer)getField(b)).floatValue();
				}
				catch (Exception g)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					f.printStackTrace();
					g.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	@Override
	public String toString()
	{
		if(npc != null)
			return a + "("+a()+") " + op + " " + b + "("+b()+ ") = "+evaluate();
		return this.a + " " + this.op + " " + this.b + " = ?";
	}

	@Override
	boolean evaluate()
	{
		if(op == "lt")
		{
			return a() < b();
		}
		else if(op == "lte")
		{
			return a() <= b();
		}
		else if(op == "gte")
		{
			return a() >= b();
		}
		else if(op == "gt")
		{
			return a() > b();
		}
		else if(op == "eq")
		{
			return a() == b();
		}
		return false;
	}
}