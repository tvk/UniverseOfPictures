package com.senselessweb.android.universeofpictures.scene.objects;

import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.service.TextureLoader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;

public class RenderablePicture extends Object3D implements TouchableObject
{

	private static final long serialVersionUID = -3909155293063304533L;
	
	private final AlbumPicture picture;
	
	private boolean textureLoaded = false;
	
	public RenderablePicture(final AlbumPicture picture, final float distance, final float angle)
	{
		super(Primitives.getPlane(4, 1f));
		this.picture = picture;
		
		// Apply transformations
		this.rotateX((float) (Math.PI / 2.0));
		this.rotateY(angle);
		final Matrix m = new Matrix();
		m.translate(distance, 0, 0);
		m.rotateY(angle);
		this.setTranslationMatrix(m);
		
		this.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
	}

	@Override
	public void handleTouchEvent()
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
				TextureLoader.getInstance().applyTexture(this, picture);
				this.textureLoaded = true;
			}
		}
		
		super.setVisibility(mode);
	}
	
}
