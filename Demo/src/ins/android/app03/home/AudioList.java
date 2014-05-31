package ins.android.app03.home;

import java.util.ArrayList;

import android.util.Log;

public class AudioList {
	
	/* Playing Mode */
	public static final int NORMAL = 0x01;
	public static final int REPEAT_ONE = 0x02;
	public static final int REPEAT_ALL = 0x03;
	public static final int SHUFFLE = 0x04;
	public static final int PAUSE = 0x05;
	public static final int PLAYING = 0x06;
	
	private ArrayList<MySong> mAudioList = new ArrayList<MySong>();

	private int mPlayMode = NORMAL;
	private int mState = PAUSE;
	private int mAudioPlaying;
	private boolean mIsEnableEditList;
	
	public AudioList(int playingMode, boolean isEnableEditList) 
	{
		this.setmPlayMode(playingMode);
		this.setmIsEnableEditList(isEnableEditList);
	}
	
	/**
	 * @return the mAudioList
	 */
	public ArrayList<MySong> getmAudioList() {
		return mAudioList;
	}

	/**
	 * @param mAudioList the mAudioList to set
	 */
	public void setmAudioList(ArrayList<MySong> mAudioList) {
		this.mAudioList = mAudioList;
	}
	
	/**
	 * @return the mAudioSongList
	 */
	public MySong getmAudioSongListItem(int index) {
		return mAudioList.get(index);
	}


	/**
	 * @param mAudioSongList the mAudioSongList to set
	 */
	public void addmAudioSongListItem(MySong audio) {
		if (ismIsEnableEditList())
			this.mAudioList.add(audio);
		else {
			Log.w("setmAudioList", "Could not edit this List");
		}
	}


	/**
	 * @return the mPlayMode
	 */
	public int getmPlayMode() {
		return mPlayMode;
	}


	/**
	 * @param mPlayMode the mPlayMode to set
	 */
	public void setmPlayMode(int mPlayMode) {
		this.mPlayMode = mPlayMode;
	}


	/**
	 * @return the mState
	 */
	public int getmState() {
		return mState;
	}


	/**
	 * @param mState the mState to set
	 */
	public void setmState(int mState) {
		this.mState = mState;
	}


	/**
	 * @return the mAudioPlaying
	 */
	public int getmAudioPlaying() {
		return mAudioPlaying;
	}


	/**
	 * @param mAudioPlaying the mAudioPlaying to set
	 */
	public void setmAudioPlaying(int mAudioPlaying) {
		this.mAudioPlaying = mAudioPlaying;
	}


	/**
	 * @return the mIsEnableEditList
	 */
	public boolean ismIsEnableEditList() {
		return mIsEnableEditList;
	}


	/**
	 * @param mIsEnableEditList the mIsEnableEditList to set
	 */
	public void setmIsEnableEditList(boolean mIsEnableEditList) {
		this.mIsEnableEditList = mIsEnableEditList;
	}
	
}
