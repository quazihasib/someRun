package com.example.objectpool;

import java.util.Iterator;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import android.util.Log;

import com.example.some.MainActivity;

public class ObjectsPool extends GenericPool<Sprite> {
	
	private TiledTextureRegion mTextureRegion;
	public static IUpdateHandler detectObject1;
	
	public ObjectsPool(TiledTextureRegion pTextureRegion) {
		if (pTextureRegion == null) {
			// Need to be able to create a Sprite so the Pool needs to have a
			// TextureRegion
			throw new IllegalArgumentException(
					"The texture region must not be NULL");
		}
		mTextureRegion = pTextureRegion;
	}

	public static void checkCollisionObject()
	{
		
		/** TimerHandler for collision detection and cleaning up */
		detectObject1 = new IUpdateHandler() 
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
		MainActivity.mScene.registerUpdateHandler(detectObject1);
	}
	
	/** Called when a projectile is required but there isn't one in the pool */
	@Override
	protected Sprite onAllocatePoolItem() {
		return new Sprite(0, 0, mTextureRegion.deepCopy(), MainActivity.vertexBufferObjectManager);
	}
	//test

	/** Called when a projectile is sent to the pool */
	protected void onHandleRecycleItem(final Sprite projectile) {
		projectile.clearEntityModifiers();
		projectile.clearUpdateHandlers();
		projectile.setVisible(false);
		projectile.detachSelf();
		projectile.reset();
	}
}