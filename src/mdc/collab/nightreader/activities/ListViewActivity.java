package mdc.collab.nightreader.activities;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.application.NightReader.Sorting;
import mdc.collab.nightreader.util.Audio;
import mdc.collab.nightreader.util.AudioFileGroup;
import mdc.collab.nightreader.util.AudioFileInfo;
import mdc.collab.nightreader.util.AudioFileInfoAdapter;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ListViewActivity extends Activity
{
	private static final String TAG = "ListViewActivity";
	
	private static NightReader application;
	private ListView listView;
	
	//the current list of items being displayed
	private static ArrayList<AudioFileInfo> list;
	
	//if the list is not null, this refers to the last position in the list selected
	private static int lastPositionSelected;
	
	//controls what level of the list we are on. behaviors such as selecting items & the back button depend on this
	private boolean isMainMenu;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_list_view );
		
		application = (NightReader) getApplication();
		
		listView = (ListView) findViewById( R.id.AudioListView );
		listView.setClickable( true );
		listView.setOnItemClickListener( new OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> adapter, View v, int position, long id )
			{
				Sorting sort = application.getSorting();
				if( list != null )
				{
					lastPositionSelected = position;
					application.playMedia( list, position );
					ListViewActivity.this.finish();
				}
				else //if( isMainMenu )//&& ( sort == Sorting.ALBUM || sort == Sorting.ARTIST ) )
				{
					ArrayList<AudioFileGroup> grouping = sort == Sorting.ALBUM ? application.getAlbums() : application.getArtists();
					list = grouping.get( position ).getItems();
					application.sortAudioFiles( Sorting.SONG, list );
					populateListView( list );
					isMainMenu = false;
				}
			}
		} );
		

		
//		switch( application.getSorting() )
//		{
//		default:
//			list = application.getAllAudioFiles();
//			populateListView( list );
//			break;
//			
//		case ALBUM:
//			list = null;
//			populateListView( application.getAlbums() );
//			break;
//			
//		case ARTIST:
//			list = null;
//			populateListView( application.getArtists() );
//			break;
//		}
		
		//set up the list
		
		if( list == null )
		{
			list = application.getAllAudioFiles();
			populateListView( list );
			isMainMenu = true;
		}
		else
		{
			populateListView( list );
			isMainMenu = false;
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.list_view, menu );
		return true;
	}
	
	
	
	/**
	 * override the behavior of the back button, to prevent from automatically exiting
	 * 	the activity if we are browsing an artist, album, or other
	 */
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		if( isMainMenu ) finish();
		else
		{
			switch( application.getSorting() )
			{
			default:
				sortByTitle( null );
				break;
				
			case ALBUM:
				sortByAlbum( null );
				break;
				
			case ARTIST:
				sortByAlbum( null );
				break;
			}
		}
	}
	
	
	/**
	 * re-applies the proper icons to the buttons across the top
	 */
	public void updateButtonIcons()
	{
		int song = R.drawable.notes;
		int artist = R.drawable.microphone;
		int album = R.drawable.record;
		int genre = R.drawable.book;
		
		switch( application.getSorting() )
		{
		case SONG:
			song = R.drawable.notes_select;
			break;
			
		case ARTIST:
			artist = R.drawable.microphone_select;
			break;

		case ALBUM:
			album = R.drawable.record_select;
			break;

		case GENRE:
			genre = R.drawable.book_select;
			break;
			
		default:
			break;
		}

		((Button) findViewById( R.id.SongTitleButton )).setBackgroundResource( song );
		((Button) findViewById( R.id.ArtistNameButton )).setBackgroundResource( artist );
		((Button) findViewById( R.id.AlbumNameButton )).setBackgroundResource( album );
		((Button) findViewById( R.id.GenreNameButton )).setBackgroundResource( genre );
	}
	
	
	/**
	 * populates the list view with the titles of the given list of audio files
	 */
	private <E extends Audio> void populateListView( ArrayList<E> audio )
	{
		AudioFileInfoAdapter arrayAdapter = new AudioFileInfoAdapter( application, audio );
        listView.setAdapter( arrayAdapter );
        updateButtonIcons();
	}



	/**
	 * the callback method for title sorting button
	 */
	public void sortByTitle( View view )
	{
		isMainMenu = true;
		ArrayList<AudioFileInfo> allSongs = application.getAllAudioFiles();
		application.sortAudioFiles( Sorting.SONG, allSongs );
		populateListView( allSongs );
	}



	/**
	 * the callback method for artist sorting button
	 */
	public void sortByArtist( View view )
	{
		list = null;
		isMainMenu = true;
		application.sortAudioFiles( Sorting.ARTIST, null );
		populateListView( application.getArtists() );
	}


	/**
	 * the callback method for album sorting button
	 */
	public void sortByAlbum( View view )
	{
		list = null;
		isMainMenu = true;
		application.sortAudioFiles( Sorting.ALBUM, null );
		populateListView( application.getAlbums() );
	}

	
	
	/**
	 * the callback method for genre sorting button
	 */
	public void sortByGenre( View view )
	{
		application.sortAudioFiles( Sorting.GENRE, null );
		populateListView( application.getAllAudioFiles() );
	}
}
