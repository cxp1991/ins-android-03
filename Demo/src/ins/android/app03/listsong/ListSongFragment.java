package ins.android.app03.listsong;

import ins.android.app03.home.MySong;
import ins.android.app03.home.R;
import ins.android.app03.home.SongManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;





public class ListSongFragment extends ActionBarActivity
{
	 private SearchView mSearchView;
	 private ListView lv;
	 private AllSongAdapter adapter;
		
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listviewlayout);
		
		/* Enable add UP action into ActionBar */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_pre_sign_in_press);
	 
		adapter = new AllSongAdapter(SongManager.mListAllSong, this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(itemClickListener);
		lv.setAdapter(adapter);

	}
	
	/**
	 * Checkbox onclick listener
	 */
	 OnItemClickListener itemClickListener = new OnItemClickListener() {

		 @Override
			public void onItemClick (AdapterView<?> listview, View viewItem, int position,
					long id) 
			{
				viewItem.findViewById(R.id.thumbnail).performClick();
			}
	 };
	
	/**
	 * Add Search item into Actionbar
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_song_menu, menu);
        
        /* Search View */
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setIconifiedByDefault(true);
		mSearchView.setOnQueryTextListener(searchViewListener);
		mSearchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d("TAG", "Huraaaaaaaaaa");
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	/**
	 * Actionbar listener
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.action_done:
//			setResult(adapter.getmNumberItemIsChecked());
//			finish();
//			break;
//		case R.id.action_selectall:
//			Log.i("onQueryTextChange", "Select all");
//			selectAllSong();
//			break;
//		case R.id.action_unselectall:
//			Log.i("onQueryTextChange", "UnSelect all");
//			unSelectAllSong();
//			break;
//		case android.R.id.home:
//			/*Intent intent = new Intent (this, HomeFragment.class);
//			intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME);
//	        NavUtils.navigateUpTo(this, intent)*/;
//	        return true;
//	        
//		default:
//			break;
//		}
//		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Select All
	 */
	private void selectAllSong() 
	{
		Log.i("TAG", "firstVisiblePosition = " + lv.getFirstVisiblePosition());
		Log.i("TAG", "LastVisiblePosition = " + lv.getLastVisiblePosition());
		for (int position = lv.getFirstVisiblePosition(); position < lv.getLastVisiblePosition(); position++)
		{
			/*
			 * Sometime, item is visible but item's checkbox is not
			 */
//			try 
//			{
//				CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
//				checkbox.setChecked(true);
//			}
//			catch (Exception e)
//			{
//				
//			}
		}
		
		for (MySong song : SongManager.mListAllSong)
		{
			song.setmSelected(true); 
		}
		
		adapter.notifyDataSetChanged();
	}

	/**
	 * Un Select All
	 */
	private void unSelectAllSong()
	{
		for (int position = lv.getFirstVisiblePosition(); position < lv.getLastVisiblePosition(); position++)
		{
//			try
//			{
//				Log.d("TAG", "count  = " + lv.getCount());
//				CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
//				checkbox.setChecked(false);
//			}
//			catch (Exception e)
//			{
//				
//			}
		}
		

		for (MySong song : SongManager.mListAllSong)
		{
			song.setmSelected(false); 
		}
		
		adapter.notifyDataSetChanged();
		
	}
	
	/**
	 * SearchView listener 
	 */
	SearchView.OnQueryTextListener searchViewListener = new OnQueryTextListener() 
    {
		
		@Override
		public boolean onQueryTextSubmit(String arg0) 
		{
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String newText) 
		{
			adapter.myGetFilter().filter(newText);
			return true;
		}
	};
	
}
