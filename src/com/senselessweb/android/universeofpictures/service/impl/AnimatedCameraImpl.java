package com.senselessweb.android.universeofpictures.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.Log;

import com.senselessweb.android.universeofpictures.scene.Scene;
import com.senselessweb.android.universeofpictures.service.AnimatedCamera;
import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

@Singleton
public class AnimatedCameraImpl implements AnimatedCamera
{
	private static final SimpleVector startPosition = new SimpleVector(0, 0, 0);
	
	private final Scene scene;
	
	private BezierBasedCameraAnimation currentAnimation = null;
	
	@Inject
	public AnimatedCameraImpl(final Scene scene)
	{
		this.scene = scene;
		this.getCam().setPosition(startPosition);
	}
	
	private Camera getCam()
	{
		return this.scene.getCamera();
	}
	
	public synchronized void moveTo(final SimpleVector newPosition, final SimpleVector newDirection)
	{
		Log.i("AnimatedCamera", "Moving to: " + newPosition + ", " + newDirection);

		if (this.currentAnimation != null)
		{
			// Skip everything if we are already on target, stop the current animation if we are not
			if (this.currentAnimation.getTargetPosition().equals(newPosition)) return;
			else this.currentAnimation.stop();
		}
		
		this.currentAnimation = new BezierBasedCameraAnimation(
				this.getCam().getPosition(), this.getCam().getDirection(),
				newPosition, newDirection, this.getCam().getUpVector(),
				20000)
		{
			
			@Override
			public void animatePosition(final SimpleVector positon)
			{
				AnimatedCameraImpl.this.getCam().setPosition(positon);
			}

			@Override
			public void animateOrientation(final SimpleVector direction, final SimpleVector up)
			{
				AnimatedCameraImpl.this.getCam().setOrientation(direction, up);
			}

			@Override
			public void animateLookAt(final SimpleVector lookAt)
			{
				AnimatedCameraImpl.this.getCam().lookAt(lookAt);
			}
			
		};
		
		this.currentAnimation.start();
	}

	public void moveToStartPosition()
	{
		this.moveTo(startPosition, new SimpleVector(0f, 0f, 1f));
	}
	
	
}
