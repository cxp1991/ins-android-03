package com.example.demo;

import android.graphics.Bitmap;

public class MySong
{
	private String mSongName;
	private String mSongPath;
	private String mSongArtist;
	private int    mSongDurationSecond;
	private Bitmap mThumbnail;
	
	/**
	 * Constructor 
	 * 
	 * @param songName
	 * @param songArtist
	 * @param songDurationSecond
	 * @param songPath
	 * @param thumbnail
	 */
	public MySong (
			String songName,
			String songArtist, 
			int songDurationSecond, 
			String songPath, Bitmap thumbnail)
	{
		this.mSongName = songName;
		this.mSongArtist = songArtist;
		this.mSongDurationSecond = songDurationSecond;
		this.mSongPath = songPath;
		this.mThumbnail = thumbnail;
	}

	/**
	 * @return the mSongName
	 */
	public String getmSongName() {
		return mSongName;
	}

	/**
	 * @return the mSongPath
	 */
	public String getmSongPath() {
		return mSongPath;
	}

	/**
	 * @return the mSongArtist
	 */
	public String getmSongArtist() {
		return mSongArtist;
	}

	/**
	 * @return the mSongDurationSecond
	 */
	public int getmSongDurationSecond() {
		return mSongDurationSecond;
	}
	
	/**
	 * @return the mThumbnail
	 */
	public Bitmap getmThumbnail() {
		return mThumbnail;
	}
}


