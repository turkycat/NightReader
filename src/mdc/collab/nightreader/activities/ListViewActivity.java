package mdc.collab.nightreader.activities;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.application.NightReader.Sorting;
import mdc.collab.nightreader.util.AudioFileInfo;
import mdc.collab.nightreader.util.AudioFileInfoAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
	private ArrayList<AudioFileInfo> audioFiles;
	private ListView listView;
	private int last = -1;
	
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
				Log.i( TAG, "onItemSelected" );
				if( position == last )
				{
					last = -1;
					application.stopMedia();
				}
				else
				{
					last = position;
					application.playMedia( audioFiles.get( position ) );
					ListViewActivity.this.finish();
				}
			}
		} );
		
		
		audioFiles = application.getAudioFileList();
		populateListView();
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.list_view, menu );
		return true;
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
	private void populateListView()
	{
		AudioFileInfoAdapter arrayAdapter = new AudioFileInfoAdapter( application );
        listView.setAdapter(arrayAdapter);
        updateButtonIcons();
	}



	/**
	 * the callback method for title sorting button
	 */
	public void sortByTitle( View view )
	{
		application.sortAudioFiles( Sorting.SONG );
		populateListView();
	}



	/**
	 * the callback method for artist sorting button
	 */
	public void sortByArtist( View view )
	{
		application.sortAudioFiles( Sorting.ARTIST );
		populateListView();
	}


	/**
	 * the callback method for album sorting button
	 */
	public void sortByAlbum( View view )
	{
		application.sortAudioFiles( Sorting.ALBUM );
		populateListView();
	}

	
	
	/**
	 * the callback method for genre sorting button
	 */
	public void sortByGenre( View view )
	{
		application.sortAudioFiles( Sorting.GENRE );
		populateListView();
	}
}
