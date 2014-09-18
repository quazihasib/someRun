package com.example.objects;

import java.util.Iterator;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import com.example.some.MainActivity;

import android.content.Context;
import android.util.Log;

public class Controller 
{

	public Context context;
	
	public Controller(Context con)
	{
		this.context = con;
	}
	
	public static void playerAnimate()
	{
		MainActivity.player.animate(new long[]{200, 200, 200}, 6, 8, true);
		MainActivity.mScene.registerUpdateHandler(new TimerHandler(1,
				new ITimerCallback() {
					
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						// TODO Auto-generated method stub
						MainActivity.player.animate(new long[]{200, 200, 200}, 3, 5, true);
					}
				}));
	}
	
	public static void jump(AnimatedSprite sp)
	{

		if (!CoolDown.sharedCoolDown().checkValidity()) 
		{
			return;
		}

		Path ourPath = new Path(3).to(sp.getX(), sp.getY()).to(sp.getX()+10, sp.getY()-70)
				 .to(sp.getX()+20, sp.getY());
		 
		sp.registerEntityModifier(new PathModifier((float) 0.5, ourPath, null, new IPathModifierListener() {
			
			@Override
			public void onPathWaypointStarted(PathModifier arg0, IEntity arg1, int arg2) {
				// TODO Auto-generated method stub
				MainActivity.player.animate(new long[] { 100, 100, 100}, 0, 2, true);
			}
			
			@Override
			public void onPathWaypointFinished(PathModifier arg0, IEntity arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPathStarted(PathModifier arg0, IEntity arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPathFinished(PathModifier arg0, IEntity arg1) {
				// TODO Auto-generated method stub
				MainActivity.touch = false;
				MainActivity.player.animate(new long[] { 100, 100, 100}, 3, 5, true);
			}
		}));
	}
	
	public void fail() 
	{
//		if (MainActivity.mEngine.isRunning()) 
//		{
//			MainActivity.winSprite.setVisible(false);
//			MainActivity.failSprite.setVisible(true);
//			MainActivity.mScene.setChildScene(MainActivity.mResultScene, false,
//					true, true);
//			MainActivity.mEngine.stop();
//		}
	}

	public void win()
	{
//		if (MainActivity.mEngine.isRunning()) 
//		{
//			MainActivity.failSprite.setVisible(false);
//			MainActivity.winSprite.setVisible(true);
//			MainActivity.mScene.setChildScene(MainActivity.mResultScene, false,
//					true, true);
//			MainActivity.mEngine.stop();
//		}
	}
	

}
