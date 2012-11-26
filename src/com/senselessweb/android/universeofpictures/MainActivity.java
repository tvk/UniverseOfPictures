package com.senselessweb.android.universeofpictures;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.senselessweb.android.universeofpictures.scene.Picture;
import com.senselessweb.android.universeofpictures.scene.RenderableScene;
import com.threed.jpct.Logger;

public class MainActivity extends Activity
{

	private GLSurfaceView mGLView;

	private static RenderableScene scene;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		Logger.log("onCreate");
		super.onCreate(savedInstanceState);

		if (scene == null)
			scene = new RenderableScene(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.mGLView = new GLSurfaceView(this.getApplication());
		this.mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser()
		{
			@Override
			public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display)
			{
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				final int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				final EGLConfig[] configs = new EGLConfig[1];
				final int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});

		this.mGLView.setRenderer(scene);
		this.setContentView(this.mGLView);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		this.mGLView.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		this.mGLView.onResume();
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		final Picture picture = scene.findTouchedPicture((int) event.getX(), (int) event.getY());
		
		if (picture == null)
		{
			scene.startCameraAnimation();
		}
		else
		{
			// TODO
		}
		
		return true;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}

	protected boolean isFullscreenOpaque()
	{
		return true;
	}

}
