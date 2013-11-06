package com.me.cometozion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen{

	
	private class CTexture extends TextureRegion implements Comparable<CTexture> 
	{
		private Vector2 pos;

		
		public CTexture(TextureRegion t, Vector2 p)
		{
			super(t);
			this.pos = p;
		}
		
		public CTexture(TextureRegion t, float a, float b)
		{
			super(t);
			this.pos = new Vector2(a,b);
		}
		
		@Override
		public
		int compareTo(CTexture o)
		{
			if(this.pos.y > o.pos.y)
				return -1;
			if(this.pos.y < o.pos.y)
				return 1;
			return 0;
		}
	}
	ComeToZionMain game;
	private final ArrayList<ZionBody> destroyList = new ArrayList<ZionBody>();
	
	private final String TAG = "GameMap";
	
	private ZionOrthogonalTiledMapRenderer renderer;
	
	private OrthoCamController cameraController;
	
	private World world;
	private Box2DDebugRenderer dbgRenderer;
	private BodyDef northBorderDef;
	private BodyDef southBorderDef;
	private BodyDef eastBorderDef;
	private BodyDef westBorderDef;
	private ZionBody northBorder;
	private ZionBody southBorder;
	private ZionBody eastBorder;
	private ZionBody westBorder;
	private PolygonShape north;
	private PolygonShape south;
	private PolygonShape east;
	private PolygonShape west;

	private String currentAction = "walk";
	private BodyDef mainBodyDef;
	private ZionBody mainZionBody;
	private FixtureDef fixtureDef;
	private CircleShape mainShape;
	private float rendersize;
	private float tilesize;
	private int picnum = 0;
	private Label lifeValLabel;
	private Label faithValLabel;
	private Label cashValLabel;
	private Label foodValLabel;
	
	
	private void createBorders()
	{
		this.northBorderDef = new BodyDef();
		this.northBorderDef.position.set(game.width/2,game.height);
		this.northBorder = new ZionBody(world.createBody(northBorderDef));
		this.north = new PolygonShape();
		this.north.setAsBox(game.width/2, .1f);
		this.northBorder.createFixture(north,0.0f);
		this.southBorderDef = new BodyDef();
		this.southBorderDef.position.set(game.width/2,0);
		this.southBorder = new ZionBody(world.createBody(southBorderDef));
		this.south = new PolygonShape();
		this.south.setAsBox(game.width/2, .1f);
		this.southBorder.createFixture(south,0.0f);
		this.eastBorderDef = new BodyDef();
		this.eastBorderDef.position.set(game.width,game.height/2);
		this.eastBorder = new ZionBody(world.createBody(eastBorderDef));
		
		this.east = new PolygonShape();
		this.east.setAsBox(.1f, game.height);
		this.eastBorder.createFixture(east,0.0f);
		this.westBorderDef = new BodyDef();
		this.westBorderDef.position.set(0,game.height/2);
		this.westBorder = new ZionBody(world.createBody(westBorderDef));
		this.west = new PolygonShape();
		this.west.setAsBox(.1f,game.height);
		this.westBorder.createFixture(west,0.0f);
	}
	
	
	
	private TextureRegion getCurrentFrame(Animation[] animation)
	{
		Vector2 tmp = this.mainZionBody.getLinearVelocity();
		float vel = (float) Math.sqrt(tmp.x*tmp.x+tmp.y*tmp.y);
		//System.out.println(vel);
		stateTime += vel/120.0f;
		int numdirs = animation.length;
		if(currentAction == "walk")
		{
			if(tmp.x != 0 && tmp.y != 0)
			{
				picnum = numdirs-((int) Math.round((Math.atan2(tmp.y,tmp.x)*(72f/numdirs)/Math.PI+(numdirs+numdirs/4))%numdirs)%numdirs);
				if(picnum >= numdirs)
					picnum = 0;
			}
			else
			{
				if(GlobalData.stopping)
				{
					GlobalData.life -= stateTime/50f;
					GlobalData.stopping = false;
					//System.out.println("Life:"+GlobalData.life);
				}
				stateTime %= .5f;
				if(stateTime > 0)
					stateTime -= 1/40f;
			}
		}
		else
		{
			stateTime += 1/190f;
		}
		
		return animation[picnum].getKeyFrame(stateTime, true);
	}

	public GameScreen(final ComeToZionMain theGame) {
		this.game = theGame;
		final Table table = new Table();
        table.setFillParent(true);
		Texture.setEnforcePotImages(false);
		//float w = Gdx.graphics.getWidth();
		//float h = Gdx.graphics.getHeight();
		
		rendersize = 1f/32f;
		tilesize = Float.parseFloat(Assets.map.getLayers().get("Base").getProperties().get("tilesize").toString());
		game.width = Float.parseFloat(Assets.map.getLayers().get("Base").getProperties().get("width").toString())*rendersize*tilesize;
		game.height = Float.parseFloat(Assets.map.getLayers().get("Base").getProperties().get("height").toString())*tilesize*rendersize;
		Assets.px_to_local_scale = game.width/(Float.parseFloat(game.assets.map.getLayers().get("Base").getProperties().get("height").toString())*tilesize);
		
		MapObjects mObjects = game.assets.map.getLayers().get("Objects").getObjects();
		//Gdx.app.log(TAG, "Objects:"+mObjects.getCount());
		MapObjects nObjects = game.assets.map.getLayers().get("npcs").getObjects();
		//Gdx.app.log(TAG, "NPC Objects:"+nObjects.getCount());
		
		renderer = new ZionOrthogonalTiledMapRenderer(game.assets.map, rendersize);

		world = new World(new Vector2(0,0),true);
		dbgRenderer = new Box2DDebugRenderer();
		mainBodyDef = new BodyDef();
		mainBodyDef.type = BodyType.DynamicBody;
		mainBodyDef.position.set(75,29);
		mainBodyDef.linearDamping = 0.5f; // simulate gravity
		mainZionBody = new ZionBody(world.createBody(mainBodyDef));
		mainZionBody.setUserData("main");
		mainShape = new CircleShape();
		mainShape.setRadius(0.3f);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = mainShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		mainZionBody.createFixture(fixtureDef);
		this.createBorders();
		cameraController = new OrthoCamController(game.camera,game.width,game.height,mainZionBody);
        cameraController.addActor(table);

        Texture texture1 = new Texture(Gdx.files.internal("data/sprites/inven.png"));
        Texture texture2 = new Texture(Gdx.files.internal("data/sprites/vitals.png"));
		TextureRegion image = new TextureRegion(texture1);
		TextureRegion imageFlipped = new TextureRegion(image);
		imageFlipped.flip(true, true);
		ImageButtonStyle style = new ImageButtonStyle(Assets.skin.get(ImageButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(image);
		style.imageDown = new TextureRegionDrawable(imageFlipped);
		style.imageChecked = new TextureRegionDrawable(imageFlipped);
		
		final ImageButton iconButton = new ImageButton(style);
		
		ImageButtonStyle style2 = new ImageButtonStyle(Assets.skin.get(ImageButtonStyle.class));
		TextureRegion image2 = new TextureRegion(texture2);
		TextureRegion image2Flipped = new TextureRegion(texture2);
		image2Flipped.flip(true, true);
		style2.imageUp = new TextureRegionDrawable(image2);
		style2.imageDown = new TextureRegionDrawable(image2Flipped);
		style2.imageChecked = new TextureRegionDrawable(image2Flipped);
		final ImageButton iconButton2 = new ImageButton(style2);
		
		//ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
		//imageButtonStyle.up = Assets.skin.newDrawable("white", Color.DARK_GRAY);
		//final ImageButton scale = new ImageButton(imageButtonStyle);
		
		//iconButton2.setPosition(50, 150);
		table.align(Align.top|Align.left);

		iconButton.addListener(new TopicChangeListener(null,cameraController));
		iconButton2.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				Button doneButton = new TextButton("Done", Assets.skin);
				Table t = new Table();
				t.setFillParent(true);
				final Window w = new Window("Inventory", Assets.skin);
				w.setFillParent(true);
				w.addActor(t);
				w.setPosition(0, 10);
				w.defaults().spaceBottom(10);
				doneButton.addListener(new ChangeListener() {
					public void changed (ChangeEvent event, Actor actor) {
						Assets.playSound("click");
						w.clear();
						w.remove();
					}
				});
				for(String s: GlobalData.inventory.keySet())
				{
					Label l1 = new Label(s+": "+GlobalData.inventory.get(s), Assets.skin);
					t.row();
					t.add(l1);
				}
				t.row();
				t.add(doneButton);
				t.pack();

				cameraController.addActor(w);
			}
		});
        Label lifeLabel = new Label(" Life:", Assets.skin);
        lifeValLabel = new Label((int)Math.round(GlobalData.life)+"%", Assets.skin);
        Label faithLabel = new Label("Faith:", Assets.skin);
        faithValLabel = new Label((int)Math.round(GlobalData.faith)+"%", Assets.skin);
        Label cashLabel = new Label("Cash:", Assets.skin);
        cashValLabel = new Label("$"+GlobalData.cash, Assets.skin);
        Label foodLabel = new Label("Food:", Assets.skin);
        foodValLabel = new Label(""+GlobalData.food, Assets.skin);
		//myLabel.setPosition(50, 50);
		
		table.add(iconButton);
		table.add(iconButton2);
		table.row();
		table.add(lifeLabel);
		table.add(lifeValLabel);
		table.row();
		table.add(faithLabel);
		table.add(faithValLabel);
		table.row();
		table.add(cashLabel);
		table.add(cashValLabel);
		table.row();
		table.add(foodLabel);
		table.add(foodValLabel);


		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
		//table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);
		Gdx.input.setInputProcessor(cameraController);
		
		HashMap<String,Polyline> paths = new HashMap<String,Polyline>();
		for(Iterator<MapObject> nObjs = nObjects.iterator(); nObjs.hasNext();){
	        // I have set just about everything possible(I only have one object at the moment so nObj only gets set once.
	            MapObject nObj = nObjs.next();
	            //Gdx.app.log(TAG, "Obj:"+nObj.getClass().getName());
	            //System.out.println("***"+nObj.getClass().getName());
	            if(nObj.getClass().getName() == "com.badlogic.gdx.maps.objects.PolylineMapObject")
	            {
	            	PolylineMapObject robj = (PolylineMapObject)nObj;
	            	String pathfor = robj.getName();
	            	paths.put(pathfor,robj.getPolyline());
	            	//System.out.println(robj.getPolyline().getTransformedVertices());
	            	//System.out.println(robj.getPolyline().getVertices());
	            }
	            else if(nObj.getClass().getName() == "com.badlogic.gdx.maps.objects.EllipseMapObject")
	            {
	            	EllipseMapObject robj = (EllipseMapObject)nObj;
	            	BodyDef rdef = new BodyDef();
	            	rdef.type = BodyType.KinematicBody;
	            	//Gdx.app.log("test", "w: "+width+" x:"+robj.getRectangle().x+" "+this.px_to_local_scale+" ");
	            	rdef.position.set(robj.getEllipse().x*Assets.px_to_local_scale+robj.getEllipse().width*Assets.px_to_local_scale/2,
	            			robj.getEllipse().y*Assets.px_to_local_scale+robj.getEllipse().height*Assets.px_to_local_scale/2);
	            	ZionBody rbody = new ZionBody(world.createBody(rdef));
	            	rbody.setUserData("npc");
	            	CircleShape rshape = new CircleShape(); // TODO this needs to be disposed of
	            	rshape.setRadius((robj.getEllipse().width+robj.getEllipse().height)*Assets.px_to_local_scale/4);
	            	FixtureDef rfdef = new FixtureDef();
	            	rfdef.shape = rshape;
	            	rfdef.density = 0.5f; 
	            	rfdef.friction = 1.0f;
	            	rfdef.restitution = 0.0f;
	            	rbody.createFixture(rfdef);
	            	Npc n = new Npc(robj.getName(),robj.getProperties().get("type").toString().split(":")[0].split(";"),robj.getProperties().get("type").toString().split(":")[1],rbody,0.4f);
	            	n.spriteName = robj.getProperties().get("type").toString().split(":")[2];
	            	GlobalData.npcs.put(rbody.body,n);
	            }      
		}
		for(Iterator<MapObject> mObjs = mObjects.iterator(); mObjs.hasNext();){
	        // I have set just about everything possible(I only have one object at the moment so mObj only gets set once.
	            MapObject mObj = mObjs.next();
	            //Gdx.app.log(TAG, "Obj!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!:"+mObj.getClass().getName()+" "+mObj.getName());
	            if(mObj.getClass().getName() == "com.badlogic.gdx.maps.objects.RectangleMapObject")
	            {
	            	RectangleMapObject robj = (RectangleMapObject)mObj;
	            	BodyDef rdef = new BodyDef();
	            	rdef.type = BodyType.StaticBody;
	            	//Gdx.app.log("test", "w: "+robj.getRectangle().width+" x:"+robj.getRectangle().x+" "+Assets.px_to_local_scale+" ");
	            	if(robj.getRectangle().width == 0)
	            	{
	            		//Check if this is actually an image object
	            		Texture tmpTex = renderer.textures.get(robj.getName());
	    				if(tmpTex == null)
	    				{
	    					tmpTex = new Texture(Gdx.files.internal("data/sprites/"+robj.getName()+".png"));
	    					renderer.textures.put(robj.getName(),tmpTex);
	    					robj.getRectangle().setWidth(tmpTex.getWidth());
	    					robj.getRectangle().setHeight(tmpTex.getHeight());
	    				}
	            	}
	            	rdef.position.set(robj.getRectangle().x*Assets.px_to_local_scale+robj.getRectangle().width*Assets.px_to_local_scale/2,
	            			robj.getRectangle().y*Assets.px_to_local_scale+robj.getRectangle().height*Assets.px_to_local_scale/2);
	            	ZionBody rbody = new ZionBody(world.createBody(rdef));
	            	if(robj.getProperties().get("type") != null)
	            	{
	            		rbody.setUserData(robj);
	            	}
	            	
	            	PolygonShape rshape = new PolygonShape(); // TODO this needs to be disposed of
	            	rshape.setAsBox(robj.getRectangle().width*Assets.px_to_local_scale/2,robj.getRectangle().height*Assets.px_to_local_scale/2);
	            	FixtureDef rfdef = new FixtureDef();
	            	rfdef.shape = rshape;
	            	rfdef.density = 0.5f; 
	            	rfdef.friction = 1.0f;
	            	rfdef.restitution = 0.0f;
	            	rbody.createFixture(rfdef);
	            }
	            else if(mObj.getClass().getName() == "com.badlogic.gdx.maps.objects.EllipseMapObject")
	            {
	            	EllipseMapObject robj = (EllipseMapObject)mObj;
	            	BodyDef rdef = new BodyDef();
	            	rdef.type = BodyType.StaticBody;
	            	//Gdx.app.log("test", "w: "+width+" x:"+robj.getRectangle().x+" "+this.px_to_local_scale+" ");
	            	rdef.position.set(robj.getEllipse().x*Assets.px_to_local_scale+robj.getEllipse().width*Assets.px_to_local_scale/2,
	            			robj.getEllipse().y*Assets.px_to_local_scale+robj.getEllipse().height*Assets.px_to_local_scale/2);
	            	ZionBody rbody = new ZionBody(world.createBody(rdef));
	            	if(robj.getProperties().get("type") != null)
	            	{
	            		rbody.setUserData(robj);
	            	}
	            	
	            	//rbody.setUserData("static");
	            	CircleShape rshape = new CircleShape(); // TODO this needs to be disposed of
	            	rshape.setRadius((robj.getEllipse().width+robj.getEllipse().height)*Assets.px_to_local_scale/4);
	            	FixtureDef rfdef = new FixtureDef();
	            	rfdef.shape = rshape;
	            	rfdef.density = 0.5f; 
	            	rfdef.friction = 1.0f;
	            	rfdef.restitution = 0.0f;
	            	rbody.createFixture(rfdef);
	            }
	            else if(mObj.getClass().getName() == "com.badlogic.gdx.maps.objects.PolygonMapObject")
	            {
	            	PolygonMapObject robj = (PolygonMapObject)mObj;
	            	BodyDef rdef = new BodyDef();
	            	rdef.type = BodyType.StaticBody;
	            	
	            	//Gdx.app.log("test", "w: "+game.width+" x:"+robj.getPolygon().getX()*this.px_to_local_scale+" ");
	            	//rdef.position.set(robj.getPolygon().getX()*this.px_to_local_scale,robj.getPolygon().getY()*this.px_to_local_scale);
	            	
	            	ZionBody rbody = new ZionBody(world.createBody(rdef));
	            	rbody.setUserData(robj);
	            	PolygonShape rshape = new PolygonShape(); // TODO this needs to be disposed of
	            	float[] f = robj.getPolygon().getTransformedVertices();
	            	Vector2[] v = new Vector2[f.length/2];
	            	for(int i=0;i<f.length;i+=2)
	            	{
	            		f[i]*=Assets.px_to_local_scale;
	            		v[i/2] = new Vector2(f[i],f[i+1]*Assets.px_to_local_scale);
	            		//Gdx.app.log("test", ""+f[i]+","+f[i+1]*this.px_to_local_scale);
	            	}
	            	rshape.set(v);
	            	
	            	FixtureDef rfdef = new FixtureDef();
	            	rfdef.shape = rshape;
	            	rfdef.density = 0.5f; 
	            	rfdef.friction = 1.0f;
	            	rfdef.restitution = 0.0f;
	            	rbody.createFixture(rfdef);
	            }
	            
		}
	            
		world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
            	if(contact.getFixtureA() == null || contact.getFixtureB() == null)
            		return;
            	ZionBody mainguy = null;
            	ZionBody npc = null;
                ZionBody bodyA = new ZionBody(contact.getFixtureA().getBody());
                ZionBody bodyB = new ZionBody(contact.getFixtureB().getBody());
                ZionBody thing = null;
                if(bodyA.toString() == "npc")
                	npc = bodyA;
                if(bodyB.toString() == "npc")
                	npc = bodyB;
                if(bodyA.toString() == "main")
                	mainguy = bodyA;
                if(bodyB.toString() == "main")
                	mainguy = bodyB;
                if(bodyA != npc && bodyA != mainguy)
                	thing = bodyA;
                if(bodyB != npc && bodyB != mainguy)
                	thing = bodyB;
                
                //Gdx.app.log("beginContact", "between " + bodyA.toString() + " and " + bodyB.toString());
                if(mainguy != null)
                {
                	//Gdx.app.log(TAG, "Obj:"+contact.getFixtureA().getZionBody().getUserData()+" "+contact.getFixtureB().getZionBody());
                	if(!mainguy.getLinearVelocity().epsilonEquals(0f, 0f, .01f))
                	{
                		GlobalData.life -= stateTime/50f;
                		mainguy.setLinearVelocity(0, 0);
                		//this.cameraController.setDestination(mainguy.getPosition());
                	}
                	if(npc != null)
                	{
                		Npc n = GlobalData.npcs.get(npc.body);
                		n.currentAction = "talk";
                		currentAction = "talk";
                		Assets.playSound("talk");
                		int numdirs =n.animations.get(n.currentAction).length;
                		Vector2 tmp = n.position();
                		tmp.sub(mainZionBody.getPosition());
                		picnum = numdirs-((int) Math.round((Math.atan2(tmp.y,tmp.x)*(72f/numdirs)/Math.PI+(numdirs+numdirs/4))%numdirs)%numdirs);
                		if(picnum >= numdirs)
                			picnum = 0;
                		n.picnum = (picnum+numdirs/2)%numdirs;
                		n.body.setLinearVelocity(0, 0);
                		Conversation c = new Conversation(n,GlobalData.npcDialogs.get("generic"),cameraController);
                		
                	}
                	if(thing != null)
                	{
                		if(thing.toString().contentEquals("cash"))
                		{
                			Assets.playSound("cash");
                			if(((MapObject)thing.getUserData()).getProperties().get("type") != null)
                			{
                				GlobalData.cash += Integer.parseInt(((MapObject)thing.getUserData()).getProperties().get("type").toString());
                			}
                			else
                			{
                				GlobalData.cash += MathUtils.random(30);
                			}
                			destroyList.add(thing);
                		}
                		if(thing.toString().contentEquals("scroll"))
                		{
                			Assets.playSound("scroll");
                			if(((MapObject)thing.getUserData()).getProperties().get("type") != null)
                			{
                				String ref = ((MapObject)thing.getUserData()).getProperties().get("type").toString().replace("@", ":");
                				GlobalData.knowledge.get("Other").put(ref,Assets.scriptures.get(ref));
                			}
                			destroyList.add(thing);
                		}
                	}
                }
            }

            @Override
            public void endContact(Contact contact){
            	if(contact.getFixtureA() == null || contact.getFixtureB() == null)
            		return;
            	ZionBody mainguy = null;
            	ZionBody npc = null;
                ZionBody bodyA = new ZionBody(contact.getFixtureA().getBody());
                ZionBody bodyB = new ZionBody(contact.getFixtureB().getBody());
                if(bodyA.toString() == "npc")
                	npc = bodyA;
                if(bodyB.toString() == "npc")
                	npc = bodyB;
                if(bodyA.toString() == "main")
                	mainguy = bodyA;
                if(bodyB.toString() == "main")
                	mainguy = bodyB;
                //Gdx.app.log("endContact", "between " + bodyA.toString() + " and " + bodyB.toString());
                if(npc != null)
                {
                	Npc n = GlobalData.npcs.get(npc.body);
                	n.currentAction = "walk";
                }
                if(mainguy != null)
                	currentAction = "walk";
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
		// Assign final stuff to all npcs
		for(Object n1: GlobalData.npcs.values().toArray())
		{
			Npc n = (Npc)n1;
			n.setPath(paths.get(n.attributes.get("name")));
			n.addAnimation("walk", Assets.animations.get(n.spriteName).get("walk"));
			n.addAnimation("talk", Assets.animations.get(n.spriteName).get("talk"));
		}
		
	}
	private static final float TIMESTEP = 1/60f;
	private static float stateTime = 0;
	private TextureRegion currentFrame;
	private PriorityQueue<CTexture> sprites = new PriorityQueue<CTexture>();

	@Override
	public void render(float delta) {
		while(!destroyList.isEmpty())
		{
			ZionBody z = destroyList.remove(0);
			MapObjects mObjects = Assets.map.getLayers().get("Objects").getObjects();
			mObjects.remove((MapObject)z.getUserData());
			world.destroyBody(z.body);
		}
		
		lifeValLabel.setText((int)Math.round(GlobalData.life)+"%");
        faithValLabel.setText((int)Math.round(GlobalData.faith)+"%");
        cashValLabel.setText("$"+GlobalData.cash);
        foodValLabel.setText(""+GlobalData.food);
		
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0.55f, 0.55f, 0.55f, 1f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		game.camera.update();
		renderer.setView(game.camera);
		int[] bg = {0,1,3};
		int[] fg = {2};
		//System.out.println(Assets.map.getLayers().get(4).getName());
		renderer.render(bg);
		Array<Body> bi = new Array<Body>();
		world.getBodies(bi);
        
        //System.out.println(spriteheight);
		for(Body bb: bi)
		{
			ZionBody b = new ZionBody(bb);
		    // Get the bodies user data - in this example, our user 
		    
		    // data is an instance of the Entity class
		    String e = b.toString();
		    //Gdx.app.log(TAG,e);

		    if (e == "main") {
		        // Update the entities/sprites position and angle
		        game.camera.position.set(b.getPosition().x, b.getPosition().y,0);
		    }
		    
		    if(e == "npc")
		    {
		    	Npc n = GlobalData.npcs.get(bb);
		    	if (n.position().epsilonEquals(n.nextposition(), 0.01f))
		    	{
		    		if(n.reverse)
		    		{
		    			n.nextpos--;
		    			n.currpos--;
		    		}
		    		else
		    		{
		    			n.nextpos++;
		    			n.currpos++;
		    		}
		    		if(n.nextpos == n.numpositions())
		    		{
		    			n.nextpos = n.numpositions()-2;
		    			n.currpos = n.numpositions()-1;
		    			n.reverse = true;
		    		}
		    		if(n.nextpos == -1)
		    		{
		    			n.nextpos = 1;
		    			n.currpos = 0;
		    			n.reverse = true;
		    		}
		    	}
		    	if(n.currentAction == "walk")
		    	{
		    		Vector2 delt = new Vector2(n.nextposition());
		    		delt.sub(n.position());
		    		delt.nor();
		    		float walk_speed = 0.9f;
		    		float mul = (walk_speed*walk_speed)/(delt.x*delt.x+delt.y*delt.y);
		    		b.setLinearVelocity(delt.x*mul,delt.y*mul);
		    	}
    			Vector2 diff = n.position().sub(mainZionBody.getPosition());
    			if(Math.abs(diff.x) < game.camera.viewportWidth && Math.abs(diff.y)<game.camera.viewportHeight)
    			{
    				Vector3 tmp = new Vector3(n.position().x, n.position().y,0f);
    				game.camera.project(tmp);
    				sprites.add(new CTexture(n.getCurrentFrame(), tmp.x-24, tmp.y-25));
    			}
		    }
		    
		}
		//System.out.println(currentAction);
		currentFrame = getCurrentFrame(Assets.animations.get("mainplayer").get(currentAction));
		
        Vector3 tmp = new Vector3(mainZionBody.getPosition().x, mainZionBody.getPosition().y,0f);
        game.camera.project(tmp);
        sprites.add(new CTexture(currentFrame, tmp.x-currentFrame.getRegionWidth()/2f, tmp.y-25));
        game.batch.begin();
        while(!sprites.isEmpty())
        {
        	CTexture sprite = sprites.remove();
        	game.batch.draw(sprite,sprite.pos.x,sprite.pos.y);
        }
        game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 8, 20); 
        game.batch.end();
		dbgRenderer.render(world, game.camera.combined);
		world.step(TIMESTEP,6,2);
		
		renderer.render(fg); //render foreground stuff
		cameraController.constrainMovement();
		cameraController.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		cameraController.draw();
		//Table.drawDebug(cameraController);
	}

	@Override
	public void dispose () {
		Assets.map.dispose();
		mainShape.dispose();
		north.dispose();
		south.dispose();
		east.dispose();
		west.dispose();
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

}