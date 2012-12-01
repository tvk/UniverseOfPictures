package com.senselessweb.android.universeofpictures.scene;

import java.util.Arrays;

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
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;

public class RenderableScene extends World implements Renderer
{

	private static final long serialVersionUID = -9075219920811753949L;

	private FrameBuffer frameBuffer = null;

	private final SkyBox skybox;

	private final Light sun;
	
	private final AlbumService albumService;
	
	private Point<Float> lastTouchPosition = null;

	public RenderableScene(final Context context)
	{
		
		final Texture skyTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.sky)), 512, 512));
		TextureManager.getInstance().addTexture("sky", skyTexture);
		this.skybox = new SkyBox("sky", "sky", "sky", "sky", "sky", "sky", 100000);

		this.sun = new Light(this);
		this.sun.setPosition(new SimpleVector(10000, 0, -2000));
		this.sun.setIntensity(255f, 255f, 255f);
		
		this.albumService = new AlbumService(context, this);
		this.addObjects(this.albumService.getAlbums().toArray(new RenderableAlbum[0]));
		
		AnimatedCamera.init(this.getCamera());
		
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
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			this.lastTouchPosition = new Point<Float>(event.getX(), event.getY());
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			final Point<Float> newPosition = new Point<Float>(event.getX(), event.getY());
			final float dx = this.lastTouchPosition.getX() - newPosition.getX();
			final float dy = this.lastTouchPosition.getY() - newPosition.getY();
			
			this.getCamera().rotateCameraY(dx / 300.0f);
			this.getCamera().rotateCameraX(dy / 300.0f);
			
			this.lastTouchPosition = newPosition;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			// Find the touched object
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final SimpleVector dir = Interact2D.reproject2D3DWS(this.getCamera(), this.frameBuffer, x, y).normalize();
			final Object[] res = this.calcMinDistanceAndObject3D(this.getCamera().getPosition(), dir, 100000);
			Log.i("RenderableScene", "Located object (Dir: " + dir + "): " + Arrays.toString(res));
			
			// Touchable objects handle events by their self
			if (res[1] instanceof TouchableObject)
				((TouchableObject) res[1]).handleTouchEvent();
		}
	}
	
	public void onBackPressed()
	{
		AnimatedCamera.getInstance().moveToStartPosition();
		
		for (final RenderableAlbum album : this.albumService.getAlbums())
			album.setPicturesVisibilityState(false);
	}
	
	@Override
	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) { }
	
	
}
