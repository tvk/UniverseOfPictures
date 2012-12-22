package com.senselessweb.android.universeofpictures.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import android.util.Log;

import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.domain.LocalPicture;
import com.senselessweb.android.universeofpictures.domain.PictureOperation;
import com.senselessweb.android.universeofpictures.scene.objects.RenderablePicture;
import com.senselessweb.android.universeofpictures.service.TextureService;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;

@Singleton
public class TextureServiceImpl implements TextureService
{
	private final ExecutorService worker = Executors.newFixedThreadPool(2);

	public void applyTexture(final RenderablePicture object, final AlbumPicture picture)
	{
		this.worker.submit(new TextureLoadingOperation(object, picture));
	}
}


class TextureLoadingOperation implements PictureOperation, Runnable
{
	
	private static final AtomicInteger counter = new AtomicInteger();
	
	private final RenderablePicture object;
	
	private final AlbumPicture picture;
	
	TextureLoadingOperation(final RenderablePicture object, final AlbumPicture picture)
	{
		this.picture = picture;
		this.object = object;
	}

	@Override
	public void apply(final LocalPicture localPicture)
	{
		final File file = localPicture.getFile();
		final String name =  "picturetexture-" + counter.incrementAndGet();
		Log.d("TextureLoader", "Loading texture for file " + file);
		try
		{
			final FileInputStream fis = new FileInputStream(file);
			final Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.loadImage(fis), 32, 32));
			TextureManager.getInstance().addTexture(name, texture);
			this.object.setTexture(name);
			
			try {fis.close();} catch (final IOException e) { /* ignore */ }
		}
		catch (final Exception e)
		{
			Log.e("TextureLoader", "Could not load texture for file " + file, e);
		}
	}

	@Override
	public void run()
	{
		this.picture.apply(this);
	}

}