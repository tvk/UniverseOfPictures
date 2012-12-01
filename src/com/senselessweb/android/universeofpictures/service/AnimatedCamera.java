package com.senselessweb.android.universeofpictures.service;

import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

public class AnimatedCamera
{
	private static final SimpleVector startPosition = new SimpleVector(0, 0, 0);
	
	private static AnimatedCamera instance;
	
	private final Camera camera;
	
	private BezierBasedCameraAnimation animatedTranslation = null;
	
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
		{
			// Skip everything if we are already on target, stop the current animation if we are not
			if (this.animatedTranslation.getTargetPosition().equals(newPosition)) return;
			else this.animatedTranslation.stop();
		}
		
		this.animatedTranslation = new BezierBasedCameraAnimation(
				this.camera.getPosition(), this.camera.getDirection(),
				newPosition, newDirection,
				this.camera.getUpVector(), new SimpleVector(0f, -1f, 0f),
				20000)
		{
			
			@Override
			public void animate(final SimpleVector current, final SimpleVector direction, final SimpleVector up)
			{
				AnimatedCamera.this.camera.setPosition(current);
				AnimatedCamera.this.camera.setOrientation(direction, up);
			}
		};
		
		this.animatedTranslation.start();
	}

	public void moveToStartPosition()
	{
		this.moveTo(startPosition, new SimpleVector(0f, 0f, 1f));
	}
	
}
