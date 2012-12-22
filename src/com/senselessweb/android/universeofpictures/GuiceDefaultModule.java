package com.senselessweb.android.universeofpictures;

import com.google.inject.AbstractModule;
import com.senselessweb.android.universeofpictures.scene.RenderableScene;
import com.senselessweb.android.universeofpictures.scene.Scene;
import com.senselessweb.android.universeofpictures.service.AlbumService;
import com.senselessweb.android.universeofpictures.service.AnimatedCamera;
import com.senselessweb.android.universeofpictures.service.TextureService;
import com.senselessweb.android.universeofpictures.service.impl.AlbumServiceImpl;
import com.senselessweb.android.universeofpictures.service.impl.AnimatedCameraImpl;
import com.senselessweb.android.universeofpictures.service.impl.TextureServiceImpl;

public class GuiceDefaultModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind(AnimatedCamera.class).to(AnimatedCameraImpl.class);
		bind(AlbumService.class).to(AlbumServiceImpl.class);
		bind(TextureService.class).to(TextureServiceImpl.class);
		bind(Scene.class).to(RenderableScene.class);
	}
}
