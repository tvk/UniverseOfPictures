package com.senselessweb.android.universeofpictures.scene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;

public class PictureTextures
{
	
	public static final PictureTextures instance = new PictureTextures();

	private final File[] files;
	
	private final String[] textures;
	
	private int current = 0;
	
	@SuppressLint("DefaultLocale")
	private PictureTextures()
	{
		final File picturesDir = new File(Environment.getExternalStorageDirectory(), "example-images");
		this.files = picturesDir.listFiles(new FileFilter()
		{
			
			@Override
			public boolean accept(final File file)
			{
				return file.isFile() && file.getName().toLowerCase().endsWith("jpg");
			}
		});
		this.textures = new String[this.files.length];
	}
	
	public String nextTexture()
	{
		if (this.textures[this.current] == null)
		{
			try
			{
				final FileInputStream fis = new FileInputStream(this.files[this.current]);
				final Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.loadImage(fis), 128, 128));
				final String name =  "picturetexture-" + this.current;
				TextureManager.getInstance().addTexture(name, texture);
				this.textures[this.current] = name;
			} 
			catch (final Exception e)
			{
				throw new RuntimeException("Could not load texture from file " + this.files[this.current], e);
			}
		}
		
		final String result = this.textures[this.current];
		this.current++;
		if (this.current >= this.files.length) this.current = 0;
		
		return result;
	}
	
	
}
