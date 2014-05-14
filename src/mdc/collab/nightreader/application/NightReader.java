package mdc.collab.nightreader.application;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.singleton.MediaState;
import mdc.collab.nightreader.util.AudioFileGroup;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Application;
import android.graphics.Typeface;

/**
 * this class serves like an umbrella over the other activities. It's properties
 * 	dictate behavior of subtasks/threads 
 * @author Jesse Frush
 * 
 */
public class NightReader extends Application
{
	public enum SortingMode
	{
		NONE, SONG, ARTIST, ALBUM, GENRE
	};
	
	//the typeface used throughout this application
	private static Typeface applicationFont;
	
	//a collection of songs
	private ArrayList<AudioFileInfo> audioFiles;
	
	//a collection of artists
	ArrayList<AudioFileGroup> artists;
	
	//a collection of albums
	ArrayList<AudioFileGroup> albums;

	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		MediaState.setContext( getApplicationContext() );

		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/ABANDON.TTF" );
		//		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/bellerose.ttf" );
		//		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/nougat.ttf" );
	}
	
	
	
	/**
	 * sets the current application's audio file list to the given list
	 */
	public synchronized void setAudioFileList( ArrayList<AudioFileInfo> allFiles )
	{
		this.audioFiles = allFiles;
		int size = audioFiles.size();
		

		//create a grouping for artists
		sortAudioFiles( SortingMode.ARTIST, audioFiles );
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
		sortAudioFiles( SortingMode.ALBUM, audioFiles );
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
		sortAudioFiles( SortingMode.SONG, audioFiles );
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
	 * sorts the list of songs given a requested sorting type
	 */
	public static void sortAudioFiles( SortingMode requestedSort, ArrayList<AudioFileInfo> toSort )
	{
		if( toSort == null ) return;
		
		Comparator<AudioFileInfo> sortingComparator;
		switch( requestedSort )
		{
		default:
			requestedSort = SortingMode.SONG;

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

		Collections.sort( toSort, sortingComparator );
	}
	
	
	
		
}
