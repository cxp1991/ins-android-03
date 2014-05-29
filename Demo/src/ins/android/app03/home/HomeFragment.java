package ins.android.app03.home;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailAddListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;


public class HomeFragment extends Fragment
{
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("TAG", "onCreateView");
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        
        MyHorizontalScrollView ringtoneHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinish);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneLongTouchListener);
        ringtoneHrScrollView.setOnThumbnailAddListener(ringtoneThumbnailAddListener);
        
        MyHorizontalScrollView musicHrScrollView = (MyHorizontalScrollView) rootView.findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("TAG", "onActivityCreated");
		
		/* Query to list all song in device */
    	if (Utils.mListAllSong.size() == 0)
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
            	if (Utils.mListAllSong.size() == 0)
            	{
            		Looper.prepare();
                    Utils.insertQueryResultIntoSonglist();
                    Looper.loop();
            	}
            }
        }).start();
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
