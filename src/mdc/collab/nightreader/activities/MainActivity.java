package mdc.collab.nightreader.activities;

import mdc.collab.nightreader.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		//this is where we will resume next time yay!!
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

}
