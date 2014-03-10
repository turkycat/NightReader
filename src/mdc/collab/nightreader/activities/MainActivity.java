package mdc.collab.nightreader.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";
	
	private static Context applicationContext;
	private static ASyncSongLoader loader;
	private static MediaPlayer mediaPlayer;
	private ProgressBar progressBar;
	private TextView infoText; 
	
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		//set the context for later use
		applicationContext = getApplicationContext();
		
		//initialize the font to be used
		Typeface type = Typeface.createFromAsset( getAssets(), "fonts/MAYBE MAYBE NOT.TTF" );
		
		//get a reference to the inflated TextView objects and set their font
		TextView title = (TextView) findViewById( R.id.MainActivity_Title );
		title.setTypeface( type );
		infoText = (TextView) findViewById( R.id.MainActivity_MenuText );
		infoText.setTypeface( type );
		
		//initialize the progress bar
		progressBar = (ProgressBar) findViewById( R.id.MainActivity_ProgressBar );
		//progressBar.
		
		//begin detecting audio files with an async task
		loader = new ASyncSongLoader();
		loader.execute();
	}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}
	
	
	public void OpenListView( View view )
	{
		Intent intent = new Intent( MainActivity.this, ListViewActivity.class );
		startActivity( intent );
	}
	
	
	public static ArrayList<AudioFileInfo> getAudioFileList()
	{
		try
		{
			return loader.get();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		catch( ExecutionException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * will attempt to load and play the given Uri
	 */
	public static void playMedia( Uri uri )
	{
		stopMedia();
		mediaPlayer = MediaPlayer.create( applicationContext, uri );
		mediaPlayer.start();
	}
	
	
	/**
	 * stops the active player, if necessary
	 */
	public static void stopMedia()
	{
		if( mediaPlayer != null ) mediaPlayer.stop();
	}
	
	
	
	private class ASyncSongLoader extends AsyncTask<Void, Integer, ArrayList<AudioFileInfo>>
	{
		private static final String TAG = "ASyncSongLoader";
		
		@Override
		protected void onPreExecute()
		{
			//no need to do anything
			super.onPreExecute();
		}
		
		@Override
		protected ArrayList<AudioFileInfo> doInBackground( Void... arg0 )
		{
			Log.i( TAG, "doInBackground " );
			return detectAudioFiles();
		}
		
		@Override
		protected void onProgressUpdate( Integer... values )
		{
			if( values[0] != null )
			{
				Log.i( TAG, "onProgressUpdate " + values[0] );
				progressBar.setProgress( (int) values[0] );
			}
		}
		
		@Override
		protected void onPostExecute( ArrayList<AudioFileInfo> result )
		{
			Log.i( TAG, "onPostExecute " );
			infoText.setText( "boats n hoes" );
			super.onPostExecute( result );
		}
		
		/**
		 * will query and load the audio files
		 */
		private ArrayList<AudioFileInfo> detectAudioFiles()
		{
			ArrayList<AudioFileInfo> localList = new ArrayList<AudioFileInfo>();
			
			Log.d( TAG, "Starting query...");
			
			final Uri externalContent = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			final String[] cursor_cols = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.DURATION };
			
			final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
			final Cursor cursor = MainActivity.this.getContentResolver().query(externalContent, cursor_cols, where, null, null);
			int count = 0;
			final float totalFiles = (float) cursor.getCount();

			while (cursor.moveToNext()) 
			{
				String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)).trim();
				String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)).trim();
				String track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)).trim();
				String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
				String id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
				Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
				int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				
				Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
	            
	            Uri fileUri = Uri.withAppendedPath( externalContent, id );
				
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
				info.uri = fileUri;
				info.artist = artist;
				info.album = album;
				info.title = track;
				info.year = year;
				info.albumArt = null;
				//info.albumArtUri = albumArtUri;
				
				localList.add(info);
				
				//this AsyncTask method will invoke onProgressUpdate on the UI thread
				int percentage = (int)( ( ++count / totalFiles ) * 100 );
				publishProgress( percentage );
				try
				{
					Thread.sleep( 30 );
				}
				catch( InterruptedException e )
				{
					//do nothing
				}
			}
			
			return localList;
		}
	}

}