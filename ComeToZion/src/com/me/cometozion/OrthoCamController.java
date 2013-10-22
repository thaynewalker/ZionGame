package com.me.cometozion;

//import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class OrthoCamController extends Stage {
        private final OrthographicCamera camera;
        private Vector2 destination = new Vector2();
        private final Vector3 curr = new Vector3();
        private final Vector3 last = new Vector3(-1, -1, -1);   
        private final Vector3 delta = new Vector3();
        private final float width;
        private final float height;
        private final Body mainBody;
        
        public OrthoCamController(OrthographicCamera camera, float width, float height, Body mainBody) {
                this.camera = camera;
                this.width = width;
                this.height = height;
                this.mainBody = mainBody;
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {

        	if(! super.touchUp(x,y,pointer,button))
        	{
        		//System.out.println(x+ " " + y + " - " + last + " " + curr.set(x,y,0));
        		if(!last.equals(curr.set(x,y,0))) {
        			last.set(mainBody.getPosition().x,mainBody.getPosition().y,0);
        			camera.unproject(curr.set(x, y,0));
        			destination.set(curr.x,curr.y);
        			//System.out.println(" "+curr);
        			delta.set(curr);
        			delta.sub(last);
        			delta.nor();
        			float walk_speed = 2.0f;
        			float mul = (walk_speed*walk_speed)/(delta.x*delta.x+delta.y*delta.y);
        			mainBody.setLinearVelocity(delta.x*mul,delta.y*mul);
        			//camera.position.set(curr);
        		}


        		last.set(-1, -1, -1);
        		Assets.playSound("walk");
        	}
        	return true;
        }
        
        public void setDestination(Vector2 newdest)
        {
        	this.destination = newdest;
        }
        
        public void constrainMovement()
        {
        	if(mainBody.getPosition().epsilonEquals(destination, .05f))
        	{
        		return;
        	}
        	//System.out.println(camera.position.x+ "," + camera.position.y + " - " + camera.viewportWidth + "," + camera.viewportHeight + " - " + width + ","+height);
        	if(camera.position.x <= camera.viewportWidth/2)
        		camera.position.x = camera.viewportWidth/2;
        	if(camera.position.x >= width - camera.viewportWidth/2)
        		camera.position.x = width - camera.viewportWidth/2;
        	if(camera.position.y <= camera.viewportHeight/2)
        		camera.position.y = camera.viewportHeight/2;
        	if(camera.position.y >= height - camera.viewportHeight/2)
        		camera.position.y = height - camera.viewportHeight/2;

        	if( mainBody.getLinearVelocity().epsilonEquals(0, 0, 0))
        	{
        		return;
        	}
        	if(mainBody.getPosition().epsilonEquals(destination, .2f))
        	{
        		GlobalData.stopping = true;
        		mainBody.setLinearVelocity(mainBody.getLinearVelocity().div(2f));
        		//System.out.println(destination+" "+mainBody.getPosition()+" "+mainBody.getLinearVelocity());
        	}
        	else
        	{
        		Vector2 df = new Vector2(destination);
        		df.sub(mainBody.getPosition());
        		df.nor();
    			float walk_speed = Math.min(2.50f, Math.max(1.0f,2.0f*(GlobalData.life/50f)));
    			float mul = (walk_speed*walk_speed)/(df.x*df.x+df.y*df.y);
    			mainBody.setLinearVelocity(df.x*mul,df.y*mul);
        		//System.out.println(destination+" "+mainBody.getPosition()+" "+mainBody.getLinearVelocity()+" "+df);
        	}
        }
        
}
