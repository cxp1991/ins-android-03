package ins.android.app03.home;

import ins.android.app03.home.SongList.OnEndSongListener;
import ins.android.app03.listsong.ListSongFragment;
import ins.android.app03.main.Main;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemAddListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;


public class HomeFragment extends Fragment
{
	/* FIXME: I have used static variable to save fragment's 
	 * state when user switch between fragsments.
	 * */
	
	private MyHorizontalScrollView musicHrScrollView;
	private MyHorizontalScrollView ringtoneHrScrollView;
	
	/*
	 * RingtoneList & MusicList
	 * Using static to Save state of data after fragment commit times
	 * */
	public static SongList mSongList = null; 
	public static RingtoneList mRingtoneList = null; 
	
	public static View rootView = null;
	
	private TextView mTextViewLoading = null;
	
	private Button buttonPlayAll = null;
	
	private Context mContext;
	
	private Activity mActivity = null;
	
	private LinearLayout mVolumeLayout = null;
	
	private int mFutureInMillis = 5000;
	
	private SeekBar mRingtoneSeekBar = null;
	
	private SeekBar mMusicSeekBar = null;
	
	private AudioManager mAudioManager = null;
	
	private int mNumberSongChecked = 0;
	
	private final int PLAYBACK_PAUSE = 0x01;
	private final int PLAYBACK_PLAYING = 0x02;
	private final int PLAYBACK_STOP = 0x03;
	
	private int systemPlaybackState = PLAYBACK_STOP;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("", "oncreateView");
		
		mContext = getActivity().getBaseContext();
		mActivity = getActivity();
		
		/*
		 * Because user will switch bween many fragments usually, we
		 * want this HomeFragment's layout is stable. Then my solution is 
		 * use static view to save this layout.
		 */
		if (rootView == null)
		{
			rootView = inflater.inflate(R.layout.activity_main, container, false);
			mRingtoneList = new RingtoneList(AudioList.REPEAT_ONE, getActivity());
			mSongList = new SongList(AudioList.REPEAT_ALL, getActivity());
		}
        
        /* 
         * Fragment need it to add item to Actionbar 
         * */
        setHasOptionsMenu(true);
        
        return rootView;
    }
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinishListener);
        if (mSongList.getCount() == 0) {
        	ringtoneHrScrollView.setVisibility(View.INVISIBLE);
        }
        
        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        musicHrScrollView.setOnThumbnailLongTouchListener(musicOnLongTouchListener);
        musicHrScrollView.setOnItemAddListener(musicItemAddListener);
        
        /*
         * Button stop play/pause both ringtone & music
         */
        buttonPlayAll = (Button) rootView.findViewById(R.id.btplayall);
        buttonPlayAll.setOnClickListener(buttonPlayAllOnClickListener);
        
        if (mSongList.getmState() == AudioList.PLAYING) {
        	buttonPlayAll.setText("Pause");
        } else {
        	buttonPlayAll.setText("Play");
        }

        /*
         * Volume controller
         */
        mVolumeLayout = (LinearLayout) getView().findViewById(R.id.ln_volume);
        mVolumeLayout.setVisibility(View.GONE);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mRingtoneSeekBar = (SeekBar) getView().findViewById(R.id.ringtone_volume_seekbar);
        mMusicSeekBar = (SeekBar) getView().findViewById(R.id.music_volume_seekbar);
        mRingtoneSeekBar.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mMusicSeekBar.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mRingtoneSeekBar.setProgress(mAudioManager
        .getStreamVolume(AudioManager.STREAM_MUSIC));
mMusicSeekBar.setProgress(mAudioManager
        .getStreamVolume(AudioManager.STREAM_MUSIC));

        
        mSongList.setEndSongListener(new OnEndSongListener() {
			
			@Override
			public void onEndSong(int index) {
				Log.i("", "setEndSongListener, index = " + index);
				
				musicHrScrollView.updateLayout(index);
				musicHrScrollView.setCenterIndex(index);

			}
        });
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("FRAGMENT", "onResume");
        Log.i("FRAGMENT", "onResume, mNumberSongChecked = " + mNumberSongChecked);
        
        if(mNumberSongChecked > 0)
        {      	
        	for (MySong song : SongManager.mListAllSong)
        	{
        		/* Add new selected item */
        		if (song.ismSelected())
        		{
        			mSongList.addmAudio(song);
        			musicHrScrollView.addThumbnailToParent();
        			
        			/* 
        			 * To add 1 song after, 
        			 * we uncheck slected item in listview
        			 */
	    			song.setmSelected(false);
	    			mNumberSongChecked = 0;
        		}
        	}
        }
	}

	/**
	 * Button Play All listener
	 */
	OnClickListener buttonPlayAllOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			/* From STOP state */
			if (mSongList.getmState() == AudioList.STOP) {
				
				mSongList.playMediaPlayer(1);
				musicHrScrollView.updateLayout(1);
				musicHrScrollView.setCenterIndex(1);
				
				for (MySong song : mRingtoneList.getmAudioList()) {
					if (song.isChoose()) {
						if (song.getmPlayerState() == AudioList.PAUSE) {
							mRingtoneList.resumePlayer(song);
						} else {
							mRingtoneList.playMediaPlayer(getActivity().getBaseContext(), song);
						}
					}
				}
			
			/* From PAUSE state */
			} else if (mSongList.getmState() == AudioList.PAUSE) {
				
				if (mSongList.getmAudioPlaying() == musicHrScrollView.getCenterIndex()) {
					mSongList.resumePlayer();
				} else {
					mSongList.playMediaPlayer(musicHrScrollView.getCenterIndex());
				}
				
				for (MySong song : mRingtoneList.getmAudioList()) {
					if (song.isChoose()) {
						Log.i("", "song = " + song.getmSongName());
						Log.i("", "song = " + song.getmPlayerState());
						if (song.getmPlayerState() == AudioList.PAUSE) {
							mRingtoneList.resumePlayer(song);
						} else {
							mRingtoneList.playMediaPlayer(getActivity().getBaseContext(), song);
						}
					}
				}
				
				buttonPlayAll.setText("Pause");
				
			/* From PLAYING state */
			} else if (mSongList.getmState() == AudioList.PLAYING) {
				
				mSongList.pauseMediaPlayer();
			
				for (MySong song : mRingtoneList.getmAudioList()) {
					if (song.isChoose()) {
						mRingtoneList.pausePlayer(song);
					}
				}
				
				buttonPlayAll.setText("Play");
				
			}
			
		}
	};
	
	/**
	 * Add menu tinto Actionbar
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.homemenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
		case R.id.action_add_song:
			while(!((Main)getActivity()).isInitializeListSongDone());
			Intent intent = new Intent(getActivity(), ListSongFragment.class);
			startActivityForResult(intent, 0);
			break;
			
		case R.id.action_volume:
			
			if (mVolumeLayout.getVisibility() == View.GONE) {
				
				mVolumeLayout.setVisibility(View.VISIBLE);
				
				mRingtoneSeekBar.setProgress(mAudioManager
 	                .getStreamVolume(AudioManager.STREAM_MUSIC));
				mMusicSeekBar.setProgress(mAudioManager
    	                .getStreamVolume(AudioManager.STREAM_MUSIC));
				
				/*
				 * User not touch in 5s, then close
				 */
				CountDownTimer remainingTimeCounter = new CountDownTimer(mFutureInMillis, 1000) {

					private CountDownTimer mCountDownTimer = this;
					
		            public void onTick(long millisUntilFinished) {
		            	
		            	
		            	mMusicSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							
							@Override
							public void onStopTrackingTouch(SeekBar arg0) {
								
							}
							
							@Override
							public void onStartTrackingTouch(SeekBar arg0) {
							}
							
							@Override
							public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
								Log.i("","onProgressChanged");
								mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
			                            progress, 0);
								mFutureInMillis = 5000;
								mCountDownTimer.start();
							}
						});
		            	
		            	mRingtoneSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							
							@Override
							public void onStopTrackingTouch(SeekBar arg0) {
								
							}
							
							@Override
							public void onStartTrackingTouch(SeekBar arg0) {
							}
							
							@Override
							public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
								Log.i("","max= " + mRingtoneSeekBar.getMax());
								mRingtoneList.setVolume((float)progress/mRingtoneSeekBar.getMax());
								mFutureInMillis = 5000;
								mCountDownTimer.start();
							}
						});
		            }

					@Override
					public void onFinish() {
						mVolumeLayout.setVisibility(View.GONE);
					}
		        }.start();
		        
			} else {
				mVolumeLayout.setVisibility(View.GONE);
			}
			
			break;
			
		case R.id.action_remove_all_song:
			// Not null list 
			if (musicHrScrollView.getNumberThumbnail() > 0)
			{
				LinearLayout lnlayout = musicHrScrollView.getTopLnLayout();
				lnlayout.removeViews(1, musicHrScrollView.getNumberThumbnail());
				musicHrScrollView.setNumberThumbnail(0);
				musicHrScrollView.setCenterIndex(0);
				mSongList.getmAudioList().clear();
				mSongList.stopMediaPlayer();
				
				mRingtoneList.pauseMediaPlayer();
			}
			
			break;
		case R.id.action_scroll_to_head:
			// Not null list & item is highlighting is not first item  
			if (musicHrScrollView.getNumberThumbnail() > 0 &&
					(musicHrScrollView.getCenterIndex() != 1))
			{
				musicHrScrollView.setCenterIndex(1);
				musicHrScrollView.updateLayout(1);
				mSongList.playMediaPlayer(1);
			}
			break;
		case R.id.action_scroll_to_end:
			// Not null list & item is highlighting is not last item  
			if (musicHrScrollView.getNumberThumbnail() > 0 && 
					(musicHrScrollView.getCenterIndex() != musicHrScrollView.getNumberThumbnail()))
			{
				musicHrScrollView.setCenterIndex(musicHrScrollView.getNumberThumbnail());
				musicHrScrollView.updateLayout(musicHrScrollView.getNumberThumbnail());
				mSongList.playMediaPlayer(mSongList.getCount());
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Singletapconfirmed or Scroll is ended.
	 */
	OnTouchFinishListener musicOnTouchFinish = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(View longtouchView, int centerIndex) {
			/*
			 *  We must check condition because our library if longtouch event ended
			 *  , singletapconfirmed will happen too.
			 */
			if (centerIndex == musicHrScrollView.getmLongTouchItemIndex())
				return;

			/*
			 * In music horizontalscrollview if 1 longtouch item is visible,
			 * then when calling singletap or scroll this item's background must be gone.
			 * And playmode come back to PALY_ALL insteads of REPEAT_ONE.
			 */
			if(longtouchView != null)
			{
				//Log.i("TAG", "onTouchFinish has long touch item");
				final ImageView img =  (ImageView) longtouchView.findViewById(R.id.thumbnailImage);
				
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						img.setBackgroundResource(R.drawable.image_transparent_border);
					}
				});
				
				mSongList.setmPlayMode(AudioList.PLAY_ALL);
			}
			
			/*
			 * Only play this song if selected item is different.
			 */
			if ((centerIndex != mSongList.getmAudioPlaying())
					&& (mSongList.getmState() != AudioList.PAUSE))
			{
				mSongList.stopMediaPlayer();
				mSongList.playMediaPlayer(centerIndex);
			}
		}
	};
	
	/* Same as  music above */
	OnTouchFinishListener ringtoneOnTouchFinishListener = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(View parrent, int index) {
//			if (centerIndex == ringtoneHrScrollView.getmLongTouchItemIndex())
//				return;
//			
//			/* Has longtouch Item */
//			if(parrent != null)
//			{
//				Log.i("TAG", "onTouchFinish has long touch item");
//				final ImageView img =  (ImageView) parrent.findViewById(R.id.thumbnailImage);
//				
//				getActivity().runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						img.setBackgroundResource(R.drawable.image_transparent_border);
//					}
//				});
//				
//				mRingtoneList.setmPlayMode(AudioList.PLAY_ALL);
//			}
			
				if (ringtoneHrScrollView.isItemHighLight(index)) {
					
					mRingtoneList.stopPlayer(index);
					ringtoneHrScrollView.unHighlightIdex(index);
					mRingtoneList.getAudio(index-1).setChoose(false);
					
				} else {
					
					if (mSongList.getmState() == AudioList.PLAYING) {
						mRingtoneList.playMediaPlayer(getActivity().getBaseContext(), index);
					}
					
					ringtoneHrScrollView.highlightIdex(index);
					mRingtoneList.getAudio(index-1).setChoose(true);
					
				}
		}
	};

	/**
	 * In longtouch event, we change item's background
	 * , and repeat that song.  
	 */
	OnThumbnailLongTouchListener musicOnLongTouchListener = new OnThumbnailLongTouchListener() {
		
		@Override
		public void onMyLongTouch(View view, int centerIndex) {
			//FrameLayout itemLayout = (FrameLayout) view;
			//Log.d("TAG", "OnThumbnailLongTouchListener");
			ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnailImage);
			thumbnail.setBackgroundResource(R.drawable.image_border);
			
			mSongList.setmPlayMode(AudioList.REPEAT_ONE);
			mSongList.setLooping(true);
		}
	};

	OnThumbnailLongTouchListener ringtoneOnLongTouchListener = new OnThumbnailLongTouchListener() {
		
		@Override
		public void onMyLongTouch(View view, int centerIndex) {
			//FrameLayout itemLayout = (FrameLayout) view;
			Log.d("TAG", "OnThumbnailLongTouchListener");
			ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnailImage);
			thumbnail.setBackgroundResource(R.drawable.image_border);
			
			mRingtoneList.setmPlayMode(AudioList.REPEAT_ONE);
			mRingtoneList.setLooping(true);
		}
	};

	OnItemAddListener musicItemAddListener = new OnItemAddListener() {
		
		@Override
		public void onItemnailAdd(int numberThumnail) {
			if (ringtoneHrScrollView.getVisibility() == View.INVISIBLE)
				ringtoneHrScrollView.setVisibility(View.VISIBLE);
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("","onActivityResult, resultCode = " + resultCode);
		mNumberSongChecked = resultCode;
	};
	
}
