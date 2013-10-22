package com.me.cometozion;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;


public class ZionOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

	public HashMap<String,Texture> textures = new HashMap<String,Texture>();
	public ZionOrthogonalTiledMapRenderer(TiledMap map, float unitScale) {
		super(map, unitScale);
	}
	
	@Override
	public void renderObject(MapObject object)
	{
		boolean isVisible = object.isVisible();
			
		//camera.update();
		
		if( false == isVisible){
			return;
		}
		
		float x = 0.0f;
		float y = 0.0f;
		//float w = 0.0f;
		//float h = 0.0f;
		if(object instanceof CircleMapObject) {
			Circle tmp = ((CircleMapObject)object).getCircle();
			x = tmp.x;
			y = tmp.y;
			//w = h = tmp.radius*2;
		}else if(object instanceof EllipseMapObject) {
			Ellipse tmp = ((EllipseMapObject)object).getEllipse();
			x = tmp.x;
			y = tmp.y;
			//w = tmp.width; 
			//h = tmp.height;
		}else if(object instanceof PolygonMapObject) {
			Polygon tmp = ((PolygonMapObject)object).getPolygon();
			x = tmp.getBoundingRectangle().x;
			y = tmp.getBoundingRectangle().y;
			//w = tmp.getBoundingRectangle().width; 
			//h = tmp.getBoundingRectangle().height;
		}else if(object instanceof PolylineMapObject) {
			Polyline tmp = ((PolylineMapObject)object).getPolyline();
			x = tmp.getX();
			y = tmp.getY();
			//w = h = tmp.getScaledLength();
		}else if(object instanceof RectangleMapObject) {
			Rectangle tmp = ((RectangleMapObject)object).getRectangle();
			x = tmp.x;
			y = tmp.y;
			//w = tmp.width; 
			//h = tmp.height;
		}else if(object instanceof TextureMapObject) {
			TextureMapObject tmp = (TextureMapObject)object;
			x = tmp.getX();
			y = tmp.getY();
			//w = tmp.getTextureRegion().getRegionWidth();
			//h = tmp.getTextureRegion().getRegionHeight();
		}
		
		try
		{
			if(object.getName() != null)
			{
				Texture texture1 = textures.get(object.getName());
				if(texture1 == null)
				{
					texture1 = new Texture(Gdx.files.internal("data/sprites/"+object.getName()+".png"));
					textures.put(object.getName(),texture1);
				}
				spriteBatch.draw(texture1, x/32,y/32,texture1.getWidth()/32f,texture1.getHeight()/32f);
				//System.out.println((x/32)+" "+(y/32));
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
