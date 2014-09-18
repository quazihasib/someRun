package com.example.objectpool;

import java.util.Iterator;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.pool.GenericPool;

import com.example.objects.Controller;
import com.example.some.MainActivity;

public class Enemy2Pool extends GenericPool<AnimatedSprite> {

	private TiledTextureRegion mTextureRegion;
	public static IUpdateHandler detectEnemy2;
	public static AnimatedSprite _target;

	public Enemy2Pool(TiledTextureRegion mTargetTextureRegion) {
		if (mTargetTextureRegion == null) {
			throw new IllegalArgumentException(
					"The texture region must not be NULL");
		}
		mTextureRegion = mTargetTextureRegion;
	}
	
	public static void checkCollisionEnemy2()
	{
		
		/** TimerHandler for collision detection and cleaning up */
		detectEnemy2 = new IUpdateHandler() 
		{
			@Override
			public void reset() 
			{
				
			}

			@Override
			public void onUpdate(float pSecondsElapsed) 
			{

				Iterator<AnimatedSprite> targets = MainActivity.targetLLEnemy2.iterator();

				// iterating over the targets
				while (targets.hasNext()) 
				{
					_target = targets.next();

					// if target passed the left edge of the screen, then remove
					// it and call a fail
					if (_target.getX() <= -_target.getWidth()) 
					{
						// removeSprite(_target, targets);
						MainActivity.e2Pool.recyclePoolItem(_target);
						targets.remove();
						// fail();
						break;
					}
					
						// if the targets collides with a projectile, remove the
						// projectile and set the hit flag to true
						if (_target.collidesWith(MainActivity.player)) 
						{
							if(MainActivity.touch == true)
							{
								
								MainActivity.e2Pool.recyclePoolItem(_target);
								targets.remove();
								
								Controller.playerAnimate();
								//MainActivity.hitCount++;
							}
							else 
							{
								targetAnimate();
							}
							break;
						}

				}

				MainActivity.targetLLEnemy2.addAll(MainActivity.TargetsToBeAddedEnemy2);
				MainActivity.TargetsToBeAddedEnemy2.clear();

			}
		};
		MainActivity.mScene.registerUpdateHandler(detectEnemy2);
	}
	
	public static void targetAnimate()
	{
		_target.animate(new long[]{200, 200, 200}, 3, 5, true);
		
		MainActivity.mScene.registerUpdateHandler(new TimerHandler(1,
				new ITimerCallback() {
					
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						// TODO Auto-generated method stub
						_target.animate(new long[]{200, 200, 200}, 9, 11, true);
					}
				}));
	}

	@Override
	protected AnimatedSprite onAllocatePoolItem() {
		return new AnimatedSprite(0, 0, mTextureRegion.deepCopy(), MainActivity.vertexBufferObjectManager);
	}

	protected void onHandleRecycleItem(final AnimatedSprite target) {
		target.clearEntityModifiers();
		target.clearUpdateHandlers();
		target.setVisible(false);
		target.detachSelf();
		target.reset();
	}

}