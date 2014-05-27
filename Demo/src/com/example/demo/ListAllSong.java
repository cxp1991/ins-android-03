package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListAllSong extends Activity
{
	 private SearchView mSearchView;
	 private ListView lv;
	 private  AllSongAdapter adapter;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.listviewlayout);

        adapter = new AllSongAdapter(this, Utils.mListAllSong);
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(adapter);
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
	{
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview_in_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);

        return true;
    }

    private void setupSearchView(MenuItem searchItem) 
    {
    	/* Set SearchView is always visible */
        mSearchView.setIconifiedByDefault(false);
        
        mSearchView.setOnQueryTextListener(searchViewListener);
        mSearchView.setSubmitButtonEnabled(false);
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
