package mdc.collab.nightreader.activities;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.application.NightReader.Sorting;
import mdc.collab.nightreader.singleton.MediaState;
import mdc.collab.nightreader.util.Audio;
import mdc.collab.nightreader.util.AudioFileGroup;
import mdc.collab.nightreader.util.AudioFileInfo;
import mdc.collab.nightreader.util.AudioFileInfoAdapter;
import android.app.Activity;
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
		listView.setOnItemClickListener( new ItemClickListener() );
		

		
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
		
		//TODO lets make this reset somehow
		else
		{
			switch( MediaState.getInstance().getSorting() )
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
	 * the callback method for title sorting button
	 */
	public void sortByTitle( View view )
	{
		isMainMenu = true;
		ArrayList<AudioFileInfo> allSongs = application.getAllAudioFiles();
		NightReader.sortAudioFiles( Sorting.SONG, allSongs );
		populateListView( allSongs );
	}



	/**
	 * the callback method for artist sorting button
	 */
	public void sortByArtist( View view )
	{
		list = null;
		isMainMenu = true;
		populateListView( application.getArtists() );
	}


	/**
	 * the callback method for album sorting button
	 */
	public void sortByAlbum( View view )
	{
		list = null;
		isMainMenu = true;
		populateListView( application.getAlbums() );
	}

	
	
	/**
	 * the callback method for genre sorting button
	 */
	public void sortByGenre( View view )
	{
		NightReader.sortAudioFiles( Sorting.GENRE, null );
		populateListView( application.getAllAudioFiles() );
	}
	
	
	/**
	 * populates the list view with the titles of the given list of audio files
	 */
	private <E extends Audio> void populateListView( ArrayList<E> audio )
	{
		AudioFileInfoAdapter<Audio, ArrayList<Audio>> arrayAdapter = new AudioFileInfoAdapter<Audio, ArrayList<Audio>>( application, audio );
        listView.setAdapter( arrayAdapter );
        updateButtonIcons();
	}
	
	
	/**
	 * re-applies the proper icons to the buttons across the top
	 */
	private void updateButtonIcons()
	{
		int song = R.drawable.notes;
		int artist = R.drawable.microphone;
		int album = R.drawable.record;
		int genre = R.drawable.book;
		
		switch( MediaState.getInstance().getSorting() )
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
		//((Button) findViewById( R.id.GenreNameButton )).setBackgroundResource( genre );
	}



	
	
	
	private class ItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick( AdapterView<?> parent, View view, int position, long id )
		{
			MediaState mediaState = MediaState.getInstance();
			Sorting sort = mediaState.getSorting();
			if( list != null )
			{
				mediaState.playMedia( list, position );
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
		
	}
}
