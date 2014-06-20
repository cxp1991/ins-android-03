package ins.android.app03.home;

import ins.android.app03.home.SongList.OnEndSongListener;
import ins.android.app03.listsong.AllSongAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemAddListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;

public class HomeActivity extends ActionBarActivity 
{
	private MyHorizontalScrollView musicHrScrollView;
	private MyHorizontalScrollView ringtoneHrScrollView;
	public SongList mSongList = null; 
	public RingtoneList mRingtoneList = null; 
	private Button buttonPlayAll = null;
	private Activity mActivity = null;
	private LinearLayout mVolumeLayout = null;
	private int mFutureInMillis = 5000;
	private SeekBar mRingtoneSeekBar = null;
	private SeekBar mMusicSeekBar = null;
	private AudioManager mAudioManager = null;
	private int mNumberSongChecked = 0;
	private SongManager mSongManager;
	private CountDownTimer remainingTimeCounter = null;
	private Thread mAddSongIntoDatabase;
	private boolean isInitializeListSongDone = false;
	 private SearchView mSearchView;
	 private ListView lv;
	 private AllSongAdapter adapter;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// List All Songs in device
		mSongManager = new SongManager(this);
		mSongManager.getAllAudio();
		mAddSongIntoDatabase = new Thread(new AddSongIntoDatabase());
		mAddSongIntoDatabase.start();
		
		// Set GUI
		setContentView(R.layout.activity_main);
		
		// Configure ListView
		adapter = new AllSongAdapter(SongManager.mListAllSong, this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(itemClickListener);
		lv.setAdapter(adapter);
		lv.setVisibility(View.GONE);
		
		// Create 2 lists
		mRingtoneList = new RingtoneList( AudioList.REPEAT_ONE, this);
		mSongList = new SongList(AudioList.REPEAT_ALL, this);
		
		// Initialize 2 horizontialScrollview
		ringtoneHrScrollView = (MyHorizontalScrollView) findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinishListener);
        if (mSongList.getCount() == 0) {
        	ringtoneHrScrollView.setVisibility(View.INVISIBLE);
        }
        
        musicHrScrollView = (MyHorizontalScrollView) findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        musicHrScrollView.setOnThumbnailLongTouchListener(musicOnLongTouchListener);
        musicHrScrollView.setOnItemAddListener(musicItemAddListener);
        mSongList.setEndSongListener(new OnEndSongListener() {
			
			@Override
			public void onEndSong(int index) {
				Log.i("", "setEndSongListener, index = " + index);
				
				musicHrScrollView.updateLayout(index);
				musicHrScrollView.setCenterIndex(index);
			}
        });
        
        // Button stop play/pause both ringtone & music
        buttonPlayAll = (Button) findViewById(R.id.btplayall);
        buttonPlayAll.setOnClickListener(buttonPlayAllOnClickListener);
        if (mSongList.getmState() == AudioList.PLAYING) {
        	buttonPlayAll.setBackgroundResource(R.drawable.button_black_pause);
        } else {
        	buttonPlayAll.setBackgroundResource(R.drawable.button_black_play);
        }

        // Volume Controller
        mVolumeLayout = (LinearLayout) findViewById(R.id.ln_volume);
        mVolumeLayout.setVisibility(View.GONE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRingtoneSeekBar = (SeekBar) findViewById(R.id.ringtone_volume_seekbar);
        mMusicSeekBar = (SeekBar) findViewById(R.id.music_volume_seekbar);
       
        mRingtoneSeekBar.setMax(mAudioManager
        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mMusicSeekBar.setMax(mAudioManager
        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mRingtoneSeekBar.setProgress(mAudioManager
        .getStreamVolume(AudioManager.STREAM_MUSIC));
		mMusicSeekBar.setProgress(mAudioManager
        .getStreamVolume(AudioManager.STREAM_MUSIC));
		
		// Update GUI
		updateGui();

	}
	
	private void updateGui(){
		 if (mSongList.getCount() == 0) {
	        	ringtoneHrScrollView.setVisibility(View.INVISIBLE);
        }
		 mNumberSongChecked = adapter.getmNumberItemIsChecked();
		if(mNumberSongChecked > 0)
        {      	
        	for (MySong song : SongManager.mListAllSong)
        	{
        		// Add new selected item 
        		if (song.ismSelected())
        		{
        			mSongList.addmAudio(song);
        			if (song.getmThumbnail() != null)
        				musicHrScrollView.addThumbnailToParent(song.getmThumbnail());
        			else
        				musicHrScrollView.addThumbnailToParent(R.drawable.ic_music_02);
        			
        			 /* To add 1 song after, 
        			 * we uncheck slected item in listview
        			 */
	    			song.setmSelected(false);
	    			mNumberSongChecked = 0;
        		}
        	}
        }
	}
	
	/**
	 * Checkbox onclick listener
	 */
	 OnItemClickListener itemClickListener = new OnItemClickListener() {

		 @Override
			public void onItemClick (AdapterView<?> listview, View viewItem, int position,
					long id) 
			{
				viewItem.findViewById(R.id.thumbnail).performClick();
			}
	 };
	
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.homemenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	class AddSongIntoDatabase extends Thread implements Runnable {
		
		@Override
		public void run() {
          mSongManager.insertQueryResultIntoSonglist();
          isInitializeListSongDone = true;
		}
	}
	
	/**
	 * Button Play All listener
	 */
	OnClickListener buttonPlayAllOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mSongList.getCount() == 0) return;
			
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
							mRingtoneList.playMediaPlayer(getBaseContext(), song);
						}
					}
				}
			
				buttonPlayAll.setBackgroundResource(R.drawable.button_black_pause);
				
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
							mRingtoneList.playMediaPlayer(getBaseContext(), song);
						}
					}
				}
				
				buttonPlayAll.setBackgroundResource(R.drawable.button_black_pause);
				
			/* From PLAYING state */
			} else if (mSongList.getmState() == AudioList.PLAYING) {
				
				mSongList.pauseMediaPlayer();
			
				for (MySong song : mRingtoneList.getmAudioList()) {
					if (song.isChoose()) {
						mRingtoneList.pausePlayer(song);
					}
				}
				
				buttonPlayAll.setBackgroundResource(R.drawable.button_black_play);
				
			}
			
		}
	};
	
	/**
	 * Add menu tinto Actionbar
	 *//*
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.homemenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
		case R.id.action_add_song:
			while(!isInitializeListSongDone);
			// Show listsong listview
			if (lv.getVisibility() == View.GONE) {
				lv.setVisibility(View.VISIBLE);
				lv.setSelection(0);
			} else {
				lv.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				updateGui();
			}
			break;
			
		case R.id.action_volume:
			Log.i("", "left = " + mVolumeLayout.getLeft()
	        		+ "\nright = " + mVolumeLayout.getRight()
	        		+ "\ntop = " + mVolumeLayout.getTop()
	        		+ "\nbottom = " + mVolumeLayout.getBottom());
			
			if (mVolumeLayout.getVisibility() == View.GONE) {
				
				mVolumeLayout.setVisibility(View.VISIBLE);
				mRingtoneSeekBar.setProgress(mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
				mMusicSeekBar.setProgress(mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
				
				/*
				 * User not touch in 5s, then close
				 */
				remainingTimeCounter = new CountDownTimer(mFutureInMillis, 1000) {

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
				
				if (remainingTimeCounter != null) {
					remainingTimeCounter.cancel();
				}
				
				mVolumeLayout.setVisibility(View.GONE);
			}
			
			break;
			
		/*case R.id.action_remove_all_song:
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
			break;*/
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
				
				/*getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {*/
						img.setBackgroundResource(R.drawable.image_transparent_border);
			/*		}
				});
			*/	
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
						mRingtoneList.playMediaPlayer(getBaseContext(), index);
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
	
	OnTouchListener checkTouchOutsideVolumeController = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if ( event.getAction () == MotionEvent.ACTION_UP ) {
			    Rect r = new Rect ( 0, 0, 0, 0 );
			    mVolumeLayout.getHitRect(r);
			    
			    Log.i("volume", "left = " + r.left +
			    				"\n right = " + r.right + 
			    				"\n top = " + r.top + 
			    				"\n bottom = " + r.bottom);
			    
			    Log.i ("", "X = " + event.getX() + 
			    		   "\nY = " + event.getY());
			    
			    boolean intersects = r.contains ( (int) event.getX (), (int) event.getY () );
			    
			    if ( !intersects ) {
			    	Log.i("volume", "INSIDE");
			      return true;
			    }
			  }
			Log.i("volume", "OUTSIDE");
		  return true;
		}
	};

}
