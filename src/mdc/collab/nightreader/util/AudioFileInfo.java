package mdc.collab.nightreader.util;

/**
 * @author Jesse Frush
 */

import java.net.URI;

import android.graphics.Bitmap;
import android.net.Uri;

public class AudioFileInfo
{
	public Uri uri;
	public String rawPath;
	public String artist;
	public String album;
	public String title;
	public String year;
	public String genre;
	public Uri albumArtUri;
	public Bitmap albumArt;
	public int duration;
	
	@Override
	public String toString()
	{
		if( title == null && artist == null ) return "Unknown Audio File";
		else if( title == null ) return "Unknown Title - " + artist;
		else if( artist == null ) return title + " - Unknown Artist";
		return title + " - " + artist;
	}
	
	
	public String getSongTitle()
	{
		return title == null ? "Unknown Title" : title;
	}
	
	
	public String getArtistName()
	{
		return artist == null ? "Unknown Artist" : artist;
	}
	
	
	public String getAlbumName()
	{
		return album == null ? "Unknown Album" : album;
	}
	
	
	public String getGenre()
	{
		return genre == null ? "Unknown Genre" : genre;
	}
}