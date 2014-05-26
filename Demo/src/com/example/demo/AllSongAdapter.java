package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AllSongAdapter extends BaseAdapter 
{
	private Context mContext;
	
	public AllSongAdapter(Context context)
	{
		this.mContext = context;
	}
	
	/**
	 * Return number row of listview
	 */
	@Override
	public int getCount()
	{
		return Utils.mListAllSong.size();
	}

	@Override
	public Object getItem(int position)
	{
		return Utils.mListAllSong.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	class CompleteListViewHolder 
	{  
		
	      public TextView mTVItem;  
	      public CompleteListViewHolder(View base) 
	      {  
	           mTVItem = (TextView) base.findViewById(R.id.tv01);  
	      }  
	 }  
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;  
        CompleteListViewHolder viewHolder;
        
        if (convertView == null) 
        {  
             LayoutInflater li = (LayoutInflater) mContext  
                       .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
             v = li.inflate(R.layout.itemlistviewlayout, null);  
             viewHolder = new CompleteListViewHolder(v);  
             v.setTag(viewHolder);  
        }
        else 
        {  
             viewHolder = (CompleteListViewHolder) v.getTag();  
        }  
        viewHolder.mTVItem.setText(Utils.mListAllSong.get(position).getmSongName());  
        return v;
	}

}
