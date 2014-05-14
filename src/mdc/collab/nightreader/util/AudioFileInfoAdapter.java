package mdc.collab.nightreader.util;

/**
 * @author Jesse Frush
 */

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AudioFileInfoAdapter<T extends Audio, E extends ArrayList<T>> extends BaseAdapter
{
	//private static NightReader application;
	private LayoutInflater mInflater;
	
	private ArrayList<T> list;

	public AudioFileInfoAdapter( NightReader app, ArrayList<T> list )
	{
		//application = app;
		this.list = list;
		this.mInflater = LayoutInflater.from( app.getApplicationContext() );
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem( int item )
	{
		return list.get( item );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;
		if( convertView == null )
		{
			convertView = mInflater.inflate( R.layout.audio_file_item_view, null );
			holder = new ViewHolder();
			holder.separator = (TextView) convertView.findViewById( R.id.Separator );
			holder.titleField = (TextView) convertView.findViewById( R.id.Title );
			holder.subTitleField = (TextView) convertView.findViewById( R.id.Subtitle );

			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		Audio audio = (Audio) list.get( position );
		String titleString = audio.getTitle();
		String subTitleString = audio.getSubtitle();

		//enable the view separator if we are on the first item or if the first letter of our current item does not match the previous
		if( position == 0 || titleString.charAt( 0 ) != ( (Audio)list.get( position - 1 ) ).getTitle().charAt( 0 ) )
		{
			holder.separator.setVisibility( View.VISIBLE );
			holder.separator.setText( "" + titleString.charAt( 0 ) );
		}
		else
		{
			holder.separator.setVisibility( View.GONE );
		}

		//set the main text field to the Audio item's title string
		holder.titleField.setText( titleString );
		
		//enable/disable the subtitle field, populate if necessary
		if( subTitleString == null || subTitleString.equals( "" ) )
		{
			holder.subTitleField.setVisibility( View.GONE );
		}
		else
		{
			holder.subTitleField.setText( subTitleString );
		}

		return convertView;
	}

	static class ViewHolder
	{
		TextView separator;
		TextView titleField;
		TextView subTitleField;
	}
}
