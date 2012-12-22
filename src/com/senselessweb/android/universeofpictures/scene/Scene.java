package com.senselessweb.android.universeofpictures.scene;

import java.util.Collection;

import com.threed.jpct.Camera;
import com.threed.jpct.Object3D;

public interface Scene
{

	public void addObject3D(final Object3D object);

	public void addObject3Ds(final Collection<Object3D> object);
	
	public Camera getCamera();
}
