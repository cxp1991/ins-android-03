package ins.android.app03.listsong;

import ins.android.app03.home.HomeFragment;
import ins.android.app03.home.R;
import ins.android.app03.home.Utils;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListSongFragment extends Activity
{
	 private SearchView mSearchView;
	 private ListView lv;
	 private AllSongAdapter adapter;
		
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		while (!HomeFragment.isMusicListed);
		setContentView(R.layout.listviewlayout);

		adapter = new AllSongAdapter(Utils.mListAllSong, this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(itemClickListener);
		lv.setAdapter(adapter);

	}
	
	/**
	 * Checkbox onclick listener
	 */
	OnItemClickListener itemClickListener = new OnItemClickListener() 
	{

		@Override
		public void onItemClick (AdapterView<?> listview, View viewItem, int position,
				long id) 
		{
			Log.i("TAG", "onItemClick");
			CheckBox checkbox = (CheckBox) viewItem.findViewById(R.id.checkbox);
			
			if (checkbox.isChecked())
			{
				checkbox.setChecked(false);
			}
			else
			{
				checkbox.setChecked(true);
			}
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
		mSearchView = (SearchView) searchItem.getActionView();
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
		switch (item.getItemId()) {
		case R.id.action_done:
			setResult(adapter.getmNumberItemIsChecked());
			finish();
			break;
		case R.id.action_selectall:
			Log.i("onQueryTextChange", "Select all");
			selectAllSong();
			break;
		case R.id.action_unselectall:
			Log.i("onQueryTextChange", "UnSelect all");
			unSelectAllSong();
			break;
		default:
			break;
		}
		
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
			try 
			{
				CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
				checkbox.setChecked(true);
			}
			catch (Exception e)
			{
				
			}
		}
		
		for (int i = 0; i < Utils.mListAllSong.size(); i++)
		{
			Utils.mListAllSong.get(i).setmSelected(true); 
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
			try
			{
				Log.d("TAG", "count  = " + lv.getCount());
				CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
				checkbox.setChecked(false);
			}
			catch (Exception e)
			{
				
			}
		}
		
		for (int i = 0; i < Utils.mListAllSong.size(); i++)
		{
			Utils.mListAllSong.get(i).setmSelected(false); 
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
			//Log.i("TAG", "onQueryTextSubmit");
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String newText) 
		{
			//Log.i("onQueryTextChange", "" + newText);
			adapter.myGetFilter().filter(newText);
			return true;
		}
	};
	
}
