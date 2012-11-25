package com.senselessweb.android.universeofpictures.scene;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;

public class Picture extends Object3D
{
	private static final int pictureScale = 4;

	private static final int pictureDistance = 2 * pictureScale + 2;

	private static final long serialVersionUID = 9018072087016918351L;

	public Picture(final RenderableScene parent, final int orbitIndex, final float angle)
	{
		super(Primitives.getPlane(pictureScale * 2, 1f));
		parent.addObject(this);

		this.setTexture("testfoto");
		
		// These transformations don't make sense in my opinion, but there're working :)
		this.rotateX((float) (Math.PI / 2.0));
		this.rotateY(angle);
		final Matrix m = new Matrix();
		m.translate(140 + orbitIndex * pictureDistance, 0, 0);
		m.rotateY(angle);

		this.setTranslationMatrix(m);
		
		this.build();
		this.compile();
		
		this.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);

	}
}
