package com.me.cometozion;

import java.util.Iterator;
import java.util.Vector;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;



public class Conversation {
  public Npc npc;
  private NpcDialog dlg;
  private Stage stage;
  
  Object getRandomFromVector(Vector v)
  {
	  if(v.size() == 0)
		  return null;
	  return v.get(MathUtils.random(0,v.size()-1));
  }
  
  private class ConversationChangeListener extends ChangeListener
  {
	  private Table table;
	  private NpcDialog topic;
	  private Conversation conversation;
	  public ConversationChangeListener(Table t, NpcDialog c,Conversation cc)
	  {
		  this.table = t;
		  this.topic = c;
		  this.conversation = cc;
		  
	  }
	  
	@Override
	public void changed(ChangeEvent event, Actor actor)
	{			
		if(this.topic.result != null)
			this.topic.result.execute();
		this.topic.didit = true;
		Assets.playSound("click");
		
		Button doneButton = new TextButton("Done", Assets.skin);
		doneButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Assets.playSound("click");
				table.clearChildren();
				table.getParent().remove();
				table.remove();
			}
		});
		
		Vector<NpcDialog> answers = new Vector<NpcDialog>();
		NpcDialog answer = null;
		Iterator<NpcDialog> refss = topic.choices.iterator();
		while(refss.hasNext())
		{
			NpcDialog dlg = refss.next();
			dlg.setNpc(topic.npc);
			if(dlg.condition != null && dlg.condition.evaluate())
				answers.add(dlg);
		}
		if(answers.size() == 0)
		{
			refss = topic.choices.iterator();
			while(refss.hasNext())
			{
				NpcDialog dlg = refss.next();
				if(dlg.condition == null)
					answers.add(dlg);
			}
		}
		answer = ((NpcDialog)getRandomFromVector(answers));
		if(null == answer)
		{
			return;
		}
		String anstext = answer.text;
		boolean isQ = false;
		Question q = null;
		if(anstext.contains("===QUESTIONDIALOG==="))
		{
			isQ = true;
			
			for(Question qq: npc.questions.keySet())
			{
				if(!npc.questions.get(qq))
				{
					q = qq;
					break;
				}
			}
			if(q!=null)
			{
				anstext = q.text;
			}
		}
		if(answer.result != null)
		{
			answer.result.execute();
		}
		
		
		this.table.clearChildren();
		Label main;
		if(npc.nameKnown)
			main = new Label("\n"+npc.attributes.get("name")+" says:",Assets.skin);
		else
			main = new Label("\nThe reply:",Assets.skin);
		
		if(anstext.contains("#"))
		{
			String anss[] = anstext.split("#"); 
			anstext = anss[0]+npc.attributes.get(anss[1])+anss[2];
		}
		main.setText(main.getText()+"\n"+anstext+"\n\n");
		main.setAlignment(Align.top, Align.center);
		table.row().fill().expandX();
		table.add(main);
		if(!answer.choices.isEmpty())
			main.setText(main.getText()+"You say:");
		refss = answer.choices.iterator();
		while(refss.hasNext())
		{
			NpcDialog dlg = refss.next();
			dlg.setNpc(topic.npc);
			if(dlg.condition == null || dlg.condition.evaluate())
			{
				String dlgtext = dlg.text;
				if(dlgtext.contains("#"))
				{
					String anss[] = dlgtext.split("#"); 
					dlgtext = anss[0]+npc.attributes.get(anss[1])+anss[2];
				}
				Button tmpButton = new TextButton(dlgtext, Assets.skin);
				table.row().fill().expandX().expandY();
				table.add(tmpButton);
				//table.row();
				//System.out.println(ref);
				tmpButton.addListener(new ConversationChangeListener(table,dlg,conversation));
			}
		}
		if(isQ)
		{
			Button tmpButton = new TextButton("Choose the answer", Assets.skin);
			table.row().fill().expandX().expandY();
			table.add(tmpButton);
			tmpButton.addListener(new TopicChangeListener(null,stage,conversation,main,q));

			tmpButton = new TextButton("I don't know, I'll get back to you.", Assets.skin);
			table.row().fill().expandX().expandY();
			table.add(tmpButton);
			tmpButton.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					Assets.playSound("click");
					table.clearChildren();
					table.getParent().remove();
					table.remove();
				}
			});
		}

		table.row().fill().expandX();
		table.add(doneButton);
		table.pack();
	}
  }
  
  public Conversation(Npc n, NpcDialog d, Stage s)
  {
    this.npc = n;
    this.dlg = d;
    this.dlg.setNpc(this.npc);
    this.stage = s;
    String title = "Talking to somebody new";
    if(npc.nameKnown)
    	title = "Talking to " + npc.attributes.get("name");
    Window w = new Window(title, Assets.skin);
    final Table table = new Table();
	w.setFillParent(true);
	w.addActor(table);
	w.setPosition(0, 10);
	w.defaults().spaceBottom(10);
	table.setFillParent(true);
	
	Label main = new Label("\nYou say:",Assets.skin);
	main.setAlignment(Align.top, Align.center);
	table.row().fill().expandX();
	table.add(main);
	
	Iterator<NpcDialog> nditr = dlg.choices.iterator();
	while(nditr.hasNext())
	{
		NpcDialog topic = nditr.next();
		topic.setNpc(n);
		System.out.println(topic);
		if(topic.condition == null || topic.condition.evaluate())
		{
			String dlgtext = topic.text;
			if(dlgtext.contains("#"))
			{
				String anss[] = dlgtext.split("#"); 
				dlgtext = anss[0]+npc.attributes.get(anss[1])+anss[2];
			}
			Button tmpButton = new TextButton(dlgtext, Assets.skin);
			//Button tmpButton = new TextButton(topic.text, Assets.skin);
			tmpButton.addListener(new ConversationChangeListener(table,topic,this));
			table.row().fill().expandX().expandY();
			table.add(tmpButton);
		}
	}
	Button doneButton = new TextButton("Done", Assets.skin);
	doneButton.addListener(new ChangeListener() {
		public void changed (ChangeEvent event, Actor actor) {
			table.getParent().remove();
		}
	});

	
	table.row().fill().expandX();
	table.add(doneButton);
	table.pack();

	stage.addActor(w);
  }
}
