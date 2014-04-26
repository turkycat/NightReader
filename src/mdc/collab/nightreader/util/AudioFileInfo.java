package mdc.collab.nightreader.util;

/**
 * @author Jesse Frush
 */

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

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
	
	
	public Bitmap getAlbumArt( Context context )
	{
		Bitmap bitmap = null;
		
        try 
        {
            bitmap = MediaStore.Images.Media.getBitmap( context.getContentResolver(), albumArtUri);
            //bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        } 
        catch (FileNotFoundException e) // Song has no album art!
        {
            e.printStackTrace();
            //bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.audio_file);
        } 
        catch (IOException e) // Other exception
        {
            e.printStackTrace();
        }
        
        return bitmap;
	}
}