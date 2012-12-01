package com.senselessweb.android.universeofpictures.domain;

import java.util.Collection;

public abstract class Album
{

	private final String title;
	
	private final Collection<AlbumPicture> pictures;
	
	public Album(final String title, final Collection<AlbumPicture> pictures)
	{
		this.title = title;
		this.pictures = pictures;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public Collection<AlbumPicture> getPictures()
	{
		return pictures;
	}
	
}
