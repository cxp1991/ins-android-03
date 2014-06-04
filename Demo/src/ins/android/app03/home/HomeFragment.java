package ins.android.app03.home;

import ins.android.app03.listsong.ListSongFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.GetChars;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemAddListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemRemoveListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;


public class HomeFragment extends Fragment
{
	private MyHorizontalScrollView musicHrScrollView;
	private MyHorizontalScrollView ringtoneHrScrollView;
	
	/*
	 * Press 2 times to exit app.
	 * */
	private int keyBackPressCount = 2;
	
	/*
	 * Because fragment replace many times, we only need request 
	 * all audio in device 1 time at the first
	 * */
	public static boolean IS_REQUEST_MUSIC_IN_DEVICE = false;
	
	/*
	 * RingtoneList & MusicList
	 * Using static to Save state of data after fragment commit times
	 * */
	private static SongList mSongList = new SongList(AudioList.REPEAT_ALL);; 
	private static RingtoneList mRingtoneList = null; 
	
	/*
	 *	Check if thread to switch to next item
	 *	when 1 song is ended
	 */
	private static boolean isThreadSwitchIemMusicRun = false;
	
	/*
	 *	Check if thread to switch to next item
	 *	when 1 ringtone is ended
	 */
	private static boolean isThreadSwitchIemRingtoneRun = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Log.i("HomeFragment", "onCreate, savedInstanceState = " + savedInstanceState);
		super.onCreate(null);

		/*
		 * For the first time we need to request all audio file in
		 * device (just 1 time)
		 */
    	try 
    	{
    		//Log.i("HomeFragment", "IS_REQUEST_MUSIC_IN_DEVICE = " + IS_REQUEST_MUSIC_IN_DEVICE);
    		/* Query to list all song in device */
        	if (IS_REQUEST_MUSIC_IN_DEVICE == false)
        	{
    			Utils.getAllAudio(getActivity());
        	}
        	
    		/* Add result to data container 
        	 * Using thread to not block UI thread
        	 * when add audio into our database
        	 * */
        	new Thread( new Runnable()
        	{
    			@Override
                public void run()
                {
                	if (IS_REQUEST_MUSIC_IN_DEVICE == false)
                	{
                		Looper.prepare();
                        Utils.insertQueryResultIntoSonglist();
                        IS_REQUEST_MUSIC_IN_DEVICE = true;
                        Looper.loop();
                	}
                }
            }).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.i("HomeFragment", "onAttach");
		super.onAttach(activity);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.i("HomeFragment", "onResume");
		// TODO Auto-generated method stub
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.i("HomeFragment", "onStart");
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		Log.i("HomeFragment", "onCreateView, savedInstanceState = " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        
        if (mRingtoneList == null)
        	mRingtoneList = new RingtoneList(AudioList.REPEAT_ALL, getActivity());
        
        ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
        
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinishListener);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneOnLongTouchListener);
        
        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        musicHrScrollView.setOnItemRemoveListener(musicOnItemRemove);
        musicHrScrollView.setOnThumbnailLongTouchListener(musicOnLongTouchListener);
        musicHrScrollView.setOnItemAddListener(itemAddListener);
        
        /*
         * Back press listener 
         */
        rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		final DialogFragment dialog = new ExitAppDialogFragment();
		
		rootView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				
				if( (keyCode == KeyEvent.KEYCODE_BACK) && (keyEvent.getAction() == KeyEvent.ACTION_DOWN))
				{
					dialog.show(getFragmentManager(), null);
				}
				
				return true;
			}
		  });
        
        /* 
         * Fragment need it to add item to Actionbar 
         * */
        setHasOptionsMenu(true);
        
        return rootView;
    }
	
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("HomeFragment", "onActivityCreated, savedInstanceState = " + savedInstanceState);
    	
    	/*
    	 * Initialize MyHorizontalScrollView for Song
    	 */

    	/*
    	 *  At fragment start up time, we need to re-initialize 
    	 *  item in MyHorizontalSrollView again. 
    	 * */
		for (int i = 0; i < mSongList.getmAudioList().size(); i++)
		{
			musicHrScrollView.addThumbnailToParent();
		}
		
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
    			 * To add 1 song multiple time &
    			 * uncheck slected item in listview
    			 */
    			Utils.mListAllSong.get(i).setmSelected(false);
    		}
    	}
    	
    	/*
    	 * Scroll to previous playing index & highlight it
    	 */
    	if (mSongList.getCount() > 0)
    	{
    		musicHrScrollView.highlightIdex(mSongList.getmAudioPlaying());
    		//Log.i("TAG", "center index = " + musicHrScrollView.getCenterIndex());
    		
    		//Log.i("TAG", "mSongList.getmState() = " + mSongList.getmState());
    		if (mSongList.getmState() == AudioList.PAUSE)
    			mSongList.playMediaPlayer(mSongList.getmAudioPlaying());
    	}
    	
    	/*
    	 * Switch next when 1 end of playback
    	 * Because we switch between many fragment
    	 * so we use condition to run thread or not
    	 */
    	if (!isThreadSwitchIemMusicRun)
    	{
    		new Thread (new Runnable() 
        	{
    			@Override
    			public void run() 
    			{
    				while(true)
    				{
    					Log.i("TAG", "Switch next Music item, waiting ...");
    					while (!mSongList.isEndOnePlayback());
    					Log.i("TAG", "Switch next musicitem, done");
    					Log.i("TAG", "current music playing = " + mSongList.getmAudioPlaying());
    					musicHrScrollView.setCenterIndex(mSongList.getmAudioPlaying());
    					musicHrScrollView.updateLayout(mSongList.getmAudioPlaying());
    					
    					mSongList.setEndOnePlayback(false);
    				}
    			}
    		}).start();
    		
    		isThreadSwitchIemMusicRun = true;
    	}
    	
    	/*
    	 * Switch next when 1 end of playback
    	 * Because we switch between many fragment
    	 * so we use condition to run thread or not
    	 */
    	if (!isThreadSwitchIemRingtoneRun)
    	{
    		new Thread (new Runnable() 
        	{
    			@Override
    			public void run() 
    			{
    				while(true)
    				{
    					Log.i("TAG", "Switch next Ringtone item, waiting ...");
    					Log.i("TAG", "mRingtoneList.isRingtoneItemEnd() = " + mRingtoneList.isRingtoneItemEnd());
    					while (!mRingtoneList.isRingtoneItemEnd());
    					Log.i("TAG", "Switch next ringtone item, done");
    					Log.i("TAG", "current ringtone playing = " + mRingtoneList.getmAudioPlaying());
    					ringtoneHrScrollView.setCenterIndex(mRingtoneList.getmAudioPlaying());
    					ringtoneHrScrollView.updateLayout(mRingtoneList.getmAudioPlaying());
    					
    					mRingtoneList.setRingtoneItemEnd(false);
    				}
    			}
    		}).start();
    		
    		isThreadSwitchIemRingtoneRun = true;
    	}
    	
    	/*
    	 * Ringtone HorizontalScrollView
    	 * Return to old playing
    	 */
    	ringtoneHrScrollView.unHighlightIdex(1);
    	ringtoneHrScrollView.highlightIdex(mRingtoneList.getmAudioPlaying());
    	
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.homemenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_add_song:
			Fragment fragment = new ListSongFragment();
			final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
			ft.addToBackStack(null);
			ft.replace(R.id.content_frame, fragment, "LIST_SONG_FRAGMENT").commit();
			break;
			
		case R.id.action_remove_all_song:
			break;
		case R.id.action_scroll_to_head:
			break;
		case R.id.action_scroll_to_end:
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() 
	{
		Log.i("TAG", "OnDestroy");
		mSongList.releasePlayer();
		mRingtoneList.pauseMediaPlayer();
		super.onDestroy();
	}

	OnTouchFinishListener musicOnTouchFinish = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(View parrent, int centerIndex) {
			//Log.d("musicOnTouchFinish", "centerIndex = " + centerIndex);
			//Log.d("musicOnTouchFinish", "current playing = " + mSongList.getmAudioPlaying());
			Log.i("TAG", "onTouchFinish");
			
			if (centerIndex != musicHrScrollView.getmLongTouchItemIndex())
			{
				/* Has longtouch Item */
				if(parrent != null)
				{
					Log.i("TAG", "onTouchFinish has long touch item");
					final ImageView img =  (ImageView) parrent.findViewById(R.id.thumbnailImage);
					
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							img.setBackgroundResource(R.drawable.image_transparent_border);
						}
					});
					
					mSongList.setmPlayMode(AudioList.PLAY_ALL);
				}
				
				/*
				 * 1. Don't play again the song is playing
				 * Exception: If playlist is added first time
				 * centerIndex = mAudioPlaying = 1
				 */
				if (centerIndex != mSongList.getmAudioPlaying())
				{
					mSongList.playMediaPlayer(centerIndex);
				}
			}
		}
	};
	
	OnTouchFinishListener ringtoneOnTouchFinishListener = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(View parrent, int centerIndex) {
			if (centerIndex != ringtoneHrScrollView.getmLongTouchItemIndex())
			{
				/* Has longtouch Item */
				if(parrent != null)
				{
					Log.i("TAG", "onTouchFinish has long touch item");
					final ImageView img =  (ImageView) parrent.findViewById(R.id.thumbnailImage);
					
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							img.setBackgroundResource(R.drawable.image_transparent_border);
						}
					});
					
					mRingtoneList.setmPlayMode(AudioList.PLAY_ALL);
				}
				
				if (centerIndex != mRingtoneList.getmAudioPlaying())
					mRingtoneList.playMediaPlayer(mRingtoneList.getAudio(centerIndex - 1).getmResSongId(), 
							getActivity().getBaseContext(), centerIndex);
			}
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
					mSongList.pauseMediaPlayer();
					return;
				}
		
				/*
				 * Last item, play prevoius item
				 */
				if (itemRemoved > mSongList.getCount())
				{
					mSongList.playMediaPlayer(itemRemoved - 1);
				}
				
				/*
				 * Middle item, play next song
				 */
				else
				{
					mSongList.playMediaPlayer(itemRemoved);
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
				 * */
				
				if (itemRemoved < mSongList.getmAudioPlaying())
					mSongList.setmAudioPlaying(mSongList.getmAudioPlaying() - 1);
				
			}
			musicHrScrollView.highlightIdex(mSongList.getmAudioPlaying());
		}
		
	};

	OnThumbnailLongTouchListener musicOnLongTouchListener = new OnThumbnailLongTouchListener() {
		
		@Override
		public void onMyLongTouch(View view, int centerIndex) {
			//FrameLayout itemLayout = (FrameLayout) view;
			Log.d("TAG", "OnThumbnailLongTouchListener");
			ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnailImage);
			thumbnail.setBackgroundResource(R.drawable.image_border);
			
			mSongList.setmPlayMode(AudioList.REPEAT_ONE);
			mSongList.setLooping(true);
			
		}
	};

	OnItemAddListener itemAddListener = new OnItemAddListener() {
		
		@Override
		public void onItemnailAdd(int numberThumnail) {
			Log.i("TAG", "item music added");
			/*
			 * Null songlist
			 */
			if (mSongList.getmState() == AudioList.PAUSE)
			{
				mSongList.playMediaPlayer(1);
			}
		}
	};
	
	/**
	 * Music volume controller
	 */
}
