package mdc.collab.nightreader.singleton;

import java.io.IOException;
import java.util.ArrayList;

import mdc.collab.nightreader.activities.MainActivity;
import mdc.collab.nightreader.application.NightReader.SortingMode;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

/**
 * A singleton class which maintains the overall state of audio-related media
 * within the application
 * 
 * @author Jesse Frush
 */
public class MediaState
{
	private static MediaState instance;

	public enum MediaStatus
	{
		NONE, PLAYING, PAUSED, STOP
	};

	//a static reference to the MediaPlayer object that will be used to play media
	private static MediaPlayer mediaPlayer;

	//a static reference to the application context
	private static Context context;

	//the current status of the application's media player
	//private MediaStatus status;

	//the current playlist
	private ArrayList<AudioFileInfo> playlist;

	//the current audio file being played
	private AudioFileInfo currentAudioFile;

	//the current position within the playlist
	private int currentPlaylistPosition;
	
	private boolean isPaused = false;
	
	//-------------------------------------------------------------------------media related methods


	/**
	 * will attempt to load and play the given Uri
	 */
	public synchronized void playMedia( ArrayList<AudioFileInfo> list, int position )
	{
		if( list == null || list.size() <= position ) return;

		//stop the current media
		stopMedia();
		
		//update the state-tracking variables
		playlist = list;

		initializeAndPlayMedia( position);
				
		//status = MediaStatus.PLAYING;
		
		MainActivity.resetUI();
	}

	
	/**
	 * stops the active player, if necessary
	 */
	public synchronized void stopMedia()
	{
		if( mediaPlayer.isPlaying() || isPaused )
		{
			mediaPlayer.stop();
			mediaPlayer.reset();
			isPaused = false;
			//status = MediaStatus.STOP;
			MainActivity.resetUI();
		}
	}

	/**
	 * stops the active player, if necessary
	 * @return true if the player is now paused, false otherwise
	 */
	public synchronized boolean pauseOrResumeMedia()
	{
		if( mediaPlayer == null ) return false;

		if( mediaPlayer.isPlaying() )// && status == MediaStatus.PLAYING )
		{
			mediaPlayer.pause();
			isPaused = true;
			//status = MediaStatus.PAUSED;
		}
		else if( isPaused )
		{
			mediaPlayer.start();
			isPaused = false;
			//if( status == MediaStatus.STOP )
			//{
			//stopMedia();
				//initializeAndPlayMedia( currentPlaylistPosition );
			//}
			//status = MediaStatus.PLAYING;
		}
		else
		{
			initializeAndPlayMedia( currentPlaylistPosition );
		}
		
		MainActivity.resetUI();
		return isPaused;
	}
	
	
	/**
	 * advances the playlist to the next track
	 */
	public synchronized void nextTrack()
	{
		if( playlist != null )
		{
			int next = currentPlaylistPosition + 1;
			if( next >= playlist.size() )
			{
				next = 0;
			}
			if( next != currentPlaylistPosition )
			{
				mediaPlayer.stop();
				mediaPlayer.reset();
				initializeAndPlayMedia( next );
				
				mediaPlayer.start();
				currentPlaylistPosition = next;
				MainActivity.resetUI();
			}
		}
	}
	
	
	/**
	 * returns the playlist to the previous track
	 */
	public synchronized void previousTrack()
	{
		if( playlist != null )
		{
			int previous = currentPlaylistPosition - 1;
			if( previous < 0 )
			{
				previous = playlist.size() - 1;
			}
			if( previous != currentPlaylistPosition )
			{
				mediaPlayer.stop();
				mediaPlayer.reset();
				initializeAndPlayMedia( previous );
				
				mediaPlayer.start();
				currentPlaylistPosition = previous;
				MainActivity.resetUI();
			}
		}
	}
	
	
	
	//--------------------------------------------------------------determinate methods

	
	/**
	 * returns true if the mediaplayer is currently active and playing
	 */
	public synchronized boolean isMediaPlaying()
	{
		return mediaPlayer != null && mediaPlayer.isPlaying();
	}
	
	/**
	 * returns true if the mediaplayer is currently active and playing
	 */
	public synchronized boolean isPlaylistLoaded()
	{
		return playlist != null;
	}
	

	//--------------------------------------------------------------getters and setters

	/**
	 * returns the sorting type which the audio files are currently arranged by
	 */
	//public synchronized Sorting getSorting()
	//{
	//	return sorting;
	//}

	/**
	 * returns the current audio file being played
	 */
	public synchronized AudioFileInfo getCurrentAudioFile()
	{
		return currentAudioFile;
	}

	/**
	 * returns the current percentage of the song being played
	 */
	public synchronized MediaPlayer getCurrentMediaPlayer()
	{
		return mediaPlayer;
	}
	
	
	
	/**
	 * a helper method which will initialize and play a media file
	 */
	private void initializeAndPlayMedia( int position )
	{
		currentPlaylistPosition = position;
		currentAudioFile = playlist.get( position );
		try
		{
			mediaPlayer.setDataSource( context, currentAudioFile.uri );
			mediaPlayer.prepare();
			mediaPlayer.start();
			isPaused = false;
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( SecurityException e )
		{
			e.printStackTrace();
		}
		catch( IllegalStateException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	//--------------------------------------------------------------singleton pattern

	public static MediaState getInstance()
	{
		synchronized( MediaState.class )
		{
			if( instance == null )
			{
				instance = new MediaState();
			}
			return instance;
		}
	}
	
	public static void setContext( Context context )
	{
		synchronized( MediaState.class )
		{
			MediaState.context = context;
		}
	}

	/**
	 * a private constructor
	 */
	private MediaState()
	{
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener( new OnCompletionListener()
		{
			@Override
			public void onCompletion( MediaPlayer mp )
			{
				nextTrack();
			}
		} );
		//status = MediaStatus.NONE;
		currentAudioFile = null;
		playlist = null;
		currentPlaylistPosition = -1;
	}
}
