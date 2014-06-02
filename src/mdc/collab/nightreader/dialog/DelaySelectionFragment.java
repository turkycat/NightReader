package mdc.collab.nightreader.dialog;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.activities.MainActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class DelaySelectionFragment extends DialogFragment
{
	private SeekBar selectionBar;
	private TextView selectionText;
	private Button confirm;
	private Dialog dialog;

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		View inflated = getActivity().getLayoutInflater().inflate( R.layout.delay_selection_dialog, null );
		
		//grab the selection bar and set a value changed listener to modify the text field
		selectionBar = (SeekBar) inflated.findViewById( R.id.delay_selection_bar );
		selectionBar.setProgress( ( (MainActivity)getActivity() ).getTimerDelay() );
		selectionBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
			{
				selectionText.setText( "" + progress + " Minutes." );
			}

			@Override
			public void onStartTrackingTouch( SeekBar seekBar ) {}

			@Override
			public void onStopTrackingTouch( SeekBar seekBar ) {}
		} );

		//grab a reference to the selection text and set an initial text value
		selectionText = (TextView) inflated.findViewById( R.id.selection_bar_text_field );
		selectionText.setText( "" + selectionBar.getProgress() + " Minutes." );

		//grab the button so that we can manually dismiss the dialog as well as save the selected settings
		confirm = (Button) inflated.findViewById( R.id.timer_settings_confirm );
		confirm.setOnClickListener( new View.OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				int delay = selectionBar.getProgress();
				MainActivity activity = (MainActivity) getActivity();
				activity.setTimerDelay( delay );
				dialog.dismiss();
			}
			
		} );
		builder.setView( inflated );
		
		// Create the AlertDialog object and return it
		dialog = builder.create();
		dialog.getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
		
		return dialog;
	}
}
