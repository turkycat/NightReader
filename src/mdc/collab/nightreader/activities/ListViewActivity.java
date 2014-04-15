package mdc.collab.nightreader.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.R;
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
	private ArrayList<AudioFileInfo> audioFiles;
	private ListView listView;
	private int last = -1;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_list_view );
		
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
		
		
		audioFiles = MainActivity.getAudioFileList();
		sortAudioFilesBySongTitle( audioFiles );
		populateListView( audioFiles );
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.list_view, menu );
		return true;
	}
	
	
	

	
	
	
	
	/**
	 * sorts the list of songs by name
	 */
	private void sortAudioFilesBySongTitle( ArrayList<AudioFileInfo> songs )
	{
		Collections.sort( songs, new Comparator<AudioFileInfo>(){
			@Override
			public int compare( AudioFileInfo lhs, AudioFileInfo rhs )
			{
				return lhs.title.compareTo( rhs.title );
			}
		});
	}
	
	
	
	/**
	 * populates the list view with the titles of the given list of audio files
	 */
	private void populateListView( ArrayList<AudioFileInfo> songs )
	{
//		ArrayAdapter<AudioFileInfo> arrayAdapter = new ArrayAdapter<AudioFileInfo>(
//                this, 
//                android.R.layout.simple_list_item_1,
//                songs );
		
		AudioFileInfoAdapter arrayAdapter = new AudioFileInfoAdapter( getBaseContext(), songs );

        listView.setAdapter(arrayAdapter); 
	}

}
