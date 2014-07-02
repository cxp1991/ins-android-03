package ins.android.app03.home;

import ins.android.app03.home.R;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;



public class AllSongAdapter extends BaseAdapter
{
	private Filter mFilter;
	private ArrayList<MySong> mArraylist;
	private AllSongAdapter mAdapter;
	private Activity mActivity;
	private int mNumberItemIsChecked = 0;
	 
	public AllSongAdapter(ArrayList<MySong> listSong, Activity activity)
	{
		this.mArraylist = listSong;
		this.mAdapter = this;
		this.mActivity = activity;
	}
	
	/**
	 * Return number row of listview
	 */
	@Override
	public int getCount()
	{
		return SongManager.mListAllSong.size();
	}

	@Override
	public Object getItem(int position)
	{
		return SongManager.mListAllSong.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

    /**
     * Implement search 
     * @return
     */
	public Filter myGetFilter()
	{
		  if (mFilter == null) 
		  {
              mFilter = new myFilter();
          }
		  
          return mFilter;
	}
	
	class myFilter extends Filter
	{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) 
		{
			Log.i("TAG", "performFiltering");
			FilterResults results=new FilterResults();
			
			String prefix  = constraint.toString().toLowerCase(Locale.ENGLISH);
			ArrayList<MySong> valueArraylist = new ArrayList<MySong>();                              
			
			/* reset data */
			if (SongManager.mListAllSong.size() < mArraylist.size())
			{
				SongManager.mListAllSong.clear();
				SongManager.mListAllSong.addAll(mArraylist);
				
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();						
					}
				});
			}	
				
            for (MySong song : SongManager.mListAllSong) 
            {
            	final String value = song.getmSongName().toLowerCase(Locale.ENGLISH);
                final String valueText = value.toString().toLowerCase(Locale.ENGLISH);

				if (valueText.startsWith(prefix)) 
				{
					valueArraylist.add(song);
				} 
				else 
				{
					final String[] words = valueText.split(" ");
					final int wordCount = words.length;
				 
					 //Start at index 0, in case valueText starts with space(s)
					for (int k = 0; k < wordCount; k++) 
					{
						if (words[k].startsWith(prefix)) 
						{
							valueArraylist.add(song);
							break;
						}
					}
				}

				results.count = valueArraylist.size();
				results.values = valueArraylist;
            }
            
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) 
		{
			Log.i("TAG", "publishResults");
			 SongManager.mListAllSong = (ArrayList<MySong>) results.values;
			 
			 if (results.count > 0) 
			 {
				 Log.i("TAG", "adapter line 145");
				mAdapter.notifyDataSetChanged();
			 }
			 else 
			 {
				 Log.i("TAG", "adapter line 150");
				mAdapter.notifyDataSetInvalidated();
             }
		}
		
	}

	class CompleteListViewHolder 
	{  
		public ImageView mThumbnail;
		public TextView mSongName;
		public TextView mArtist;
		
		public CompleteListViewHolder(View base) 
		{  
			mThumbnail = (ImageView) base.findViewById(R.id.thumbnail);
			mSongName  = (TextView) base.findViewById(R.id.tvsongname);
			mArtist    = (TextView) base.findViewById(R.id.tvartist);
			
			mThumbnail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					try {
						int position = (Integer) v.getTag();
						
						if (SongManager.mListAllSong.get(position).ismSelected()) {
							mNumberItemIsChecked --;
							SongManager.mListAllSong.get(position).setmSelected(false);
							if (SongManager.mListAllSong.get(position).getmThumbnail() != null)
				             	mThumbnail.setImageBitmap(SongManager.mListAllSong.get(position).getmThumbnail());
				             else
				             	mThumbnail.setImageResource(R.drawable.ic_music_02);
						} else {
							mNumberItemIsChecked ++;
							SongManager.mListAllSong.get(position).setmSelected(true);
							mThumbnail.setImageResource(R.drawable.check_1);
						}
						
						mActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								mAdapter.notifyDataSetChanged();							
							}
						});
					} catch (Exception e) {
						
					}
				}
			});
		}  
	 }  
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
        CompleteListViewHolder viewHolder;
        
        /* Times to do is equal number item displays at start time */
        if (convertView == null) 
        {  
             LayoutInflater li = (LayoutInflater) mActivity  
                       .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
             convertView = li.inflate(R.layout.itemlistviewlayout, parent, false);  

             viewHolder = new CompleteListViewHolder(convertView);  
             convertView.setTag(viewHolder);  
        }
        else 
        {  
             viewHolder = (CompleteListViewHolder) convertView.getTag();  
        }  

        viewHolder.mSongName.setText(SongManager.mListAllSong.get(position).getmSongName());
        viewHolder.mArtist.setText(SongManager.mListAllSong.get(position).getmSongArtist());
        
        if (SongManager.mListAllSong.get(position).ismSelected()) {
        	viewHolder.mThumbnail.setImageResource(R.drawable.check_1);
        } else {
        	 if (SongManager.mListAllSong.get(position).getmThumbnail() != null) {
             	viewHolder.mThumbnail.setImageBitmap(SongManager.mListAllSong.
             			get(position).getmThumbnail());
        	 }
             else
             	viewHolder.mThumbnail.setImageResource(R.drawable.ic_music_02);
        }
        
        viewHolder.mThumbnail.setTag(position);
        
        return convertView;
	}

	/**
	 * @return the mNumberItemIsChecked
	 */
	public int getmNumberItemIsChecked() {
		return mNumberItemIsChecked;
	}
	
}
