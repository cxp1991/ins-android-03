package com.example.myhorizontalscrollview;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * Custom HorizontalScrollView
 * @date: 20/05/2014
 * @author: Phong Cao
 * @email: caoxuanphong.khtn@gmail.com
 * */

public class MyHorizontalScrollView extends HorizontalScrollView {
	
	private int numberThumbnail;
	private Drawable thumbnailInitialImage;
	private FrameLayout itemLayout;
	private LinearLayout topLnLayout;
	private int centerIndex, previousCenterIndex = 1;
	private ImageView centerImageView;
	private float initialLocation, newLocation;
	private long intinialTime, newTime;
	private MyHorizontalScrollView instance;
	private int thumbnailWidthDp;
	//private boolean isFling = false;
	
	private int THUMBNAIL_WIDTH;// pixel
	private int SCREEN_WIDTH;
	private int ANIMATION_DURATION = 200; // micro seconds
	private int CENTER_LEFT_EDGE, CENTER_RIGHT_EDGE;
	private int SCROLL_DIRECTION;
	private int SCROLL_FROM_LEFT_TO_RIGHT = 0x01;
	private int SCROLL_FROM_RIGHT_TO_LEFT = 0x02;
	private float startLocation;
	private float stopLocation;
	private float ONCLICK_THREADHOLD;
	private int VELOCITY_X_THRESHOLD = 3000;
	private int WANTED_VELOCITY_X = 2000;
	
	private OnTouchFinishListener touchFinishListener = null;
	private OnThumbnailLongTouchListener thumbnailLongTouchListener = null;
	private GestureDetector gesturedetector;
	private int hilighLightThumbnailDrawable;
	private int normalThumbnailDrawable;
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = this;
		
		Log.d("TAG", "MyHorizontalScrollView(Context context, AttributeSet attrs)");
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.activity_my_horizontal_scroll_view, null));
        
        /* Get "numberThumbnail" attribute */
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.numberThumbnail);
           
            /* Default value is 10 Thumbnails */
            numberThumbnail = a.getInt(R.styleable.numberThumbnail_numberThumbnail, 10);
            thumbnailWidthDp = a.getInt(R.styleable.numberThumbnail_Thumbnail_width, 100);
            thumbnailInitialImage = a.getDrawable(R.styleable.numberThumbnail_ThumbnailImage);
        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }

		/* Convert 100dp [width in XML] to pixel */
		Resources r = getResources();
		THUMBNAIL_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbnailWidthDp, r.getDisplayMetrics());
		ONCLICK_THREADHOLD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
//		/* Get Center location */
		SCREEN_WIDTH =  context.getResources().getDisplayMetrics().widthPixels;
		CENTER_LEFT_EDGE = SCREEN_WIDTH/2 - THUMBNAIL_WIDTH/2;
		CENTER_RIGHT_EDGE = SCREEN_WIDTH/2 + THUMBNAIL_WIDTH/2;
		
		topLnLayout = (LinearLayout) findViewById(R.id.toplnlayout);
        itemLayout = (FrameLayout) inflater.inflate(R.layout.item, null, false);
//        
//        /* Add header */
        LinearLayout headerlayout = (LinearLayout) inflater.inflate(R.layout.headerfooterlayout, null, false);
    	headerlayout.setLayoutParams(new LinearLayout.LayoutParams(CENTER_LEFT_EDGE, LayoutParams.MATCH_PARENT));
        topLnLayout.addView(headerlayout);
        
        /* Add Thumbnails into main layout */
        for (int i = 0; i < numberThumbnail; i++)
        {
        	itemLayout = (FrameLayout) inflater.inflate(R.layout.item, null, false);
        	topLnLayout.addView(itemLayout);
        }
        
        /* Add footer */
        LinearLayout footerlayout = (LinearLayout) inflater.inflate(R.layout.headerfooterlayout, null, false);
        footerlayout.setLayoutParams(new LinearLayout.LayoutParams(CENTER_LEFT_EDGE, LayoutParams.MATCH_PARENT));
        topLnLayout.addView(footerlayout);
        
        /* Set thumbnail's title */
        for (int i = 1; i < numberThumbnail + 1; i++)
        {
        	setThumbnailTitle(i,"" + i);
        	setThumbnailImageResourceFromDrawable(i, thumbnailInitialImage);
        }
        
        gesturedetector = new GestureDetector(context, GestureDetectorListener);
        setHilighLightThumbnailDrawable(R.drawable.balloon_light);
        setNormalThumbnailDrawable(R.drawable.balloon_dark);
        
	}
	
	/* On Touch Finish Listener */
	public void setOnTouchFinishListener(OnTouchFinishListener listener) 
	{
	    this.touchFinishListener = listener;
	}
	
	public interface OnTouchFinishListener 
	{
	    public void onTouchFinish (int centerIndex);
	}
	
	/* On Thumbnail long touch listener */
	public void setOnThumbnailLongTouchListener(OnThumbnailLongTouchListener listener) 
	{
	    this.thumbnailLongTouchListener = listener;
	}
	
	public interface OnThumbnailLongTouchListener 
	{
	    public void onMyLongTouch (View view, int centerIndex);
	}
	
	/* Set thumbnail's image resource from ID */
	public void setThumbnailImageResourceFromId(int index, int drawbleId)
	{
		ImageView imgView = (ImageView)topLnLayout.getChildAt(index).findViewById(R.id.thumbnailImage);
		imgView.setImageResource(drawbleId);
	}
	
	/* Set thumbnail's image resource from Drawable */
	public void setThumbnailImageResourceFromDrawable(int index, Drawable drawable)
	{
		ImageView imgView = (ImageView)topLnLayout.getChildAt(index).findViewById(R.id.thumbnailImage);
		imgView.setImageDrawable(drawable);
	}
	
	public void setHilighLightThumbnailDrawable (int drawableId)
	{
		this.hilighLightThumbnailDrawable  = drawableId;
	}
	
	public void setNormalThumbnailDrawable (int drawableId)
	{
		this.normalThumbnailDrawable = drawableId;
	}
	
	/* Set thumbnail's padding */
	public void setThumbnailPadding(int value)
	{
		ImageView imgView = null;
		for (int i = 0; i < numberThumbnail; i++)
		{
			imgView = (ImageView)topLnLayout.getChildAt(i).findViewById(R.id.thumbnailImage);
			imgView.setPadding(value, value, value, value);
		}
	}
	
	/* Set Thumbnail's title */
	public void setThumbnailTitle (int index, String title)
	{
        	TextView tv = (TextView) topLnLayout.getChildAt(index).findViewById(R.id.tv);
        	tv.setText(title);
	}
	
	GestureDetector.OnGestureListener GestureDetectorListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
	    public void onLongPress(MotionEvent e)
	    {
			longPress(e.getX());
	    }
	};

	/* set velocityX speed threshold */
	public void setFlingVelocityXThreshold(int velocityX)
	{
		this.VELOCITY_X_THRESHOLD = velocityX;
	}
	
	/* set WANTED_VELOCITY_X */
	public void setWantedVelocityX (int velocityX)
	{
		this.WANTED_VELOCITY_X = velocityX;
	}

	/* Limit HorizontalScrollView fling speed */
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
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/* To pass all action to onTouchEvent */
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
			return true;
		
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gesturedetector.onTouchEvent(event);
		
		switch (event.getAction()) 
		{
			case MotionEvent.ACTION_DOWN:
				//Log.d("TAG", "MotionEvent.ACTION_DOWN");
				startLocation = event.getX();
				break;
			
			case MotionEvent.ACTION_UP:
				//Log.d("TAG", "MotionEvent.ACTION_UP");
				stopLocation = event.getX();
				if (Math.abs(stopLocation - startLocation) <= ONCLICK_THREADHOLD)
				{
					singleTapConfirmed(stopLocation);
				}
				else
				{
					Log.d("TAG", "onScroll");
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
		
//		 this.gesturedetector.onTouchEvent(event);
//	        // Be sure to call the superclass implementation
//	        return super.onTouchEvent(event);

	}
	
	/* Find thumbail index from x coordinate */
	private int findThumbnailIndex(double locationX) 
	{
		int[] location = new int[2];
		int index = -1;
		
		int i;
		/* Find all items location */
		for (i = 1; i < this.numberThumbnail + 1; i++)
		{
			topLnLayout.getChildAt(i).getLocationOnScreen(location);
			if ((locationX >= location[0]) && (locationX <= (location[0] + THUMBNAIL_WIDTH)))
			{
				index = i;
				break;
			}
		}
		
		return index;
	}

	/* Update layout */
	private void updateLayout()
	{
		instance.post(new Runnable() {
			
			@Override
			public void run() {
				ObjectAnimator animator=ObjectAnimator.ofInt(instance,
						"scrollX", (centerIndex - 1)*THUMBNAIL_WIDTH);
				
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
						
						/* UnHighlight previous center item */
						centerImageView = (ImageView) topLnLayout.getChildAt(previousCenterIndex).findViewById(R.id.thumbnailImage);
						centerImageView.setImageResource(normalThumbnailDrawable);
						
						/* Highlight center item */
						centerImageView = (ImageView) topLnLayout.getChildAt(centerIndex).findViewById(R.id.thumbnailImage);
						centerImageView.setImageResource(hilighLightThumbnailDrawable);
						previousCenterIndex = centerIndex;
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
	
	private void longPress(double location)
	{
		int index = findThumbnailIndex(location);
		if (index == centerIndex)
		{
			if (thumbnailLongTouchListener != null)
				this.thumbnailLongTouchListener.onMyLongTouch(topLnLayout.getChildAt(index), centerIndex);
		}
	}
	
	private void singleTapConfirmed(double stopLocation)
	{
		centerIndex = findThumbnailIndex(stopLocation);
		if (centerIndex < 0) return;
		updateLayout();
		
		if (touchFinishListener != null)
			touchFinishListener.onTouchFinish(this.centerIndex);
	}

	private void onScrollEvent()
	{
		initialLocation = this.getScrollX();
		intinialTime = System.currentTimeMillis();
		Thread checkStopScrollThread = new Thread(checkScrollStopEvent);
		checkStopScrollThread.start();
	}
	
	Runnable checkScrollStopEvent = new Runnable()
	{

		@Override
		public void run() {
			while (true)
			{
				Delay(20);
				newLocation = instance.getScrollX();
				newTime = System.currentTimeMillis();
				
				//Log.d("VELOCITY", "Quang duong = " + Math.abs((newLocation - initialLocation)));
				//Log.d("VELOCITY", "Thoi gian = " + Math.abs((newTime - intinialTime)*1000));
				//Log.d("onscroll", "Van toc() = " + Math.abs((newLocation - initialLocation)/(newTime - intinialTime)*1000));
				/* Horizontal Stop scroll */
				if(Math.abs((newLocation - initialLocation)/(newTime - intinialTime)*1000) <= 2)
				{
					centerIndex = FindItemNearestCenter();
					if (centerIndex < 0) break;
					Log.d("SCROLL", "centerindex = " + centerIndex);
				
					instance.post(new Runnable() {
						
						@Override
						public void run() {
							int value = 0;
							
							if(centerIndex > 0)
								value = (centerIndex-1)*THUMBNAIL_WIDTH;
							
							/* Cool, I can change speed of scroll using this animation */
							ObjectAnimator animator=ObjectAnimator.ofInt(instance,
									"scrollX", value);
							
							animator.addListener(new AnimatorListener() {
								
								@Override
								public void onAnimationStart(Animator animation) {
								}
								
								@Override
								public void onAnimationRepeat(Animator animation) {
								}
								
								@Override
								public void onAnimationEnd(Animator animation) {
									/* UnHighlight previous center item */
									centerImageView = (ImageView) topLnLayout.getChildAt(previousCenterIndex).findViewById(R.id.thumbnailImage);
									centerImageView.setImageResource(R.drawable.balloon_dark);
									
									/* Highlight center item */
									centerImageView = (ImageView) topLnLayout.getChildAt(centerIndex).findViewById(R.id.thumbnailImage);
									centerImageView.setImageResource(R.drawable.balloon_light);
									previousCenterIndex = centerIndex;
								}
								
								@Override
								public void onAnimationCancel(Animator animation) {
								}
							});
							
							animator.setDuration(ANIMATION_DURATION);
							animator.start();
							
						}
					});
					
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

	private int FindItemNearestCenter()
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
		}
		
		int nearestIndex;
		
		if(arrlocation.size() == 0)
		{
			return -1;
		}
		
		nearestIndex = FindMin(arrlocation);
		return nearestIndex;
	}

	/* Find minimum value */
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
	
	/*
	* Delay using Thread
	* */
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

	/* Object save index & distance from center position of each item */
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
