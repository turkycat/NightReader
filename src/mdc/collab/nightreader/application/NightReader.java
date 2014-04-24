package mdc.collab.nightreader.application;

/**
 * @author Jesse Frush
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.activities.MainActivity;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.GetChars;
import android.util.Log;

public class NightReader extends Application
{
	//a static reference to the MediaPlayer object that will be used to play media
	private static MediaPlayer mediaPlayer;
	
	private Typeface applicationFont;
	private ArrayList<AudioFileInfo> audioFiles;
	private Sorting sortedBy;
	private MediaStatus status;
	
	private AudioFileInfo currentAudioFile;

	public enum Sorting
	{
		NONE, SONG, ARTIST, ALBUM, GENRE
	};
	
	public enum MediaStatus
	{
		NONE, PLAYING, PAUSED, STOP
	};

	
	
	@Override
	public void onCreate()
	{
		super.onCreate();

		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/ABANDON.TTF" );
//		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/bellerose.ttf" );
//		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/nougat.ttf" );
		sortedBy = Sorting.NONE;
		status = MediaStatus.NONE;
		currentAudioFile = null;
	}
	
	
	
	/**
	 * returns the current audio file being played
	 */
	public AudioFileInfo getCurrentAudioFile()
	{
		return currentAudioFile;
	}
	

	
	/**
	 * will attempt to load and play the given Uri
	 */
	public void playMedia( AudioFileInfo file )
	{
		if( file == null || file.uri == null ) return;
		
		stopMedia();
		currentAudioFile = file;
		mediaPlayer = MediaPlayer.create( getApplicationContext(), file.uri );
		MainActivity.onMediaEvent( MediaStatus.PLAYING );
		mediaPlayer.start();
		status = MediaStatus.PLAYING;
	}
	
	
	/**
	 * stops the active player, if necessary
	 */
	public void stopMedia()
	{
		if( mediaPlayer != null )
		{
			mediaPlayer.stop();
			MainActivity.onMediaEvent( MediaStatus.STOP );
			status = MediaStatus.STOP;
		}
	}
	
	
	/**
	 * stops the active player, if necessary
	 */
	public void pauseOrResumeMedia()
	{
		if( mediaPlayer != null )
		{
			if( mediaPlayer.isPlaying() && status == MediaStatus.PLAYING )
			{
				mediaPlayer.pause();
				MainActivity.onMediaEvent( MediaStatus.PAUSED );
				status = MediaStatus.PAUSED;
			}
			else
			{
				if( status == MediaStatus.STOP )
				{
					try
					{
						mediaPlayer.prepare();
					}
					catch( IllegalStateException e )
					{
						//do nothing
						//e.printStackTrace();
					}
					catch( IOException e )
					{
						//do nothing
						//e.printStackTrace();
					}
					mediaPlayer.seekTo( 0 );
				}
				
				mediaPlayer.start();
				MainActivity.onMediaEvent( MediaStatus.PLAYING );
				status = MediaStatus.PLAYING;
			}
		}
	}
	
	
	public boolean isMediaPlaying()
	{
		if( mediaPlayer == null || !mediaPlayer.isPlaying() ) return false;
		return true;
	}
	
	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public void setAudioFileList( ArrayList<AudioFileInfo> audioFiles )
	{
		this.audioFiles = audioFiles;
		sortAudioFiles( Sorting.SONG );
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
	 * returns the Typeface used by this application for custom titles and other
	 * text items.
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
	 * sorts the list of songs given a requested sorting type
	 */
	public void sortAudioFiles( Sorting requestedSort )
	{
		if( !isAudioFileListLoaded() ) return;

		Comparator<AudioFileInfo> sortingComparator;
		switch( requestedSort )
		{
		default:
			requestedSort = Sorting.SONG;
			
		case SONG:
			sortingComparator = new Comparator<AudioFileInfo>()
			{
				@Override
				public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
				{
					return lhs.getSongTitle().compareTo( rhs.getSongTitle() );
				}
			};
			break;
			

		case ARTIST:
			sortingComparator = new Comparator<AudioFileInfo>()
			{
				@Override
				public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
				{
					return lhs.getArtistName().compareTo( rhs.getArtistName() );
				}
			};
			break;
			

		case ALBUM:
			sortingComparator = new Comparator<AudioFileInfo>()
			{
				@Override
				public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
				{
					return lhs.getArtistName().compareTo( rhs.getArtistName() );
				}
			};
			break;
			

		case GENRE:
			sortingComparator = new Comparator<AudioFileInfo>()
			{
				@Override
				public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
				{
					return lhs.getGenre().compareTo( rhs.getGenre() );
				}
			};
			break;			
			
		}

		Collections.sort( audioFiles, sortingComparator );
		sortedBy = requestedSort;
	}
}
