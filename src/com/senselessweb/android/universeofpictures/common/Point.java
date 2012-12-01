package com.senselessweb.android.universeofpictures.common;

public class Point<T extends Number>
{
	
	private final T x;
	
	private final T y;

	public Point(final T x, final T y)
	{
		this.x = x;
		this.y = y;
	}
	
	public T getX()
	{
		return this.x;
	}
	
	public T getY()
	{
		return this.y;
	}
}
