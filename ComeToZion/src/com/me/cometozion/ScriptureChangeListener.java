package com.me.cometozion;

import java.util.Iterator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

class ScriptureChangeListener extends ChangeListener
{
	private String topic;
	private String ref;
	private Table table;
	private Scripture scrip;
	private Stage stage;
	private Conversation conversation;
	private Label dlgLabel;
	public Question question;

	public ScriptureChangeListener(String topic, String ref, Table table, Scripture scrip, Stage s)
	{
		this.topic = topic;
		this.ref = ref;
		this.table = table;
		this.scrip = scrip;
		this.stage = s;
	}
	
	public ScriptureChangeListener(String topic, String ref, Table table, Scripture scrip, Stage s, Conversation c, Label l, Question q)
	{
		this(topic,ref,table,scrip,s);
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
		Button pickButton = new TextButton("Select", Assets.skin);
		Button changeButton = new TextButton("Change Category", Assets.skin);
		changeButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				Dialog dlg = new Dialog("Choose a Category", Assets.skin)
				{
					protected void result (Object object) {
						String cat = (String)object;
						GlobalData.knowledge.get(cat).put(scrip.ref,GlobalData.knowledge.get(topic).remove(scrip.ref));
					}
				}.text("Choose a category");
				Iterator<String> refss = GlobalData.knowledge.keySet().iterator();
				while(refss.hasNext())
				{
					String topictext = refss.next();
					dlg.button(topictext,topictext);
				}
				dlg.show(table.getStage());
			}
		});
		pickButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				table.clearChildren();
				table.getParent().remove();
				table.remove();
				if(question == scrip.question)
				{
					final Table t = (Table) dlgLabel.getParent();
					t.clearChildren();
					Button done = new TextButton("Done", Assets.skin);
					done.addListener(new ChangeListener() {
						public void changed (ChangeEvent event, Actor actor) {
							Assets.playSound("click");
							t.clearChildren();
							t.getParent().remove();
							t.remove();
						}
					});
					conversation.npc.questions.put(scrip.question,true);
					conversation.npc.numQuestions--;
					dlgLabel.setText("\nThat answers my question! Thank you.");
					t.row().fill().expandX().pad(5f);
					t.add(dlgLabel);
					t.row().fill().expandX().pad(5f);
					t.add(done);
					System.out.println(dlgLabel.getParent());
					conversation.npc.addRandomTrust("0.1", "0.3");
				}
				else
				{
					dlgLabel.setText("\nThat doesn't exactly answer my question:\n"+scrip.question.text);
					System.out.println(dlgLabel.getParent());
				}
				GlobalData.pickedScripture = scrip;
			}
		});
		doneButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				table.clearChildren();
				table.getParent().remove();
				table.remove();
			}
		});
		backButton.addListener(new ScripRefChangeListener(topic,table,GlobalData.knowledge.get(topic).keySet(),this.stage,conversation,dlgLabel,question));
		this.table.clearChildren();

		Label heading = new Label(scrip.reference, Assets.skin);
		heading.setAlignment(Align.top, Align.center);
		table.row().fill().expandX().pad(5f);
		table.add(heading);
		Label keyword = new Label(scrip.keyword, Assets.skin);
		keyword.setAlignment(Align.top, Align.center);
		table.row().fill().expandX().pad(5f);
		table.add(keyword);
		Label body = new Label(scrip.text, Assets.skin);
		body.setWrap(true);
		body.setAlignment(Align.center, Align.left);
		table.row().fill().pad(2f);
		table.add(body);
		table.row().fill().pad(2f);
		table.add(new Label("\n",Assets.skin));
		Label category = new Label("Category: "+ topic, Assets.skin);
		category.setAlignment(Align.top, Align.center);
		table.row().fill().expandX().pad(5f);
		table.add(category);
		if(conversation != null)
		{
			table.row().fill().expandX();
			table.add(pickButton);
		}


		table.row().fill().expandX();
		table.add(changeButton);
		table.row().fill().expandX();
		table.add(backButton);
		table.row().fill().expandX();
		table.add(doneButton);
		table.pack();
	}
}