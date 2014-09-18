package com.example.objectpool;

import java.util.Iterator;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.pool.GenericPool;

import com.example.some.MainActivity;

public class Enemy1Pool extends GenericPool<AnimatedSprite> {

	private TiledTextureRegion mTextureRegion;
	public static IUpdateHandler detectEnemy1;

	public Enemy1Pool(TiledTextureRegion mTargetTextureRegion) {
		if (mTargetTextureRegion == null) {
			throw new IllegalArgumentException(
					"The texture region must not be NULL");
		}
		mTextureRegion = mTargetTextureRegion;
	}

	/** a Time Handler for spawning targets, triggers every 1 second */
	public static void checkCollisionEnemy1()
	{
		
		/** TimerHandler for collision detection and cleaning up */
		detectEnemy1 = new IUpdateHandler() 
		{
			@Override
			public void reset() 
			{
				
			}

			@Override
			public void onUpdate(float pSecondsElapsed) 
			{

				Iterator<AnimatedSprite> targets = MainActivity.targetLLEnemy1.iterator();
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
					//win();
				}

				// a work around to avoid ConcurrentAccessException
				MainActivity.projectileLL.addAll(MainActivity.projectilesToBeAdded);
				MainActivity.projectilesToBeAdded.clear();

				MainActivity.targetLLEnemy1.addAll(MainActivity.TargetsToBeAddedEnemy1);
				MainActivity.TargetsToBeAddedEnemy1.clear();

			}
		};
		MainActivity.mScene.registerUpdateHandler(detectEnemy1);
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