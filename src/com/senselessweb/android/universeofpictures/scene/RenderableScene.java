package com.senselessweb.android.universeofpictures.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.senselessweb.android.universeofpictures.R;
import com.senselessweb.android.universeofpictures.common.Point;
import com.senselessweb.android.universeofpictures.scene.objects.RenderableAlbum;
import com.senselessweb.android.universeofpictures.scene.objects.TouchableObject;
import com.senselessweb.android.universeofpictures.service.AlbumService;
import com.senselessweb.android.universeofpictures.service.AnimatedCamera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;

@Singleton
public class RenderableScene extends World implements Scene, Renderer
{

	private static final long serialVersionUID = -9075219920811753949L;
	
	private FrameBuffer frameBuffer = null;

	private final SkyBox skybox;

	private final Light sun;
	
	private final AlbumService albumService;
	
	private List<Point<Float>> lastTouchPosition = null;
	
	private TouchableObject lastTouchedObject = null;
	
	private boolean wasMoving = false;

	@Inject
	public RenderableScene(
			final AlbumService albumService,
			final Provider<Context> contextProvider,
			final AnimatedCamera camera)
	{
		Log.i(this.toString(), "init called...");
		
		this.albumService = albumService;
		
		final Texture skyTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(
				contextProvider.get().getResources().getDrawable(R.drawable.sky)), 2048, 2048));
		TextureManager.getInstance().addTexture("sky", skyTexture);
		this.skybox = new SkyBox("sky", "sky", "sky", "sky", "sky", "sky", 2000);

		this.sun = new Light(this);
		this.sun.setPosition(new SimpleVector(10000, 0, -2000));
		this.sun.setIntensity(255f, 255f, 255f);
		
		this.addObjects(this.albumService.getAlbums().toArray(new RenderableAlbum[0]));
		
		MemoryHelper.compact();
	}

	@Override
	public void onSurfaceChanged(final GL10 gl, final int width, final int height)
	{
		if (this.frameBuffer != null)
			this.frameBuffer.dispose();
		this.frameBuffer = new FrameBuffer(gl, width, height);
	}

	@Override
	public void onDrawFrame(final GL10 gl)
	{
		this.frameBuffer.clear(RGBColor.BLACK);
		this.skybox.render(this, this.frameBuffer);
		this.renderScene(this.frameBuffer);
		this.draw(this.frameBuffer);
		this.frameBuffer.display();
	}

	public synchronized void onTouch(final GLSurfaceView view, final MotionEvent event, final ImageView pictureView)
	{
		Log.d(this.toString(), "TouchEvent: " + event);
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			this.recordTouchPosition(event);
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2)
		{
			if (this.lastTouchPosition != null && this.lastTouchPosition.size() == 2)
			{
				final float dxOld = this.lastTouchPosition.get(1).getX() - this.lastTouchPosition.get(0).getX();
				final float dyOld = this.lastTouchPosition.get(1).getY() - this.lastTouchPosition.get(0).getY();
				final float angleOld = (float) Math.atan(dyOld / dxOld);
				
				final float dxNew = event.getX(1) - event.getX(0);
				final float dyNew = event.getY(1) - event.getY(0);
				final float angleNew = (float) Math.atan(dyNew / dxNew);
				
				final float angleDiff = angleNew - angleOld;

				Log.i(this.toString(), "Rotating camera by " + angleDiff + " angleOld: " + angleOld + ", angleNew: " + angleNew);
				if (!Float.isNaN(angleDiff)) this.getCamera().rotateCameraZ(angleDiff);
			}
			
			this.recordTouchPosition(event);
			this.wasMoving = true;
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if (this.lastTouchPosition != null)
			{
				final Point<Float> newPosition = new Point<Float>(event.getX(), event.getY());
				final float dx = this.lastTouchPosition.get(0).getX() - newPosition.getX();
				final float dy = this.lastTouchPosition.get(0).getY() - newPosition.getY();
				
				this.getCamera().rotateCameraY(dx / 300.0f);
				this.getCamera().rotateCameraX(dy / 300.0f);
			}
			
			this.recordTouchPosition(event);
			this.wasMoving = true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			if (this.wasMoving)
			{
				this.wasMoving = false;
				return;
			}

			// Find the touched object
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final SimpleVector dir = Interact2D.reproject2D3DWS(this.getCamera(), this.frameBuffer, x, y).normalize();
			final Object[] res = this.calcMinDistanceAndObject3D(this.getCamera().getPosition(), dir, 100000);
			Log.i(this.toString(), "Located object (Dir: " + dir + "): " + Arrays.toString(res));
			
			// Touchable objects handle events by their self
			if (res[1] instanceof TouchableObject && this.lastTouchedObject != res[1])
			{
				if (this.lastTouchedObject != null)
					this.lastTouchedObject.notifyAnotherObjectHasBeenTouched((TouchableObject) res[1]);
				this.lastTouchedObject = (TouchableObject) res[1];
				this.lastTouchedObject.handleTouchEvent();
			}
			
			this.lastTouchPosition = null;
		}
	}
	
	private void recordTouchPosition(final MotionEvent event)
	{
		this.lastTouchPosition = new ArrayList<Point<Float>>();
		for (int i = 0; i < event.getPointerCount(); i++)
			this.lastTouchPosition.add(new Point<Float>(event.getX(i), event.getY(i)));
	}
	
	public void onBackPressed()
	{
		for (final RenderableAlbum album : this.albumService.getAlbums())
			album.setPicturesVisibilityState(false);
	}
	
	@Override
	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) { }
	
	@Override
	public void addObject3D(final Object3D object)
	{
		this.addObject(object);
	}
	
	public void addObject3Ds(final Collection<Object3D> objects) 
	{
		this.addObjects(objects.toArray(new Object3D[objects.size()])); 
	}
	
}
