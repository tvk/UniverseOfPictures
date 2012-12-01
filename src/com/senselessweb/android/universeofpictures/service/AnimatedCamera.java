package com.senselessweb.android.universeofpictures.service;

import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

public class AnimatedCamera
{
	private static final SimpleVector startPosition = new SimpleVector(0, 0, 0);
	
	private static AnimatedCamera instance;
	
	private final Camera camera;
	
	private AnimatedVectors animatedTranslation = null;
	
	public AnimatedCamera(final Camera camera)
	{
		this.camera = camera;
		this.camera.setPosition(startPosition);
	}
	
	public static void init(final Camera camera)
	{
		instance = new AnimatedCamera(camera);
	}
	
	public static AnimatedCamera getInstance()
	{
		if (instance == null) 
			throw new IllegalStateException("AnimatedCamera has not been initialized. Call init() first"); 
		return instance;
	}
	
	public synchronized void moveTo(final SimpleVector newPosition, final SimpleVector newDirection)
	{
		Log.i("AnimatedCamera", "Moving to: " + newPosition + ", " + newDirection);
		
		if (this.animatedTranslation != null)
			this.animatedTranslation.stop();
		
		this.animatedTranslation = new AnimatedVectors(
				new SimpleVector[] {this.camera.getPosition(), this.camera.getDirection(), this.camera.getUpVector()},
				new SimpleVector[] {newPosition, newDirection, new SimpleVector(0f, -1f, 0f)},
				15000)
		{
			@Override
			public void animate(final SimpleVector[] current)
			{
				AnimatedCamera.this.camera.setPosition(current[0]);
				AnimatedCamera.this.camera.setOrientation(current[1], current[2]);
			}
		};
		this.animatedTranslation.start();
	}

	public void moveToStartPosition()
	{
		this.moveTo(startPosition, new SimpleVector(0f, 0f, 1f));
	}
	
}
