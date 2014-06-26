package ins.android.app03.home;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends FragmentStatePagerAdapter{

	private List<Fragment> fragments;

	public MyPagerAdapter(	FragmentManager fm, 
							List<Fragment> fragments) 
	{
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

}
