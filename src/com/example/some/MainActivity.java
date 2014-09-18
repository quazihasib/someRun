package com.example.some;

import java.io.IOException;
import java.util.LinkedList;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import com.example.objectpool.Enemy2Pool;
import com.example.objectpool.ObjectsPool;
import com.example.objectpool.ProjectilesPool;
import com.example.objectpool.Enemy1Pool;
import com.example.objects.Controller;
import com.example.objects.Models;

import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener
{

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	public static Camera mCamera;
	public static Sound shootingSound;
	public static Music backgroundMusic;
	public static Scene mScene;
	
	public Models model;
	public Controller controller;
	
	public static BitmapTextureAtlas mBitmapTextureAtlas;

	public static BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public static ITextureRegion mParallaxLayerBack;
	public static ITextureRegion mParallaxLayerMid;
	public static ITextureRegion mParallaxLayerFront;
	
	public static BuildableBitmapTextureAtlas mPlayerBitmapTextureAtlas, 
									 mEnemy1BitmapTextureAtlas, mEnemy2BitmapTextureAtlas;
	
	public static TiledTextureRegion mPlayerTextureRegion, mEnemy1TextureRegion,
									 mEnemy2TextureRegion,
									 mProjectileTextureRegion, mPausedTextureRegion,
									 mWinTextureRegion, mFailTextureRegion;
	
	public static  VertexBufferObjectManager vertexBufferObjectManager;
	
	// our object pools
	public static ProjectilesPool pPool;
	public static Enemy1Pool tPool;
	public static ObjectsPool oPool;
	public static Enemy2Pool e2Pool;

	public static LinkedList<Sprite> projectileLL;
	public static LinkedList<Sprite> projectilesToBeAdded;
	
	public static LinkedList<AnimatedSprite> targetLLEnemy1;
	public static LinkedList<AnimatedSprite> TargetsToBeAddedEnemy1;
	
	public static LinkedList<AnimatedSprite> targetLLEnemy2;
	public static LinkedList<AnimatedSprite> TargetsToBeAddedEnemy2;
		
	public static LinkedList<Sprite> objcetLL;
	public static LinkedList<Sprite> objectsToBeAdded;
	
	public static Sprite winSprite;
	public static Sprite failSprite;

	public boolean runningFlag = false;
	public boolean pauseFlag = false;
	public static CameraScene mPauseScene;
	public static CameraScene mResultScene;
	public static  int hitCount;
	public static  int hitCount1;
	public static final int maxScore = 10;

	public static AnimatedSprite player;
//	public static Engine mEngine;
	
	BitmapTextureAtlas mBitmapTextureAtlasPaused,mBitmapTextureAtlasWin,
					   mBitmapTextureAtlasFail, mBitmapTextureAtlasProjectile;
	
	public static boolean touch;
	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		EngineOptions en = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		en.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
		en.getTouchOptions().setNeedsMultiTouch(true);

//		mEngine = new Engine(en);
		if(MultiTouch.isSupported(this))
		{
			if(MultiTouch.isSupportedDistinct(this))
			{
				//Toast.makeText(this, "MultiTouch detected controls will work properly!", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				//Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
			}
		} 
		else 
		{
			//Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
		}
		
		return en;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, this, "parallax_background_layer_front.png", 0, 0);
		mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, this, "parallax_background_layer_back.png", 0, 188);
		mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, this, "parallax_background_layer_mid.png", 0, 669);
		mAutoParallaxBackgroundTexture.load();
		
		
		mBitmapTextureAtlasPaused= new BitmapTextureAtlas(getTextureManager(), 200, 50, TextureOptions.BILINEAR);
		mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				mBitmapTextureAtlasPaused, this, "paused.png", 0, 0, 1, 1);
		mBitmapTextureAtlasPaused.load();
		
		mBitmapTextureAtlasWin= new BitmapTextureAtlas(getTextureManager(), 200, 50, TextureOptions.BILINEAR);
		mWinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				mBitmapTextureAtlasWin, this, "win.png", 0, 0, 1, 1);
		mBitmapTextureAtlasWin.load();
		
		mBitmapTextureAtlasFail= new BitmapTextureAtlas(getTextureManager(), 200, 50, TextureOptions.BILINEAR);
		mFailTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				mBitmapTextureAtlasFail, this, "fail.png", 0, 0, 1, 1);
		mBitmapTextureAtlasFail.load();
		
		mBitmapTextureAtlasProjectile= new BitmapTextureAtlas(getTextureManager(), 30, 31, TextureOptions.BILINEAR);
		mProjectileTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				mBitmapTextureAtlasProjectile, this, "projectile.png", 0, 0, 1, 1);
		mBitmapTextureAtlasProjectile.load();
		
//		pPool = new ProjectilesPool(mProjectileTextureRegion);
//		tPool = new TargetsPool(mEnemyTextureRegion);
//		oPool = new ObjectsPool(mWinTextureRegion);
		
		//Player
		mPlayerBitmapTextureAtlas = new BuildableBitmapTextureAtlas(
		this.getTextureManager(), 72, 128, TextureOptions.NEAREST);
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.
		createTiledFromAsset(mPlayerBitmapTextureAtlas, this, "player.png", 3, 4);
		try
		{
			mPlayerBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			mPlayerBitmapTextureAtlas.load();
		}
		catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
		//Enemy
		mEnemy1BitmapTextureAtlas = new BuildableBitmapTextureAtlas(
		this.getTextureManager(), 211, 100, TextureOptions.NEAREST);
		mEnemy1TextureRegion = BitmapTextureAtlasTextureRegionFactory.
		createTiledFromAsset(mEnemy1BitmapTextureAtlas, this, "target.png", 3, 1);
		try
		{
			mEnemy1BitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			mEnemy1BitmapTextureAtlas.load();
		}
		catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
		mEnemy2BitmapTextureAtlas = new BuildableBitmapTextureAtlas(
		this.getTextureManager(), 72, 128, TextureOptions.NEAREST);
		mEnemy2TextureRegion = BitmapTextureAtlasTextureRegionFactory.
		createTiledFromAsset(mEnemy2BitmapTextureAtlas, this, "enemy.png", 3, 4);
		try
		{
			mEnemy2BitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			mEnemy2BitmapTextureAtlas.load();
		}
		catch (TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
		SoundFactory.setAssetBasePath("mfx/");
		try 
		{
			shootingSound = SoundFactory.createSoundFromAsset(
					mEngine.getSoundManager(), this, "pew_pew_lei.wav");
		} 
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MusicFactory.setAssetBasePath("mfx/");

		try 
		{
			backgroundMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), 
					this, "background_music.wav");
			backgroundMusic.setLooping(true);
		} 
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Scene onCreateScene() 
	{
		mEngine.registerUpdateHandler(new FPSLogger());
		
		mScene = new Scene();
		
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		vertexBufferObjectManager = this.getVertexBufferObjectManager();
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 80, this.mParallaxLayerMid, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerFront.getHeight(), this.mParallaxLayerFront, vertexBufferObjectManager)));
		mScene.setBackground(autoParallaxBackground);
		mScene.setOnSceneTouchListener(this);

		// creating a new scene for the pause menu
		mPauseScene = new CameraScene(mCamera);
		/* Make the label centered on the camera. */
		final int x = (int) (mCamera.getWidth() / 2 - mPausedTextureRegion
				.getWidth() / 2);
		final int y = (int) (mCamera.getHeight() / 2 - mPausedTextureRegion
				.getHeight() / 2);
		final Sprite pausedSprite = new Sprite(x, y, mPausedTextureRegion, vertexBufferObjectManager);
		mPauseScene.attachChild(pausedSprite);
		// makes the scene transparent
		mPauseScene.setBackgroundEnabled(false);

		// the results scene, for win/fail
		mResultScene = new CameraScene(mCamera);
		winSprite = new Sprite(x, y, mWinTextureRegion, vertexBufferObjectManager);
		failSprite = new Sprite(x, y, mFailTextureRegion, vertexBufferObjectManager);
		mResultScene.attachChild(winSprite);
		mResultScene.attachChild(failSprite);
		// makes the scene transparent
		mResultScene.setBackgroundEnabled(false);

		winSprite.setVisible(false);
		failSprite.setVisible(false);
		
		touch = false;
		
//		final float playerX = (CAMERA_WIDTH - mPlayerTextureRegion.getWidth()) / 2;
//		final float playerY = CAMERA_HEIGHT - mPlayerTextureRegion.getHeight() - 5;
//
//		player = new AnimatedSprite(playerX, playerY, mPlayerTextureRegion, vertexBufferObjectManager);
//		player.setScaleCenterY(mPlayerTextureRegion.getHeight());
////		player.setScale(2);
//		player.animate(new long[]{200, 200, 200}, 3, 5, true);
//
//		final AnimatedSprite enemy = new AnimatedSprite(playerX - 80, playerY, mEnemyTextureRegion, vertexBufferObjectManager);
//		enemy.setScaleCenterY(mEnemyTextureRegion.getHeight());
////		enemy.setScale(2);
//		enemy.animate(new long[]{200, 200, 200}, 0, 2, true);
//
//		mScene.attachChild(player);
//		mScene.attachChild(enemy);
		
		model = new Models(this);
		controller = new Controller(this);
		
		Models.createHero();
		mScene.setTouchAreaBindingOnActionDownEnabled(true);

		// initializing variables
		projectileLL = new LinkedList<Sprite>();
		projectilesToBeAdded = new LinkedList<Sprite>();
		
		targetLLEnemy1 = new LinkedList<AnimatedSprite>();
		TargetsToBeAddedEnemy1 = new LinkedList<AnimatedSprite>();
		
		targetLLEnemy2 = new LinkedList<AnimatedSprite>();
		TargetsToBeAddedEnemy2 = new LinkedList<AnimatedSprite>();
		
		objcetLL = new LinkedList<Sprite>();
		objectsToBeAdded = new LinkedList<Sprite>();

		// settings score to the value of the max score to make sure it appears
		// correctly on the screen
//		score = new ChangeableText(0, 0, mFont, String.valueOf(maxScore));
//		// repositioning the score later so we can use the score.getWidth()
//		score.setPosition(mCamera.getWidth() - score.getWidth() - 5, 5);

		//adding enemies, objects
		createSpriteSpawnTimeHandler();
		
		Enemy1Pool.checkCollisionEnemy1();
		ObjectsPool.checkCollisionObject();
		Enemy2Pool.checkCollisionEnemy2();

		// starting background music
		backgroundMusic.play();
//		runningFlag = true;
//		pauseFlag = false;
		
		controller.restart(this);

		return mScene;
	}

	public void createSpriteSpawnTimeHandler()
	{
		TimerHandler spriteTimerHandler;
		float mEffectSpawnDelay = 1f;

		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback()
		{
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) 
					{
						Models.addEnemy1();
						//Models.addEnemy2();
						//Models.addObjects();
					}
				});

		MainActivity.mScene.registerUpdateHandler(spriteTimerHandler);
	}  

	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) 
	{
		// TODO Auto-generated method stub
		if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) 
		{
			final float touchX = pSceneTouchEvent.getX();
			final float touchY = pSceneTouchEvent.getY();
			
			touch = true;
			
			Models.shootProjectile(touchX, touchY);
//			Controller.jump(player);
			
			return true;
		}
		else if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) 
		{
			touch = false;
		}
		
		Debug.d("touch:"+touch);
		return false;
	}

	@Override
	// pauses the music and the game when the game goes to the background
	protected void onPause() 
	{
		if(runningFlag) 
		{
			pauseMusic();
			if(mEngine.isRunning()) 
			{
				pauseGame();
				pauseFlag = true;
			}
		}
		super.onPause();
	}

	@Override
	public void onResumeGame() 
	{
		super.onResumeGame();
		// shows this Toast when coming back to the game
		if(runningFlag==true) 
		{
			if(pauseFlag==true) 
			{
				pauseFlag = false;
				Toast.makeText(this, "Menu button to resume",
						Toast.LENGTH_SHORT).show();
			} 
//			else 
//			{
//				// in case the user clicks the home button while the game on the
//				// resultScene
//				resumeMusic();
//				mEngine.stop();
//			}
		} 
		else 
		{
			runningFlag = true;
		}
	}

	public void pauseMusic()
	{
		if(runningFlag)
			if(backgroundMusic.isPlaying())
				backgroundMusic.pause();
	}

	public void resumeMusic()
	{
		if(runningFlag)
			if(!backgroundMusic.isPlaying())
				backgroundMusic.resume();
	}

	public void pauseGame()
	{
		if(runningFlag) 
		{
			mScene.setChildScene(mPauseScene, false, true, true);
			mEngine.stop();
		}
	}

	public void unPauseGame()
	{
		mScene.clearChildScene();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent)
	{
		// if menu button is pressed
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(mEngine.isRunning() && backgroundMusic.isPlaying()) 
			{
				pauseMusic();
				pauseFlag = true;
				pauseGame();
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				if(!backgroundMusic.isPlaying()) 
				{
					unPauseGame();
					pauseFlag = false;
					resumeMusic();
					mEngine.start();
				}
				return true;
			}
			// if back key was pressed
		} 
		else if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) 
		{
			if(!mEngine.isRunning() && backgroundMusic.isPlaying())
			{
				mScene.clearChildScene(); 
				mEngine.start();
				controller.restart(this);
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
	
}
