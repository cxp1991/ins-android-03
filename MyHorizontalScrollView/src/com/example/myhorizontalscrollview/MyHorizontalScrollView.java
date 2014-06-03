package com.example.myhorizontalscrollview;

import java.util.ArrayList;

import android.R.mipmap;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 	Custom HorizontalScrollView
 * 
 * @date: 2014
 * @author: Ideas&Solutions Android Team, PhongCao
 * @email: caoxuanphong.khtn@gmail.com
 * */

public class MyHorizontalScrollView extends HorizontalScrollView 
{
	private int numberThumbnail;
	private FrameLayout itemLayout;
	private LinearLayout topLnLayout;
	private FrameLayout addItemLayout;
	private int centerIndex;
	private float initialLocation, newLocation;
	private long intinialTime, newTime;
	private MyHorizontalScrollView instance;
	private int thumbnailWidthDp, thumbnailHeightDp;
	private float startLocation;
	private float stopLocation;
	private LayoutInflater inflater;
	private int layoutLocation; // 0 = top, 1 = middle, 2 = bottom
	private LayoutParams params;
	private LayoutTransition layoutTransition;
	
	private int THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT;// pixel
	private int SCREEN_WIDTH;
	private int ANIMATION_DURATION = 200; // micro seconds
	private int CENTER_LEFT_EDGE, CENTER_RIGHT_EDGE;
	private int SCROLL_DIRECTION;
	private int SCROLL_FROM_LEFT_TO_RIGHT = 0x01;
	private int SCROLL_FROM_RIGHT_TO_LEFT = 0x02;
	private float ONCLICK_THREADHOLD;
	private int VELOCITY_X_THRESHOLD = 4000;
	private int WANTED_VELOCITY_X = 4000;
	private final int SCROLL_STOP_VELOCITY = 2;
	private final int MOVE_TO_TOP_THRESHOLD = -1;
	private final int MOVE_TO_BOTTOM_THRESHOLD = -2;
	private final int MOVE_TO_TOP_OR_BOTTOM  = -3;
	private final int COUNT_TO_CALC_DIRECTION = 3;
	private final int ACTION_REMOVE_UP = 0x03;
	private final int ACTION_REMOVE_DOWN = 0x04;
	
	private OnTouchFinishListener touchFinishListener = null;
	private OnThumbnailLongTouchListener thumbnailLongTouchListener = null;
	private OnItemAddListener itemAddListener = null;
	private OnItemRemoveListener itemRemoveListener = null;
	private GestureDetector gesturedetector;
	private Drawable hilighLightThumbnailDrawable;
	private Drawable normalThumbnailDrawable;
	private boolean enableScroll = true;
	private int mItemRemovedIndex;
	
	private float x0 = 0, y0 = 0;
    private float x1 = 0, y1 = 0;
    private float dx, dy;
    private int count = 0;
    private boolean isActionRemove = false;
    
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = this;
		
		Log.d("TAG", "MyHorizontalScrollView(Context context, AttributeSet attrs)");
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.activity_my_horizontal_scroll_view, null));
        
        /* Get  attributes */
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.numberThumbnail);
           
            /* Default value is 10 Thumbnails */
            numberThumbnail = a.getInt(R.styleable.numberThumbnail_numberThumbnail, 10);
            thumbnailWidthDp = a.getInt(R.styleable.numberThumbnail_Thumbnail_width, 100);
            thumbnailHeightDp = a.getInt(R.styleable.numberThumbnail_Thumbnail_height, 100);
            normalThumbnailDrawable = a.getDrawable(R.styleable.numberThumbnail_NormalThumbnailImage);
            hilighLightThumbnailDrawable = a.getDrawable(R.styleable.numberThumbnail_HighlightThumbnailImage);
            layoutLocation = a.getInt(R.styleable.numberThumbnail_layout_location, 1); // Default is middle
        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }

		/* Convert 100dp [width in XML] to pixel */
		Resources r = getResources();
		THUMBNAIL_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbnailWidthDp, r.getDisplayMetrics());
		THUMBNAIL_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbnailHeightDp, r.getDisplayMetrics());
		ONCLICK_THREADHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
		
		/* Get Center location */
		SCREEN_WIDTH =  context.getResources().getDisplayMetrics().widthPixels;
		CENTER_LEFT_EDGE = SCREEN_WIDTH/2 - THUMBNAIL_WIDTH/2;
		CENTER_RIGHT_EDGE = SCREEN_WIDTH/2 + THUMBNAIL_WIDTH/2;
		
		topLnLayout = (LinearLayout) findViewById(R.id.toplinearlayout);
        itemLayout = (FrameLayout) inflater.inflate(R.layout.item, null, false);
       
        /* Control thumbnail position */
        params = new LayoutParams(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        
        //Log.d("params", "location = " + layoutLocation);
        
        if (layoutLocation == 0)
        	params.gravity = Gravity.TOP;
        else if (layoutLocation == 1)
        	params.gravity = Gravity.CENTER_VERTICAL;
        else if (layoutLocation == 2)
        	params.gravity = Gravity.BOTTOM;
        
        /* Add header */
        LinearLayout headerlayout = (LinearLayout) inflater.inflate(R.layout.headerfooterlayout, null, false);
    	headerlayout.setLayoutParams(new LinearLayout.LayoutParams(CENTER_LEFT_EDGE, LayoutParams.MATCH_PARENT ));
        topLnLayout.addView(headerlayout);
        
        /* Add Thumbnails into main layout */
        for (int i = 0; i < numberThumbnail; i++)
        {
        	itemLayout = (FrameLayout) inflater.inflate(R.layout.item, null, false);
        	itemLayout.setLayoutParams(new LinearLayout.LayoutParams(THUMBNAIL_WIDTH, LayoutParams.MATCH_PARENT));
        	
        	ImageView thumbnail = (ImageView) itemLayout.findViewById(R.id.thumbnailImage);
    		TextView title = (TextView) itemLayout.findViewById(R.id.tv);
    		
    		thumbnail.setLayoutParams(params);
    		title.setLayoutParams(params);
    		
        	topLnLayout.addView(itemLayout);
        }
        
        /* Add "add" icon */
//        addItemLayout = (FrameLayout) inflater.inflate(R.layout.additem, null, false);
//        addItemLayout.setLayoutParams(new LayoutParams(THUMBNAIL_WIDTH, LayoutParams.MATCH_PARENT));
//        ImageView addImgaView = (ImageView) addItemLayout.findViewById(R.id.additemImgV);
//        addImgaView.setLayoutParams(params);
//        topLnLayout.addView(addItemLayout);
        
        /* Add footer */
        LinearLayout footerlayout = (LinearLayout) inflater.inflate(R.layout.headerfooterlayout, null, false);
        footerlayout.setLayoutParams(new LinearLayout.LayoutParams(CENTER_LEFT_EDGE, 
        		LayoutParams.MATCH_PARENT));
        topLnLayout.addView(footerlayout);
        
        /* Set thumbnail's title */
        /* Highlight 1st thumbnail */
        if (numberThumbnail >= 1)
        {
        	setThumbnailTitle(1, "1");
        	setThumbnailImageResourceFromDrawable(1, hilighLightThumbnailDrawable);
        	centerIndex = 1;
        }
        
        if (numberThumbnail >= 2)
        {
        	for (int i = 2; i < numberThumbnail + 1; i++)
            {
            	setThumbnailTitle(i,"" + i);
            	setThumbnailImageResourceFromDrawable(i, normalThumbnailDrawable);
            }
        }
        
        /* Gesture detector for "long press" event */
        gesturedetector = new GestureDetector(context, GestureDetectorListener);
        
        /* Set animation for add or remove view */
        layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(100);
        layoutTransition.addTransitionListener(OnLayoutTranslation);
		topLnLayout.setLayoutTransition(layoutTransition);
	}
	
	/**
	 *  On Touch Finish Listener 
	 */
	public void setOnTouchFinishListener(OnTouchFinishListener listener) 
	{
	    this.touchFinishListener = listener;
	}
	
	public interface OnTouchFinishListener 
	{
	    public void onTouchFinish (int centerIndex);
	}
	
	/**
	 *  On Thumbnail long touch listener 
	 */
	public void setOnThumbnailLongTouchListener(OnThumbnailLongTouchListener listener) 
	{
	    this.thumbnailLongTouchListener = listener;
	}
	
	public interface OnThumbnailLongTouchListener 
	{
	    public void onMyLongTouch (View view, int centerIndex);
	}
	
	/**
	 * Item Add Listener
	 * @param listener
	 */
	public void setOnItemAddListener (OnItemAddListener listener)
	{
		this.itemAddListener = listener;
	}
	
	public interface OnItemAddListener
	{
		public void onItemnailAdd(int numberThumnail);
	}
	

	/**
	 * Item remove Listener
	 * @param listener
	 */
	public void setOnItemRemoveListener (OnItemRemoveListener listener)
	{
		this.itemRemoveListener = listener;
	}
	
	public interface OnItemRemoveListener
	{
		public void onItemRemove(int itemRemoved);
	}
	
	/**
	 * Get @centerIndex
	 */
	public int getCenterIndex()
	{
		return this.centerIndex;
	}
	
	/**
	 * Set CenterIndex
	 */
	public void setCenterIndex(int newCenterIndex)
	{
		this.centerIndex = newCenterIndex;
	}
	
	
	/**
	 *  Set thumbnail's image resource from ID 
	 */
	public void setThumbnailImageResourceFromId(int index, int drawbleId)
	{
		ImageView imgView = (ImageView)topLnLayout.getChildAt(index).findViewById(R.id.thumbnailImage);
		imgView.setImageResource(drawbleId);
	}
	
	/**
	 *  Set thumbnail's image resource from Drawable 
	 */
	public void setThumbnailImageResourceFromDrawable(int index, Drawable drawable)
	{
		ImageView imgView = (ImageView)topLnLayout.getChildAt(index).findViewById(R.id.thumbnailImage);
		
		if (imgView != null) // Fast removing
			imgView.setImageDrawable(drawable);
	}
	
	/**
	 * Set highlight thumbnail.
	 * 
	 * @param drawable
	 */
	public void setHilighLightThumbnailDrawable (Drawable drawable)
	{
		this.hilighLightThumbnailDrawable  = drawable;
	}
	
	/**
	 * Set normal thumbnail.
	 * 
	 * @param drawable
	 */
	public void setNormalThumbnailDrawable (Drawable drawable)
	{
		this.normalThumbnailDrawable = drawable;
	}
	
	/** 
	 * Set item's thumbnail padding 
	 */
	public void setThumbnailPadding(int value)
	{
		ImageView imgView = null;
		for (int i = 0; i < numberThumbnail; i++)
		{
			imgView = (ImageView)topLnLayout.getChildAt(i).findViewById(R.id.thumbnailImage);
			imgView.setPadding(value, value, value, value);
		}
	}
	
	/**
	 * Set item's title.
	 * 
	 * @param index
	 * @param title
	 */
	public void setThumbnailTitle (int index, String title)
	{
        	TextView tv = (TextView) topLnLayout.getChildAt(index).findViewById(R.id.tv);
        	
        	if (tv != null) // For fast removing
        		tv.setText(title);
	}
	
	/**
	 * Long-press evetn listener
	 */
	GestureDetector.OnGestureListener GestureDetectorListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
	    public void onLongPress(MotionEvent e)
	    {
			longPress(e.getX());
	    }
	};

	/**
	 *  Set velocityX speed threshold 
	 */
	public void setFlingVelocityXThreshold(int velocityX)
	{
		this.VELOCITY_X_THRESHOLD = velocityX;
	}
	
	/**
	 *  set WANTED_VELOCITY_X
	 */ 
	public void setWantedVelocityX (int velocityX)
	{
		this.WANTED_VELOCITY_X = velocityX;
	}

	/**
	 *  Limit @MyHorizontalScrollView fling speed
	 *  when user flings too fast 
	 */
	@Override
	public void fling(int velocityX) 
	{
		if (velocityX > VELOCITY_X_THRESHOLD)
	    {
	        velocityX = WANTED_VELOCITY_X;
	    }
	    else if(velocityX < VELOCITY_X_THRESHOLD*(-1))
	    {
	        velocityX = (WANTED_VELOCITY_X)*(-1);
	    }
	    
		super.fling(velocityX);
	}
	
	/**
	 *  Transfer full event to onTouchEvent().
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/* To pass all action to onTouchEvent */
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
			return true;
		
		return super.onTouchEvent(ev);
	}
	
	
	/**
	 *  Enable/Disable scroll event 
	 */
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		
		if (enableScroll)
		{
			Log.i ("onOverScrolled", "onOverScrolled");
			super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		}
	}

	
	/**
	 * Scroll to item has index is @index
	 */
	public void scrollToIndex(int index)
	{
		Log.d("TAG", "scrollToIndex = " + index);
		Log.d("TAG", "scrollToIndex = " + (index - 1)*THUMBNAIL_WIDTH);
		
		//this.scrollTo((index - 1)*THUMBNAIL_WIDTH, 0);
		
		ObjectAnimator animator = ObjectAnimator.ofInt(instance,
				"scrollX", (index - 1)*THUMBNAIL_WIDTH);
		animator.start();
	}
	
	/**
	 * Highlight item at @index
	 */
	public void highlightIdex(int index)
	{
		/*
		 * This has only meaning at the first time we add song
		 * into scrollview.
		 * No event is catched.
		 * */
		centerIndex = index;
		
		try
		{
			//this.scrollToIndex(index);
			Log.d("TAG", "highlightIdex = " + index);
			/* Then, highlight @centerIndex item */
			ImageView imgView = (ImageView) topLnLayout.getChildAt(index).findViewById(R.id.thumbnailImage);
			imgView.setImageDrawable(hilighLightThumbnailDrawable);
		}
		catch(Exception e)
		{
			Log.e("TAG", "highlightIdex exaption");
		}
	}
	
	/**
	 * Listen events:
	 * 	1. Single-tap: add action, choose item action
	 * 	2. Long-press
	 * 	3. Scroll
	 * 	4. Remove
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		this.gesturedetector.onTouchEvent(event);
		
		switch (event.getAction()) 
		{
			case MotionEvent.ACTION_DOWN:
				startLocation = event.getX();
				
				isActionRemove = false;
				enableScroll = false;
				
				/* For determine move direction */
				x0 = event.getX();
				y0 = event.getY();
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				Log.d("MotionEvent.ACTION_MOVE", "MotionEvent.ACTION_MOVE");
				
				/* Wait to get threshold to find direction of action event */
				if (count < COUNT_TO_CALC_DIRECTION)
				{
					count ++;
					break;
				}
				
				/* Find-out direction action  */
				if (count == COUNT_TO_CALC_DIRECTION)
				{
					count++;
					x1 = event.getX();
					y1 = event.getY();
					
					/* Calculate direction */
					dx = (float) Math.sqrt ((x1 - x0) * (x1 - x0));
					dy = (float) Math.sqrt ((y1 - y0) * (y1 - y0));
					
					Log.d ("TAG", "dx = " + dx + ", dy = " + dy);
					Log.i ("TAG", "y = " + (y1 - y0));
					
					/* Is up-to-down or down-to-up touch action */
					if ((dy > (THUMBNAIL_WIDTH/5)) && dy >= 2*dx)
					{
						final int action_mode;
						isActionRemove = true;
						
						final int index = findThumbnailIndex(x0);
						Log.d("TAG", "Remove thumbnail, index = " + index) ;
						if ((y1 - y0) >= 0)
							action_mode = ACTION_REMOVE_DOWN;
						else 
							action_mode = ACTION_REMOVE_UP;
						
						removeThumbnailFromParent (topLnLayout.getChildAt(index), action_mode, index);
						
						break;
					}
					
					enableScroll = true;
				}
				
				break;
				
			case MotionEvent.ACTION_UP:
				
				stopLocation = event.getX();
				count = 0;
				
				/* If remove item action we stop here */
				if (isActionRemove)
				{
					isActionRemove = false;
					break;
				}
				
				/* SingleTap */
				if (Math.abs(stopLocation - startLocation) <= ONCLICK_THREADHOLD)
				{
					singleTapConfirmed(stopLocation);
				}
				
				/* Scroll */
				else
				{
					if (stopLocation - startLocation >= 0)
						SCROLL_DIRECTION = SCROLL_FROM_LEFT_TO_RIGHT;
					else
						SCROLL_DIRECTION = SCROLL_FROM_RIGHT_TO_LEFT;
					onScrollEvent();
				}
				
				break;
				
			default:
				break;
		}		
		
		return super.onTouchEvent(event);
	}
	
	/**
	 *  Add 1 item at the end of parrent
	 *  I do not update layout(centerIndex & highlight it)
	 *  Do not using animation
	 */ 
	public void addThumbnailToParent() 
	{
		Log.i("TAG", "addThumbnailToParent");
		/* Insert thumbnail at the end */ 
		itemLayout = (FrameLayout) inflater.inflate(R.layout.item, null, false);
		itemLayout.setLayoutParams(new LayoutParams(THUMBNAIL_WIDTH, LayoutParams.MATCH_PARENT));
		ImageView thumbnail = (ImageView) itemLayout.findViewById(R.id.thumbnailImage);
		TextView title = (TextView) itemLayout.findViewById(R.id.tv);
		
		thumbnail.setLayoutParams(params);
		title.setLayoutParams(params);
    	topLnLayout.addView(itemLayout,numberThumbnail + 1);
		
    	numberThumbnail++;
    	
    	/* Set all title again */
        if (numberThumbnail >= 1)
        {
        	for (int i = 1; i < numberThumbnail + 1; i++)
            {
            	setThumbnailTitle(i,"" + i);
            	setThumbnailImageResourceFromDrawable(i, normalThumbnailDrawable);
            }
        }
        
        /* 
         * Scroll to end 
         * Using animation
         * */
        //updateLayout(numberThumbnail);
        
        /*
         * Not using animation
         */
	}

	/**
	 *  Remove 1 item from its parent 
	 *  Remove using view not index to remove bugs when remove multiple items at the same time.
	 *  FIXME: Sometimes insteads of remove item It'll scroll the horizontalscrollview
	 */
	private void removeThumbnailFromParent (final View v, final int action, final int index)
	{
		this.mItemRemovedIndex = index;
		/*
		 * Framelayout contains item will be removed
		 */
		final FrameLayout view = (FrameLayout)v;
		
		Log.d ("removeThumbnailFromParent", "removeThumbnailFromParent");

		/* 
		 * Alpha & Translate animation 
		 * to display how item will be removed
		 */
		
		/* Alpha animation */
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setDuration(750);
		
		/* Translate animation */
		Animation translate = null;
		
		/* Check user gesture is valid */
		
		/* Our horizontalscrollview is in top of screen */
		if (layoutLocation == 0 && (action == ACTION_REMOVE_DOWN))
			translate = new TranslateAnimation(0,0,0, THUMBNAIL_WIDTH);
		else if (layoutLocation == 0 && (action == ACTION_REMOVE_UP))
		{
			Log.w ("removeThumbnailFromParent", "Invalid motion");
			return;
		}
		
		/* Our horizontalscrollview is in bottom of screen */
		if (layoutLocation == 2 && (action == ACTION_REMOVE_UP))
			translate = new TranslateAnimation(0,0,0, THUMBNAIL_WIDTH*(-1));
		else if (layoutLocation == 2 && (action == ACTION_REMOVE_DOWN))
		{
			Log.w ("removeThumbnailFromParent", "Invalid motion");
			return;
		}
		
		translate.setDuration(750);
		
		/* Merge 2 animations using Animationset*/
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(fadeOut);
		animationSet.addAnimation(translate);
		
		try {
			/* Thumbnail & text will use above animation */
			ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnailImage);
			TextView title = (TextView) view.findViewById(R.id.tv);
			
			thumbnail.setAnimation(animationSet);
			title.setAnimation(animationSet);
			thumbnail.startAnimation(animationSet);
			title.startAnimation(animationSet);
			
			/* Wait until animation terminate, then remove item out of our horizontal scrollview */
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					Log.i ("TAG", "Start animationset");
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					instance.post(new Runnable() {
						
						@Override
						public void run() {
							int indexOfView = topLnLayout.indexOfChild(view);
							
							/* Remove imageview & textview are inside removing item */
							view.removeAllViews();
							
							/* Remove item */
							topLnLayout.removeView(view);
							numberThumbnail--;
							Log.i ("Remove Item", "item = " + index);
							
							/* Auto call LayoutTranslation listener */
							
							/* Enale scroll */
							enableScroll = true;

							/*
							 * Call item removing listener
							 * Don't use index, use index of removed view instead 
							 */
							if (itemRemoveListener != null)
					        	itemRemoveListener.onItemRemove(indexOfView);
					        
							
						}
					});
				
					Log.i ("TAG", "End animationset");
				}
			});

		}catch (NullPointerException ex)
		{
			
		}
	}

	/**
	 * LayoutTranslation Listener for 2 event:
	 * Add item & remove item
	 */
	LayoutTransition.TransitionListener OnLayoutTranslation = new TransitionListener() {

		@Override
		public void startTransition(LayoutTransition transition,
				ViewGroup container, View view, int transitionType) {
			/* 
			 * Start Dispear animation
			 */
			if (transitionType == LayoutTransition.DISAPPEARING)
			{
				Log.i ("TAG", "Start Dispear translation");
			}
		}
		
		@Override
		public void endTransition(LayoutTransition transition, ViewGroup container,
				View view, int transitionType) {
			
			/* End dispear animation */
			if (transitionType == LayoutTransition.CHANGE_DISAPPEARING
					&& container.getId() == instance.getId())
			{
				/* Set all title again */ 
		        if (numberThumbnail >= 1)
		        {
		        	for (int i = 1; i < numberThumbnail + 1; i++)
		            {
		            	setThumbnailTitle(i,"" + i);
		            	//setThumbnailImageResourceFromDrawable(i, normalThumbnailDrawable);
		            }
		        }
		        
		        /*
		         *  Update center position 
		         *  and hghlight it. 
		         */ 
		        
	        	if (mItemRemovedIndex < centerIndex)
	        	{
	        		Log.d("TAG", "Remove item: item before center item");
	        		centerIndex --;
	        		updateLayout(centerIndex);
	        	}
		        	/* Item removed's index = centerIndex */
		            if (mItemRemovedIndex == centerIndex)
		         	{
		         		Log.d("TAG", "Remove item: item is center item");
		         		
		         		/* 1 item remain */
		         		if (numberThumbnail == 0)
		         		{
		         			centerIndex = 0;
		         		}
		         		
		         		/* The last item was removed */
		         		else if (centerIndex > numberThumbnail)
		         		{
		         			Log.d("TAG", "Remove item: Last item");
				        	centerIndex --;
				        	updateLayout(centerIndex);
		         		}
		         		else
		         			updateLayout(centerIndex);
			         		
		         	}
		         	else if (mItemRemovedIndex > centerIndex)
		         	{
		         		// NOTHING NEED TO DO
		         		Log.d("TAG", "Remove item: item is after center item");
		         	}
		         	else
			        {
			        	Log.d("TAG", "Remove item: Hmm, don't update layout" +
			        			"Check below information. It's out of my guess");
			        	Log.d("TAG", "center index = " + centerIndex + ", numberthumbnail = " + numberThumbnail);
			        }
					        
			    	Log.i ("Remove Item", "centerIndex = " + centerIndex);
			        Log.i ("TAG", "Stop Dispear translation");
		        
			}
			
		}
		
	};
	
	/**
	 *  Find item index from x coordinate
	 */ 
	private int findThumbnailIndex(double locationX) 
	{
		int[] location = new int[2];
		int index = MOVE_TO_TOP_OR_BOTTOM;
		
		int i;
		
		/* Find all items location */
		for (i = 1; i < this.numberThumbnail + 1; i++)
		{
			topLnLayout.getChildAt(i).getLocationOnScreen(location);
			
			/* Filter wanted item */
			if ((locationX >= location[0]) && (locationX <= (location[0] + THUMBNAIL_WIDTH)))
			{
				index = i;
				break;
			}
		}
		
		return index;
	}

	/**
	 *  Update layout
	 *  
	 *   + Scroll @MyHorizontalScrollview to @index
	 *   + Highlight @centerIndex item 
	 */
	public void updateLayout(final int index)
	{
		Log.i("updateLayout", "updateLayout");
		
		instance.post(new Runnable() {
			
			@Override
			public void run() {
					/* Using animation to scroll @MyHorizontalScrollView */
					ObjectAnimator animator = ObjectAnimator.ofInt(instance,
							"scrollX", (index - 1)*THUMBNAIL_WIDTH);
					
					/* Wait until scroll animation stop, we highlight center thumbnail */
					animator.addListener(new AnimatorListener() {
					
						@Override
						public void onAnimationStart(Animator animation) {
						}
						
						@Override
						public void onAnimationRepeat(Animator animation) {
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
								Log.i ("updateLayout", "New center index = " + index);
								Log.i ("updateLayout", "numberThumbnail = " + numberThumbnail);
								ImageView imgView = null;
								
								try 
								{
									/* First reset highlight */
									for (int i = 1; i <= numberThumbnail; i ++)
									{
										imgView = (ImageView) topLnLayout.getChildAt(i).findViewById(R.id.thumbnailImage);
										if (imgView != null) // fast removing
											imgView.setImageDrawable(normalThumbnailDrawable);
									}
									
									/* Then, highlight @centerIndex item */
									imgView = (ImageView) topLnLayout.getChildAt(centerIndex).findViewById(R.id.thumbnailImage);
									if (imgView != null) // fast removing
									{
										imgView.setImageDrawable(hilighLightThumbnailDrawable);
									}
								}
								catch (NullPointerException e)
								{
								}
								Log.i("updateLayout", "Update Layout finish");
						}
						
						@Override
						public void onAnimationCancel(Animator animation) {
						}
					});
					
					animator.setDuration(ANIMATION_DURATION);
					animator.start();
				}
			});
		
	}
	
	/**
	 * Long-press event -> find chosen item's index -> 
	 * create long-press listener event.
	 * 
	 * @param location
	 */
	private void longPress(double location)
	{
		Log.d("longPress", "longPress");
		
		int index = findThumbnailIndex(location);
		if (index == centerIndex)
		{
			if (thumbnailLongTouchListener != null)
				this.thumbnailLongTouchListener.onMyLongTouch(topLnLayout.getChildAt(index), centerIndex);
		}
	}
	
	/**
	 *	Scroll @MyHorizontalScrollView to the chosen item by
	 *	single-tap event. 
	 * 
	 * 	Update @centerIndex is chosen item's index.
	 * 
	 *  If item is "add" item then add new item into MyHorizontalScrollView 
	 * 
	 * @param stopLocation is x-coordinate of single-tap event.
	 */
	
	private void singleTapConfirmed (double stopLocation)
	{
		Log.d("singleTapConfirmed", "singleTapConfirmed");
		
		centerIndex = findThumbnailIndex(stopLocation);
		
		/* "Add" item 
		if (centerIndex == numberThumbnail + 1)
		{
			addThumbnailToParent();
			if (thumbnailAddListener != null)
				thumbnailAddListener.onThumbnailAdd(instance.numberThumbnail);
		}*/
		/* Normal item */
		if (centerIndex != MOVE_TO_TOP_OR_BOTTOM)
		{
			updateLayout(centerIndex);
			if (touchFinishListener != null)
				touchFinishListener.onTouchFinish(instance.centerIndex);
		}
	}

	/**
	 *  Wait until scroll event finish. Then find the nearst index, and
	 *  scroll MyHorizontalScrollView to this index.
	 */
	
	private void onScrollEvent()
	{
		Log.d("TAG", "onScroll");
		initialLocation = this.getScrollX();
		intinialTime = System.currentTimeMillis();
		Thread checkStopScrollThread = new Thread(checkScrollStopEvent);
		checkStopScrollThread.start();
	}
	
	Runnable checkScrollStopEvent = new Runnable()
	{
		@Override
		public void run() 
		{
			while (true)
			{
				Delay(20);
				newLocation = instance.getScrollX();
				newTime = System.currentTimeMillis();
				
				/* Horizontal Stop scroll */
				if(Math.abs((newLocation - initialLocation)/(newTime - intinialTime)*1000) 
						<= SCROLL_STOP_VELOCITY)
				{
					centerIndex = ScrollEventFindItemNearestCenter();
					
					if (centerIndex == MOVE_TO_TOP_THRESHOLD)
						centerIndex = 1;
					else if (centerIndex == MOVE_TO_BOTTOM_THRESHOLD)
						centerIndex = numberThumbnail;

					updateLayout(centerIndex);
					if (touchFinishListener != null)
						touchFinishListener.onTouchFinish(instance.centerIndex);
					
					break;
				}
				else
				{
					Delay(20);
					initialLocation = instance.getScrollX();
				}
			}
		}
	};

	/**
	 * Find index of item is nearest center in scroll event.
	 * We will find x-location of all item then compare with "threshold"
	 * depends on scroll direction.
	 *  
	 * @return index of item is nearest center
	 */
	private int ScrollEventFindItemNearestCenter()
	{
	
		int[] location = new int[2];
		ArrayList<xLocation> arrlocation = new ArrayList<xLocation>();
		
		/* Scroll from left to right */
		if (SCROLL_DIRECTION == SCROLL_FROM_LEFT_TO_RIGHT)
		{
			/* Compare RightEdge of item with RightEdge of center */
			for (int i = 1; i < this.numberThumbnail + 1; i++)
			{
				topLnLayout.getChildAt(i).findViewById(R.id.thumbnailImage).getLocationOnScreen(location);
				if ( ((location[0] + THUMBNAIL_WIDTH) <= CENTER_RIGHT_EDGE) && (location[0] + THUMBNAIL_WIDTH >= 0))
				{
					//Log.d("TAG", "location[0] = " + location[0]);
					//Log.d("TAG", "SCROLL_FROM_LEFT_TO_RIGHT " + i + " " + (CENTER_RIGHT_EDGE - (location[0] + THUMBNAIL_WIDTH)));
					arrlocation.add(new xLocation(i, (CENTER_RIGHT_EDGE - (location[0] + THUMBNAIL_WIDTH))));
				}
			}
			
			if(arrlocation.size() == 0)
			{
				return MOVE_TO_TOP_THRESHOLD;
			}
		}
		
		/* Scroll from right to left */
		else if (SCROLL_DIRECTION == SCROLL_FROM_RIGHT_TO_LEFT)
		{
			/* Compare LeftEdge of item with LeftEdge of center */
			for (int i = 1; i < this.numberThumbnail + 1; i++)
			{
				topLnLayout.getChildAt(i).findViewById(R.id.thumbnailImage).getLocationOnScreen(location);
				if ((location[0] >= CENTER_LEFT_EDGE) && (location[0] <= SCREEN_WIDTH))
				{
					//Log.d("TAG", "location[0] = " + location[0]);
					//Log.d("TAG", "SCROLL_FROM_RIGHT_TO_LEFT " + i + " " + (location[0] - CENTER_LEFT_EDGE));
					arrlocation.add(new xLocation(i, location[0] - CENTER_LEFT_EDGE));
				}
			}
			
			if(arrlocation.size() == 0)
			{
				return MOVE_TO_BOTTOM_THRESHOLD;
			}
		}
		
		int nearestIndex;
		nearestIndex = FindMin(arrlocation);
		return nearestIndex;
	}

	/**
	 *  Find minimum value
	 *  
	 * @param arrLocation
	 * @return minimum value
	 */
	private int FindMin(ArrayList<xLocation> arrLocation) {
	
		int minIndex =arrLocation.get(0).getIndex();
		int minValue = arrLocation.get(0).getdistanceFromCenter();
		
		for(int i=1; i < arrLocation.size() ;i++)
		{
			if(arrLocation.get(i).getdistanceFromCenter() < minValue){
				minValue = arrLocation.get(i).getdistanceFromCenter();
				minIndex = arrLocation.get(i).getIndex();
			}
		}

		return minIndex;
	}
	
	/**
	 * Delay using Thread
	 * 
	 * @param time in milliseconds
	 */
	private void Delay(int time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}	
	}

	/**
	 *  Object save index & distance from center position of each item 
	 */
	private class xLocation
	{
		private int index;
		private int distanceFromCenter;
		
		public xLocation(int index, int distanceFromCenter)
		{
			this.index = index;
			this.distanceFromCenter = distanceFromCenter;
		}
		
		public int getIndex()
		{
			return this.index;
		}
		
		public int getdistanceFromCenter()
		{
			return this.distanceFromCenter;
		}
	}

}
