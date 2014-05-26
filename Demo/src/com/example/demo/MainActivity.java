package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailAddListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;

public class MainActivity extends Activity {

	private Activity mActivity;
	private ImageView imgView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main);
        
        MyHorizontalScrollView ringtoneHrScrollView = (MyHorizontalScrollView) findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinish);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneLongTouchListener);
        ringtoneHrScrollView.setOnThumbnailAddListener(ringtoneThumbnailAddListener);
        
        MyHorizontalScrollView musicHrScrollView = (MyHorizontalScrollView) findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
        
        /* Query to list all song in device */
    	if (Utils.mListAllSong.size() == 0)
    	{
			Utils.getAllAudio(this);
    	}
    	
    	/* Add result to data container 
    	 * Using thread to not block UI thread
    	 * */
    	new Thread( new Runnable()
    	{
            private int index;

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
