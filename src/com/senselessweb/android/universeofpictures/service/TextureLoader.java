package com.senselessweb.android.universeofpictures.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.scene.objects.RenderablePicture;

public class TextureLoader
{
	
	private static final TextureLoader instance = new TextureLoader();
	
	private final ExecutorService worker = Executors.newFixedThreadPool(2);

	private TextureLoader() { }
	
	public void applyTexture(final RenderablePicture object, final AlbumPicture picture)
	{
		this.worker.submit(new TextureLoadingOperation(object, picture));
	}
	
	public static TextureLoader getInstance()
	{
		return instance;
	}
}
