package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.myhorizontalscrollview.MyHorizontalScrollView;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnThumbnailLongTouchListener;
import com.example.myhorizontalscrollview.MyHorizontalScrollView.OnTouchFinishListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        MyHorizontalScrollView ringtoneHrScrollView = (MyHorizontalScrollView) findViewById(R.id.ringtonescrollview);
        ringtoneHrScrollView.setOnTouchFinishListener(ringtoneOnTouchFinish);
        ringtoneHrScrollView.setOnThumbnailLongTouchListener(ringtoneLongTouchListener);
        
        MyHorizontalScrollView musicHrScrollView = (MyHorizontalScrollView) findViewById(R.id.musicscrollview);
        musicHrScrollView.setOnTouchFinishListener(musicOnTouchFinish);
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
}
