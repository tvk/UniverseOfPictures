package com.senselessweb.android.universeofpictures.scene.objects;

public interface TouchableObject
{

	public void handleTouchEvent(); 
	
	public void notifyAnotherObjectHasBeenTouched();
}
