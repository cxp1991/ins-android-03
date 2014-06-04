package ins.android.app03.listsong;

import ins.android.app03.home.HomeFragment;
import ins.android.app03.home.R;
import ins.android.app03.home.Utils;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

public class ListSongFragment extends Fragment
{
	 private SearchView mSearchView;
	 private ListView lv;
	 private  AllSongAdapter adapter;
	 private static int numberItemIsChecked = 0;
		/* (non-Javadoc)
		 * @see android.app.Fragment#onAttach(android.app.Activity)
		 */
		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			Log.i("ListSongFragment", "onAttach");
			super.onAttach(activity);
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.i("ListSongFragment", "onResume");
			// TODO Auto-generated method stub
			super.onResume();
		}

		/* (non-Javadoc)
		 * @see android.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			Log.i("ListSongFragment", "onStart");
			// TODO Auto-generated method stub
			super.onStart();
		}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		  Log.i("ListSongFragment", "oncreateView");
		  while (!HomeFragment.IS_REQUEST_MUSIC_IN_DEVICE);
		  final View rootView = inflater.inflate(R.layout.listviewlayout, container, false);
		  
		  adapter = new AllSongAdapter(getActivity(), Utils.mListAllSong, getActivity());
		  lv = (ListView) rootView.findViewById(R.id.lv);
		  lv.setOnItemClickListener(itemClickListener);
		  lv.setAdapter(adapter);

		  /* Fragment need it to add item to Actionbar */
		  setHasOptionsMenu(true);
		  return rootView;
	}

	/**
	 *  Switch back to Homefragment insteads of exit app.
	 */
	private void SwitchToHomeFragment()
	{
		Log.i("TAG", "SwitchToHomeFragment");
		FragmentManager fragm = getFragmentManager();
		fragm.popBackStack();
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
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
        
		super.onCreateOptionsMenu(menu,inflater);
	}
	
	/**
	 * Actionbar listener
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			SwitchToHomeFragment();
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
	 * @return the numberItemIsChecked
	 */
	public static int getNumberItemIsChecked() {
		return numberItemIsChecked;
	}
	
	 /**
	 * @param numberItemIsChecked the numberItemIsChecked to set
	 */
	public static void setNumberItemIsChecked(int numberItemIsChecked) {
		ListSongFragment.numberItemIsChecked = numberItemIsChecked;
	}

	/**
	 * SearchView listener 
	 */
	SearchView.OnQueryTextListener searchViewListener = new OnQueryTextListener() 
    {
		
		@Override
		public boolean onQueryTextSubmit(String arg0) 
		{
			Log.i("TAG", "onQueryTextSubmit");
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String newText) 
		{
			Log.i("onQueryTextChange", "" + newText);
			adapter.myGetFilter().filter(newText);
			return true;
		}
	};
	
}
