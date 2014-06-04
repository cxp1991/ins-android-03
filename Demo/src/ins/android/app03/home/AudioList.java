package ins.android.app03.home;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AudioList {
	
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
	
	private ArrayList<MySong> mAudioList = new ArrayList<MySong>();

	private int mPlayMode = SINGLE;
	private int mState = PAUSE;
	private int mAudioPlaying = 0;
	private boolean mIsEnableEditList;
	private MediaPlayer mMediaPlayer;

	private boolean isEndOnePlayback = false;
	
	public AudioList(int playingMode, boolean isEnableEditList) 
	{
		this.setmPlayMode(playingMode);
		this.setmIsEnableEditList(isEnableEditList);
		this.mMediaPlayer = new MediaPlayer(); 
		this.setmState(PAUSE);
		initializeMediaPlayer();
	}
	
	public AudioList(int playingMode) 
	{
		this.setmPlayMode(playingMode);
		this.setmState(PAUSE);
	}

	/**
	 * Initialize Mediaplayer
	 */
	public void initializeMediaPlayer() 
	{
		Log.i("TAG", "Audiolist constructor");
		
		/*
		 * Configure when end of 1 playback
		 */
		this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() 
		{
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i("onCompletion", "setOnCompletionListener");
				Log.i("onCompletion", "Play mode = " + getmPlayMode());
				switch (getmPlayMode()) {
				
				case SINGLE:
					break;
					
				case REPEAT_ONE:
					break;
					
				case REPEAT_ALL:
					
					resetPlayer();
					
					/*
					 * End of playlist
					 */
					Log.i("onCompletion", "Audio plauing index = " + getmAudioPlaying());
					Log.i("onCompletion", "Count = " + getCount());
					if(getmAudioPlaying() == getCount())
					{
						Log.i("onCompletion", "End of playlist");
						playMediaPlayer(1);
					}
					
					/*
					 * Normal, switch to next song
					 */
					else
					{
						Log.i("onCompletion", "Not end of playlist");
						playMediaPlayer(getmAudioPlaying() + 1);
					}
					
					break;
					
				case PLAY_ALL:
					
					resetPlayer();
					
					if(getmAudioPlaying() < getCount())
						playMediaPlayer(getmAudioPlaying() + 1);
					
					/*
					 *	Last song stop play 
					 */
					
					break;
					
				default:
					break;
				}
				
				isEndOnePlayback = true;
			}
		});
		
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
			if (this.mMediaPlayer.isPlaying())
				this.pauseMediaPlayer();
			
			mMediaPlayer.setDataSource(song.getmSongPath());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			Log.e("TAG", "Playing");
			this.setmState(PLAYING);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		this.setmAudioPlaying(index);
	}
	
	/**
	 * Stop player
	 */
	public void pauseMediaPlayer()
	{
		if (mMediaPlayer != null){
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.reset();
			Log.e("TAG", "Pause");
			this.setmState(PAUSE);
		}
	}
	
	/**
	 * Reset player
	 * */
	public void resetPlayer()
	{
		this.mMediaPlayer.reset();
	}
	
	/**
	 * Release player
	 */
	public void releasePlayer()
	{
		this.mMediaPlayer.release();
	}
	
	/**
	 * @return the isEndOnePlayback
	 */
	public boolean isEndOnePlayback() {
		return isEndOnePlayback;
	}

	/**
	 * Set looping
	 */
	public void setLooping(boolean value)
	{
		this.mMediaPlayer.setLooping(value);
	}
	
	/**
	 * @param isEndOnePlayback the isEndOnePlayback to set
	 */
	public void setEndOnePlayback(boolean isEndOnePlayback) {
		this.isEndOnePlayback = isEndOnePlayback;
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
}
