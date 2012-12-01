package com.senselessweb.android.universeofpictures.domain;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class LocalFolderAlbum extends Album
{
	
	private static FileFilter picturesFilter = new FileFilter()
	{
		@Override
		public boolean accept(final File file)
		{
			return file.isFile() && file.getName().toLowerCase(Locale.ENGLISH).endsWith("jpg");
		}
	};
	
	public static Function<File, AlbumPicture> fileToAlbumPicture = new Function<File, AlbumPicture>()
			{
		public LocalPicture apply(final File file) 
		{
			return new LocalPicture(file); 
		}
	};

	public LocalFolderAlbum(final File folder)
	{
		super(folder.getName(), Collections2.transform(
				Lists.newArrayList(folder.listFiles(picturesFilter)), fileToAlbumPicture)); 
	}
}
