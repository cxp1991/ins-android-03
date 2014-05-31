package ins.android.app03.home;

import ins.android.app03.listsong.ListSongFragment;

import java.util.ArrayList;

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
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailAddListener;
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
	private static boolean IS_REQUEST_MUSIC_IN_DEVICE = false;
	
	/*
	 * RingtoneList & MusicList
	 * Using static to Save state of data after fragment commit times
	 * */
	private static SongList mSongArraylist = new SongList(AudioList.REPEAT_ALL); 
	private static RingtoneList mRingtoneArraylist = new RingtoneList(AudioList.REPEAT_ALL); 
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("HomeFragment", "onCreateView");
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        
        MyHorizontalScrollView ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinish);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneLongTouchListener);
        ringtoneHrScrollView.setOnThumbnailAddListener(ringtoneThumbnailAddListener);
        
        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        
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
		for (int i = 0; i < mSongArraylist.getmAudioList().size(); i++)
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
    			mSongArraylist.addmAudioSongListItem(Utils.mListAllSong.get(i));
    			musicHrScrollView.addThumbnailToParent();
    			
    			/* 
    			 * To add 1 song multiple time &
    			 * uncheck slected item in listview
    			 * */
    			Utils.mListAllSong.get(i).setmSelected(false);
    		}
    	}
    	
    	/*
    	 * Sroll top 1st index & highlight it
    	 */
    	musicHrScrollView.highlightIdex(1);
    	
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




	OnTouchFinishListener ringtoneOnTouchFinish = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(int centerIndex) {
			Log.d("ringtoneOnTouchFinish", "centerIndex = " + centerIndex);
		}
	};
	
	OnTouchFinishListener musicOnTouchFinish = new OnTouchFinishListener() {
		
		@Override
		public void onTouchFinish(int centerIndex) {
			Log.d("musicOnTouchFinish", "centerIndex = " + centerIndex);
		}
	};
	
	OnThumbnailLongTouchListener ringtoneLongTouchListener = new OnThumbnailLongTouchListener() {
		
		@Override
		public void onMyLongTouch(View view, int centerIndex) {
			/*Log.d("LONGPRESS", "centerIndex = " + centerIndex);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			tv.setText("LONG");*/
		}
	};

	OnThumbnailAddListener ringtoneThumbnailAddListener = new OnThumbnailAddListener() {
		
		@Override
		public void onThumbnailAdd(int numberThumnail) {
			Log.d("OnThumbnailAddListener", "numberThumnail = " + numberThumnail);
			Log.d("TAG", "size = " + Utils.mListAllSong.size());
		}
	};

}
