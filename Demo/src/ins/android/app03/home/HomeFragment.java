package ins.android.app03.home;

import ins.android.app03.listsong.ListSongFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
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
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemRemoveListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;


public class HomeFragment extends Fragment
{
	/* FIXME: I have used static variable to save fragment's 
	 * state when user switch between fragsments.
	 * */
	
	private static MyHorizontalScrollView musicHrScrollView;
	private static MyHorizontalScrollView ringtoneHrScrollView;
	
	/*
	 * Because fragment replace many times, we only need request 
	 * all audio in device 1 time at the first
	 * */
	public static boolean isMusicListed;
	
	/*
	 * RingtoneList & MusicList
	 * Using static to Save state of data after fragment commit times
	 * */
	public static SongList mSongList = new SongList(AudioList.REPEAT_ALL);; 
	public static RingtoneList mRingtoneList = null; 
	
	/*
	 *	Check if thread to switch to next item
	 *	when 1 song is ended
	 */
	private static boolean isThreadSwitchIemMusicRun = false;
	
	public static View rootView = null;
	
	private TextView mTextViewLoading = null;
	
	private Button buttonPlayAll = null;
	
	private Context mContext;
	
	private Activity mActivity = null;
	
	private static boolean isShowRingtoneThreadRun = false;
	
	private Thread mShowRingtoneThread = null;
	
	private LinearLayout mVolumeLayout = null;
	
	private int mFutureInMillis = 5000;
	
	private SeekBar mRingtoneSeekBar = null;
	
	private SeekBar mMusicSeekBar = null;
	
	private AudioManager mAudioManager = null;
	
	private int mNumberSongChecked = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("HomeFragment", "onCreate");
		Log.i("HomeFragment", "savedInstanceState = " + savedInstanceState);
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		mContext = getActivity().getBaseContext();
		mActivity = getActivity();
		/*
		 * For the first time we need to request all audio file in
		 * device (just 1 time).
		 * 
		 * As this app, we can do it at onCreate() in Activity, but It's not logic. 
		 * From Fragment's lifecycle this method will just call when user choose "Home" 
		 * on Navigation Drawer.
		 */
		
		if (!isMusicListed){
			try {
	    		Log.i("HomeFragment", "List music");
	    		
	    		/* 
	    		 * Query to list all song in device 
	    		 */
    			Utils.getAllAudio(getActivity());
	        	
	    		/* Add result to data container 
	        	 * Using thread to not block UI thread
	        	 * when add audio into our database
	        	 * */
	        	new Thread( new Runnable() {
	        		
	    			@Override
	                public void run() {
                		Looper.prepare();
                        Utils.insertQueryResultIntoSonglist();
                        isMusicListed = true;
                        Looper.loop();
	                }
	            }).start();
	        	
			} catch (Exception e) {
			}
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("", "onDestroy");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i("", "onDestroyView");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.i("", "onDetach");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("", "onPause");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("", "onStop");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.i("", "onAttach");
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("FRAGMENT", "onResume");
		 ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
	        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinishListener);
	        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneOnLongTouchListener);
	        
	        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
	        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
	        musicHrScrollView.setOnItemRemoveListener(musicOnItemRemove);
	        musicHrScrollView.setOnThumbnailLongTouchListener(musicOnLongTouchListener);
		        
	        Log.i("FRAGMENT", "onResume, mNumberSongChecked = " + mNumberSongChecked);
	        if(mNumberSongChecked > 0)
	        {
	    		/*
	    		 * Add new item selected
	    		 */
	        	for (int i = 0; i < Utils.mListAllSong.size(); i++)
	        	{
	        		/* Add new selected item */
	        		if (Utils.mListAllSong.get(i).ismSelected())
	        		{
	        			mSongList.addmAudio(Utils.mListAllSong.get(i));
	        			musicHrScrollView.addThumbnailToParent();
	        			
	        			/* 
	        			 * To add 1 song after, 
	        			 * we uncheck slected item in listview
	        			 */
	        			Utils.mListAllSong.get(i).setmSelected(false);
	        		}
	        	}
	        	
	        }
	    	
	    	/*
	    	 * TextView is to show when ListSongFragment's data
	    	 * is still not available.
	    	 */
	        mTextViewLoading = (TextView) rootView.findViewById(R.id.tvloading);
	        mTextViewLoading.setVisibility(View.INVISIBLE);
	        
	        /*
	         * Button stop play/pause both ringtone & music
	         */
	        buttonPlayAll = (Button) rootView.findViewById(R.id.btplayall);
	        buttonPlayAll.setOnClickListener(buttonPlayAllOnClickListener);
	        if (mSongList.getmState() == AudioList.PLAYING || 
					mRingtoneList.getmState() == AudioList.PLAYING)
	        	buttonPlayAll.setText("Pause");
	        else if(mSongList.getmState() == AudioList.PAUSE && 
					mRingtoneList.getmState() == AudioList.PAUSE)
	        	buttonPlayAll.setActivated(false);
	        	
	        else
	        	buttonPlayAll.setText("Play");
	        
	        if (!isShowRingtoneThreadRun){
	        	mShowRingtoneThread = new Thread(new ShowUpRingtone());
	        	mShowRingtoneThread.start();
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
	        mRingtoneSeekBar.setProgress(mAudioManager
	                .getStreamVolume(AudioManager.STREAM_MUSIC)); 
	        
	        mMusicSeekBar.setMax(mAudioManager
	                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
	        mMusicSeekBar.setProgress(mAudioManager
	                .getStreamVolume(AudioManager.STREAM_MUSIC)); 
	        
	        /*
	    	 * Again this Fragment is switch multiple times.
	    	 * And we want to these threads runs during app is running so
	    	 * we do not set condition to stop these threads.
	    	 * Therefore, to not run them again we must to check
	    	 * if they already run or not.
	    	 */
			
			/* Music */
	    	if (!isThreadSwitchIemMusicRun)
	    	{
	    		new Thread (new Runnable() 
	        	{
	    			@Override
	    			public void run() 
	    			{
	    				while(true)
	    				{
	    					int index;
	    					
	    					while (!mSongList.isEndOnePlayback());
	    					
	    					Log.i("", "End 1 song");
	    					
	    					/*
	    					 * Update GUI
	    					 */
	    					if (musicHrScrollView.getCenterIndex() + 1 >
	    						musicHrScrollView.getNumberThumbnail())
	    					{
	    						index = 1;
	    					}
	    					else
	    					{
	    						index = musicHrScrollView.getCenterIndex() + 1;
	    					}
	    					
							musicHrScrollView.updateLayout(index);
	    					musicHrScrollView.setCenterIndex(index);
	    					mSongList.setEndOnePlayback(false);
	    					
	    					if (ringtoneHrScrollView.getCenterIndex() == 
	    							ringtoneHrScrollView.getmLongTouchItemIndex())
	    						continue;
	    					
	    					if (ringtoneHrScrollView.getCenterIndex() + 1 > 
	    						ringtoneHrScrollView.getNumberThumbnail())
	    					{
	    						index = 1;
	    					}
	    					else
	    					{
	    						index = ringtoneHrScrollView.getCenterIndex() + 1;
	    					}
	    					
							ringtoneHrScrollView.updateLayout(index);
	    					ringtoneHrScrollView.setCenterIndex(index);
	    					
	    					/*
	    					 * Update ringtone
	    					 */
	    					mRingtoneList.resetPlayer();
	    					if(mRingtoneList.getmAudioPlaying() == 
	    							mRingtoneList.getCount()) {
	    						
	    						mRingtoneList.playMediaPlayer(mRingtoneList.
	    								getAudio(0).getmResSongId(), mContext, 1);
	    					}
	    					else
	    					{
	    						mRingtoneList.playMediaPlayer(mRingtoneList.getAudio(mRingtoneList.
	    								getmAudioPlaying()).getmResSongId(),
	    								mContext, mRingtoneList.getmAudioPlaying() + 1);
	    					}
	    				}
	    			}
	    		}).start();
	    		
	    		isThreadSwitchIemMusicRun = true;
	    	}
	    	
	    	/*
	    	 * This is implemented because our library highlight
	    	 * 1st in every time fragment is visible.
	    	 */
	    	ringtoneHrScrollView.unHighlightIdex(1);
	    	//ringtoneHrScrollView.highlightIdex(mRingtoneList.getmAudioPlaying());
	    	
	}




	/* (non-Javadoc)
	 * @see android.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("FRAGMENT", "onStart");
	}




	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("", "oncreateView");
		/*
		 * Because user will switch bween many fragments usually, we
		 * want this HomeFragment's layout is stable. Then my solution is 
		 * use static view to save this layout.
		 */
		if (rootView == null)
		{
			rootView = inflater.inflate(R.layout.activity_main, container, false);
			mRingtoneList = new RingtoneList(AudioList.REPEAT_ONE, getActivity());
		}
        
        /* 
         * Fragment need it to add item to Actionbar 
         * */
        setHasOptionsMenu(true);
        
        return rootView;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.i("","onViewCreated");
	}
	
	class ShowUpRingtone extends Thread implements Runnable {
		
		private boolean mRunning = true;
		
		public void run() {
			while(mRunning){

				if (musicHrScrollView.getNumberThumbnail() > 0 
						&& (ringtoneHrScrollView.getVisibility() == View.GONE)){
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							ringtoneHrScrollView.setVisibility(View.VISIBLE);							
						}
					});
				}
					
				else if (musicHrScrollView.getNumberThumbnail() == 0 
						&& (ringtoneHrScrollView.getVisibility() == View.VISIBLE)){
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							ringtoneHrScrollView.setVisibility(View.GONE);							
						}
					});	
				}
				
			}
		}
		
		public void terminate(){
			mRunning = false;
		}
	};
	
	/**
	 * Dialog show when user press back button
	 * @author cxphong
	 */
	public static class ExitAppDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Do you want to exit?")
	               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       getActivity().finish();
	                   }
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       dismiss();
	                   }
	               });
	        
	        return builder.create();
	    }
	}

	
	/**
	 * Button Play All listener
	 */
	
	OnClickListener buttonPlayAllOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btplayall:
				
				/*
				 * Song playing -> pause
				 */
				if (mSongList.getmState() == AudioList.PLAYING)
				{
					mSongList.pauseMediaPlayer();
					mRingtoneList.pauseMediaPlayer();
					buttonPlayAll.setText("Play");
				}
				/*
				 * Song pause -> playing
				 */
				else if (mSongList.getmState() == AudioList.PAUSE)
				{
					/*
					 * No songs anymore
					 */
					if (mSongList.getCount() == 0){
						mSongList.stopMediaPlayer();
						buttonPlayAll.setText("Play");
						break;
					}
				
					/*
					 * Song
					 * Choose another song
					 */
					if (mSongList.getmAudioPlaying() != musicHrScrollView.getCenterIndex()){
						mSongList.playMediaPlayer(musicHrScrollView.getCenterIndex());
					}
					else{
						mSongList.resumePlayer();
						buttonPlayAll.setText("Pause");
					}
					
					/*
					 * Ringtone
					 * Choose another song
					 */
					if (mRingtoneList.getmAudioPlaying() != ringtoneHrScrollView.getCenterIndex()){
						mRingtoneList.playMediaPlayer(mRingtoneList.getAudio(
								ringtoneHrScrollView.getCenterIndex()-1).getmResSongId(), getActivity().
								getBaseContext(), ringtoneHrScrollView.getCenterIndex());
					} else {
						mRingtoneList.resumePlayer();
						buttonPlayAll.setText("Pause");
					}
					
				}

				
				break;

			default:
				break;
			}
		}
	};
	/**
	 * Using this callback to listen event when a song/ringtone is end,
	 * then we need to scroll horizontalscrollview to next item.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//Log.i("HomeFragment", "onActivityCreated, savedInstanceState = " + savedInstanceState);
    	
	}
	
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
			Intent intent = new Intent(getActivity(), ListSongFragment.class);
			intent.putExtra("LIST_SONG_DONE", isMusicListed);
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
								mFutureInMillis = 5000;
								mCountDownTimer.start();
								
								mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
			                            progress, 0);
								
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
								mFutureInMillis = 5000;
								mCountDownTimer.start();
								mRingtoneList.setmVolume((float)progress/mRingtoneSeekBar.getMax());
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
			//Log.d("musicOnTouchFinish", "centerIndex = " + centerIndex);
			//Log.d("musicOnTouchFinish", "current playing = " + mSongList.getmAudioPlaying());
			//Log.i("TAG", "onTouchFinish");
			
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
			if (centerIndex != mSongList.getmAudioPlaying())
			{
				mSongList.stopMediaPlayer();
				mSongList.playMediaPlayer(centerIndex);
			}
		}
	};
	
	/* Same as  music above */
	OnTouchFinishListener ringtoneOnTouchFinishListener = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(View parrent, int centerIndex) {
			if (centerIndex == ringtoneHrScrollView.getmLongTouchItemIndex())
				return;
			
			/* Has longtouch Item */
			if(parrent != null)
			{
				Log.i("TAG", "onTouchFinish has long touch item");
				final ImageView img =  (ImageView) parrent.findViewById(R.id.thumbnailImage);
				
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						img.setBackgroundResource(R.drawable.image_transparent_border);
					}
				});
				
				mRingtoneList.setmPlayMode(AudioList.PLAY_ALL);
			}
			
			if (centerIndex != mRingtoneList.getmAudioPlaying() &&
					mSongList.getmState() == AudioList.PLAYING)
				mRingtoneList.stopMediaPlayer();
				mRingtoneList.playMediaPlayer(mRingtoneList.getAudio(centerIndex - 1).getmResSongId(), 
						getActivity().getBaseContext(), centerIndex);
			}
	};
	
	/**
	 * Music item is removed out of list
	 */
	OnItemRemoveListener musicOnItemRemove = new OnItemRemoveListener() {
		
		@Override
		public void onItemRemove(int itemRemoved) 
		{
			Log.i("Remove song list", "Item removed = " + itemRemoved);
			Log.i("Remove song list", "List size = " + mSongList.getCount());
			
			mSongList.getmAudioList().remove(itemRemoved - 1);
			
			/*
			 * Removed song is also the song is playing   
			 */
			if (itemRemoved == mSongList.getmAudioPlaying())
			{
				/*
				 * 1 item remain, stop play
				 */
				if (mSongList.getCount() == 0)
				{
					mSongList.stopMediaPlayer();
					return;
				}
		
				/*
				 * Last item, play 1st
				 */
				if (itemRemoved > mSongList.getCount())
				{
					mSongList.playMediaPlayer(1);
				}
				
				/*
				 * Middle item, play next song
				 */
				else
				{
					mSongList.playMediaPlayer(itemRemoved);
				}
				
				/*
				 * Removed Item is also longTouch item 
				 * So change playmode
				 */
				if (itemRemoved == musicHrScrollView.getmLongTouchItemIndex())
				{
					musicHrScrollView.setmLongTouchItemIndex(-1);
					mSongList.setmPlayMode(AudioList.REPEAT_ALL);
				}
			}
			
			/*
			 * Removed song is not playing song
			 */
			else
			{
				/*
				 * Update index of current song is playing
				 * It happens only when remove song has index less than curren playing song
				 */
				
				if (itemRemoved < mSongList.getmAudioPlaying())
					mSongList.setmAudioPlaying(mSongList.getmAudioPlaying() - 1);
				
			}
			
			musicHrScrollView.highlightIdex(mSongList.getmAudioPlaying());
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("","onActivityResult, resultCode = " + resultCode);
		mNumberSongChecked = resultCode;
	};
}
