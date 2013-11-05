package com.me.cometozion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.maps.MapObject;

// Wrapper class for Body
public class ZionBody {

	Body body;
	public ZionBody (Body body) {
		this.body = body;
	}
	
	@Override
	public String toString()
	{
		if(body.getUserData() != null)
		{
			if(body.getUserData().getClass().getName().contains("MapObject"))
			{
				String s = ((MapObject)body.getUserData()).getName();
				if(s != null)
					return s;
			}
			return body.getUserData().toString();
		}
		return "undefined";
	}
	
	public Fixture createFixture (FixtureDef def) {
		return body.createFixture(def);
	}
	
	public Fixture createFixture (Shape shape, float density) {
		return body.createFixture(shape,density);
	}
	
	public Vector2 getLinearVelocity()
	{
		return body.getLinearVelocity();
	}
	
	public void setUserData(Object o)
	{
		body.setUserData(o);
	}
	
	public Vector2 getPosition()
	{
		return body.getPosition();
	}
	
	public void setLinearVelocity(float x, float y)
	{
		body.setLinearVelocity(x,y);
	}
	
	public void setLinearVelocity(Vector2 x)
	{
		body.setLinearVelocity(x);
	}
	
	public void setTransform(float x, float y, float angle)
	{
		body.setTransform(x, y, angle);
	}

	public Object getUserData() {
		return body.getUserData();
	}
}
