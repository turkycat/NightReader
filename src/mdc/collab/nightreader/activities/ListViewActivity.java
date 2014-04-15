package mdc.collab.nightreader.activities;

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.util.AudioFileInfo;
import mdc.collab.nightreader.util.AudioFileInfoAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
					MainActivity.stopMedia();
				}
				else
				{
					last = position;
					MainActivity.playMedia( audioFiles.get( position ).uri );
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
	 * populates the list view with the titles of the given list of audio files
	 */
	private void populateListView()
	{
//		ArrayAdapter<AudioFileInfo> arrayAdapter = new ArrayAdapter<AudioFileInfo>(
//                this, 
//                android.R.layout.simple_list_item_1,
//                songs );
		
		AudioFileInfoAdapter arrayAdapter = new AudioFileInfoAdapter( application, audioFiles );

        listView.setAdapter(arrayAdapter); 
	}

	
	public void sortByArtist( View view )
	{
		application.sortAudioFilesByArtist();
		populateListView();
	}
}
