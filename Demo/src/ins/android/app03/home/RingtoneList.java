package ins.android.app03.home;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class RingtoneList extends AudioList
{

	/**
	 * Ringtone's id in /res/raw
	 * */
	private int[] mRingtoneResourceID = new int []
			{
				R.raw.flute,
				R.raw.frog,
				R.raw.nighttime,
				R.raw.rain,
				R.raw.river,
				R.raw.thunder,
				R.raw.whistle,
				R.raw.wind
			};
	
	/**
	 * Name of ringtone
	 */
	private String[] mRingtoneName = new String[] 
	{
		"Flute", "Frog", "Nighttime", "Rain", "River", "Thunder", "Whistle", "Wind"
	};
	
	private int NUMER_RINGTOMES = 8;
	
	private MediaPlayer mPlayer;
	
	/**
	 * Disable edit list 
	 */
	public RingtoneList(int playingMode, Activity activity) 
	{
		super(REPEAT_ONE);
		this.setmMediaPlayer(mPlayer);
		initializeRingtone(activity);
	}
	
	/**
	 * Initialize ringtone list
	 * @param activity
	 */
	public void initializeRingtone(Activity activity)
	{
		MySong song;
		
		for (int i = 0; i < NUMER_RINGTOMES; i++)
		{
			song = new MySong(mRingtoneName[i], null, mRingtoneResourceID[i]);
			this.addmAudio(song);
		}
		
		Log.i("TAG", "Init ringtone done!");
	}

	@Override
	public void initialize() {
		if (getmMediaPlayer() == null)
			return;
		
		if (getmPlayMode() == REPEAT_ONE){
			Log.i("TAG", "ringtoe looping");
			getmMediaPlayer().setLooping(true);
		}		
	}
	

	public void playMediaPlayer(int songId, Context context, int index)
	{
		try 
		{
			MediaPlayer mplayer = getmMediaPlayer();
			
			/*
			 * Stop old player
			 */
			if (mplayer != null && mplayer.isPlaying())
				this.pauseMediaPlayer();
			
			mplayer = MediaPlayer.create(context, songId);
			setmMediaPlayer(mplayer);
			initialize();
			mplayer.start();
			Log.e("TAG", "Playing");
			this.setmState(PLAYING);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		this.setmAudioPlaying(index);
		return;
		
	}

	/**
	 * @param mVolume the mVolume to set
	 */
	public void setmVolume(float mVolume) {
		
		try {
			this.getmMediaPlayer().setVolume(mVolume, mVolume);
		} catch (Exception e) {
			
		}
	}

}
