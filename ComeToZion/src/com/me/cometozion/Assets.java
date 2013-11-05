package com.me.cometozion;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;


public class Assets {
	private static final String[] entityNames = Config.asString("entities.names", "mainplayer").split(",");
	private static Map<String, Entity> entities;
	public static float px_to_local_scale = 0f;
	
	private static final String[] topics = Config.asString("Scripture.topics", "faith").split(",");
	private static final String[] versions = Config.asString("Scripture.versions", "KJV").split(",");
	public static HashMap<String, Scripture> scriptures = new HashMap<String, Scripture>();
	public static HashMap<String, Question> questions = new HashMap<String, Question>();

	private static final String FLYUP_FONT = Config.asString("global.flyupFont", "ocr_a_small.fnt");
	private static final String SCORE_FONT = Config.asString("global.scoreFont", "wellbutrin.fnt");
	private static final String TEXT_FONT = Config.asString("global.textFont", "ocr_a.fnt");

	public static TiledMap map;
	public static TextureAtlas atlas;
	public static Skin skin;
	public static HashMap< String, Vector<Sound> > sounds = new HashMap< String, Vector<Sound> >();

	public static TextureRegion pureWhiteTextureRegion;

	public static HashMap<String, HashMap<String, Animation[]> > animations;
	
	public static BitmapFont scoreFont;
	public static BitmapFont textFont;
	public static BitmapFont flyupFont;

	public static Vector<Music> gameMusic = new Vector<Music>();
	public static HashMap<String,Integer> foodTypes = new HashMap<String,Integer>();
	public static HashMap<String,String[]> occupationGoods = new HashMap<String,String[]>();
	public static HashMap<String,Point> itemValues = new HashMap<String,Point>();
	
	
	public static void load () {
		animations = new HashMap<String, HashMap<String, Animation[]> >();
		String mappath = Config.asString("map.filepath", "data/map/test.tmx");
		map = new TmxMapLoader().load(mappath);
		String textureFile = "data/pack/packed.tex";
		String textureDir = "data/pack";
		atlas = new TextureAtlas(Gdx.files.internal(textureFile), Gdx.files.internal(textureDir));
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		loadTextures();
		createAnimations();
		loadFonts();
		loadSounds();
		loadMusic();
		loadDialogs();
		loadConfigData();
		GlobalData.startBgMusic();
		loadScriptures("KJV");
	}
	private static void loadDialogs(){
		Vector<String> texts = new Vector<String>();
		String current = "";
		Vector<Condition> conditions = new Vector<Condition>();
		Vector<Kekka> actions = new Vector<Kekka>();
	
		FileHandle fh = Gdx.files.internal("data/dialogs/generic");
		String dlgName = fh.nameWithoutExtension();
		NpcDialog rootdlg = new NpcDialog("Me","root",null,null);
		
		int dlgcnt = 0;
		GlobalData.npcDialogs.put(dlgName, rootdlg);
		HashMap<String,NpcDialog> dlgmap = new HashMap<String,NpcDialog>();
		dlgmap.put("root",rootdlg);
		String[] data = fh.readString().split("\n");
		for(int i=0;i<data.length;++i)
		{
			if(i<5)
				continue;
			String line = data[i].trim();
			if(line.contains("text = ["))
			{
				current = "texts";
				texts.add("root");
				continue;
			}
			if(line.contains("cond = ["))
			{
				current = "cond";
				continue;
			}
			if(line.contains("code = ["))
			{
				current = "code";
				continue;
			}
			if(line.contains("dlg = ["))
			{
				current = "dlg";
				continue;
			}
			if(current == "texts" && line.contains("\""))
			{
				texts.add(line.replaceAll(".*\"([^\"]*)\".*","$1"));
			}
			if(current == "cond" && line.contains("\""))
			{
				try {
					conditions.add(ConditionFactory.makeCondition(line.replaceAll("\"(.*)\\\\n\".*","$1")));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(current == "code" && line.contains("\""))
			{
				try {
					for(String line2: line.split("\\n"))
					{
						if(line2.length() > 5)
							actions.add(new Kekka(line2.replaceAll("\"(.*)\\\\n\".*","$1")));
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(current == "dlg" && line.contains("#"))
			{
				try {
					System.out.println("For splitting: "+line.replaceAll("\\(([^,]*),\\s*([^,]*),\\s*(.*)\\s*#.*", "$1@$2@$3"));
					String[] dlgtxt = line.replaceAll("\\(([^,]*),\\s*([^,]*),\\s*(.*)(,|\\])\\s*#.*", "$1@$2@$3").split("@");
					String speaker = dlgtxt[0] == "None" ? "Me" : dlgtxt[0].replace("\"", "");
					Kekka code = !dlgtxt[1].equals("-1") ? actions.get(Integer.parseInt(dlgtxt[1])) : null;
					NpcDialog dlg;
					if(dlgmap.containsKey(texts.get(dlgcnt)))
					{
						dlg = dlgmap.get(texts.get(dlgcnt));
						dlg.setSpeaker(speaker);
						dlg.setKekka(code);
					}
					else
					{
						dlg = new NpcDialog(speaker,texts.get(dlgcnt),null,code);
					}
					for(String s: dlgtxt[2].split("\\),"))
					{
						s = s.replaceAll("[^0-9-]*([0-9-]*), 0, ([0-9-]*).*","$1,$2");
						System.out.println(s);
						if(s.contains(","))
						{
							String text = texts.get(Integer.parseInt(s.split(",")[0]));
							if(dlgmap.containsKey(text))
							{
								System.out.println("Add "+dlgmap.get(text)+" to "+dlg);
								dlg.addDialog(dlgmap.get(text));
							}
							else
							{
								Condition c = null;
								int ci = Integer.parseInt(s.split(",")[1]);
								if(ci != -1)
									c = conditions.get(ci);
								NpcDialog ndlg = new NpcDialog(null,text,c,null);
								if(c != null)
									c.setDialog(ndlg);
								dlgmap.put(text,ndlg);
								System.out.println("Add "+ndlg+" to "+dlg);
								dlg.addDialog(ndlg);
							}
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println(line+" "+dlgcnt);
					e1.printStackTrace();
				}
				dlgcnt++;
			}
		}
		
		
	}
	private static void loadMusic(){
		FileHandle dh;
		if (Gdx.app.getType() == ApplicationType.Android) {
			  dh = Gdx.files.internal("data/music/");
			} else {
			  // ApplicationType.Desktop ..
			  dh = Gdx.files.internal("./bin/data/music/");
			}
		FileHandle[] fhs = dh.list();
		System.out.println(fhs.length);
		for (int i = 0; i < fhs.length; i++) {
			String name = fhs[i].name();
			System.out.println(dh.path()+"/"+name);
			Music tmp = Gdx.audio.newMusic(Gdx.files.internal("data/music/"+name));
			tmp.setLooping(false);
			tmp.setVolume(0.09f);
			gameMusic.add(tmp);
		}
	} 

	private static String SCRIP_ERROR = "ERROR";
	public static String fixstr(String in)
	{
		return in.replace('_', ' ').replace('@', ':').replace("\\n", "\n");
	}
	private static void loadScriptures(String v) // KJV = king james version
	{
		for(String topic: topics)
		{
			if(GlobalData.knowledge.get(topic) == null)
			{
				GlobalData.knowledge.put(fixstr(topic), new HashMap<String, Scripture>());
				//scriptures.put(fixstr(topic), new HashMap<String, Scripture>());
			}
		}

		String[] references = Config.asString("Scripture.references", "").split(";");

		for(String reference: references)
		{
			String ref = Config.asString("Scripture."+reference+".ref", SCRIP_ERROR);
			//System.out.println("Scripture."+reference+".ref="+ref);
			String qnum = fixstr(Config.asString("Scripture."+reference+".question", "q0"));
			Question question = new Question(fixstr(Config.asString("Questions."+qnum, "Some question")));
			questions.put(qnum, question);
			String text = fixstr(Config.asString("Scripture."+reference+"."+v+".text", SCRIP_ERROR));
			String keyword = fixstr(Config.asString("Scripture."+reference+".keyword", SCRIP_ERROR));
			if(scriptures.get(reference) == null)
			{
				scriptures.put(fixstr(reference), new Scripture(fixstr(reference),fixstr(ref),question,keyword,text));
			}
		}
		
		// Load a couple of defaults into knowledge
		GlobalData.knowledge.get("Faith").put("Mat17:20",Assets.scriptures.get("Mat17:20"));
		GlobalData.knowledge.get("Other").put("1Ne3:7",Assets.scriptures.get("1Ne3:7"));
		GlobalData.knowledge.get("Other").put("Jas2:17-18",Assets.scriptures.get("Jas2:17-18"));
	}

	private static void loadConfigData()
	{
		String[] occupationTypes = Config.asString("Npc.occupationTypes","Unknown").split(",");
		String[] itemTypes = Config.asString("ItemTypes","thing").split(",");
		String[] food = Config.asString("FoodTypes","bread:1").split(",");
		for(String f: food)
		{
			foodTypes.put(f.split(":")[0], Integer.parseInt(f.split(":")[1]));
		}
		for(String o: occupationTypes)
		{
			Assets.occupationGoods.put(o, Config.asString(o+".goodsType", "food").split(","));
		}
		for(String g: itemTypes)
		{
			String[] v = Config.asString(g+".value", "5").split(",");
			Assets.itemValues.put(g, new Point(Integer.parseInt(v[0]),Integer.parseInt(v[1])));
		}
	}
	private static void createAnimations()
	{
		for(String ename: entityNames)
		{
			String[] actionNames = Config.asString("entities."+ename+".actions", "walk").split(",");
			if(animations.get(ename) == null)
				animations.put(ename, new HashMap<String, Animation[]>());
			for(String aname: actionNames)
			{
				
				int numDirs = Config.asInt("entities."+ename+"."+aname+".directions", 12);
				if(animations.get(ename).get(aname) == null)
					animations.get(ename).put(aname, new Animation[numDirs]);
				for(int d = 0; d< numDirs; ++d)
				{
					animations.get(ename).get(aname)[d] = loadAnimation(ename+"/direction"+d+"/"+aname,Config.asFloat("entities."+ename+"."+aname+".duration", 1.0f));
				}
			}
		}
	}
	
	public static Animation loadAnimation(String regionName, float animDuration)
	{
		Array<AtlasRegion> tmp = atlas.findRegions(regionName);
		return new Animation(animDuration/tmp.size, tmp); 
	}

	private static void loadTextures () {
		pureWhiteTextureRegion = atlas.findRegion("inven");
		//System.out.println(pureWhiteTextureRegion.toString());
	}

	private static void loadFonts () {
		String fontDir = "data/fonts/";

		scoreFont = new BitmapFont(Gdx.files.internal(fontDir + SCORE_FONT), false);
		textFont = new BitmapFont(Gdx.files.internal(fontDir + TEXT_FONT), false);
		flyupFont = new BitmapFont(Gdx.files.internal(fontDir + FLYUP_FONT), false);

		scoreFont.setScale(1.0f / 24);
		textFont.setScale(1.0f / 24);
		flyupFont.setScale(1.0f / 24);
	}

	private static void loadSounds ()
	{
		System.out.println("foo");
		FileHandle dh;
		if (Gdx.app.getType() == ApplicationType.Android) {
			  dh = Gdx.files.internal("data/sounds/");
			} else {
			  // ApplicationType.Desktop ..
			  dh = Gdx.files.internal("./bin/data/sounds");
			}
		FileHandle[] fhs = dh.list();
		System.out.println(fhs.length);
		for (int i = 0; i < fhs.length; i++) {
			String name = fhs[i].name();
			String name_base = fhs[i].nameWithoutExtension().split("_")[0];
			if(sounds.get(name_base) == null)
			{
				sounds.put(name_base, new Vector<Sound>());
			}
			sounds.get(name_base).add(loadSound(name));
			System.out.println("added "+name);
		}
	}

	private static Sound loadSound (String filename) {
		return Gdx.audio.newSound(Gdx.files.internal("data/sounds/" + filename));
	}


	public static void playSound(String name){
		sounds.get(name).get(MathUtils.random(0,sounds.get(name).size()-1)).play(1);
	}
}
