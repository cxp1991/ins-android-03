package ins.android.app03.home;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyFragment extends Fragment{

	private static final String DRAWABLE_ID="IMAGE_ID";
	private int resId;
	private GifDecoderView gifView;
	private InputStream stream = null;
	
	public static final MyFragment newInstance(int drawableId)
	{
		MyFragment f = new MyFragment();
		Bundle bdl = new Bundle(1);
		bdl.putInt(DRAWABLE_ID, drawableId);
		f.setArguments(bdl);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		
		resId = getArguments().getInt(DRAWABLE_ID);
		gifView = (GifDecoderView) inflater.inflate(
							  R.layout.viewpager_layout, 
							  container, false);
		gifView.setImageResource(resId);
		
		return gifView;
	}
	
	public void startGifAnimation (String gif)
	{
		try {
			stream = getActivity().getAssets().open(gif);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gifView.playGif(stream);
			
		
	}
	
	public void stopGifAnimation (String gif)
	{
		gifView.stopRendering();	
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gifView.setImageResource(0);
	}
	
}
