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
	
	private Activity mActivity;
	
	private boolean isRingtoneItemEnd = false; 
	
	/**
	 * Disable edit list 
	 */
	public RingtoneList(int playingMode, Activity activity) 
	{
		super(playingMode);
		mActivity = activity;
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

	/* (non-Javadoc)
	 * @see ins.android.app03.home.AudioList#initializeMediaPlayer()
	 */
	@Override
	public void initializeMediaPlayer() 
	{
		if (getmMediaPlayer() == null)
			return;
		
		/*
		 * Configure when end of 1 playback
		 */
		getmMediaPlayer().setOnCompletionListener(new OnCompletionListener() 
		{
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i("TAG", "Ringtone setOnCompletionListener");
				switch (getmPlayMode()) {
				
				case SINGLE:
					break;
					
				case REPEAT_ONE:
					break;
					
				case REPEAT_ALL:
					
					resetPlayer();
					
					Log.d("TAG", "mplaying = " + getmAudioPlaying());
					
					/*
					 * End of playlist
					 */
					if(getmAudioPlaying() == getCount())
						playMediaPlayer(getAudio(0).getmResSongId(), mActivity.getBaseContext(), 1);
					
					/*
					 * Normal, switch to next song
					 */
					else
					{
						playMediaPlayer(getAudio(getmAudioPlaying()).getmResSongId(), mActivity.getBaseContext(), getmAudioPlaying() + 1);
					}
					
					break;
					
				case PLAY_ALL:
					
					resetPlayer();
					
					if(getmAudioPlaying() < getCount())
					{
						Log.d("TAG", "mplaying = " + getmAudioPlaying());
						playMediaPlayer(getAudio(getmAudioPlaying()).getmResSongId(), mActivity.getBaseContext(), getmAudioPlaying() + 1);
					}
					
					/*
					 *	Last song stop play 
					 */
					
					break;
					
				default:
					break;
				}
				
				setRingtoneItemEnd(true);
			}
		});
		
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
			
			Log.d("TAG", "song id = "+ songId);
			mplayer = MediaPlayer.create(context, songId);
			setmMediaPlayer(mplayer);
			initializeMediaPlayer();
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
	 * @return the isRingtoneItemEnd
	 */
	public boolean isRingtoneItemEnd() {
		return isRingtoneItemEnd;
	}

	/**
	 * @param isRingtoneItemEnd the isRingtoneItemEnd to set
	 */
	public void setRingtoneItemEnd(boolean isRingtoneItemEnd) {
		this.isRingtoneItemEnd = isRingtoneItemEnd;
	}
	
	
	
	
}
