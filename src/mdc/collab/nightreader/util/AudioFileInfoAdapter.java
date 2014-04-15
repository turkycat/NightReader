package mdc.collab.nightreader.util;

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import mdc.collab.nightreader.application.NightReader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AudioFileInfoAdapter extends BaseAdapter
{
	private static NightReader application;
	private ArrayList<AudioFileInfo> audioFiles;
	private LayoutInflater mInflater;

	public AudioFileInfoAdapter( NightReader app, ArrayList<AudioFileInfo> results )
	{
		application = app;
		this.audioFiles = results;
		this.mInflater = LayoutInflater.from( application.getApplicationContext() );
	}

	@Override
	public int getCount()
	{
		return audioFiles.size();
	}

	@Override
	public Object getItem( int item )
	{
		if( audioFiles.size() - 1 < item ) return null;
		return audioFiles.get( item );
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
			holder.Separator = (TextView) convertView.findViewById( R.id.Separator );
			holder.SongTitleField = (TextView) convertView.findViewById( R.id.Title );
			holder.ArtistNameField = (TextView) convertView.findViewById( R.id.Subtitle );

			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		AudioFileInfo audioFile = audioFiles.get( position );

		String titleString = getTitleString( position );

		if( position == 0 || titleString.charAt( 0 ) != getTitleString( position - 1 ).charAt( 0 ) )
		{
			holder.Separator.setVisibility( View.VISIBLE );
			holder.Separator.setText( "" + titleString.charAt( 0 ) );
		}
		else
		{
			holder.Separator.setVisibility( View.GONE );
		}

		holder.SongTitleField.setText( titleString );
		holder.ArtistNameField.setText( audioFile.getArtistName() );

		return convertView;
	}

	static class ViewHolder
	{
		TextView Separator;
		TextView SongTitleField;
		TextView ArtistNameField;
	}

	private String getTitleString( int position )
	{
		if( position < 0 || position >= audioFiles.size() ) return "Unknown";

		AudioFileInfo requestedFile = audioFiles.get( position );
		switch( application.getSorting() )
		{
		case SONG:
			return requestedFile.getSongTitle();

		case ARTIST:
			return requestedFile.getArtistName();

		case ALBUM:
			return "Not Yet Implemented.";

		case GENRE:
			return "Not Yet Implemented.";
		}
		
		return "Unknown";
	}
}
