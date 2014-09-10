package com.example.objects;

import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.example.objectpool.ObjectsPool;
import com.example.objectpool.ProjectilesPool;
import com.example.objectpool.TargetsPool;
import com.example.some.MainActivity;

import android.content.Context;

public class Models 
{

	public Context context;

	public Models(Context con) 
	{
		this.context = con;
	}

	/** adds a target at a random location and let it move along the x-axis */
	public static void addTarget() 
	{
		Random rand = new Random();

		float x = (int) MainActivity.mCamera.getWidth()
				+ MainActivity.mEnemyTextureRegion.getWidth();
		int minY = (int)MainActivity.mEnemyTextureRegion.getHeight();
		int maxY = (int) (MainActivity.mCamera.getHeight() - MainActivity.mEnemyTextureRegion
				.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		AnimatedSprite target;
		MainActivity.tPool = new TargetsPool(MainActivity.mEnemyTextureRegion);
		target = MainActivity.tPool.obtainPoolItem();
		target.setPosition(x, y);
		target.animate(300);
		MainActivity.mScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(),
				-target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		MainActivity.TargetsToBeAdded.add(target);

	}

	/** shoots a projectile from the player's position along the touched area */
	public static void shootProjectile(final float pX, final float pY) 
	{
		if (!CoolDown.sharedCoolDown().checkValidity()) 
		{
			return;
		}

		int offX = (int) (pX - MainActivity.player.getX());
		int offY = (int) (pY - MainActivity.player.getY());
		if (offX <= 0)
			return;

		final Sprite projectile;
		MainActivity.pPool = new ProjectilesPool(MainActivity.mProjectileTextureRegion);
		// position the projectile on the player
		projectile = MainActivity.pPool.obtainPoolItem();
		projectile.setPosition(
				MainActivity.player.getX() + MainActivity.player.getWidth(),
				MainActivity.player.getY());

		int realX = (int) (MainActivity.mCamera.getWidth() + projectile
				.getWidth() / 2.0f);
		float ratio = (float) offY / (float) offX;
		int realY = (int) ((realX * ratio) + projectile.getY());

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());
		float length = (float) Math.sqrt((offRealX * offRealX)
				+ (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		// defining a moveBymodifier from the projectile's position to the
		// calculated one
		MoveByModifier movMByod = new MoveByModifier(realMoveDuration, realX, realY);
		LoopEntityModifier loopMod = new LoopEntityModifier( new RotationModifier(0.5f, 0, -360));

		final ParallelEntityModifier par = new ParallelEntityModifier(movMByod, loopMod);

		DelayModifier dMod = new DelayModifier(0.55f);
		dMod.addModifierListener(new IModifierListener<IEntity>()
		{

			@Override
			public void onModifierStarted(IModifier<IEntity> arg0, IEntity arg1) 
			{
				
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> arg0, IEntity arg1) 
			{
				// TODO Auto-generated method stub
				MainActivity.shootingSound.play();
				projectile.setVisible(true);
				projectile.setPosition(
						MainActivity.player.getX() + MainActivity.player.getWidth(),
						MainActivity.player.getY()
								+ MainActivity.player.getHeight() / 3);
				MainActivity.projectilesToBeAdded.add(projectile);
			}
		});

		SequenceEntityModifier seq = new SequenceEntityModifier(dMod, par);
		projectile.registerEntityModifier(seq);
		projectile.setVisible(false);
		MainActivity.mScene.attachChild(projectile);

		MainActivity.player.animate(50, false);
//		MainActivity.player.animate(new long[] { 100, 100, 100, 100, 100, 100,
//				100, 100, 100, 100, 100 }, 23, 33, false);
	}

	public static void createHero()
	{
		// TODO Auto-generated method stub
		// set coordinates for the player
		final int PlayerX = (int) (MainActivity.mPlayerTextureRegion.getWidth() / 20);
		final int PlayerY = (int) ((MainActivity.mCamera.getHeight() - MainActivity.mPlayerTextureRegion
				.getHeight()) / 2);

		// set the player on the scene
		MainActivity.player = new AnimatedSprite(PlayerX, PlayerY, MainActivity.mPlayerTextureRegion, MainActivity.vertexBufferObjectManager) 
		{
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) 
			{
				this.setPosition(this.getX(),
						pSceneTouchEvent.getY() - this.getHeight() / 2);

				return true;
			}
		};

		MainActivity.mScene.registerTouchArea(MainActivity.player);
	}

	public static void addObjects() 
	{
		Random rand = new Random();

		float x = (int) MainActivity.mCamera.getWidth()
				+ MainActivity.mEnemyTextureRegion.getWidth();
		int minY = (int)MainActivity.mEnemyTextureRegion.getHeight();
		int maxY = (int) (MainActivity.mCamera.getHeight() - MainActivity.mEnemyTextureRegion
				.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite Object;
		MainActivity.oPool = new ObjectsPool(MainActivity.mWinTextureRegion);
		Object = MainActivity.oPool.obtainPoolItem();
		Object.setPosition(x, y);
//		Object.animate(300);
		MainActivity.mScene.attachChild(Object);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, Object.getX(),
				-Object.getWidth());
		Object.registerEntityModifier(mod.deepCopy());

		MainActivity.objectsToBeAdded.add(Object);

	}

}
