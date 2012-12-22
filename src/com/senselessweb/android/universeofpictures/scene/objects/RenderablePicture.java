package com.senselessweb.android.universeofpictures.scene.objects;

import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.scene.Scene;
import com.senselessweb.android.universeofpictures.service.TextureService;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;

public class RenderablePicture extends Object3D implements TouchableObject
{

	private static final long serialVersionUID = -3909155293063304533L;
	
	private final TextureService textureService;
	
	private final AlbumPicture picture;
	
	private boolean textureLoaded = false;
	
	private final Object3D backside;
	
	public RenderablePicture(final Scene scene, final TextureService textureService, 
			final AlbumPicture picture, final float distance, final float angle)
	{
		super(Primitives.getPlane(2, 1f));
		this.textureService = textureService;
		
		this.backside = Primitives.getPlane(2, 1f);
		this.backside.rotateY((float) Math.PI);
		this.backside.setTexture("backside");
		this.addChild(this.backside);
		
		this.picture = picture;
		
		// Apply transformations
		this.rotateX((float) (Math.PI / 2.0));
		this.rotateY(angle);
		final Matrix m = new Matrix();
		m.translate(distance, 0, 0);
		m.rotateY(angle);
		this.setTranslationMatrix(m);
		
		this.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		
		scene.addObject3D(this);
		scene.addObject3D(backside);
	}

	@Override
	public void handleTouchEvent()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void notifyAnotherObjectHasBeenTouched(final TouchableObject object)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setVisibility(final boolean mode)
	{
		if (mode) 
		{
			if (!this.textureLoaded)
			{
				this.textureService.applyTexture(this, picture);
				this.textureLoaded = true;
			}
		}
		
		super.setVisibility(mode);
		this.backside.setVisibility(mode);
	}
	
}
