package com.senselessweb.android.universeofpictures.service;

import static java.lang.Math.pow;

import java.util.concurrent.Executors;

import com.senselessweb.android.universeofpictures.common.Utils;
import com.threed.jpct.SimpleVector;

/**
 * Animates the position, direction and the up vector of a camera based on the
 * algorithm for bezier curves to get a smooth camera movement where the camera is always 
 * directed to the front.
 * 
 * @author thomas
 */
public abstract class BezierBasedCameraAnimation implements Runnable
{

	// Bezier coordinates as defined here:
	// http://de.wikipedia.org/wiki/B%C3%A9zierkurve
	private final SimpleVector p1;
	private final SimpleVector p2;
	private final SimpleVector p3;
	private final SimpleVector p4;

	// Source and destination up vectors
	private final SimpleVector startUp;
	private final SimpleVector endUp;
	
	private final long time;
	
	private long starttime;
	
	private SimpleVector previous;
	
	private boolean running = true;

	public BezierBasedCameraAnimation(
			final SimpleVector startPosition, final SimpleVector startDirection, 
			final SimpleVector endPosition, final SimpleVector endDirection,
			final SimpleVector startUp, final SimpleVector endUp,
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
		
		this.startUp = startUp;
		this.endUp = endUp;
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
		this.previous = this.p3;
		
		while (this.running && System.currentTimeMillis() < this.starttime + time)
		{
			final float relTime = (float) (System.currentTimeMillis() - this.starttime) / time;
			final float t = (float) ( (Math.cos(Math.PI + relTime * Math.PI) + 1) / 2.0 );
			
			final SimpleVector current = new SimpleVector(
					this.calculateBezier(this.p1.x, this.p2.x, this.p3.x, this.p4.x, t),
					this.calculateBezier(this.p1.y, this.p2.y, this.p3.y, this.p4.y, t),
					this.calculateBezier(this.p1.z, this.p2.z, this.p3.z, this.p4.z, t));
			final SimpleVector direction = new SimpleVector(
					current.x - this.previous.x,
					current.y - this.previous.y,
					current.z - this.previous.z).normalize();
			final SimpleVector up = new SimpleVector(
					this.getCosinusAnimatedValue(this.startUp.x, this.endUp.x, relTime),
					this.getCosinusAnimatedValue(this.startUp.y, this.endUp.y, relTime),
					this.getCosinusAnimatedValue(this.startUp.z, this.endUp.z, relTime));
			
			this.previous = current;
			this.animate(current, direction, up);
			Utils.sleepSafe(15);
		}
	}
	
	public abstract void animate(final SimpleVector current, final SimpleVector direction, final SimpleVector up);
	
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
