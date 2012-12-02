package com.senselessweb.android.universeofpictures.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import android.content.Context;
import android.os.Environment;

import com.senselessweb.android.universeofpictures.R;
import com.senselessweb.android.universeofpictures.domain.Album;
import com.senselessweb.android.universeofpictures.domain.LocalFolderAlbum;
import com.senselessweb.android.universeofpictures.scene.objects.RenderableAlbum;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

public class AlbumService
{
	
	private static final int[] textureIds = new int[] {
		R.drawable.texture_planet_1,
		R.drawable.texture_planet_2,
		R.drawable.texture_planet_3,
		R.drawable.texture_planet_4,
		R.drawable.texture_planet_5
	};
	
	private static final Album dummyAlbum = new LocalFolderAlbum(new File(Environment.getExternalStorageDirectory(), "example-images"));

	private final Random random = new Random();
	
	private final Collection<RenderableAlbum> albums = new ArrayList<RenderableAlbum>();
	
	public AlbumService(final Context context, final World world) 
	{
		// Load textures
		for (int i = 0; i < textureIds.length; i++)
		{
			final Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(
					context.getResources().getDrawable(textureIds[i])), 512, 512));
			TextureManager.getInstance().addTexture("texture_planet_" + i, texture);
		}
		final Texture backsideTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(
				context.getResources().getDrawable(R.drawable.backside)), 512, 512));
		TextureManager.getInstance().addTexture("backside", backsideTexture);

		// Create albums
		for (int i = 0; i < 12; i++)
		{
			final String texture = "texture_planet_" + (i % textureIds.length);
			SimpleVector position = null;

			// Check if we collide with another planet 
			while (position == null)
			{
				final SimpleVector tempPosition = new SimpleVector(0, 0, 300 + random.nextInt(100));
				tempPosition.rotateX((float) (Math.random() * Math.PI * 2));
				tempPosition.rotateY((float) (Math.random() * Math.PI * 2));
				
				boolean positionOk = true;
				for (final RenderableAlbum album : this.albums)
					if (album.getTransformedCenter().distance(tempPosition) < 200f)
						positionOk = false;
				
				if (positionOk) position = tempPosition;
			}
			
			this.albums.add(new RenderableAlbum(world, dummyAlbum, texture, position));
		}
		
	}
	
	public Collection<RenderableAlbum> getAlbums()
	{
		return this.albums;
	}
}
