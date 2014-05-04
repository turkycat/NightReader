package mdc.collab.nightreader.singleton;

import java.io.IOException;
import java.util.ArrayList;

import mdc.collab.nightreader.activities.MainActivity;
import mdc.collab.nightreader.application.NightReader.Sorting;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

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
	private MediaStatus status;

	//the current playlist
	private ArrayList<AudioFileInfo> playlist;

	//the current sorting mode
	private Sorting sorting;

	//the current audio file being played
	private AudioFileInfo currentAudioFile;

	//the current position within the playlist
	private int currentPlaylistPosition;
	
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
		currentPlaylistPosition = position;
		currentAudioFile = list.get( position );

		status = MediaStatus.PLAYING;
		
		//TODO
		MainActivity.onMediaEvent( status );
		//TODO
		
		mediaPlayer.start();
	}

	
	/**
	 * stops the active player, if necessary
	 */
	public synchronized void stopMedia()
	{
		if( mediaPlayer.isPlaying() )
		{
			mediaPlayer.stop();

			status = MediaStatus.STOP;
			
			//TODO
			MainActivity.onMediaEvent( status );
			//TODO
			
			
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
	
	
	
	//--------------------------------------------------------------determinate methods

	
	/**
	 * returns true if the mediaplayer is currently active and playing
	 */
	public synchronized boolean isMediaPlaying()
	{
		return mediaPlayer != null && mediaPlayer.isPlaying();
	}
	

	//--------------------------------------------------------------getters and setters

	/**
	 * returns the sorting type which the audio files are currently arranged by
	 */
	public synchronized Sorting getSorting()
	{
		return sorting;
	}

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
	 * receives callbacks & sets up the next song in the playlist depending
	 * 	on the current state of media
	 */
	private class SongCompletionListener implements OnCompletionListener
	{

		@Override
		public void onCompletion( MediaPlayer mp )
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
					try
					{
						mediaPlayer.stop();
						mediaPlayer.reset();
						currentAudioFile = playlist.get( next );
						mediaPlayer.setDataSource( context, currentAudioFile.uri );
						mediaPlayer.prepare();
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
					
					mediaPlayer.start();
					MainActivity.onMediaEvent( status );
					currentPlaylistPosition = next;
				}
			}
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
		mediaPlayer.setOnCompletionListener( new SongCompletionListener() );
		status = MediaStatus.NONE;
		sorting = Sorting.NONE;
		currentAudioFile = null;
		currentPlaylistPosition = -1;
	}
}
