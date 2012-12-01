package com.senselessweb.android.universeofpictures.common;

public class Utils
{

	public static void sleepSafe(final long time)
	{
		try
		{
			Thread.sleep(time);
		} 
		catch (final InterruptedException e)
		{
			throw new RuntimeException("Animated vector interrupted", e);
		}
	}
}
