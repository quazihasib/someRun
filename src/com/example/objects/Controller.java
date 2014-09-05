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
	public IUpdateHandler detect;
	public IUpdateHandler detect1;
	
	public Controller(Context con)
	{
		this.context = con;
	}
	
	/** a Time Handler for spawning targets, triggers every 1 second */
	public void createSpriteSpawnTimeHandler()
	{
		TimerHandler spriteTimerHandler;
		float mEffectSpawnDelay = 1f;

		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback()
		{
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) 
					{
						Models.addTarget();
						Models.addObjects();
					}
				});

		MainActivity.mScene.registerUpdateHandler(spriteTimerHandler);
	}  

	public void checkCollision()
	{
		
		/** TimerHandler for collision detection and cleaning up */
		detect = new IUpdateHandler() 
		{
			@Override
			public void reset() 
			{
				
			}

			@Override
			public void onUpdate(float pSecondsElapsed) 
			{

				Iterator<AnimatedSprite> targets = MainActivity.targetLL.iterator();
				AnimatedSprite _target;
				boolean hit = false;

				// iterating over the targets
				while (targets.hasNext()) 
				{
					_target = targets.next();

					// if target passed the left edge of the screen, then remove
					// it and call a fail
					if (_target.getX() <= -_target.getWidth()) 
					{
						// removeSprite(_target, targets);
						MainActivity.tPool.recyclePoolItem(_target);
						targets.remove();
						// fail();
						break;
					}
					
					Iterator<Sprite> projectiles = MainActivity.projectileLL.iterator();
					Sprite _projectile;
					// iterating over all the projectiles (bullets)
					while (projectiles.hasNext())
					{
						_projectile = projectiles.next();

						// in case the projectile left the screen
						if(_projectile.getX() >= MainActivity.mCamera.getWidth()
						||_projectile.getY() >= MainActivity.mCamera.getHeight()+_projectile.getHeight()
						||_projectile.getY() <= -_projectile.getHeight()) 
						{
							MainActivity.pPool.recyclePoolItem(_projectile);
							projectiles.remove();
							continue;
						}

						// if the targets collides with a projectile, remove the
						// projectile and set the hit flag to true
						if (_target.collidesWith(_projectile)) 
						{
							MainActivity.pPool.recyclePoolItem(_projectile);
							projectiles.remove();
							hit = true;
							break;
						}
					}

					// if a projectile hit the target, remove the target,
					// increment the hit count, and update the score
					if (hit) 
					{
						// removeSprite(_target, targets);
						MainActivity.tPool.recyclePoolItem(_target);
						targets.remove();
						hit = false;
						MainActivity.hitCount++;
//						MainActivity.score.setText(String
//								.valueOf(MainActivity.hitCount));
					}
				}

				// if max score , then we are done
				if(MainActivity.hitCount >= MainActivity.maxScore)
				{
					win();
				}

				// a work around to avoid ConcurrentAccessException
				MainActivity.projectileLL.addAll(MainActivity.projectilesToBeAdded);
				MainActivity.projectilesToBeAdded.clear();

				MainActivity.targetLL.addAll(MainActivity.TargetsToBeAdded);
				MainActivity.TargetsToBeAdded.clear();

			}
		};
		MainActivity.mScene.registerUpdateHandler(detect);
	}
	
	public void checkCollision1()
	{
		
		/** TimerHandler for collision detection and cleaning up */
		detect1 = new IUpdateHandler() 
		{
			@Override
			public void reset() 
			{
				
			}

			@Override
			public void onUpdate(float pSecondsElapsed) 
			{

				Iterator<Sprite> objects = MainActivity.objcetLL.iterator();
				Sprite _object;

				// iterating over the targets
				while (objects.hasNext()) 
				{
					_object = objects.next();

					// if target passed the left edge of the screen, then remove
					// it and call a fail
					if (_object.getX() <= -_object.getWidth()) 
					{
						// removeSprite(_target, targets);
						MainActivity.oPool.recyclePoolItem(_object);
						objects.remove();
						// fail();
						break;
					}

						// if the targets collides with a projectile, remove the
						// projectile and set the hit flag to true
						if (_object.collidesWith(MainActivity.player)) 
						{
							MainActivity.oPool.recyclePoolItem(_object);
							objects.remove();
							MainActivity.hitCount1++;
							Log.d("asdasdasd","objects:"+MainActivity.hitCount1);
							break;
						}
				}

				MainActivity.objcetLL.addAll(MainActivity.objectsToBeAdded);
				MainActivity.objectsToBeAdded.clear();

			}
		};
		MainActivity.mScene.registerUpdateHandler(detect1);
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
	
	/** to restart the game and clear the whole screen */
	public void restart(MainActivity main) 
	{
		main.runOnUpdateThread(new Runnable()
		{
			@Override
			// to safely detach and re-attach the sprites
			public void run()
			{
				MainActivity.mScene.detachChildren();
				MainActivity.mScene.attachChild(MainActivity.player);
//				MainActivity.mScene.attachChild(MainActivity.score);
			}
		});

		// resetting everything
		MainActivity.hitCount = 0;
//		MainActivity.score.setText(String.valueOf(MainActivity.hitCount));
		MainActivity.projectileLL.clear();
		MainActivity.projectilesToBeAdded.clear();
		MainActivity.TargetsToBeAdded.clear();
		MainActivity.targetLL.clear();
	}

}
