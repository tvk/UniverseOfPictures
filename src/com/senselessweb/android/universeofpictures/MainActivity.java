package com.senselessweb.android.universeofpictures;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.senselessweb.android.universeofpictures.scene.RenderableScene;
import com.senselessweb.android.universeofpictures.service.AnimatedCamera;
import com.threed.jpct.Logger;


public class MainActivity extends RoboActivity
{
	
	private GLSurfaceView mGLView;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		Logger.log("onCreate");
		super.onCreate(savedInstanceState);

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
		
		this.mGLView.setRenderer(RoboGuice.getInjector(this.getApplicationContext()).getInstance(RenderableScene.class));
		
		this.setContentView(this.mGLView);
		this.addContentView(this.getLayoutInflater().inflate(R.layout.activity_main, null), 
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
		final ImageView imageView = (ImageView) this.findViewById(R.id.imageView1);
		
		if (imageView.getVisibility() == View.VISIBLE)
			imageView.setVisibility(View.INVISIBLE);
		else
			RoboGuice.getInjector(this.getApplicationContext()).getInstance(RenderableScene.class).onTouch(this.mGLView, event, imageView);
		return true;
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	public void onBackPressed()
	{
		RoboGuice.getInjector(this.getApplicationContext()).getInstance(AnimatedCamera.class).moveToStartPosition();
	}

	protected boolean isFullscreenOpaque()
	{
		return true;
	}
}
