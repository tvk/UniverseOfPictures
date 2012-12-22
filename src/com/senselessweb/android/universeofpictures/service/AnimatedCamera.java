package com.senselessweb.android.universeofpictures.service;

import com.threed.jpct.SimpleVector;

public interface AnimatedCamera
{

	public void moveTo(final SimpleVector newPosition, final SimpleVector newDirection);

	public void moveToStartPosition();

}
