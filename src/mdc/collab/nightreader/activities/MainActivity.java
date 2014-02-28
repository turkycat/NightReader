package mdc.collab.nightreader.activities;

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		//begin detecting audio files with 
		ArrayList<AudioFileInfo> audioFiles = detectAudioFiles();
		populateListView( audioFiles );
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}
	
	
	public void OpenListView(View view)
	{
//		Intent intent = new Intent( MainActivity.this, GameActivity.class );
//		startActivity( intent );
	}
	
	
	
	
	private ArrayList<AudioFileInfo> detectAudioFiles()
	{
		ArrayList<AudioFileInfo> allInfo = new ArrayList<AudioFileInfo>();

		Log.d("getAllSongs()", "Starting query...");
		final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		final String[] cursor_cols = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.DURATION };
		
		final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
		final Cursor cursor = MainActivity.this.getContentResolver().query(uri, cursor_cols, where, null, null);
		int count = 0;

		while (cursor.moveToNext()) 
		{
			String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)).trim();
			String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)).trim();
			String track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)).trim();
			String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
			Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
			
            //------------------------------album art code
            
			/*Bitmap bitmap = null;
			
            try 
            {
                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
            } 
            catch (FileNotFoundException e) // Song has no album art!
            {
                e.printStackTrace();
                //bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.audio_file);
            } 
            catch (IOException e) // Other exception
            {
                e.printStackTrace();
            }*/
            
            
            
			
			AudioFileInfo info = new AudioFileInfo();
			info.rawPath = data;
			info.artist = artist;
			info.album = album;
			info.title = track;
			info.year = year;
			info.albumArt = null;
			//info.albumArtUri = albumArtUri;
			
			allInfo.add(info);
			//publishProgress(++count, cursor.getCount());
		}
		
		return allInfo;
	}
	
	
	/**
	 * populates the list view with the titles of the given list of audio files
	 */
	private void populateListView( ArrayList<AudioFileInfo> songs )
	{
		
	}

}
