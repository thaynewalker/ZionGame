package com.me.cometozion;

import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class ScripRefChangeListener extends ChangeListener
{
	/**
	 * 
	 */
	private Stage stage;
	private String topic;
	private Table table;
	private Set<String> refs;
	private Conversation conversation;
	private Label dlgLabel;
	private Question question;

	public ScripRefChangeListener(String topic, Table table, Set<String> refs,Stage s)
	{
		stage = s;
		this.topic = topic;
		this.table = table;
		this.refs = refs;
	}
	
	public ScripRefChangeListener(String topic, Table table, Set<String> refs,Stage s, Conversation c, Label l, Question q)
	{
		this(topic,table,refs,s);
		conversation = c;
		dlgLabel = l;
		question = q;
	}

	@Override
	public void changed (ChangeEvent event, Actor actor)
	{
		Assets.playSound("click");
		Button doneButton = new TextButton("Done", Assets.skin);
		Button backButton = new TextButton("Back", Assets.skin);
		doneButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				table.clearChildren();
				table.getParent().remove();
				table.remove();
			}
		});
		backButton.addListener(new TopicChangeListener(this.table,stage,conversation,dlgLabel,question));
		this.table.clearChildren();

		Iterator<String> refss = this.refs.iterator();
		while(refss.hasNext())
		{
			String ref = refss.next();
			Button tmpButton = new TextButton(ref, Assets.skin);
			table.row().fill().expandX().expandY();
			table.add(tmpButton);
			table.row();
			System.out.println(ref);
			tmpButton.addListener(new ScriptureChangeListener(topic,ref,table,GlobalData.knowledge.get(this.topic).get(ref),stage,conversation,dlgLabel,question));
		}

		table.row().fill().expandX();
		table.add(backButton);
		table.row().fill().expandX();
		table.add(doneButton);
		table.pack();
	}
}