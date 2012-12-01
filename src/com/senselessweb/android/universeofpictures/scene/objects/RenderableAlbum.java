package com.senselessweb.android.universeofpictures.scene.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import android.util.Log;

import com.senselessweb.android.universeofpictures.common.Utils;
import com.senselessweb.android.universeofpictures.domain.Album;
import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.service.AnimatedCamera;
import com.senselessweb.android.universeofpictures.service.Rotator;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

public class RenderableAlbum extends Object3D implements TouchableObject
{
	
	private static final long serialVersionUID = 2150508392107968024L;
	
	private static final int picturesPerOrbitLane = 50;
	
	private final World world;
	
	private final Album album;
	
	private final Collection<RenderablePicture> renderablePictures = new ArrayList<RenderablePicture>();
	
	private final Rotator rotator;
	
	private Integer numberOfOrbitLanes = null;
	
	public RenderableAlbum(final World world, final Album album, final String texture, final SimpleVector translation)
	{
		super(Primitives.getSphere(60, 60));
		this.translate(translation);
		
		this.world = world;
		this.album = album;
		
		// Apply planet texture
		this.setTexture(texture);
		this.calcTextureWrapSpherical();
		
		// Start rotation
		this.rotator = new Rotator(this, Rotator.Axis.Y, 60000 + (int) (30000.0 * Math.random()));
		this.rotator.start();
		
		this.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		this.strip();
		this.build();
		this.compile();
	}
	
	private void distributePictures()
	{
		// We need at least 150 pictures to have a nice effect
		final List<AlbumPicture> pictures = new ArrayList<AlbumPicture>();
		while (pictures.size() < 150) pictures.addAll(this.album.getPictures());
		
		// Number of orbit lanes
		this.numberOfOrbitLanes = pictures.size() / picturesPerOrbitLane + 1;
		
		// Assign pictures on orbit lanes
		final List<List<AlbumPicture>> picturesOnOrbitLanes = new ArrayList<List<AlbumPicture>>(this.numberOfOrbitLanes);
		for (int i = 0; i < this.numberOfOrbitLanes; i++)
			picturesOnOrbitLanes.add(new ArrayList<AlbumPicture>());
		for (int i = 0; i < pictures.size(); i++)
			picturesOnOrbitLanes.get(i % this.numberOfOrbitLanes).add(pictures.get(i));
			
		// Distribute pictures on their orbit lane. 
		int orbitIndex = 0;
		for (final List<AlbumPicture> picturesOnOrbitLane : picturesOnOrbitLanes)
		{
			final float startAngle = (float) Math.random();
			for (int i = 0; i < picturesOnOrbitLane.size(); i++)
			{
				final float angle = startAngle + (float) (Math.PI * 2 * i / picturesOnOrbitLane.size());
				final RenderablePicture renderablePicture = new RenderablePicture(
						RenderableAlbum.this.world, picturesOnOrbitLane.get(i), 70 + orbitIndex * 5, angle);
				renderablePicture.setVisibility(false);
				RenderableAlbum.this.addChild(renderablePicture);
				RenderableAlbum.this.renderablePictures.add(renderablePicture);
			}
			orbitIndex++;
		}

		RenderableAlbum.this.strip();
		RenderableAlbum.this.build();
		RenderableAlbum.this.compile();
		
		Log.i("RenderableAlbum", "All renderable pictures for album " + RenderableAlbum.this.album + " loaded");
	}

	@Override
	public synchronized void handleTouchEvent()
	{
		Log.i("RenderableAlbum", "Planet '" + this.album + " has been touched.");
		
		if (this.numberOfOrbitLanes == null)
			this.distributePictures();
		
		this.setPicturesVisibilityState(true);
		
		AnimatedCamera.getInstance().moveTo(
				this.getTransformedCenter().calcAdd(new SimpleVector(70.0 + this.numberOfOrbitLanes * 1.5, -3, -30)), 
				new SimpleVector(0, 0, 1));
		
	}
	
	public void setPicturesVisibilityState(final boolean visible)
	{
		Executors.newSingleThreadExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				final List<RenderablePicture> pictures = 
						new ArrayList<RenderablePicture>(RenderableAlbum.this.renderablePictures);
				Collections.shuffle(pictures);
				
				for (final RenderablePicture picture : pictures)
				{
					picture.setVisibility(visible);
					Utils.sleepSafe(75);
				}
			}
		});
	}

	@Override
	public void notifyAnotherObjectHasBeenTouched()
	{
		this.setPicturesVisibilityState(false);
	}
}
