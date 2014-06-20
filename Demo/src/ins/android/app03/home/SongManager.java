package ins.android.app03.home;

import java.io.FileDescriptor;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class SongManager
{
	/* Contain data result */
	public static ArrayList<MySong> mListAllSong  = new ArrayList<MySong>();
	
	private LoaderManager mLoadermanager;
	private CursorLoader mCursorLoader;
	private Cursor mCursor;
	private boolean mRequestDone = false;
	private Context mContext;
	
	public SongManager(Context context) {
		this.mContext = context;
	}
	/**
	 * List all song in external device using cursorLoader
	 * 
	 * @param activity
	 */
	public void getAllAudio()
	{
		mLoadermanager = ((FragmentActivity) mContext).getSupportLoaderManager();
		mLoadermanager.initLoader(1, null, callback);
	}
	
	/**
	 * Using Loadermanager & Cursorloader to list all audio
	 * in external device 
	 */
	 LoaderCallbacks<Cursor> callback = new LoaderCallbacks<Cursor>() {
		
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			/*
			 *  Should not do large work here
			 */
			mCursor = cursor;
			mRequestDone = true;
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			/*
			 *  Music Filter 
			 */
			String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

			/* 
			 * Result table column 
			 */
			String[] projection = {
					MediaStore.Audio.Media.TITLE, // Name
					MediaStore.Audio.Media.ARTIST,		 // artist
					MediaStore.Audio.Media.DURATION,	 // time to play
					MediaStore.Audio.Media.DATA,		 // full path
					MediaStore.Audio.Media.ALBUM_ID,
			};
			
			
			mCursorLoader = new  CursorLoader(
				                    mContext,   // Parent activity context
				                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,        // Table to query
				                    projection,     // Projection to return
				                    selection,            // No selection clause
				                    null,            // No selection arguments
				                    MediaStore.Audio.Media.TITLE + " ASC"            //sort order
								);

			return mCursorLoader;
		}
	}; 
	
	/**
	 * Insert query's result to @mListAllSong
	 */
	public void insertQueryResultIntoSonglist()
	{
		/*
		 * Wait util  onLoadFinished of Loadermanager done
		 */
		while (!mRequestDone);
		
		/*
		 * No audio is external storage
		 */
		if (mCursor == null)
			return;
		
		Bitmap thumbnail = null;
		
		try {
			if (mCursor.moveToFirst()) 
			{
				do 
				{
					/* 
					 * Get thumbnail from this audio
					 * */
					thumbnail = getAudioThumbnail(mContext, mCursor.getLong(4));
					
					/*
					 * Add to container 
					 */
					mListAllSong.add(new MySong(
		    				  mCursor.getString(0), 
		    				  mCursor.getString(1), 
		    				  mCursor.getInt(2)/1000, 
		    				  mCursor.getString(3), 
		    				  thumbnail));
					
				} while (mCursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			
		}
		
	}
	
	/**
	 * Get thumbnail from audio file
	 * 
	 * @param context
	 * @param album_id
	 * @return Bitmap thumbnail from this audio
	 */
	private Bitmap getAudioThumbnail (Context context, long album_id)
	{
		Bitmap bm = null;
		
	    try 
	    {
	    	/*
	    	 * Get album's uri
	    	 * */
	        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

	        /*
	         * Open uri
	         */
	        ParcelFileDescriptor pfd = context.getContentResolver()
	    	            .openFileDescriptor(uri, "r");

	        /*
	         * Decode bitmap from album
	         */
	        if (pfd != null) 
	        {
	        	FileDescriptor fd = pfd.getFileDescriptor();
	            bm = decodeBitmap(fd, 48, 48);
	        }
	    } 
	    catch (Exception e) 
	    {
	    	//e.printStackTrace();
		}
	    
		return bm;
	}
	
	private Bitmap decodeBitmap(FileDescriptor fd,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFileDescriptor(fd, null, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFileDescriptor(fd, null, options);
	}
	
	private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
}

