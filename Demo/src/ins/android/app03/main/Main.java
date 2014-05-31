package ins.android.app03.main;
import ins.android.app03.home.HomeFragment;
import ins.android.app03.home.R;
import ins.android.app03.listsong.ListSongFragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Control Navigation drawer
 * 
 * @author cxphong
 */
public class Main extends Activity
{
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;
	private String[] navMenuTitles;

	private ArrayList<DrawerItem> mDrawerItem;
	private DrawerListAdapter mDrawerAdapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		
		/* Add item on navigation drawer */
		mDrawerItem = new ArrayList<DrawerItem>();
		mDrawerItem.add(new DrawerItem(navMenuTitles[0], R.drawable.ic_home));
		mDrawerItem.add(new DrawerItem(navMenuTitles[1], R.drawable.ic_communities));
		
		mDrawerAdapter = new DrawerListAdapter(this, mDrawerItem);
		mDrawerList.setAdapter(mDrawerAdapter);
		mDrawerList.setOnItemClickListener(SlideMenuClickListener);
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
				
	}
	
	
	ListView.OnItemClickListener SlideMenuClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			displayView(position);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		Log.i("displayView", "displayView");
		Fragment fragment = null;
		final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
		
		/*
		 * When press back button from listsongfragment, I want to come back to homefragment
		 * insteads of exit program
		 * */
		Fragment mHomeFragment = (Fragment)getFragmentManager().findFragmentByTag("HOME_FRAGMENT");
		Fragment mListSongFragment = (Fragment)getFragmentManager().findFragmentByTag("LIST_SONG_FRAGMENT");
		
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			/*
			 * If current fragment is HomeFragment then save current app state
			 * into backstack
			 * */
			if (mListSongFragment != null)
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
				
			
			ft.replace(R.id.content_frame, fragment, "HOME_FRAGMENT").commit();
			break;
		case 1:
			fragment = new ListSongFragment();
			
			/*
			 * If current fragment is HomeFragment then save current app state
			 * into backstack
			 * */
			if (mHomeFragment != null)
				ft.addToBackStack(null);
			
			ft.replace(R.id.content_frame, fragment, "LIST_SONG_FRAGMENT").commit();
			break;
		default:
			break;
		}

		if (fragment != null) 
		{
//			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			Log.i("TAG", "Close Navigation");
			mDrawerLayout.closeDrawer(mDrawerList);
			
		}
		else 
		{
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		Log.i("setTitle", "setTitle");
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	
}
