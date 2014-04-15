package mdc.collab.nightreader.application;

import android.app.Application;
import android.graphics.Typeface;

public class NightReader extends Application
{
	private Typeface applicationFont;
	
	public enum Sorting
	{
		SONG,
		ARTIST,
		ALBUM,
		GENRE,
	};
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		applicationFont = Typeface.createFromAsset( getAssets(), "fonts/MAYBE MAYBE NOT.TTF" );
	}
	
	
	
	public Typeface getApplicationTypeface()
	{
		return applicationFont;
	}
}
