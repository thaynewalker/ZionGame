package com.me.cometozion;

import java.util.HashMap;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class GlobalData
{
	public static HashMap<String,Entity> entities;
	public static String preferredBibleVersion = Config.asString("Prefs.preferredBibleVersion", "KJV");
	public static float life = 100f;
	public static int faith = Config.asInt("Global.startingFaith", 10);
	public static int cash = Config.asInt("Global.startingCash", 3);
	public static int food = Config.asInt("Global.startingFood", 2);
	public static HashMap<String, Item> inventory = new HashMap<String, Item>(); // Item name to item
	public static HashMap<Body,Npc> npcs = new HashMap<Body,Npc>();
	public static HashMap<String, HashMap<String, Scripture> > knowledge = new HashMap<String, HashMap<String, Scripture> >(); //  topic to Scripture ref to scripture
	public static boolean stopping = false;
	public static Music bgMusic;
	public static int musicNum = 0;
	public static HashMap<String, NpcDialog> npcDialogs = new HashMap<String, NpcDialog>();
	protected static Scripture pickedScripture = null;
	public static int totalItems(String name)
	{
		return inventory.get(name).quantity;
	}
	public static void startBgMusic()
	{
		bgMusic = Assets.gameMusic.get(musicNum);
		bgMusic.setOnCompletionListener(new Music.OnCompletionListener(){
			public void onCompletion(Music m)
			{
				musicNum++;
				if(musicNum >= Assets.gameMusic.size())
				{
					musicNum = 0;
				}
				
				startBgMusic();
			}
		});
		bgMusic.play();
	}
	
}