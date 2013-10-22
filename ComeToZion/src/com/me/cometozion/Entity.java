package com.me.cometozion;

import java.util.Map;

class Entity
{
	private Map<String,Action> actions;
	
	public void addAction(String name, Action action)
	{
		actions.put(name,action);
	}
	
	public Action getAction(String name)
	{
		return actions.get(name);
	}
}