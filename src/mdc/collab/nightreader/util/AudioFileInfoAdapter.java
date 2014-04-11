package mdc.collab.nightreader.util;

import java.util.ArrayList;

import mdc.collab.nightreader.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AudioFileInfoAdapter extends BaseAdapter
{
	ArrayList<AudioFileInfo> audioFiles;
	LayoutInflater mInflater;

	public AudioFileInfoAdapter( Context context, ArrayList<AudioFileInfo> results )
	{
		audioFiles = results;
		this.mInflater = LayoutInflater.from( context );
	}
	
	@Override
	public int getCount() {
		return audioFiles.size();
	}

	@Override
	public Object getItem(int item) {
		if( audioFiles.size() - 1 < item ) return null;
		return audioFiles.get( item );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if( convertView == null )
		{
			convertView = mInflater.inflate( R.layout.audio_file_view, null );
			holder = new ViewHolder();
			holder.SongTitleField = (TextView) convertView.findViewById( R.id.SongTitleView );
			holder.ArtistNameField = (TextView) convertView.findViewById( R.id.ArtistNameView );

			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		AudioFileInfo audioFile = audioFiles.get( position );
		holder.SongTitleField.setText( audioFile.getSongTitle() );
		holder.ArtistNameField.setText( audioFile.getArtistName() );

		return convertView;
	}

	static class ViewHolder
	{
		TextView SongTitleField;
		TextView ArtistNameField;
	}
}
