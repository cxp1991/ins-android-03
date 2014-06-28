
package ins.android.app03.home;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GifDecoderView extends ImageButton {

    private boolean mIsPlayingGif = false;

    private GifDecoder mGifDecoder;

    private Bitmap mTmpBitmap;

    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
                GifDecoderView.this.setImageBitmap(mTmpBitmap);
            }
        }
    };

    public GifDecoderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GifDecoderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GifDecoderView(Context context) {
		super(context);
	}

	public GifDecoderView(Context context, InputStream stream) {
        super(context);
        playGif(stream);
    }

    public void playGif(InputStream stream) {
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(stream);

        mIsPlayingGif = true;

        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes = mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                mGifDecoder.freeResource();
                
                do {
                    for (int i = 0; i < n; i++) {
                    	mTmpBitmap = mGifDecoder.getFrame(i);
                        int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        if (t > 0) {
                        	 try {
                         		Thread.sleep(t);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                        }
                    }
                    if(ntimes != 0) {
                        repetitionCounter ++;
                    }
                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
                
                mGifDecoder.freeAllResource();
                mTmpBitmap = null;
                mGifDecoder = null;
            }
        }).start();
        
    }
    
    public void stopRendering() {
        mIsPlayingGif = false;
    }
}
