package ins.android.app03.home;


import java.io.FileDescriptor;
import java.util.ArrayList;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

public class Utils
{
	/* Contain data result */
	public static ArrayList<MySong> mListAllSong  = new ArrayList<MySong>();
	
	private static LoaderManager mLoadermanager;
	private static CursorLoader mCursorLoader;
	private static Activity mActivity;
	private static Cursor mCursor;
	private static boolean mRequestDone = false;
	
	/**
	 * List all song in external device using cursorLoader
	 * 
	 * @param activity
	 */
	public static void getAllAudio(Activity activity)
	{
		mActivity = activity;
		mLoadermanager = activity.getLoaderManager();
		mLoadermanager.initLoader(1, null, callback);
	}
	
	/**
	 * Using Loadermanager & Cursorloader to list all audio
	 * in external device 
	 */
	static LoaderCallbacks<Cursor> callback = new LoaderCallbacks<Cursor>() {
		
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
					MediaStore.Audio.Media.DISPLAY_NAME, // Name
					MediaStore.Audio.Media.ARTIST,		 // artist
					MediaStore.Audio.Media.DURATION,	 // time to play
					MediaStore.Audio.Media.DATA,		 // full path
					MediaStore.Audio.Media.ALBUM_ID
			};
			
			
			mCursorLoader = new  CursorLoader(
				                    mActivity,   // Parent activity context
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
	public static void insertQueryResultIntoSonglist()
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
					thumbnail = getAudioThumbnail(mActivity.getBaseContext(), mCursor.getLong(4));
					
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
		
		Log.d("insertQueryResultIntoSonglist", "size = " + mListAllSong.size());
	}
	
	/**
	 * Get thumbnail from audio file
	 * 
	 * @param context
	 * @param album_id
	 * @return Bitmap thumbnail from this audio
	 */
	public static Bitmap getAudioThumbnail (Context context, long album_id)
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
	            bm = BitmapFactory.decodeFileDescriptor(fd);
	        }
	    } 
	    catch (Exception e) 
	    {
	    	//e.printStackTrace();
		}
	    
		return bm;
	}
}

