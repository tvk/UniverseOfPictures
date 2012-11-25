package com.senselessweb.android.universeofpictures.scene;

import java.util.concurrent.Executors;

import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class CameraAnimator
{
	
	private static final int animationTime = 12000;
	
	private final Camera camera;
	
	private final SimpleVector startPosition = new SimpleVector(-100, -100, -700);
	private final SimpleVector endPosition = new SimpleVector(140, -5, -60);

	private final SimpleVector startLookAt;
	private final SimpleVector endLookAt = new SimpleVector(0, 0, 1);
	
	private SimpleVector currentPosition = startPosition;
	private SimpleVector currentLookAt;
	
	private boolean animationRunning = false;

	public CameraAnimator(final Camera camera, final Object3D planet)
	{
		this.camera = camera;
		
		this.camera.setPosition(this.currentPosition);
		this.camera.lookAt(planet.getCenter());
		this.startLookAt = this.camera.getDirection();
		this.currentLookAt = this.startLookAt;
	}
	
	public void startAnimation()
	{
		synchronized(this)
		{
			if (this.animationRunning) return;
			this.animationRunning = true;
		}
		
		Log.d(CameraAnimator.class.getCanonicalName(), "Starting camera animation");
		
		final long startTime = System.currentTimeMillis();
		Executors.newSingleThreadExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				while (System.currentTimeMillis() < startTime + animationTime)
				{
					float relTime = (float) (System.currentTimeMillis() - startTime) / animationTime;
					currentPosition = new SimpleVector(
							getAnimatedValue(startPosition.x, endPosition.x, relTime),
							getAnimatedValue(startPosition.y, endPosition.y, relTime),
							getAnimatedValue(startPosition.z, endPosition.z, relTime));
					currentLookAt = new SimpleVector(
							getAnimatedValue(startLookAt.x, endLookAt.x, relTime),
							getAnimatedValue(startLookAt.y, endLookAt.y, relTime),
							getAnimatedValue(startLookAt.z, endLookAt.z, relTime));
					
					camera.setPosition(currentPosition);
					camera.setOrientation(currentLookAt, camera.getUpVector());
					
					//Log.d(CameraAnimator.class.getCanonicalName(), relTime + ": " + currentPosition.toString()  + ", " + currentLookAt.toString());
					//Log.d(CameraAnimator.class.getCanonicalName(), camera.getDirection().toString());
					
					try
					{
						Thread.sleep(25);
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				camera.setPosition(endPosition);
				camera.setOrientation(endLookAt, camera.getUpVector());
				animationRunning = false;
			}
		});
		
	}
	
	private static float getAnimatedValue(float start, float end, float relTime)
	{
		// Animated values are determined by the values of cos [pi => 2pi] 
		//
		//							cos (pi + t*pi) + 1
		// value = xs + (xe - xs) * -------------------
		//									2
		//
		// t = relative time (0 <= t <= 1)
		// xs = start position
		// xe = end position
		
		return (float) (start + (end - start) * ( (Math.cos(Math.PI + relTime * Math.PI) + 1) / 2.0 ));
	}
	
	
	
}
