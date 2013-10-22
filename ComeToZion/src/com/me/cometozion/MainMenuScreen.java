package com.me.cometozion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
 
public class MainMenuScreen implements Screen {

	final ComeToZionMain game;
	Stage stage;
	Texture texture1;
	Texture texture2;
	Label fpsLabel;

	public MainMenuScreen (ComeToZionMain game) {
		this.game = game;
		
		texture1 = new Texture(Gdx.files.internal("data/sprites/inven.png"));
		texture2 = new Texture(Gdx.files.internal("data/sprites/vitals.png"));
		TextureRegion image = new TextureRegion(texture1);
		TextureRegion imageFlipped = new TextureRegion(image);
		imageFlipped.flip(true, true);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		Gdx.input.setInputProcessor(stage);
		ImageButtonStyle style = new ImageButtonStyle(Assets.skin.get(ButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(image);
		style.imageDown = new TextureRegionDrawable(imageFlipped);
		ImageButton iconButton = new ImageButton(style);

		Button play = new TextButton("Play", Assets.skin, "toggle");
		play.setWidth(200);
		Button review = new TextButton("Review", Assets.skin, "toggle");
		Button credits = new TextButton("Credits and Disclaimers", Assets.skin, "toggle");

		Label myLabel = new Label("Building Zion", Assets.skin);
		myLabel.setAlignment(0, 2);//center
		myLabel.setWrap(true);

		Table t = new Table();
		
		t.defaults().width(300);
		t.defaults().height(100);
		stage.addActor(t);
		t.setFillParent(true);

		
		t.row();
		t.add(myLabel);
		t.row();
		t.row();
		t.add(play);
		t.row();
		t.add(review);
		t.row();
		t.add(credits);
		t.layout();

		// stage.addActor(new Button("Behind Window", skin));

		iconButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				System.out.println("changed iconbutton");
			}
		});
		play.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				System.out.println("play it");
				//Assets.playSound(Assets.sounds.get(0));
				startGame();
			}
		});
		
	}

	@Override
	public void render(float f) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		//fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		Table.drawDebug(stage);
	}

	@Override
	public void resize (int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void dispose () {
		stage.dispose();
		texture1.dispose();
		texture2.dispose();
	}
	

	private void startGame() {
		game.setScreen(new GameScreen(game));
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}