package com.senselessweb.android.universeofpictures.service;

import java.util.concurrent.Executors;

import com.senselessweb.android.universeofpictures.common.Utils;
import com.threed.jpct.SimpleVector;

public abstract class AnimatedVectors implements Runnable
{
	
	private final SimpleVector[] from;
	
	private final SimpleVector[] to;
		
	private final long time;
	
	private long starttime;
	
	private boolean running = true; 

	public AnimatedVectors(final SimpleVector[] from, final SimpleVector[] to, final long time)
	{
		if (from.length != to.length)
			throw new IllegalArgumentException("from and to arrays must have the same length");
		
		this.from = from;
		this.to = to;
		this.time = time;
	}
	
	public void start()
	{
		Executors.newSingleThreadExecutor().execute(this);
	}

	@Override
	public final void run()
	{
		this.starttime = System.currentTimeMillis();
		while (this.running && System.currentTimeMillis() < this.starttime + this.time)
		{
			final float relTime = (float) (System.currentTimeMillis() - this.starttime) / this.time;
			final SimpleVector[] currents = new SimpleVector[this.from.length];
			for (int i = 0; i < this.from.length; i++)
			{
				currents[i] = new SimpleVector(
						getAnimatedValue(this.from[i].x, this.to[i].x, relTime),
						getAnimatedValue(this.from[i].y, this.to[i].y, relTime),
						getAnimatedValue(this.from[i].z, this.to[i].z, relTime));
			}
			
			this.animate(currents);
			Utils.sleepSafe(50);
		}
		
		if (this.running) this.animate(this.to);
	}
	
	public void stop()
	{
		this.running = false;
	}
	
	public abstract void animate(final SimpleVector[] vector);
	

	private float getAnimatedValue(float start, float end, float relTime)
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
