package ins.android.app03.home;


import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public abstract class PlayList 
{

	private ArrayList<SongList> mSongList = new ArrayList<SongList>();

	public PlayList()
	{
	}
	
	public void setSongList(int songId, String songName, Drawable songImage)
	{
		mSongList.add(new SongList(songId, songName, songImage));
	}
	
	public SongList getSongList(int index)
	{
		return mSongList.get(index);
	}
	
	public abstract void initializePlayList();
	public abstract void savePlayListStatus();
}

