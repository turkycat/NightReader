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

public class AudioFileInfoAdapter<E extends ArrayList<Audio>> extends BaseAdapter
{
	private static NightReader application;
	private LayoutInflater mInflater;
	
	private ArrayList<Audio> list;

	public AudioFileInfoAdapter( NightReader app, ArrayList<Audio> list )
	{
		application = app;
		this.list = list;
		this.mInflater = LayoutInflater.from( application.getApplicationContext() );
	}

	@Override
	public int getCount()
	{
//		switch( application.getSorting() )
//		{
//		default:
//			return application.getAllAudioFiles().size();
//			
//		case ALBUM:
//			return application.getAlbums().size();
//			
//		case ARTIST:
//			return application.getArtists().size();
//		}
		return list.size();
	}

	@Override
	public Object getItem( int item )
	{
//		switch( application.getSorting() )
//		{
//		default:
//			return application.getAllAudioFiles().get( item );
//			
//		case ALBUM:
//			return application.getAlbums().get( item );
//			
//		case ARTIST:
//			return application.getArtists().get( item );
//		}
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
		
		
//		switch( application.getSorting() )
//		{
//		default:
//		case SONG:
//			
//			
//			
//		case ALBUM:
//
//			ArrayList<AudioFileGroup> albums;
//			int size = albums.size();
//			
//			for( int i = 0; i < size; ++i )
//			{
//				
//			}
//			
//			break;
//		}
		
		
		Audio audio = list.get( position );
		String titleString = audio.getTitle();
		String subTitleString = audio.getSubtitle();

		if( position == 0 || titleString.charAt( 0 ) != list.get( position - 1 ).getTitle().charAt( 0 ) )
		{
			holder.separator.setVisibility( View.VISIBLE );
			holder.separator.setText( "" + titleString.charAt( 0 ) );
		}
		else
		{
			holder.separator.setVisibility( View.GONE );
		}

		holder.titleField.setText( titleString );
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
	


//	private String getTitleString( int position )
//	{
//		if( position < 0 || position >= audioFiles.size() ) return "Unknown";
//
//		AudioFileInfo requestedFile = audioFiles.get( position );
//		switch( application.getSorting() )
//		{
//		case SONG:
//			return requestedFile.getSongTitle();
//
//		case ARTIST:
//			return requestedFile.getArtistName();
//
//		case ALBUM:
//			return requestedFile.getAlbumName();
//
//		case GENRE:
//			return requestedFile.getGenre();
//		}
//		
//		return "Unknown";
//	}
}
