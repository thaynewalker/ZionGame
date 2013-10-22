package com.me.cometozion;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
 
public class PlayingWithTWL implements ApplicationListener {
	private Stage stage;

	public void create () {
	        stage = new Stage();
	        Gdx.input.setInputProcessor(stage);

	        Table table = new Table();
	        table.setFillParent(true);
	        stage.addActor(table);

	        // Add widgets to the table here.
	}

	public void resize (int width, int height) {
	        stage.setViewport(width, height, true);
	}

	public void render () {
	        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	        stage.act(Gdx.graphics.getDeltaTime());
	        stage.draw();

	        Table.drawDebug(stage); // This is optional, but enables debug lines for tables.
	}

	public void dispose() {
	        stage.dispose();
	}
	public void pause(){}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
