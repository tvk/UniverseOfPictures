package com.senselessweb.android.universeofpictures.service.impl;

import static java.lang.Math.pow;

import java.util.concurrent.Executors;

import com.senselessweb.android.universeofpictures.common.Utils;
import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;

/**
 * Animates the position, direction and the up vector of a camera based on the
 * algorithm for bezier curves to get a smooth camera movement where the camera is always 
 * directed to the front.
 * 
 * @author thomas
 */
abstract class BezierBasedCameraAnimation implements Runnable
{

	// Bezier coordinates as defined here:
	// http://de.wikipedia.org/wiki/B%C3%A9zierkurve
	private final SimpleVector p1;
	private final SimpleVector p2;
	private final SimpleVector p3;
	private final SimpleVector p4;
	
	private final SimpleVector startDirection;

	private final SimpleVector startUp;

	private final long time;
	
	private long starttime;
	
	private boolean running = true;

	public BezierBasedCameraAnimation(
			final SimpleVector startPosition, final SimpleVector startDirection, 
			final SimpleVector endPosition, final SimpleVector endDirection,
			final SimpleVector startUp,
			final long time)
	{
		this.p1 = startPosition;
		
		final SimpleVector theStartDirection = startDirection.normalize();
		theStartDirection.scalarMul(200f);
		this.p2 = this.p1.calcAdd(theStartDirection);
		
		this.p4 = endPosition;
		
		final SimpleVector theEndDirection = endDirection.normalize();
		theEndDirection.scalarMul(200f);
		this.p3 = this.p4.calcSub(theEndDirection);
		
		this.startDirection = startDirection;
		this.startUp = startUp;
		this.time = time;
	}
	
	public SimpleVector getTargetPosition()
	{
		return this.p4;
	}
	
	public void start()
	{
		Executors.newSingleThreadExecutor().execute(this);
	}

	public void stop()
	{
		this.running = false;
	}	
	
	@Override
	public final void run()
	{
		this.starttime = System.currentTimeMillis();
		final Camera cameraAt10Percent = this.getCameraAt10Percent();
		
		while (this.running && System.currentTimeMillis() < this.starttime + time)
		{
			final float relTime = (float) (System.currentTimeMillis() - this.starttime) / time;
			final float t = (float) ( (Math.cos(Math.PI + relTime * Math.PI) + 1) / 2.0 );

			// The current position
			final SimpleVector current = this.calculatePosition(t);
			this.animatePosition(current);

			// In the first 10% of this animation we rotate only the up vector
			if (t <= 0.1f)
			{
				final SimpleVector up = new SimpleVector(
						this.getCosinusAnimatedValue(this.startUp.x, cameraAt10Percent.getUpVector().x, t * 10f),
						this.getCosinusAnimatedValue(this.startUp.y, cameraAt10Percent.getUpVector().y, t * 10f),
						this.getCosinusAnimatedValue(this.startUp.z, cameraAt10Percent.getUpVector().z, t * 10f));
				final SimpleVector dir = new SimpleVector(
						this.getCosinusAnimatedValue(this.startDirection.x, cameraAt10Percent.getDirection().x, t * 10f),
						this.getCosinusAnimatedValue(this.startDirection.y, cameraAt10Percent.getDirection().y, t * 10f),
						this.getCosinusAnimatedValue(this.startDirection.z, cameraAt10Percent.getDirection().z, t * 10f));
				
				this.animateOrientation(dir, up);
			}
			else if (t <= 0.99f)
			{
				// Calculate next position to know where to look at. 
				final SimpleVector next = this.calculatePosition(t + 0.01f); 
				this.animateLookAt(next);
			}
			
			Utils.sleepSafe(15);
		}
	}
	
	public abstract void animatePosition(final SimpleVector position);
	
	public abstract void animateOrientation(final SimpleVector direction, final SimpleVector up);

	public abstract void animateLookAt(final SimpleVector lookAt);

	private Camera getCameraAt10Percent()
	{
		final SimpleVector positon = this.calculatePosition(0.1f); 
		final SimpleVector next = this.calculatePosition(0.11f); 

		final Camera cam = new Camera();
		cam.setPosition(positon);
		cam.lookAt(next);
		
		return cam;
	}
	
	private SimpleVector calculatePosition(final float t)
	{
		return new SimpleVector(
				this.calculateBezier(this.p1.x, this.p2.x, this.p3.x, this.p4.x, t),
				this.calculateBezier(this.p1.y, this.p2.y, this.p3.y, this.p4.y, t),
				this.calculateBezier(this.p1.z, this.p2.z, this.p3.z, this.p4.z, t));
	}
	
	private float calculateBezier(final float p1, final float p2, final float p3, final float p4, final float t)
	{
		// http://www.codeworx.org/opengl_tut28.php
		return (float) (p4*pow(t,3) + p3*3*pow(t,2)*(1-t) + p2*3*t*pow((1-t),2) + p1*pow((1-t),3));
	}


	private float getCosinusAnimatedValue(float start, float end, float relTime)
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
