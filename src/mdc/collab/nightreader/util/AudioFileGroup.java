package mdc.collab.nightreader.util;

import java.util.ArrayList;

public class AudioFileGroup implements Audio
{
	private String title;
	private String subtitle;
	private ArrayList<AudioFileInfo> items;
	
	public AudioFileGroup( String title, String subtitle )
	{
		this.title = title;
		this.subtitle = subtitle;
		items = new ArrayList<AudioFileInfo>();
	}
	
	
	/**
	 * adds an item to the list
	 */
	public void addItem( AudioFileInfo item )
	{
		items.add( item );
	}
	
	
	/**
	 * returns the list of items
	 */
	public ArrayList<AudioFileInfo> getItems()
	{
		return items;
	}
	
	
	/**
	 * returns the name of the grouping
	 */
	@Override
	public String getTitle()
	{
		return title;
	}
	
	
	/**
	 * returns the name of the grouping
	 */
	@Override
	public String getSubtitle()
	{
		return "";
	}
}
