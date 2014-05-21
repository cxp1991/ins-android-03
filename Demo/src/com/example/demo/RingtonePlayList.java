package com.example.demo;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class RingtonePlayList extends PlayList 
{
	private ArrayList<String> songName    = new ArrayList<String>();
	private ArrayList<Integer> songId 	  = new ArrayList<Integer>();
	private ArrayList<Drawable> songImage = new ArrayList<Drawable>();
	private int numberSongs = 0;
	
	public void changeImageBackground (View view, int index)
	{
		ImageView imgView = (ImageView) view;
		imgView.setImageDrawable(this.getSongList(index).getSongDrawable());
	}
	
	public void setNumberSong (int number)
	{
		this.numberSongs = number;
	}
	
	public int getNumberSong ()
	{
		return this.numberSongs;
	}
	
	@Override
	public void initializePlayList() 
	{
		/* Init songName */
		
		/* Init song ID */
		
		/* Init song image */
		
	}

	@Override
	public void savePlayListStatus() 
	{
	}
	
	
	
}
