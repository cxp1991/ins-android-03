package ins.android.app03.home;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public abstract class AudioList {
	
	/*
	 *  Playing Mode
	 */
	public static final int PLAY_ALL = 0x00; // play [1 to end]
	public static final int SINGLE = 0x01; // just 1 
	public static final int REPEAT_ONE = 0x02; // just 1 forever
	public static final int REPEAT_ALL = 0x03; // play [1 ... end] forever
	public static final int SHUFFLE = 0x04; // random
	public static final int PAUSE = 0x05;
	public static final int PLAYING = 0x06;
	public static final int STOP = 0x07;
	
	private ArrayList<MySong> mAudioList = new ArrayList<MySong>();

	private int mPlayMode;
	private int mState;
	private int mAudioPlaying;
	private boolean mIsEnableEditList;
	private MediaPlayer mMediaPlayer;
	
	public AudioList(int playingMode) 
	{
		this.mPlayMode = playingMode;
		this.mState = STOP;
		this.mAudioPlaying = -1;
	}

	public int getCount()
	{
		return this.mAudioList.size();
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
	public MySong getAudio(int index) {
		
		/* Invalid request */
		Log.d("TAG", "mAudioList.size() = " + mAudioList.size() + ", index = " + index );
		if(mAudioList.size() == 0 || index < 0)
			return null;
		
		return mAudioList.get(index);
	}


	/**
	 * @param mAudioSongList the mAudioSongList to set
	 */
	public void addmAudio(MySong audio) {
		//if (ismIsEnableEditList())
			this.mAudioList.add(audio);
		//else {
		//	Log.w("setmAudioList", "Could not edit this List");
		//}
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
		Log.e("TAG", "Set State = " + mState);
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
	
	/**
	 * @return the mAudioPlaying
	 */
	public int getmAudioPlaying() {
		return mAudioPlaying;
	}

	/**
	 * @param mAudioPlaying the mAudioPlaying to set
	 */
	public void setmAudioPlaying(int index) {
		this.mAudioPlaying = index;
	}

	/**
	 * Start player with song at index
	 * @param index
	 */
	public void playMediaPlayer(int index)
	{
		/*
		 *	index = 1,2,3,4
		 *  song = 0,1,2,3
		 *  So: song_index = index - 1 
		 */
		MySong song = this.getAudio(index-1);
		
		try 
		{
			/*
			 * Stop old player
			 */
			if (getmState() == PLAYING || getmState() == PAUSE)
				this.stopMediaPlayer();
			
			Log.i("TAG", "song " + song + " index " + index);
			mMediaPlayer.setDataSource(song.getmSongPath());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			Log.e("TAG", "Playing");
			mState = PLAYING;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		this.setmAudioPlaying(index);
	}
	
	/**
	 * Pause player
	 */
	public void pauseMediaPlayer()
	{
		if (mMediaPlayer != null){
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.pause();
			//Log.e("TAG", "Pause");
			mState = PAUSE;
		}
	}
	
	/**
	 * Stop player
	 */
	public void stopMediaPlayer()
	{
		if (mMediaPlayer != null){
			if (getmState() != STOP)
			{
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
			//Log.e("TAG", "Pause");
			mState = STOP;
		}
	}
	
	/**
	 * Resume player
	 */
	public void resumePlayer()
	{
		if (mMediaPlayer != null){
			mMediaPlayer.start();
			//Log.e("TAG", "Pause");
			mState = PLAYING;
		}
	}
	
	/**
	 * Reset player
	 * */
	public void resetPlayer()
	{
		if (mMediaPlayer != null)
			this.mMediaPlayer.reset();
	}
	
	/**
	 * Release player
	 */
	public void releasePlayer()
	{
		if (mMediaPlayer != null)
			this.mMediaPlayer.release();
	}
	
	/**
	 * Set looping
	 */
	public void setLooping(boolean value)
	{
		this.mMediaPlayer.setLooping(value);
	}
	
	
	/**
	 * @return the mMediaPlayer
	 */
	public MediaPlayer getmMediaPlayer() {
		return mMediaPlayer;
	}
	
	/**
	 * @param mMediaPlayer the mMediaPlayer to set
	 */
	public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
		this.mMediaPlayer = mMediaPlayer;
	}
	
	public abstract void initialize ();
	
}
