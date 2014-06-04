package ins.android.app03.home;

import ins.android.app03.listsong.ListSongFragment;
import ins.android.app03.main.Main;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnItemAddListener;
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
	public static boolean IS_REQUEST_MUSIC_IN_DEVICE = false;
	
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
	
	/*
	 *	Check if thread to switch to next item
	 *	when 1 ringtone is ended
	 */
	private static boolean isThreadSwitchIemRingtoneRun = false;
	
	public static View rootView = null;
	
	private TextView mTextViewLoading = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		//Log.i("HomeFragment", "onCreate, savedInstanceState = " + savedInstanceState);
		super.onCreate(null);
		
		/*
		 * For the first time we need to request all audio file in
		 * device (just 1 time).
		 * 
		 * As this app, we can do it at onCreate() in Activity, but It's not logic. 
		 * From Fragment's lifecycle this method will just call when user choose "Home" 
		 * on Navigation Drawer.
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
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		/*
		 * Because user will switch bween many fragments usually, we
		 * want this HomeFragment's layout is stable. Then my solution is 
		 * use static view to save this layout.
		 */
		if (rootView == null)
		{
			rootView = inflater.inflate(R.layout.activity_main, container, false);
			mRingtoneList = new RingtoneList(AudioList.REPEAT_ALL, getActivity());
		}
        
        ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinishListener);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneOnLongTouchListener);
        
        musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        musicHrScrollView.setOnItemRemoveListener(musicOnItemRemove);
        musicHrScrollView.setOnThumbnailLongTouchListener(musicOnLongTouchListener);
    
        /* Back press listener */
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
        
		/*
    	 * Initialize MyHorizontalScrollView for Song
    	 * We just need to update musicHorizontalScrollview when
    	 * new song is slected from ListSongFragment.
    	 */
		//Log.i("TAG", "ListSongFragment.getNumberItemIsChecked() = " + ListSongFragment.getNumberItemIsChecked());
        if(ListSongFragment.getNumberItemIsChecked() > 0)
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
        	
        	ListSongFragment.setNumberItemIsChecked(0);
        }
    	
    	/*
    	 * TextView is to show when ListSongFragment's data
    	 * is still not available.
    	 */
        mTextViewLoading = (TextView) rootView.findViewById(R.id.tvloading);
        mTextViewLoading.setVisibility(View.INVISIBLE);
        
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
	
	/**
	 * Using this callback to listen event when a song/ringtone is end,
	 * then we need to scroll horizontalscrollview to next item.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//Log.i("HomeFragment", "onActivityCreated, savedInstanceState = " + savedInstanceState);
    	
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
    					//Log.i("TAG", "Switch next Music item, waiting ...");
    					while (!mSongList.isEndOnePlayback());
    					//Log.i("TAG", "Switch next musicitem, done");
    					//Log.i("TAG", "current music playing = " + musicHrScrollView.getCenterIndex());
    					
    					/* Last song, then return to head */
    					if (musicHrScrollView.getCenterIndex() + 1 > musicHrScrollView.getNumberThumbnail())
    					{
    						index = 1;
    					}
    					/* Continue next song */
    					else
    					{
    						index = musicHrScrollView.getCenterIndex() + 1;
    					}
    					
						musicHrScrollView.updateLayout(index);
    					musicHrScrollView.setCenterIndex(index);
    					
    					mSongList.setEndOnePlayback(false);
    				}
    			}
    		}).start();
    		
    		isThreadSwitchIemMusicRun = true;
    	}
    	
    	/* Ringtone */ 
    	if (!isThreadSwitchIemRingtoneRun)
    	{
    		new Thread (new Runnable() 
        	{
    			@Override
    			public void run() 
    			{
    				while(true)
    				{
    					int index;
    					//Log.i("TAG", "Switch next Music item, waiting ...");
    					while (!mRingtoneList.isRingtoneItemEnd());
    					if (ringtoneHrScrollView.getCenterIndex() + 1 > ringtoneHrScrollView.getNumberThumbnail())
    					{
    						index = 1;
    					}
    					else
    					{
    						index = ringtoneHrScrollView.getCenterIndex() + 1;
    					}
    					
						ringtoneHrScrollView.updateLayout(index);
    					ringtoneHrScrollView.setCenterIndex(index);
    					
    					mRingtoneList.setRingtoneItemEnd(false);
    				}
    			}
    		}).start();
    		
    		isThreadSwitchIemRingtoneRun = true;
    	}
    	
    	/*
    	 * This is implemented because our library highlight
    	 * 1st in every time fragment is visible.
    	 */
    	ringtoneHrScrollView.unHighlightIdex(1);
    	ringtoneHrScrollView.highlightIdex(mRingtoneList.getmAudioPlaying());
    	
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
			
			/*
			 * When app start up at the first time. Incase user choose "Add song" when 
			 * requesting song from sdcard is not finish (too many songs in sdcard) so 
			 * listview isn't ready to display song list.
			 * So we wait until this requestion will finish. This waiting just happens 1 time.
			 * 
			 * Incase "new" device doesn't has any song so we will show a toast.
			 * 
			 */
			
			if (!IS_REQUEST_MUSIC_IN_DEVICE)
			{
				mTextViewLoading.setVisibility(View.VISIBLE);
				mTextViewLoading.setText("Loading ...");
			}
			
			/*
			 *  No song in device
			 *  Using condition is Utils.mListAllSong.size() because
			 *  if sdcard have no song, requesting is finish quickly than 
			 *  user choose "Add song". I have test on Emulator.
			 */
			if (Utils.mListAllSong.size() == 0)
			{
				Toast.makeText(getActivity().getBaseContext(), "No song found.", 
						Toast.LENGTH_SHORT).show();
				mTextViewLoading.setVisibility(View.GONE);
				break;
			}
			
			/* Start ListSongFragment to display all song in sdcard */
			Fragment fragment = new ListSongFragment();
			final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
			
			/*
			 * As usual, if ListSongFragment is exit then activity is exit too.
			 * we want app returns to HomeFragment instead so we push HomeFragment into
			 * BackStack. Then when user choose to back to HomeGragment we will pull it
			 * from BackStack (If user press back button, Android will do tghe same thing too).  
			 */
			ft.addToBackStack(null);
			ft.replace(R.id.content_frame, fragment, "LIST_SONG_FRAGMENT").commit();
			
			break;
			
		case R.id.action_remove_all_song:
			/* Not null list */
			if (musicHrScrollView.getNumberThumbnail() > 0)
			{
				LinearLayout lnlayout = musicHrScrollView.getTopLnLayout();
				lnlayout.removeViews(1, musicHrScrollView.getNumberThumbnail());
				musicHrScrollView.setNumberThumbnail(0);
				musicHrScrollView.setCenterIndex(0);
				mSongList.pauseMediaPlayer();
			}
			
			break;
		case R.id.action_scroll_to_head:
			/* Not null list & item is highlighting is not first item  */
			if (musicHrScrollView.getNumberThumbnail() > 0 &&
					(musicHrScrollView.getCenterIndex() != 1))
			{
				musicHrScrollView.setCenterIndex(1);
				musicHrScrollView.updateLayout(1);
				mSongList.playMediaPlayer(1);
			}
			break;
		case R.id.action_scroll_to_end:
			/* Not null list & item is highlighting is not last item  */
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
	 * Stop playing
	 */
	@Override
	public void onDestroy() 
	{
		Log.i("TAG", "OnDestroy");
		mSongList.pauseMediaPlayer();
		mRingtoneList.pauseMediaPlayer();
		super.onDestroy();
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
			if (centerIndex != musicHrScrollView.getmLongTouchItemIndex())
			{
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
					mSongList.playMediaPlayer(centerIndex);
				}
			}
		}
	};
	
	/* Same as  music above */
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
							// TODO Auto-generated method stub
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
	
	/**
	 * Music volume controller
	 */
}
