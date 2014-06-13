package ins.android.app03.home;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

public class MySong
{
	private String mSongName;
	private String mSongPath;
	private String mSongArtist;
	private int    mSongDurationSecond;
	private Bitmap mThumbnail;
	private boolean mSelected = false;
	private int mResSongId;
	
	/*For only ringtone */
	private MediaPlayer mPlayer;
	private boolean isChoose = false;
	private int mPlayerState = AudioList.STOP;
	
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
	
	public MySong(String songName, Bitmap thumbnail, int resID)
	{
		this.mSongName = songName;
		this.mThumbnail = thumbnail;
		this.mResSongId = resID;
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

	/**
	 * @return the mSelected
	 */
	public boolean ismSelected() {
		return mSelected;
	}

	/**
	 * @param mSelected the mSelected to set
	 */
	public void setmSelected(boolean mSelected) {
		this.mSelected = mSelected;
	}

	/**
	 * @return the mResSongId
	 */
	public int getmResSongId() {
		return mResSongId;
	}

	/**
	 * @return the mPlayer
	 */
	public MediaPlayer getmPlayer() {
		return mPlayer;
	}

	/**
	 * @param mPlayer the mPlayer to set
	 */
	public void setmPlayer(MediaPlayer mPlayer) {
		this.mPlayer = mPlayer;
	}

	/**
	 * @return the isChoose
	 */
	public boolean isChoose() {
		return isChoose;
	}

	/**
	 * @param isChoose the isChoose to set
	 */
	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	/**
	 * @return the mPlayerState
	 */
	public int getmPlayerState() {
		return mPlayerState;
	}

	/**
	 * @param mPlayerState the mPlayerState to set
	 */
	public void setmPlayerState(int mPlayerState) {
		this.mPlayerState = mPlayerState;
	}
	
}


