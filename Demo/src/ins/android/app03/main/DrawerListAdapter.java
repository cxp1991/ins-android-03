package ins.android.app03.main;
import ins.android.app03.home.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for Listview is to display navigation drawer item
 * @author cxphong
 *
 */
public class DrawerListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<DrawerItem> mDrawerItemArraylist;
	
	public DrawerListAdapter(Context context, ArrayList<DrawerItem> drawerItemArraylist)
	{
		this.mContext = context;
		this.mDrawerItemArraylist = drawerItemArraylist;
	}
	
	@Override
	public int getCount() 
	{
		return mDrawerItemArraylist.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDrawerItemArraylist.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	class ViewHolder
	{
		private TextView mTitle;
		private ImageView mIcon;
		
		public ViewHolder(View view)
		{
			this.mTitle = (TextView) view.findViewById(R.id.titledrawer);
			this.mIcon  = (ImageView) view.findViewById(R.id.icondrawer);
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null)
		{
			 LayoutInflater mInflater = (LayoutInflater)
	                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			 convertView = mInflater.inflate(R.layout.draweritemlayout, null);
			 
			 holder = new ViewHolder(convertView);
			 convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mIcon.setImageResource(mDrawerItemArraylist.get(position).getmIcon());
		holder.mTitle.setText(mDrawerItemArraylist.get(position).getmTtitle());
		
		return convertView;
	}
	
}
