package ins.android.app03.listsong;

import ins.android.app03.home.HomeFragment;
import ins.android.app03.home.R;
import ins.android.app03.home.Utils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListSongFragment extends Fragment
{
	 private SearchView mSearchView;
	 private ListView lv;
	 private  AllSongAdapter adapter;

	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		  final View rootView = inflater.inflate(R.layout.listviewlayout, container, false);
		  
		  adapter = new AllSongAdapter(getActivity(), Utils.mListAllSong);
		  lv = (ListView) rootView.findViewById(R.id.lv);
		  lv.setAdapter(adapter);
		  lv.setOnItemClickListener(itemClickListener);

		  /* Fragment need it to add item to Actionbar */
		  setHasOptionsMenu(true);
		  return rootView;
	}

	/**
	 *  Switch back to Homefragment insteads of exit app.
	 */
	private void SwitchToHomeFragment()
	{
		FragmentManager fragm = getFragmentManager();
		fragm.popBackStack();
		Fragment fragment = new HomeFragment();
		final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
		ft.replace(R.id.content_frame, fragment).commit();
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
			CheckBox checkbox = (CheckBox) viewItem.findViewById(R.id.checkbox);
			
			if (checkbox.isChecked())
				checkbox.setChecked(false);
			else
				checkbox.setChecked(true);
			
			Utils.mListAllSong.get(position).setmSelected(checkbox.isChecked()); 
			adapter.notifyDataSetChanged();
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
		Log.i("TAG", "" + lv.getLastVisiblePosition());
		for (int position = 0; position < lv.getLastVisiblePosition(); position++)
		{
			CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
			checkbox.setChecked(true);
		}
		
		for (int i = 0; i < Utils.mListAllSong.size(); i++)
		{
			Utils.mListAllSong.get(i).setmSelected(true); 
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Un Select All
	 */
	private void unSelectAllSong()
	{
		for (int position = 0; position < lv.getLastVisiblePosition(); position++)
		{
			Log.d("TAG", "count  = " + lv.getCount());
			CheckBox checkbox = (CheckBox) lv.getChildAt(position).findViewById(R.id.checkbox);
			checkbox.setChecked(false);
		}
		
		for (int i = 0; i < Utils.mListAllSong.size(); i++)
		{
			Utils.mListAllSong.get(i).setmSelected(false); 
			adapter.notifyDataSetChanged();
		}
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
