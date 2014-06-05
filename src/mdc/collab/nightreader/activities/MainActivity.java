package mdc.collab.nightreader.activities;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import mdc.collab.nightreader.dialog.AboutDialogFragment;
import mdc.collab.nightreader.dialog.DelaySelectionFragment;
import mdc.collab.nightreader.singleton.MediaState;
import mdc.collab.nightreader.util.AudioFileInfo;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener
{
	private static final String TAG = "MainActivity";

	//the threshold for what determines "significant" movement of the phone
	private static final double MOVEMENT_SIGNIFICANCE_THRESHOLD = 0.3;

	//the time, in millis, of the last significant event detected
	private static long lastSignificantEvent;

	//the number of minutes before cutting off the audio feed, for calculating the delay actually used
	private static int audio_cutoff_minutes;

	//the number of milliseconds to wait between significant events
	private static int audio_cutoff_millis;

	//a reference to the application instance
	private static NightReader application;

	//a reference to the AsyncTask implementation which loads assets
	private static ASyncSongLoader loader;

	//a reference to the view's progress bar
	private static ProgressBar progressBar;

	//a reference to the largest infotext
	private static TextView mainInfoText;

	//a reference to the main audio info text
	private static TextView mainAudioText;

	//a reference to the largest infotext
	private static TextView subAudioText;

	//a reference to the button which starts the ListViewActivity to select music
	private static Button loadButton;

	//a reference to the play/pause button
	private static Button playButton;

	//a reference to the previous button
	private static Button previousButton;

	//a reference to the next button
	private static Button nextButton;

	//a reference to the stop button
	private static Button stopButton;

	//a reference to the sensor button which toggles the sensor
	private static Button sensorButton;

	//a reference to the ImageView used for album art
	private static ImageView albumArtView;

	//the sensor manager is used to initialize sensors
	private static SensorManager sensorManager;

	//the accelerometer is used to detect movement
	private static Sensor accelerometer;

	//the state of the sensor
	private static boolean sensorEnabled;
	
	//a reference to a dialog fragment that is currently displayed on the screen
	private static DialogFragment dialog;

	//accounts for gravity
	float[] gravity;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		gravity = new float[3];

		//set the current timestamp to get proper delays
		lastSignificantEvent = System.currentTimeMillis();

		//grab a reference to the NightReader application class, defined ourselves
		application = (NightReader) getApplication();

		//retrieve the application's custom font by calling a special function of the NightReader class
		Typeface font = application.getApplicationTypeface();

		//get a reference to the inflated TextView objects and set their font
		Typeface titleFont = Typeface.createFromAsset( getAssets(), "fonts/sidewalk.ttf" );
		TextView title = (TextView) findViewById( R.id.MainActivity_Title );
		title.setTypeface( titleFont );
		mainInfoText = (TextView) findViewById( R.id.MainActivity_MenuText );
		mainInfoText.setTypeface( font );
		mainAudioText = (TextView) findViewById( R.id.mainactivity_mediainfo_main );
		mainAudioText.setTypeface( font );
		subAudioText = (TextView) findViewById( R.id.mainactivity_mediainfo_sub );
		subAudioText.setTypeface( font );

		//grab the reference to the album art window
		albumArtView = (ImageView) findViewById( R.id.mainactivity_albumart );

		//grab references to the four buttons on the screen
		loadButton = (Button) findViewById( R.id.mainactivity_loadbutton );
		sensorButton = (Button) findViewById( R.id.mainactivity_sensorbutton );
		playButton = (Button) findViewById( R.id.mainactivity_playpausebutton );
		nextButton = (Button) findViewById( R.id.mainactivity_nextbutton );
		previousButton = (Button) findViewById( R.id.mainactivity_previousbutton );
		stopButton = (Button) findViewById( R.id.mainactivity_stopbutton );
		

		//initialize the progress bar
		progressBar = (ProgressBar) findViewById( R.id.MainActivity_ProgressBar );
		
		//add seek capability to the progress bar
		progressBar.setOnTouchListener( new OnTouchListener()
		{
			long lastEventTime = 0;
			
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if( !MediaState.getInstance().isMediaPlaying() ) return false;
				if( System.currentTimeMillis() - lastEventTime < 200 ) return false;
				
				lastEventTime = System.currentTimeMillis();
				float x = event.getX();
				float w = v.getWidth();
				float p = x / w;
				MediaPlayer player = MediaState.getInstance().getCurrentMediaPlayer();
				player.seekTo( (int)( player.getDuration() * p ) );
				progressBar.setProgress( (int)( p * 100 ) );
				Log.i( TAG, "x position: " + x + " width: " + w + " per: " + p );
				return true;
			}
		} );

		//begin detecting audio files if not already stored in the application
		if( !application.isAudioFileListLoaded() )
		{
			loader = new ASyncSongLoader();
			loader.execute();
		}

		initializeSensors();
		setTimerDelay( 10 );

		SongStatusThread thread = new SongStatusThread();
		thread.start();
		
		resetUI();
	}

	/**
	 * forces the activity to reset it's button bar and text fields
	 */
	public static void resetUI()
	{
		lastSignificantEvent = System.currentTimeMillis();
		resetEjectButton();
		resetPlayPauseButton();
		resetPreviousButton();
		resetNextButton();
		resetStopButton();

		boolean playing = MediaState.getInstance().isMediaPlaying();
		if( playing )
		{
			//set the song information texts to the selected media file
			AudioFileInfo file = MediaState.getInstance().getCurrentAudioFile();
			mainAudioText.setText( file.getSongTitle() );
			subAudioText.setText( file.getArtistName() );

			//tell the buttons to set their states
			resetPlayPauseButton();
			resetStopButton();
			playButton.setEnabled( true );

			//set the progress bar's new max, in seconds
			progressBar.setMax( MediaState.getInstance().getCurrentMediaPlayer().getDuration() / 1000 );

			//select the album art if possible
			Bitmap albumArt = file.getAlbumArt( application.getApplicationContext() );
			if( albumArt != null )
			{
				albumArtView.setImageBitmap( albumArt );
				albumArtView.setBackgroundColor( Color.BLACK );
			}
			else
			{
				albumArtView.setImageBitmap( null );
				albumArtView.setBackgroundResource( R.drawable.icon_white );
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected( int featureId, MenuItem item )
	{
		switch( item.getItemId() )
		{
		case R.id.menuitem_about:
			dialog = new AboutDialogFragment();
			dialog.show( getFragmentManager(), "ABOUT" );
			break;

		case R.id.menuitem_delay_selector:
			dialog = new DelaySelectionFragment();
			dialog.show( getFragmentManager(), "DELAYSELECTION" );
			break;
			
		}
		
		return true;
	}

	@Override
	public void onBackPressed()
	{
		moveTaskToBack( true );
	}
	
	
	
	
	

	//------------------------------------------------------------------callback methods for buttons

	/**
	 * opens the list view for song selection
	 */
	public void ejectEvent( View view )
	{
		Intent intent = new Intent( MainActivity.this, ListViewActivity.class );
		//intent.addFlags( Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT );
		startActivity( intent );
	}

	/**
	 * toggles play/pause state and buttons
	 */
	public void playPauseEvent( View view )
	{
		//invoke the play/pause method on the mediastate object
		boolean paused = MediaState.getInstance().pauseOrResumeMedia();

		//then set the play/pause button image according to the new state of the media
		if( paused )
		{
//			isPaused = true;
			playButton.setBackgroundResource( R.drawable.play_enabled );
		}
		else
		{
//			isPaused = false;
			playButton.setBackgroundResource( R.drawable.pause_enabled );
		}
		
		resetUI();
	}

	/**
	 * stops the media state
	 */
	public void stopEvent( View view )
	{
		MediaState.getInstance().stopMedia();
		//isPaused = false;
	}

	/**
	 * moves to the next song
	 */
	public void nextEvent( View view )
	{
		MediaState.getInstance().nextTrack();
		//isPaused = false;
	}

	/**
	 * retrieves the previous song
	 */
	public void previousEvent( View view )
	{
		MediaState.getInstance().previousTrack();
		//isPaused = false;
	}
	
	

	/**
	 * toggles the sensor
	 */
	public void sensorButtonEvent( View view )
	{
		setSensorEnabled( !sensorEnabled );
	}
	
	
	/**
	 * sets the current delay timer for the pause functionality
	 */
	public void setTimerDelay( int delay )
	{
		if( delay <= 0 )
		{
			setSensorEnabled( false );
			Toast.makeText( getApplicationContext(), "sensor disabled", Toast.LENGTH_LONG ).show();
		}
		else
		{
			setSensorEnabled( true );
			audio_cutoff_minutes = delay;
			audio_cutoff_millis = 1000 * ( (int) ( 60 * audio_cutoff_minutes ) );
		}
	}
	
	
	/**
	 * returns the current delay timer
	 */
	public int getTimerDelay()
	{
		return audio_cutoff_minutes;
	}
	

	/**
	 * used to close the currently opened dialog
	 */
	public void closeDialog( View view )
	{
		Log.i( TAG, "closeDialog called" );
		
		if( dialog != null ) dialog.dismiss();
		dialog = null;
	}
	
	

	//-----------------------------------------------------accelerometer & other sensor related methods

	/**
	 * initializes the sensors, currently just the accelerometer
	 */
	private void initializeSensors()
	{
		//grab a reference to the framework's pre-built sensor service object
		sensorManager = (SensorManager) getSystemService( Service.SENSOR_SERVICE );

		//grab a reference to the Accelerometer, which is of type Sensor
		accelerometer = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );

		//register this class (implements SensorEventListener) to the SensorManager built into the framework.
		sensorManager.registerListener( this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL );

		sensorEnabled = true;
		sensorButton.setBackgroundResource( R.drawable.sensor_enabled );
	}
	
	
	/**
	 * a private method used to toggle the sensor
	 */
	private void setSensorEnabled( boolean enabled )
	{
		lastSignificantEvent = System.currentTimeMillis();
		
		sensorEnabled = enabled;
		if( sensorEnabled )
		{
			//mainInfoText.setVisibility( View.VISIBLE );
			sensorButton.setBackgroundResource( R.drawable.sensor_enabled );
		}
		else
		{
			sensorButton.setBackgroundResource( R.drawable.sensor_disabled );
			//mainInfoText.setVisibility( View.INVISIBLE );
			mainInfoText.setText( "Disabled." );
		}
	}

	@Override
	public void onAccuracyChanged( Sensor arg0, int arg1 )
	{
		//nothing to do here
	}

	@Override
	public void onSensorChanged( SensorEvent event )
	{
		if( !sensorEnabled ) return;

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

			//we can stop here if we aren't playing any music
			if( !MediaState.getInstance().isMediaPlaying() ) return;

			// Remove the gravity contribution with the high-pass filter.
			x = event.values[0] - gravity[0];
			y = event.values[1] - gravity[1];
			z = event.values[2] - gravity[2];

			//the first method shows the innaccuracy
			double magnitude = Math.sqrt( ( x * x ) + ( y * y ) + ( z * z ) );

			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - lastSignificantEvent;
			Log.i( TAG, "mag: " + magnitude + " elapsed: " + elapsedTime );
			//infoText.setText( "" + magnitude );

			//keep audio alive if movment events are detected
			if( magnitude > MOVEMENT_SIGNIFICANCE_THRESHOLD )
			{
				lastSignificantEvent = currentTime;
			}

			//check if we've reached the cutoff point
			if( currentTime - lastSignificantEvent > audio_cutoff_millis )
			{
				MediaState.getInstance().stopMedia();
				mainInfoText.setText( "Media paused" );
			}
			else
			{
				int seconds = (int) elapsedTime / 1000;
				int totalSeconds = (int) audio_cutoff_millis / 1000;
				if( sensorEnabled ) mainInfoText.setText( "pause in: " + ( totalSeconds - seconds ) );
			}
		}
	}
	
	
	
	
	

	//----------------------------------------------------------------------private methods and classes

	/**
	 * enables the eject button and applies the correct image
	 */
	private static void resetEjectButton()
	{
		boolean enabled = application.isAudioFileListLoaded();

		//set the eject button to the correct image
		if( enabled )
		{
			loadButton.setBackgroundResource( R.drawable.eject_enabled );
			mainInfoText.setText( "select a file" );
		}
		else
		{
			loadButton.setBackgroundResource( R.drawable.eject );
		}

		//set the eject button to the correct state
		loadButton.setEnabled( enabled );
	}

	/**
	 * sets the image of the play/pause button
	 */
	private static void resetPlayPauseButton()
	{
		//then set the play/pause button image according to the new state of the media
		if( MediaState.getInstance().isMediaPlaying() )
		{
			playButton.setBackgroundResource( R.drawable.pause_enabled );
		}
		else
		{
			playButton.setBackgroundResource( R.drawable.play_enabled );
		}
	}

	/**
	 * sets the image of the play/pause button
	 */
	private static void resetStopButton()
	{
		boolean enabled = MediaState.getInstance().isMediaPlaying();// || isPaused;
		stopButton.setEnabled( enabled );

		if( enabled )
		{
			stopButton.setBackgroundResource( R.drawable.stop_enabled );
		}
		else
		{
			stopButton.setBackgroundResource( R.drawable.stop );
		}
	}

	/**
	 * sets the image of the play/pause button
	 */
	private static void resetNextButton()
	{
		boolean enabled = MediaState.getInstance().isPlaylistLoaded();
		nextButton.setEnabled( enabled );

		if( enabled )
		{
			nextButton.setBackgroundResource( R.drawable.next_enabled );
		}
		else
		{
			nextButton.setBackgroundResource( R.drawable.next );
		}
	}

	/**
	 * sets the image of the play/pause button
	 */
	private static void resetPreviousButton()
	{
		boolean enabled = MediaState.getInstance().isPlaylistLoaded();
		previousButton.setEnabled( enabled );

		if( enabled )
		{
			previousButton.setBackgroundResource( R.drawable.previous_enabled );
		}
		else
		{
			previousButton.setBackgroundResource( R.drawable.previous );
		}
	}

	/**
	 * An AsyncTask implementation responsible for loading the media information
	 * from the device running this application
	 * 
	 * @author Jesse Frush
	 */
	private class ASyncSongLoader extends AsyncTask<Void, Integer, ArrayList<AudioFileInfo>>
	{
		private static final String TAG = "ASyncSongLoader";
		private int[] start_hex = { 0xff, 0, 0 };
		private int[] mid_hex = { 0xff, 0xff, 0 };
		private int[] end_hex = { 0, 0x4a, 0xff };

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
				int value = values[0];

				int[] left = ( value <= 0.5 ? start_hex : mid_hex );
				int[] right = ( value <= 0.5 ? mid_hex : end_hex );
				int[] interpolated = { ( left[0] + right[0] ) / 2,
						( left[1] + right[1] ) / 2, ( left[2] + right[2] ) / 2 };

				//interpolate the color
				progressBar.setProgress( (int) values[0] );
				progressBar.getProgressDrawable().setColorFilter( Color.rgb( interpolated[0], interpolated[1], interpolated[2] ), android.graphics.PorterDuff.Mode.DST_ATOP );
				//progressBar.setBackgroundColor( Color.rgb( interpolated[0], interpolated[1], interpolated[2] ) );
			}
		}

		@Override
		protected void onPostExecute( ArrayList<AudioFileInfo> result )
		{
			super.onPostExecute( result );
			application.setAudioFileList( result );
			resetEjectButton();
		}

		/**
		 * will query and load the audio files
		 */
		private ArrayList<AudioFileInfo> detectAudioFiles()
		{
			ArrayList<AudioFileInfo> localList = new ArrayList<AudioFileInfo>();

			Log.d( TAG, "Starting query..." );

			final Uri externalContent = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			final String[] cursor_cols = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.YEAR,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.DURATION };

			final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
			final Cursor cursor = getContentResolver().query( externalContent, cursor_cols, where, null, null );
			int count = 0;
			final float totalFiles = (float) cursor.getCount();

			while( cursor.moveToNext() )
			{
				String artist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) ).trim();
				String album = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) ).trim();
				String track = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) ).trim();
				String data = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) );
				String year = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.YEAR ) );
				String id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
				Long albumId = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
				int duration = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DURATION ) );

				Uri sArtworkUri = Uri.parse( "content://media/external/audio/albumart" );
				Uri albumArtUri = ContentUris.withAppendedId( sArtworkUri, albumId );

				Uri fileUri = Uri.withAppendedPath( externalContent, id );

				//------------------------------album art code

				AudioFileInfo info = new AudioFileInfo();
				info.rawPath = data;
				info.uri = fileUri;
				info.artist = artist;
				info.album = album;
				info.title = track;
				info.year = year;
				info.albumArtUri = albumArtUri;

				localList.add( info );

				//this AsyncTask method will invoke onProgressUpdate on the UI thread
				int percentage = (int) ( ( ++count / totalFiles ) * 100 );
				publishProgress( percentage );
			}

			return localList;
		}
	}

	private class SongStatusThread extends Thread
	{
		private boolean running;

		public SongStatusThread()
		{
			running = true;
		}

		@Override
		public void run()
		{
			while( running )
			{
				synchronized( MainActivity.this )
				{
					MediaState instance = MediaState.getInstance();
					if( instance.isMediaPlaying() )
					{
						MediaPlayer player = instance.getCurrentMediaPlayer();
						progressBar.setProgress( player.getCurrentPosition() / 1000 );
					}
					else
					{
						try
						{
							Thread.sleep( 1000 );
						}
						catch( InterruptedException e )
						{
							//do nothing
						}
					}
				}
			}
		}

		/**
		 * instructs the thread to halt at a safe time
		 */
		public void finish()
		{
			running = false;
		}
	}
}