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
import android.media.MediaPlayer.OnCompletionListener;

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

	//the typeface used throughout this application
	private static Typeface applicationFont;
	
	//a collection of songs
	private ArrayList<AudioFileInfo> audioFiles;
	
	//a collection of artists
	ArrayList<AudioFileGroup> artists;
	
	//a collection of albums
	ArrayList<AudioFileGroup> albums;
	
	//the current sorting mode
	private Sorting sortedBy;
	
	//the current status enum of the app
	private MediaStatus status;
	
	//the current playlist
	private ArrayList<AudioFileInfo> playlist;
	
	//the current position within the playlist
	private int currentPlaylistPosition;

	//the current audio file being played
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
	public synchronized void playMedia( ArrayList<AudioFileInfo> list, int position )
	{
		if( list == null || list.size() <= position ) return;

		//stop the current media
		stopMedia();
		
		//update the state-tracking variables
		playlist = list;
		currentPlaylistPosition = position;
		currentAudioFile = list.get( position );

		//create the new media player and set up a new completion listener
		MediaPlayer next = MediaPlayer.create( getApplicationContext(), currentAudioFile.uri );
		next.setOnCompletionListener( new SongCompletionListener() );
		mediaPlayer = next;

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
	public synchronized void setAudioFileList( ArrayList<AudioFileInfo> allFiles )
	{
		this.audioFiles = allFiles;
		int size = audioFiles.size();
		

		//create a grouping for artists
		sortAudioFiles( Sorting.ARTIST, audioFiles );
		this.artists = new ArrayList<AudioFileGroup>();
		
		AudioFileGroup currentGroup = null;
		for( int i = 0; i < size; ++i )
		{
			//grab the next item to be categorized
			AudioFileInfo item = audioFiles.get( i );
			
			//compare the current artist with the ongoing artist grouping
			String artist = item.getArtistName();
			if( currentGroup == null || !artist.equals( currentGroup.getTitle() ) )
			{
				if( currentGroup != null ) artists.add( currentGroup );
				currentGroup = new AudioFileGroup( artist, "" );
			}
			
			//add the item to the current group
			currentGroup.addItem( item );
		}
		artists.add( currentGroup );
		
		

		//create a grouping for albums
		sortAudioFiles( Sorting.ALBUM, audioFiles );
		this.albums = new ArrayList<AudioFileGroup>();
		
		currentGroup = null;
		for( int i = 0; i < size; ++i )
		{
			//grab the next item to be categorized
			AudioFileInfo item = audioFiles.get( i );
			
			//compare the current artist with the ongoing artist grouping
			String album = item.getAlbumName();
			if( currentGroup == null || !album.equals( currentGroup.getTitle() ) )
			{
				if( currentGroup != null ) albums.add( currentGroup );
				currentGroup = new AudioFileGroup( album , item.getArtistName() );
			}
			
			//add the item to the current group
			currentGroup.addItem( item );
		}
		albums.add( currentGroup );
		
		
		//finally, sort the files by song for the default sorting
		sortAudioFiles( Sorting.SONG, audioFiles );
	}

	
	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized ArrayList<AudioFileInfo> getAllAudioFiles()
	{
		return audioFiles;
	}


	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized ArrayList<AudioFileGroup> getArtists()
	{
		return artists; 
	}

	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized ArrayList<AudioFileGroup> getAlbums()
	{
		return albums;
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
		if( toSort == null )
		{
			if( !isAudioFileListLoaded() ) return;
			toSort = audioFiles;
		}

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
					return lhs.getAlbumName().compareTo( rhs.getAlbumName() );
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
						mediaPlayer.setDataSource( getApplicationContext(), currentAudioFile.uri );
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
}
