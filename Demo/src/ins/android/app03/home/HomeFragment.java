package ins.android.app03.home;

import ins.android.app03.listsong.ListSongFragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemRemoveListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;


public class HomeFragment extends Fragment
{
	private MyHorizontalScrollView musicHrScrollView;
	
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
	private static SongList mSongArrayList = new SongList(AudioList.REPEAT_ALL); 
	//private static RingtoneList mRingtoneArraylist = new RingtoneList(AudioList.REPEAT_ALL); 
	
	/*
	 *	Check if thread to switch to next item
	 *	when 1 song is ended
	 */
	private static boolean isThreadSwitchIemRun = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Log.i("HomeFragment", "onCreate, savedInstanceState = " + savedInstanceState);
		super.onCreate(null);
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
        
        //MyHorizontalScrollView ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
//        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinish);
//        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneLongTouchListener);
//        ringtoneHrScrollView.setOnThumbnailAddListener(ringtoneThumbnailAddListener);
        
        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        musicHrScrollView.setOnItemRemoveListener(musicOnItemRemove);
        
        /*
         * Back press listener 
         */
        rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		  
		rootView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				
				if( (keyCode == KeyEvent.KEYCODE_BACK) && (keyEvent.getAction() == KeyEvent.ACTION_DOWN))
				{
					Log.e("onKey", "back press");
					keyBackPressCount --;
					if (keyBackPressCount  > 0)
					{
						Toast toast = Toast.makeText(getActivity(), "Press back again to exit", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return true;
					}
				    return false;
				}
				
				return false;
			}
		  });
        
        /* 
         * Fragment need it to add item to Actionbar 
         * */
        setHasOptionsMenu(true);
		  
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("HomeFragment", "onActivityCreated, savedInstanceState = " + savedInstanceState);
		
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
    	
    	/*
    	 * Initialize MyHorizontalScrollView for Song
    	 */

    	/*
    	 *  At fragment start up time, we need to re-initialize 
    	 *  item in MyHorizontalSrollView again. 
    	 * */
		for (int i = 0; i < mSongArrayList.getmAudioList().size(); i++)
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
    			mSongArrayList.addmAudio(Utils.mListAllSong.get(i));
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
    	if (mSongArrayList.getCount() > 0)
    	{
    		musicHrScrollView.scrollToIndex(mSongArrayList.getmAudioPlaying());
    		musicHrScrollView.highlightIdex(mSongArrayList.getmAudioPlaying());
    		//Log.i("TAG", "center index = " + musicHrScrollView.getCenterIndex());
    		
    		//Log.i("TAG", "mSongArrayList.getmState() = " + mSongArrayList.getmState());
    		if (mSongArrayList.getmState() == AudioList.PAUSE)
    			mSongArrayList.playMediaPlayer(mSongArrayList.getmAudioPlaying());
    	}
    	
    	/*
    	 * Switch next when 1 end of playback
    	 * Because we switch between many fragment
    	 * so we use condition to run thread or not
    	 */
    	if (!isThreadSwitchIemRun)
    	{
    		new Thread (new Runnable() 
        	{
    			@Override
    			public void run() 
    			{
    				while(true)
    				{
    					Log.i("TAG", "Switch next item, waiting ...");
    					while (!mSongArrayList.isEndOnePlayback());
    					Log.i("TAG", "Switch next item, done");
    					Log.i("TAG", "current playing = " + mSongArrayList.getmAudioPlaying());
    					musicHrScrollView.setCenterIndex(mSongArrayList.getmAudioPlaying());
    					musicHrScrollView.updateLayout(mSongArrayList.getmAudioPlaying());
    					
    					mSongArrayList.setEndOnePlayback(false);
    				}
    			}
    		}).start();
    		
    		isThreadSwitchIemRun = true;
    	}
    	
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
		mSongArrayList.releasePlayer();
		//mRingtoneArraylist.pauseMediaPlayer();
		super.onDestroy();
	}

	OnTouchFinishListener musicOnTouchFinish = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(int centerIndex) {
			//Log.d("musicOnTouchFinish", "centerIndex = " + centerIndex);
			if (centerIndex != mSongArrayList.getmAudioPlaying())
				mSongArrayList.playMediaPlayer(centerIndex);
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
			Log.i("Remove song list", "List size = " + mSongArrayList.getCount());
			
			mSongArrayList.getmAudioList().remove(itemRemoved - 1);
			
			/*
			 * Removed song is also the song is playing   
			 */
			if (itemRemoved == mSongArrayList.getmAudioPlaying())
			{
				/*
				 * 1 item remain
				 */
				if (mSongArrayList.getCount() == 0)
				{
					mSongArrayList.pauseMediaPlayer();
					return;
				}
		
				/*
				 * Last item, play nearest last item
				 */
				if (itemRemoved > mSongArrayList.getCount())
				{
					mSongArrayList.playMediaPlayer(itemRemoved - 1);
				}
				
				/*
				 * Middle item
				 */
				else
				{
					mSongArrayList.playMediaPlayer(itemRemoved);
				}
			}
			
		}
	};
}
