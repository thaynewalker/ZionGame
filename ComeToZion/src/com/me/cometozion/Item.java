package com.me.cometozion;

public class Item {
	public int quantity = 0;
	public Item(int quantity)
	{
		this.quantity = quantity;
	}
	
	@Override
	public	String toString()
	{
		return ""+quantity;
	}
}
