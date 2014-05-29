package ins.android.app03.home;

import android.graphics.drawable.Drawable;

class SongList 
{
	private int songId;
	private String songName;
	private Drawable songImage;
	
	/* Status */
	private int status;
	public static final int PLAYING_STATE = 0x01;
	public static final int PAUSED_STATE = 0x02;
	
	/* Working modes */
	private int workingMode;
	public final int AUTO_SWITCH_NEXT_SONG_MODE = 0x03;
	public final int REPLAY_MODE = 0x03;
	
	public SongList(int songId, String songName, Drawable songImage) 
	{
		this.songId = songId;
		this.songName = songName;
		this.songImage = songImage;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public void setWoringMode (int workingMode)
	{
		this.workingMode = workingMode;
	}
	
	public void setSongImage (Drawable drawable)
	{
		this.songImage = drawable;
	}
	
	public int getSongID()
	{
		return this.songId;
	}
	
	public String getSongName()
	{
		return this.songName;
	}
	
	public int getStatus()
	{
		return this.status;
	}
	
	public int getWoringMode ()
	{
		return this.workingMode;
	}
	
	public Drawable getSongDrawable()
	{
		return this.songImage;
	}
	
}