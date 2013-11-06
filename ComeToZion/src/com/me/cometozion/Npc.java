package com.me.cometozion;

import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;


public class Npc
{
	public int numQuestions = 0;
	HashMap<Question,Boolean> questions;
	public boolean baptized;
	Polyline path;
	ZionBody body;
	public float trust = 0.5f;
	int currpos = 0;
	int nextpos = 1;
	boolean reverse = false;
	HashMap<String, Animation[]> animations = new HashMap<String, Animation[]>();
	float stateTime = 0f;
	String currentAction = "walk";
	int picnum = 0;
	public boolean nameKnown = false;
	public boolean occupationKnown = false;
	public int goodsQuantity;
	public int goodsPrice;
	public String goodsType;
	public HashMap<String,String> attributes = new HashMap<String,String>();
	//Vector<ConversationTopic> topics = new Vector<ConversationTopic>();
	public String spriteName;
	
	public Npc(String name, String[] questions, String occupation, ZionBody b, float trst)
	{
		this.attributes.put("name",name);
		this.trust = trst;
		this.questions = new HashMap<Question,Boolean>();
		for(String q: questions)
		{
			this.questions.put(Assets.questions.get(q),false);
			this.numQuestions++;
		}
		this.baptized = false;
		this.body = b;
		this.attributes.put("occupation",occupation);
		this.goodsQuantity = MathUtils.random(3);
		this.attributes.put("goodsType",Assets.occupationGoods.get(attributes.get("occupation"))[MathUtils.random(Assets.occupationGoods.get(attributes.get("occupation")).length-1)]);
		this.goodsType = attributes.get("goodsType");
		this.goodsPrice = Assets.itemValues.get(attributes.get("goodsType"))[1];
		this.attributes.put("goodsPrice",goodsPrice+" cash");
	}
	
	public void setPath(Polyline p)
	{
		float[] v = p.getVertices();
		for(int i = 0; i< v.length; ++i)
		{
			v[i] += (i %2 == 0) ? p.getX() : p.getY();
			System.out.println(v[i]+"-->"+v[i]*Assets.px_to_local_scale);
			v[i] *= Assets.px_to_local_scale;
			//System.out.println(v[i]);
		}
		this.path = new Polyline(v);
		//this.path.setScale(Assets.px_to_local_scale, Assets.px_to_local_scale);
		body.setTransform(path.getVertices()[0], path.getVertices()[1], 0);
		//body.setTransform(path.getTransformedVertices()[0], path.getTransformedVertices()[1], 0);
		this.currpos = 0;
		this.nextpos = 1;
		this.reverse = false;
	}
	
	public void addAnimation(String name, Animation[] anim)
	{
		this.animations.put(name,anim);
	}
	
	public TextureRegion getCurrentFrame()
	{
		Vector2 tmp = this.body.getLinearVelocity();
		float vel = (float) Math.sqrt(tmp.x*tmp.x+tmp.y*tmp.y);
		stateTime += vel/120.0f;
		Animation[] animation = animations.get(currentAction);
		int numdirs = animation.length;
		if(currentAction == "walk" && tmp.x != 0 && tmp.y != 0)
		{
			picnum = numdirs-((int) Math.round((Math.atan2(tmp.y,tmp.x)*(72f/numdirs)/Math.PI+(numdirs+numdirs/4))%numdirs)%numdirs);
			if(picnum >= numdirs)
				picnum = 0;
		}
		else
		{
			stateTime += 1/170f;
		}
		TextureRegion tr;
		try
		{
			tr = animation[picnum].getKeyFrame(stateTime, true);
		}
		catch(Exception e)
		{
			System.out.println(e);
			tr = animation[picnum].getKeyFrame(0,true);
		}
		return tr;
	}
	
	Vector2 position()
	{
		return body.getPosition();
	}
	
	Vector2 nextposition()
	{
		//return new Vector2(path.getTransformedVertices()[nextpos*2]*Assets.px_to_local_scale,path.getTransformedVertices()[nextpos*2+1]*Assets.px_to_local_scale);
		return new Vector2(path.getVertices()[nextpos*2],path.getVertices()[nextpos*2+1]);
	}
	
	int numpositions()
	{
		return path.getVertices().length/2;
	}
	
	public void setNameKnown()
	{
		this.nameKnown = true;
	}
	
	public void addRandomTrust(String low, String high)
	{
		this.trust += MathUtils.random(Float.valueOf(low), Float.valueOf(high));
	}
	
	public void setOccupationKnown()
	{
		this.occupationKnown = true;
	}
}
