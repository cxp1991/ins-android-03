package ins.android.app03.home;

import ins.android.app03.home.AudioList.OnStartPlayListener;
import ins.android.app03.home.SongList.OnEndSongListener;
import ins.android.app03.home.SongManager.OnSongAddIntoDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
	private LinearLayout mVolumeLayout = null;
	private int mFutureInMillis = 5000;
	private SeekBar mRingtoneSeekBar = null;
	private SeekBar mMusicSeekBar = null;
	private AudioManager mAudioManager = null;
	private int mNumberSongChecked = 0;
	private SongManager mSongManager;
	private CountDownTimer remainingTimeCounter = null;
	private Thread mAddSongIntoDatabase;
	private SearchView mSearchView;
	private ListView lv;
	public static AllSongAdapter adapter;
	private Menu mMenu;
	private ActionBar mActionBar;
	public boolean isListSongDone = false;
	private List<Fragment> fragments;
	private MyPagerAdapter mPager;
	private ViewPager mViewPager;
	private long mStartTime = 0, mCurrentTime = 0;
	private boolean ableDecodeGif;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set GUI
		setContentView(R.layout.activity_main);
		mActionBar = getSupportActionBar();
		
		// Configure ListView
		adapter = new AllSongAdapter(SongManager.mListAllSong, this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(itemClickListener);
		lv.setAdapter(adapter);
		lv.setVisibility(View.GONE);
		
		// List All Songs in device
		mSongManager = new SongManager(this);
		mSongManager.getAllAudio();
		mAddSongIntoDatabase = new Thread(new AddSongIntoDatabase());
		mAddSongIntoDatabase.start();
		mSongManager.setSongAddIntoDatabase(new OnSongAddIntoDatabase() {
			@Override
			public void songAddedIntoDatabase() {
				if (lv.getVisibility() == View.VISIBLE) {
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		// Create 2 lists
		mRingtoneList = new RingtoneList( AudioList.REPEAT_ONE, this);
		mSongList = new SongList(AudioList.REPEAT_ALL, this);
		mSongList.setStartPlayListener(new OnStartPlayListener() {
			
			@Override
			public void onStartPlay(final MySong song) {
				Log.i("","start play");
				runOnUiThread(new Runnable() {
					public void run() {
						mActionBar.setTitle(song.getmSongName());
					}
				});
			}
		});
		
        /* Music HorizontialScrollView */
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
		
		// init own actionbar
		//initActionBar();
		
        // Viewpager
        fragments =  getFragments();
		
		mPager = new MyPagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(mPager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int position) {
				new StartGifAnimation().execute(position);						
			}
			
			@Override
	        public void onPageScrolled(	int position, 
						        		float positionOffset, 
						        		int positionOffsetPixels) {
	        }
	
			@Override
			public void onPageScrollStateChanged(int state) {
			}
			
		});
		
	}
	
	private int mGifPos = -1;
	String [] mGifName =  new String []{"rain_gif.gif", "stream_gif.gif",
			"thunder_gif.gif", "frog_gif.gif"};
	
	class StartGifAnimation extends AsyncTask<Integer, Void , Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			final int position = params[0];
			
			/* Stop invisible pager */
			if (mGifPos >= 0)
				publishProgress();
			
			/* Start visible pager in 0.7s */
			((MyFragment) fragments.get(position)).startGifAnimation();
			
			mGifPos  = position;
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			((MyFragment) fragments.get(mGifPos)).stopGifAnimation();
		}
		
	}
	
	private List<Fragment> getFragments(){
		  List<Fragment> fList = new ArrayList<Fragment>();
		  
		  fList.add(MyFragment.newInstance(1, R.drawable.rain_jpg, "rain_gif.gif"));
		  fList.add(MyFragment.newInstance(2, R.drawable.stream_jpg, "stream_gif.gif"));
		  fList.add(MyFragment.newInstance(3, R.drawable.thunder_jpg, "thunder_gif.gif"));
		  fList.add(MyFragment.newInstance(4, R.drawable.frog_jpg, "frog_gif.gif"));
		
		  return fList;
	}
	
	private void updateGui(){

		try {
		 mNumberSongChecked = adapter.getmNumberItemIsChecked();
		if(mNumberSongChecked > 0)
        {      	
        	for (MySong song : SongManager.mListAllSong)
        	{
        		// Add new selected item 
        		if (song.ismSelected())
        		{
        			mSongList.addmAudio(song);
    				musicHrScrollView.addThumbnailToParent(R.drawable.ic_music_071);
        			
        			 /* To add 1 song after, 
        			 * we uncheck slected item in listview
        			 */
	    			song.setmSelected(false);
	    			mNumberSongChecked = 0;
        		}
        	}
        }
		 } catch (Exception e) {
			 Log.e("", "" + e);
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
		Log.i("", "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.homemenu, menu);
		mMenu  = menu;
		return super.onCreateOptionsMenu(menu);
	}
	
	class AddSongIntoDatabase extends Thread implements Runnable {
		
		@Override
		public void run() {
          mSongManager.insertQueryResultIntoSonglist();
          isListSongDone = true;
          Log.i("", "Finish add to database");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		RelativeLayout parentRl = (RelativeLayout) findViewById(R.id.parent_layout);
		
		switch (item.getItemId()) {
			
		case R.id.action_add_song:
			while (!isListSongDone);
			// Show listsong listview
			if (lv.getVisibility() == View.GONE) {
				parentRl.setBackgroundColor(0xff666666);
				musicHrScrollView.setBackgroundColor(0xff666666);
				
				mMenu.removeItem(R.id.action_add_song);
				mMenu.removeItem(R.id.action_volume);
				getMenuInflater().inflate(R.menu.list_song_menu, mMenu);
				getMenuInflater().inflate(R.menu.homemenu, mMenu);
				
				MenuItem searchItem = mMenu.findItem(R.id.action_search);
			    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			    mSearchView.setIconifiedByDefault(true);
				mSearchView.setOnQueryTextListener(searchViewListener);
				
				lv.setAdapter(lv.getAdapter());
				lv.setVisibility(View.VISIBLE);
				lv.setSelection(0);
				
				/* Hide viewpager */
				mViewPager.setVisibility(View.INVISIBLE);
				
				/* Hide HorizontialScrollView */
				musicHrScrollView.setVisibility(View.INVISIBLE);
				
			} else {
				parentRl.setBackgroundColor(0xffcccccc);
				musicHrScrollView.setBackgroundColor(0xffcccccc);
				
				lv.setVisibility(View.GONE);
				updateGui();
				
				try {
					mMenu.removeItem(R.id.action_search);
					mMenu.removeItem(R.id.action_volume);
					mMenu.removeItem(R.id.action_add_song);
					getMenuInflater().inflate(R.menu.homemenu, mMenu);
				} catch (Exception e) {
					Log.e("XXX", e + "");
				}
				
				/* Show viewpager */
				mViewPager.setVisibility(View.VISIBLE);
				
				/* Show HorizontialScrollView */
				musicHrScrollView.setVisibility(View.VISIBLE);
				//supportInvalidateOptionsMenu();
			}
			
			break;
			
		case R.id.action_volume:
			Log.i("", "left = " + mVolumeLayout.getLeft()
	        		+ "\nright = " + mVolumeLayout.getRight()
	        		+ "\ntop = " + mVolumeLayout.getTop()
	        		+ "\nbottom = " + mVolumeLayout.getBottom());
			
			if (mVolumeLayout.getVisibility() == View.GONE) {
				
				/* Disable gif clickable */
				((MyFragment)fragments.get(mViewPager.getCurrentItem())).pagerClickDisable();
				
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
						/* Enable gif clickable */
						((MyFragment)fragments.get(mViewPager.getCurrentItem())).pagerClickEnable();
						
						mVolumeLayout.setVisibility(View.GONE);
					}
		        }.start();
		        
			} else {
				
				/* Enable gif clickable */
				((MyFragment)fragments.get(mViewPager.getCurrentItem())).pagerClickEnable();
				
				if (remainingTimeCounter != null) {
					remainingTimeCounter.cancel();
				}
				
				mVolumeLayout.setVisibility(View.GONE);
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
			Log.i("", "mSongList.getmState() = " + mSongList.getmState());
			
			if ((centerIndex != mSongList.getmAudioPlaying())
					&& (mSongList.getmState() != AudioList.PAUSE))
			{
				mSongList.stopMediaPlayer();
				mSongList.playMediaPlayer(centerIndex);
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

	OnItemAddListener musicItemAddListener = new OnItemAddListener() {
		
		@Override
		public void onItemnailAdd(int numberThumnail) {
			if (mViewPager.getVisibility() == View.INVISIBLE) {
				mViewPager.setVisibility(View.VISIBLE);
				ImageView v = (ImageView) findViewById(R.id.imv_startup);
				v.setVisibility(View.GONE);
				
				/* Play song 1*/
				mSongList.playMediaPlayer(1);
				musicHrScrollView.highlightIdex(1);
				musicHrScrollView.setCenterIndex(1);
				
				/* Play Ringtone */
				new StartGifAnimation().execute(0);
			}
		}
	};
	
	/**
	 * SearchView listener 
	 */
	SearchView.OnQueryTextListener searchViewListener = new OnQueryTextListener() 
    {
		
		@Override
		public boolean onQueryTextSubmit(String arg0) 
		{
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String newText) 
		{
			adapter.myGetFilter().filter(newText);
			return true;
		}
	};
	
	protected void onDestroy(){
		mSongList.releasePlayer();
		super.onDestroy();
	};

	/* Index 1,2,3,... */
	public void playRingtone (int index) {
		mRingtoneList.playMediaPlayer(this,index);
		//mRingtoneList.getAudio(index-1).setChoose(true);
	}
	
	public void stopRingtone (int index) {
		mRingtoneList.stopPlayer(index);
		//mRingtoneList.getAudio(index-1).setChoose(false);
	}
	
}
