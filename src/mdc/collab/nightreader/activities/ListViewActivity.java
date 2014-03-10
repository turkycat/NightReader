package mdc.collab.nightreader.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewActivity extends Activity
{
	private ArrayList<AudioFileInfo> audioFiles;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_list_view );
		audioFiles = MainActivity.getAudioFileList();
		sortAudioFiles( audioFiles );
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
	private void sortAudioFiles( ArrayList<AudioFileInfo> songs )
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
		ListView lv = (ListView) findViewById(R.id.AudioView);


        ArrayAdapter<AudioFileInfo> arrayAdapter = new ArrayAdapter<AudioFileInfo>(
                this, 
                android.R.layout.simple_list_item_1,
                songs );

        lv.setAdapter(arrayAdapter); 
	}

}
