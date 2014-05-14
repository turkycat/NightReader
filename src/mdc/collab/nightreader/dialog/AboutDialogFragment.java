package mdc.collab.nightreader.dialog;

import mdc.collab.nightreader.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AboutDialogFragment extends DialogFragment
{

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setView( getActivity().getLayoutInflater().inflate( R.layout.about_dialog, null ) );
//		builder.setMessage( R.string.dialog_main_string );
//		builder.setPositiveButton( R.string.sweet, new DialogInterface.OnClickListener()
//		{
//			public void onClick( DialogInterface dialog, int id )
//			{
//				// FIRE ZE MISSILES!
//			}
//		} );
//		builder.setNegativeButton( R.string.dude, new DialogInterface.OnClickListener()
//		{
//			public void onClick( DialogInterface dialog, int id )
//			{
//				// User cancelled the dialog
//			}
//		} );
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
