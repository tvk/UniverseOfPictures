package com.senselessweb.android.universeofpictures.service;

import java.util.concurrent.Executors;

import com.threed.jpct.Object3D;

public class Rotator implements Runnable
{
	public enum Axis
	{
		X {
			@Override
			void rotate(final Object3D object, final float step)
			{
				object.rotateX(step);
			}
		}, 
		
		Y {
			@Override
			void rotate(final Object3D object, final float step)
			{
				object.rotateY(step);
			}
		}, 
		
		Z {
			@Override
			void rotate(final Object3D object, final float step)
			{
				object.rotateZ(step);
			}
		};
		
		abstract void rotate(final Object3D object, final float step);
	}
	
	private final Object3D object;
	
	private final Axis rotationAxis;
	
	private final long time;
	
	private boolean running = true;
	
	private long lasttime;

	public Rotator(final Object3D object, final Axis rotationAxis, final long time)
	{
		this.object = object;
		this.rotationAxis = rotationAxis;
		this.time = time;
	}
	
	public void start()
	{
		Executors.newSingleThreadExecutor().execute(this);
	}
	
	@Override
	public void run()
	{
		while (this.running)
		{
			final long delta = (System.currentTimeMillis() - lasttime) % time;
			final float step = (float) (Math.PI * 2.0 * delta / time);
			this.rotationAxis.rotate(this.object, step);
			this.lasttime = System.currentTimeMillis();
			
			try 
			{ 
				Thread.sleep(25);
			} 
			catch (final InterruptedException e)
			{
				throw new RuntimeException("Rotation thread interrupted", e);
			}
		}
	}
	
	public void stop()
	{
		this.running = false;
	}
}
