package ins.android.app03.home;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class SongList extends AudioList
{
	private MediaPlayer mPlayer;
	private OnEndSongListener mEndSongListener = null;

	public SongList(int playingMode) 
	{
		super(REPEAT_ALL);
		mPlayer = new MediaPlayer();
		this.setmMediaPlayer(mPlayer);
		initialize();
	}

	@Override
	public void initialize() {
		/*
		 * Configure when end of 1 playback
		 */
		mPlayer.setOnCompletionListener(new OnCompletionListener() 
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
						mEndSongListener.onEndSong(1);
					}
					
					/*
					 * Normal, switch to next song
					 */
					else
					{
						Log.i("onCompletion", "Not end of playlist");
						playMediaPlayer(getmAudioPlaying() + 1);
						mEndSongListener.onEndSong(getmAudioPlaying());
					}
					
					break;
					
				case PLAY_ALL:
					
					resetPlayer();
					
					if(getmAudioPlaying() < getCount()) {
						playMediaPlayer(getmAudioPlaying() + 1);
						mEndSongListener.onEndSong(getmAudioPlaying());
					}
					
					/*
					 *	Last song stop play 
					 */
					
					break;
					
				default:
					break;
				}
				
			}
		});
	}
	
	
	public void setEndSongListener(OnEndSongListener listener) 
	{
	    this.mEndSongListener = listener;
	}
	
	public interface OnEndSongListener 
	{
	    public void onEndSong (int newIndex);
	}
}
