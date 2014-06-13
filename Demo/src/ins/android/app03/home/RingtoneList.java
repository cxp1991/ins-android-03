package ins.android.app03.home;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
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
	}
	

	public void playMediaPlayer(Context context, int index)
	{
		try 
		{
			int songId = getAudio(index-1).getmResSongId();
			MediaPlayer mplayer = MediaPlayer.create(context, songId);
			getAudio(index-1).setmPlayer(mplayer);
			mplayer.setLooping(true);
			mplayer.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return;
		
	}
	
	public void playMediaPlayer(Context context, MySong song)
	{
		try 
		{
			int songId = song.getmResSongId();
			MediaPlayer mplayer = MediaPlayer.create(context, songId);
			song.setmPlayer(mplayer);
			mplayer.setLooping(true);
			mplayer.start();
		} 
		catch (Exception e) 
		{}
		
		return;
		
	}

		public void stopPlayer(int index) {
		MediaPlayer player = getAudio(index-1).getmPlayer();
		if (player != null) {
			player.stop();
			player.reset();
			getAudio(index-1).setmPlayerState(STOP);
		}
	}
	
	public void pausePlayer (MySong song) {
			if (song.getmPlayer() != null) {
				song.getmPlayer().pause();
				song.setmPlayerState(PAUSE);
			}
	}
	
	public void resumePlayer (MySong song) {
		if (song.getmPlayer() != null) {
			song.getmPlayer().start();
			song.setmPlayerState(PLAYING);
		}
		
	}

	@Override
	public void setVolume(float value) {
		Log.i("", "Ringtone set volume");
		for (MySong song : getmAudioList()) {
			if (song.getmPlayer() != null) {
				song.getmPlayer().setVolume(value, value);
			}
		}
	}

}
