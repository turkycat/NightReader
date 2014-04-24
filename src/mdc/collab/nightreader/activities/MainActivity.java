package mdc.collab.nightreader.activities;

/**
 * @author Jesse Frush
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener
{
	private static final String TAG = "MainActivity";
	
	private static NightReader application;
	private static Context applicationContext;
	private static ASyncSongLoader loader;
	private ProgressBar progressBar;
	private TextView infoText; 
	private Button loadButton;
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		application = (NightReader) getApplication();
		
		//set the context for later use
		applicationContext = getApplicationContext();
		
		//retrieve the application's custom font by calling a special function of the NightReader class
		Typeface font = application.getApplicationTypeface();
		
		//get a reference to the inflated TextView objects and set their font
		TextView title = (TextView) findViewById( R.id.MainActivity_Title );
		infoText = (TextView) findViewById( R.id.MainActivity_MenuText );
		title.setTypeface( font );
		infoText.setTypeface( font );
		
		loadButton = (Button) findViewById( R.id.MainActivity_LoadButton );
		
		//initialize the progress bar
		progressBar = (ProgressBar) findViewById( R.id.MainActivity_ProgressBar );
		
		//begin detecting audio files with an async task
		if( application.isAudioFileListLoaded() )
		{
			loadButton.setEnabled( true );
		}
		else
		{
			loader = new ASyncSongLoader();
			loader.execute();
		}
		
		initializeSensors();
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
	
	
	
	/**
	 * An AsyncTask implementation responsible for loading the media information from the device running this application
	 * @author Jesse Frush
	 */
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
			//Log.i( TAG, "doInBackground " );
			return detectAudioFiles();
		}
		
		@Override
		protected void onProgressUpdate( Integer... values )
		{
			if( values[0] != null )
			{
				//Log.i( TAG, "onProgressUpdate " + values[0] );
				progressBar.setProgress( (int) values[0] );
			}
		}
		
		@Override
		protected void onPostExecute( ArrayList<AudioFileInfo> result )
		{
			super.onPostExecute( result );
			
			infoText.setText( "boats n hoes" );
			application.setAudioFileList( result );
			loadButton.setEnabled( true );
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
			final Cursor cursor = getContentResolver().query(externalContent, cursor_cols, where, null, null);
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
	            
//				Bitmap bitmap = null;
//				
//	            try 
//	            {
//	                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), albumArtUri);
//	                bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
//	            } 
//	            catch (FileNotFoundException e) // Song has no album art!
//	            {
//	                e.printStackTrace();
//	                //bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.audio_file);
//	            } 
//	            catch (IOException e) // Other exception
//	            {
//	                e.printStackTrace();
//	            }
	            
	            
	            
				
				AudioFileInfo info = new AudioFileInfo();
				info.rawPath = data;
				info.uri = fileUri;
				info.artist = artist;
				info.album = album;
				info.title = track;
				info.year = year;
				//info.albumArt = bitmap;
				info.albumArtUri = albumArtUri;
				
				localList.add(info);
				
				//this AsyncTask method will invoke onProgressUpdate on the UI thread
				int percentage = (int)( ( ++count / totalFiles ) * 100 );
				publishProgress( percentage );
			}
			
			return localList;
		}
	}


//------------------------------------------------sensor related methods
	
	
	
	
	//accounts for gravity
	float[] gravity = new float[3];
	
	/**
	 * initializes the sensors, currently just the accelerometer
	 */
	public void initializeSensors()
	{
		//grab a reference to the framework's pre-built sensor service object
		sensorManager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );
		
		//grab a reference to the Accelerometer, which is of type Sensor
		accelerometer = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
		
		//register this class (implements SensorEventListener) to the SensorManager built into the framework.
		sensorManager.registerListener( this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL );
	}




	@Override
	public void onAccuracyChanged( Sensor arg0, int arg1 )
	{
		//nothing to do here
	}

	
	private long lastSignificantTime = System.currentTimeMillis();
	private static final double SIGNIFICANCE_THRESHOLD = 0.0;

	
	@Override
	public void onSensorChanged( SensorEvent event )
	{
		//if( !application.isMediaPlaying() ) return;
		
		//this should be the only event type we get callbacks for, but we will type check for safety
		if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
		{
			float x, y, z;

			//this method doesn't account for gravity, which essentially adds 9.81 m/s^2 when stationary, so less desirable.
//			x = event.values[0];
//			y = event.values[1];
//			z = event.values[2];
			
			//from the Google high/low pass filter example:
			
			// In this example, alpha is calculated as t / (t + dT),
			// where t is the low-pass filter's time-constant and
			// dT is the event delivery rate.

			final float alpha = 0.8f;

			// Isolate the force of gravity with the low-pass filter.
			gravity[0] = alpha * gravity[0] + ( 1 - alpha ) * event.values[0];
			gravity[1] = alpha * gravity[1] + ( 1 - alpha ) * event.values[1];
			gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2];

			// Remove the gravity contribution with the high-pass filter.
			x = event.values[0] - gravity[0];
			y = event.values[1] - gravity[1];
			z = event.values[2] - gravity[2];
			
			//the first method shows the innaccuracy
			double magnitude = Math.sqrt( ( x * x ) + ( y * y ) + ( z * z ) );
			
			
			Log.i(TAG, "" + magnitude);
			//infoText.setText( "" + magnitude );
			
			
		}
	}

}