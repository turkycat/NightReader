package mdc.collab.nightreader.application;

/**
 * @author Jesse Frush
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.activities.MainActivity;
import mdc.collab.nightreader.util.AudioFileGroup;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Application;
import android.graphics.Typeface;
import android.media.MediaPlayer;

/**
 * this class acts very much like a singleton, without the static getInstance
 * method. it is an extension of the Application class
 * 
 * @author Jesse Frush
 * 
 */
public class NightReader extends Application
{
	//a static reference to the MediaPlayer object that will be used to play media
	private static MediaPlayer mediaPlayer;

	private Typeface applicationFont;
	private Sorting sortedBy;
	private MediaStatus status;
	
	//a collection of songs
	private ArrayList<AudioFileInfo> audioFiles;
	
	//a collection of artists
	AudioFileGroup artists;
	
	//a collection of albums
	AudioFileGroup albums;

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
	public synchronized AudioFileInfo getCurrentAudioFile()
	{
		return currentAudioFile;
	}

	/**
	 * will attempt to load and play the given Uri
	 */
	public synchronized void playMedia( AudioFileInfo file )
	{
		if( file == null || file.uri == null ) return;

		stopMedia();

		currentAudioFile = file;
		mediaPlayer = MediaPlayer.create( getApplicationContext(), file.uri );

		status = MediaStatus.PLAYING;
		MainActivity.onMediaEvent( status );
		mediaPlayer.start();
	}

	/**
	 * stops the active player, if necessary
	 */
	public synchronized void stopMedia()
	{
		if( mediaPlayer != null )
		{
			mediaPlayer.stop();

			status = MediaStatus.STOP;
			MainActivity.onMediaEvent( status );
		}
	}

	/**
	 * stops the active player, if necessary
	 */
	public synchronized void pauseOrResumeMedia()
	{
		if( mediaPlayer == null ) return;

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
	
	
	/**
	 * returns true if the mediaplayer is currently active and playing
	 */
	public synchronized boolean isMediaPlaying()
	{
		return mediaPlayer != null && mediaPlayer.isPlaying();
	}

	/**
	 * returns the current percentage of the song being played
	 */
	public synchronized MediaPlayer getCurrentMediaPlayer()
	{
		return mediaPlayer;
	}

	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized void setAudioFileItems( ArrayList<AudioFileInfo> allFiles, AudioFileGroup artists, AudioFileGroup albums )
	{
		this.audioFiles = allFiles;
		this.artists = artists;
		this.albums = albums;
		sortAudioFiles( Sorting.SONG, audioFiles );
	}

	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized ArrayList<AudioFileInfo> getAudioFileList()
	{
		return audioFiles;
	}

	/**
	 * determines if the audio file list has already been loaded
	 */
	public synchronized boolean isAudioFileListLoaded()
	{
		return audioFiles != null;
	}

	/**
	 * returns the Typeface used by this application for custom titles and other
	 * text items.
	 */
	public synchronized Typeface getApplicationTypeface()
	{
		return applicationFont;
	}

	/**
	 * returns the sorting type which the audio files are currently arranged by
	 */
	public synchronized Sorting getSorting()
	{
		return sortedBy;
	}

	/**
	 * sorts the list of songs given a requested sorting type
	 */
	public synchronized void sortAudioFiles( Sorting requestedSort, ArrayList<AudioFileInfo> toSort )
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
		if( toSort == audioFiles ) sortedBy = requestedSort;
	}
}
