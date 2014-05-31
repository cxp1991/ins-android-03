package ins.android.app03.home;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
	private int keyBackPressCount = 2;
	private static boolean IS_REQUEST_MUSIC_IN_DEVICE = false;
	private static ArrayList<Integer> selectedPositionArray = new ArrayList<Integer>(); 
	
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
        
        /* Fragment need it to add item to Actionbar */
        setHasOptionsMenu(true);
		  
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("HomeFragment", "onActivityCreated");
		
    	try 
    	{
    		/* Query to list all song in device */
        	if (IS_REQUEST_MUSIC_IN_DEVICE == false)
        	{
    			Utils.getAllAudio(getActivity());
        	}
        	
    		/* Add result to data container 
        	 * Using thread to not block UI thread
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
                        Looper.loop();
                        IS_REQUEST_MUSIC_IN_DEVICE = true;
                	}
                }
            }).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	/*
    	 *  Add new Item into parent
    	 *  
    	 * Reset selected item in database
    	 * to when open ListFragment all item is not selected.
    	 */

    	/* Add old selected item */
		for (int j = 0; j < selectedPositionArray.size(); j++)
		{
			musicHrScrollView.addThumbnailToParent();
		}
		
    	for (int i = 0; i < Utils.mListAllSong.size(); i++)
    	{
    		/* Add new selected item */
    		if (Utils.mListAllSong.get(i).ismSelected())
    		{
    			selectedPositionArray.add(i);
    			musicHrScrollView.addThumbnailToParent();
    			Utils.mListAllSong.get(i).setmSelected(false);
    		}
    	}
    	
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
