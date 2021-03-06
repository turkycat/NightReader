package mdc.collab.nightreader.dialog;

import mdc.collab.nightreader.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
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
		
		// Create the AlertDialog object and return it
		Dialog dialog = builder.create();
		dialog.getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
		
		return dialog;
	}
}
