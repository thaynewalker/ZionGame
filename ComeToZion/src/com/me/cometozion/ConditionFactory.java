package com.me.cometozion;

import java.util.Vector;

public class ConditionFactory
{
	private static ConditionFactory instance;
	protected ConditionFactory()
	{
	}
	public static ConditionFactory getInstance()
	{
		if(instance == null)
			instance = new ConditionFactory();
		return instance;
	}
	static Condition makeCondition(String s) throws Exception
	{
		Condition result;
		Vector<Condition> conds = getExpressions(s);
		System.out.println("Creating condition from "+s);
		if(conds.size()>1)
		{
			if(s.contains("&&"))
			{
				result = new AndCondition(conds.get(0),conds.get(1));
			}
			else if(s.contains("||"))
			{
				result = new OrCondition(conds.get(0),conds.get(1));
			}
			else
				throw new Exception("Unrecognized conditional expression: "+s);
		}
		else
		{
			result = resolveType(s);
		}
		if(conds.size()>2)
		{
			for(int i=2; i<conds.size();i++)
			{
				String s2 = s.split("\\s(&&|\\|\\|)\\s",i)[1];
				if(s2.contains("&&"))
				{
					result = new AndCondition(result,conds.get(i));
				}
				else if(s2.contains("||"))
				{
					result = new OrCondition(result,conds.get(i));
				}
			}
		}
		return result;
	}

	static Condition resolveType(String s)
	{
		String[] fields = s.split("\\s(<=|>=|==|<|>)\\s");
		if(s.contains("\""))
		{
			return new StringCondition(fields[0],fields[1],getOp(s));
		}
		if(fields.length > 1)
		{
			return new FloatCondition(fields[0],fields[1],getOp(s));
		}
		else
			return new BoolCondition(fields[0]);
	}

	static String getOp(String o)
	{
		if(o.contains("=="))
		{
			return "eq";
		}
		if(o.contains("<="))
		{
			return "lte";
		}
		if(o.contains(">="))
		{
			return "gte";
		}
		if(o.contains("<"))
		{
			return "lt";
		}
		if(o.contains(">"))
		{
			return "gt";
		}
		if(o.contains("!="))
		{
			return "ne";
		}
		return "flag";
	}

	static Vector<Condition> getExpressions(String e)
	{
		Vector<Condition> result = new Vector<Condition>();
		if(e.contains("&&") || e.contains("||"))
		{
			String[] stuff = e.split("\\s(&&|\\|\\|)\\s");
			for(String s: stuff)
			{
				result.add(resolveType(s));
			}
		}
		return result;
	}
}



