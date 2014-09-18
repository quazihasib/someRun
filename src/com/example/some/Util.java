package com.example.some;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import android.opengl.GLES20;

public class Util 
{

	public static void setScore()
	{
		MainActivity.mScoreText = new Text(5, 5, MainActivity.mFont, "Score: 0", "Score: XXXX".length(), MainActivity.vertexBufferObjectManager);
		MainActivity.mScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		MainActivity.mScoreText.setAlpha(0.5f);
		MainActivity.mScene.attachChild(MainActivity.mScoreText);
		
		MainActivity.mScene.registerUpdateHandler(new TimerHandler((float) 0.05, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				// TODO Auto-generated method stub
				MainActivity.mScore++;
				MainActivity.mScoreText.setText("Score: " + MainActivity.mScore);
			}
		}));
	}
	
	public void setGameOverAnimation()
	{
		/* The title-text. */
		final Text titleText = new Text(0, 0, MainActivity.mFont, "Snake\non a Phone!", new TextOptions(HorizontalAlign.CENTER), MainActivity.vertexBufferObjectManager);
		titleText.setPosition((MainActivity.CAMERA_WIDTH - titleText.getWidth()) * 0.5f, (MainActivity.CAMERA_HEIGHT - titleText.getHeight()) * 0.5f);
		titleText.setScale(0.0f);
		titleText.registerEntityModifier(new ScaleModifier(2, 0.0f, 1.0f));
		MainActivity.mScene.attachChild(titleText);
	}

}
