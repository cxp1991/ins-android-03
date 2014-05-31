package ins.android.app03.listsong;

import ins.android.app03.home.MySong;
import ins.android.app03.home.R;
import ins.android.app03.home.Utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllSongAdapter extends BaseAdapter
{
	private Context mContext;
	private Filter mFilter;
	private ArrayList<MySong> mArraylist;
	private Typeface font; 
	private AllSongAdapter mAdapter;
	 
	public AllSongAdapter(Context context, ArrayList<MySong> listSong)
	{
		this.mContext = context;
		this.mArraylist = listSong;
		this.font = Typeface.createFromAsset(context.getAssets(), "Chantelli_Antiqua.ttf");
		this.mAdapter = this;
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
			
			String prefix  = constraint.toString().toLowerCase();
			ArrayList<MySong> valueArraylist = new ArrayList<MySong>();                              
			
			/* reset data */
			if (Utils.mListAllSong.size() < mArraylist.size())
			{
				Utils.mListAllSong.clear();
				Utils.mListAllSong.addAll(mArraylist);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			}	
				
            for (int i = 0; i < Utils.mListAllSong.size(); i++) 
            {
            	final String value = Utils.mListAllSong.get(i).getmSongName().toLowerCase();
                final String valueText = value.toString().toLowerCase();

				if (valueText.startsWith(prefix)) 
				{
					valueArraylist.add(Utils.mListAllSong.get(i));
				} 
				else 
				{
					final String[] words = valueText.split(" ");
					final int wordCount = words.length;
				 
					// Start at index 0, in case valueText starts with space(s)
					for (int k = 0; k < wordCount; k++) 
					{
						if (words[k].startsWith(prefix)) 
						{
							valueArraylist.add(Utils.mListAllSong.get(i));
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
			 Utils.mListAllSong = (ArrayList<MySong>) results.values;
			 
			 if (results.count > 0) 
			 {
				 notifyDataSetChanged();
			 }
			 else 
			 {
                 notifyDataSetInvalidated();
             }
		}
		
	}

	class CompleteListViewHolder 
	{  
		public ImageView mThumbnail;
		public TextView mSongName;
		public TextView mArtist;
		public CheckBox mCheckbox;
		
		public CompleteListViewHolder(View base) 
		{  
			mThumbnail = (ImageView) base.findViewById(R.id.thumbnail);
			mSongName  = (TextView) base.findViewById(R.id.tvsongname);
			mArtist    = (TextView) base.findViewById(R.id.tvartist);
			mCheckbox  = (CheckBox) base.findViewById(R.id.checkbox); 
			
			mCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					// Here we get the position that we have set for the checkbox using setTag.
					int getPosition = (Integer) buttonView.getTag();  
					// Set the value of checkbox to maintain its state
					Utils.mListAllSong.get(getPosition).setmSelected(buttonView.isChecked()); 
					notifyDataSetChanged();
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
             LayoutInflater li = (LayoutInflater) mContext  
                       .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
             convertView = li.inflate(R.layout.itemlistviewlayout, null);  

             viewHolder = new CompleteListViewHolder(convertView);  
             convertView.setTag(viewHolder);  
        }
        else 
        {  
             viewHolder = (CompleteListViewHolder) convertView.getTag();  
        }  

        viewHolder.mSongName.setText(Utils.mListAllSong.get(position).getmSongName());
        viewHolder.mSongName.setTypeface(this.font);
        
        viewHolder.mArtist.setText(Utils.mListAllSong.get(position).getmSongArtist());
        viewHolder.mArtist.setTypeface(this.font);
        
        if (Utils.mListAllSong.get(position).getmThumbnail() != null)
        	viewHolder.mThumbnail.setImageBitmap(Utils.mListAllSong.get(position).getmThumbnail());
        else
        	viewHolder.mThumbnail.setImageResource(R.drawable.music);
        
        viewHolder.mCheckbox.setTag(position); 
        viewHolder.mCheckbox.setChecked(Utils.mListAllSong.get(position).ismSelected()); 
        
        return convertView;
	}

}
