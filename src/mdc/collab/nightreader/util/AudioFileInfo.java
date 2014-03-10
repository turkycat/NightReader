package mdc.collab.nightreader.util;

public class AudioFileInfo
{
	public String rawPath;
	public String artist;
	public String album;
	public String title;
	public String year;
	public String albumArtUri;
	public String albumArt;
	public int duration;
	
	@Override
	public String toString()
	{
		if( title == null && artist == null ) return "Unknown Audio File";
		else if( title == null ) return "Unknown Title - " + artist;
		else if( artist == null ) return title + " - Unknown Artist";
		return title + " - " + artist;
	}
}