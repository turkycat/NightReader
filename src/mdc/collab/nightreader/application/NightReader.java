package mdc.collab.nightreader.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.activities.MainActivity;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class NightReader extends Application
{
	private Typeface applicationFont;
	private ArrayList<AudioFileInfo> audioFiles;
	private Sorting sortedBy;
	
	public enum Sorting
	{
		NONE,
		SONG,
		ARTIST,
		ALBUM,
		GENRE,
	};
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/MAYBE MAYBE NOT.TTF" );
		sortedBy = Sorting.NONE;
	}
	
	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public void setAudioFileList( ArrayList<AudioFileInfo> audioFiles )
	{
		this.audioFiles = audioFiles;
		sortAudioFilesBySongTitle();
	}
	
	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public ArrayList<AudioFileInfo> getAudioFileList()
	{
		return audioFiles;
	}
	
	
	/**
	 * determines if the audio file list has already been loaded
	 */
	public boolean isAudioFileListLoaded()
	{
		return audioFiles != null;
	}
	
	
	/**
	 * returns the Typeface used by this application for custom titles and other text items.
	 */
	public Typeface getApplicationTypeface()
	{
		return applicationFont;
	}
	
	
	/**
	 * returns the sorting type which the audio files are currently arranged by
	 */
	public Sorting getSorting()
	{
		return sortedBy;
	}
	
	

	/**
	 * sorts the list of songs by name
	 */
	public void sortAudioFilesBySongTitle()
	{
		if( !isAudioFileListLoaded() ) return;
		
		Collections.sort( audioFiles, new Comparator<AudioFileInfo>(){
			@Override
			public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
			{
				return lhs.getSongTitle().compareTo( rhs.getSongTitle() );
			}
		});
		
		sortedBy = Sorting.SONG;
	}
	
	

	/**
	 * sorts the list of songs by name
	 */
	public void sortAudioFilesByArtist()
	{
		if( !isAudioFileListLoaded() ) return;
		
		Collections.sort( audioFiles, new Comparator<AudioFileInfo>(){
			@Override
			public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
			{
				return lhs.getArtistName().compareTo( rhs.getArtistName() );
			}
		});
		
		sortedBy = Sorting.ARTIST;
	}
}
