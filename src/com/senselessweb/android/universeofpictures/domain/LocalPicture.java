package com.senselessweb.android.universeofpictures.domain;

import java.io.File;


public class LocalPicture extends AlbumPicture
{
	
	private final File file;

	public LocalPicture(final File file)
	{
		this.file = file;
	}
	
	public File getFile()
	{
		return file;
	}
	
	@Override
	public void apply(PictureOperation operation)
	{
		operation.apply(this);
	}
}
