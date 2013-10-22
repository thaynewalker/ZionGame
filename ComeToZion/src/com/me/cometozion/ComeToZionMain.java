package com.me.cometozion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ComeToZionMain extends Game{
	
	public BitmapFont font;
	public SpriteBatch batch;
	public float width;
	public float height;
	public OrthographicCamera camera;
	public static Assets assets;
	
	public void create() {	
		batch = new SpriteBatch();
		Texture.setEnforcePotImages(false);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		font = new BitmapFont();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, (width / height) * 10, 10);
		camera.update();
		//assets = new Assets();
		Assets.load();
		//this.setScreen(new GameScreen(this));
		this.setScreen(new MainMenuScreen(this));
	}

	public void dispose () {
		font.dispose();
		batch.dispose();
	}
	
}