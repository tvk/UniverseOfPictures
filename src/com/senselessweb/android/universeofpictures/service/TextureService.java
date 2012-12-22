package com.senselessweb.android.universeofpictures.service;

import com.senselessweb.android.universeofpictures.domain.AlbumPicture;
import com.senselessweb.android.universeofpictures.scene.objects.RenderablePicture;

public interface TextureService
{

	public void applyTexture(final RenderablePicture object, final AlbumPicture picture);

}
