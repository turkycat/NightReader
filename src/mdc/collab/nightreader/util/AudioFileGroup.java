package mdc.collab.nightreader.util;

import java.util.ArrayList;

public class AudioFileGroup
{
	private String name;
	private ArrayList<AudioFileInfo> items;
	
	public AudioFileGroup( String name )
	{
		this.name = name;
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
	public String getName()
	{
		return name;
	}
}
