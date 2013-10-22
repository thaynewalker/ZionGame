package com.me.cometozion;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

class TopicChangeListener extends ChangeListener
{
	/**
	 * 
	 */
	private Conversation conversation;
	private Label dlgLabel;
	private Table table;
	private Window window;
	public Stage stage;
	public Question question;
	
	public TopicChangeListener(Table table, Stage s)
	{
		this.stage = s;
		this.table = table;
		if(this.table != null && this.table.getParent() != null)
		{
			this.window = (Window) this.table.getParent();
		}
		else
		{
			this.window = null;
		}
	}
	
	public TopicChangeListener(Table table, Stage s,Conversation c, Label l, Question q)
	{
		this(table,s);
		conversation = c;
		dlgLabel = l;
		question = q;
	}
	@Override
	public void changed (ChangeEvent event, Actor actor) {
		Assets.playSound("click");
		Button doneButton = new TextButton("Done", Assets.skin);
		Button addButton = new TextButton("Add Category", Assets.skin);
		boolean newwin = (this.table == null);
		if (this.table == null)
		{
			this.table = new Table();
			this.table.setFillParent(true);
		}
		else
		{
			this.window = (Window) this.table.getParent();
			this.table.clearChildren();
		}
			if(this.window == null)
			{
				this.window = new Window("My Scriptures", Assets.skin);
				this.window.setFillParent(true);
				this.window.addActor(table);
				this.window.setPosition(0, 10);
				this.window.defaults().spaceBottom(10);
			}
			doneButton.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					Assets.playSound("click");
					table.clear();
					table.remove();
					window.clear();
					window.remove();
				}
			});
		addButton.addListener(new ChangeListener(){
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				table.clearChildren();
				Label heading = new Label("Add Category", Assets.skin);
				heading.setAlignment(Align.top, Align.center);
				table.row().fill().expandX().pad(5f);
				table.add(heading);
				final TextField name = new TextField("Category", Assets.skin);
				Button doneButton = new TextButton("Cancel", Assets.skin);
				doneButton.addListener(new TopicChangeListener(table,stage,conversation,dlgLabel,question));
				Button addButton = new TextButton("Add", Assets.skin);
				
				addButton.addListener(new TopicChangeListener(table,stage,conversation,dlgLabel,question) {
					public void changed (ChangeEvent event, Actor actor) {
						Assets.playSound("click");
						GlobalData.knowledge.put(name.getText(),new HashMap<String,Scripture>());
						table.clearChildren();
						table.getParent().remove();
						table.remove();
						
						TopicChangeListener x = new TopicChangeListener(null,stage,conversation,dlgLabel,question);
						x.changed(event, actor);
					}
				});

				table.row().fill().pad(2f);
				table.add(heading);
				table.row().fill().pad(2f);
				table.add(name);
				table.row().fill().pad(2f);
				table.add(addButton);
				table.row().fill().pad(2f);
				table.add(doneButton);

			}
		});

		Iterator<String> topics = GlobalData.knowledge.keySet().iterator();
		while(topics.hasNext())
		{
			String topic = topics.next();
			Button tmpButton = new TextButton(topic+" ("+GlobalData.knowledge.get(topic).keySet().size()+")", Assets.skin);
			tmpButton.addListener(new ScripRefChangeListener(topic,table,GlobalData.knowledge.get(topic).keySet(),stage,conversation,dlgLabel,question));
			table.row().fill().expandX().expandY();
			table.add(tmpButton);
		}
		table.row().fill().pad(2f);
		table.add(addButton);
		table.row().fill().expandX();
		table.add(doneButton);
		table.pack();

		stage.addActor(this.window);
	}
}