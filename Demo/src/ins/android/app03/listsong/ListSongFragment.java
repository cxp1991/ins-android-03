package ins.android.app03.listsong;

import ins.android.app03.home.R;
import ins.android.app03.home.Utils;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
		
		  View rootView = inflater.inflate(R.layout.listviewlayout, container, false);
		  
		  adapter = new AllSongAdapter(getActivity(), Utils.mListAllSong);
		  lv = (ListView) rootView.findViewById(R.id.lv);
		  lv.setAdapter(adapter);

		  /* Enable to add item to Actionbar */
		  setHasOptionsMenu(true);
		  
		  return rootView;
	}

	/**
	 * Add Search item into Actionbar
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.searchview_in_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(searchViewListener);
        super.onCreateOptionsMenu(menu,inflater);
	}

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
			Log.i("onQueryTextChange", "" + newText);
			adapter.getFilter().filter(newText);
			return true;
		}
	};
	
}
