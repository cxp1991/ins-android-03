package ins.android.app03.home;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyFragment extends Fragment{

	private static final String DRAWABLE_ID = "image id";
	private static final String GIF_NAME = "gif name";
	private static final String INDEX = "index";
	
	private int resId;
	private String mGifName;
	private int mIndex;
	
	private GifDecoderView gifView;
	private InputStream stream = null;
	
	private int PLAYING = 0x0;
	private int STOP = 0x1;
	private int state = STOP;
	
	public static final MyFragment newInstance (int index, int drawableId, String gifName)
	{
		MyFragment f = new MyFragment();
		Bundle bdl = new Bundle(1);
		bdl.putInt(DRAWABLE_ID, drawableId);
		bdl.putString(GIF_NAME, gifName);
		bdl.putInt(INDEX, index);
		f.setArguments(bdl);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		
		resId = getArguments().getInt(DRAWABLE_ID);
		mGifName = getArguments().getString(GIF_NAME);
		mIndex = getArguments().getInt(INDEX);
		
		gifView = (GifDecoderView) inflater.inflate(
							  R.layout.viewpager_layout, 
							  container, false);
		
		gifView.setImageResource(resId);
		
		gifView.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if (state == PLAYING) {
					stopGifAnimation();
				}
				else if (state == STOP) {
					startGifAnimation();
				}
				
			}
			
		});
		
		return gifView;
	}
	
	public void startGifAnimation ()
	{
		try {
			stream = getActivity().getAssets().open(mGifName);
			gifView.playGif(stream);
			state = PLAYING;
			((HomeActivity)getActivity()).playRingtone(mIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void stopGifAnimation ()
	{
		/* Exception is page is already stop */
		try {
			gifView.stopRendering();
			
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			gifView.setImageResource(0);
			
			state = STOP;
			((HomeActivity)getActivity()).stopRingtone(mIndex);
			
		} catch (Exception e) {
			
		}
		
	}
	
	public void pagerClickDisable () {
		gifView.setClickable(false);
	}
	
	public void pagerClickEnable () {
		gifView.setClickable(true);
	}
	
}
