package com.crossconnect.actions;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

public class HomeFeatureLayout extends HorizontalScrollView {
    private static final int SWIPE_MIN_DISTANCE = 50;

    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private List<LinearLayout> childColumns = null;
    
    private List<ImageButton> btmColumnIcons = null;

    private List<List<ImageButton>> topMenuIcons = null;

    
//    private OnClickListener need to create a custom listener that fires when colum is changed
// make it return which column it has been changed to

    private GestureDetector homeFlingDetector;
    
    private GestureDetector verticalScrollGestureDetector;

    private int mActiveFeature = 0;

    private float lastKeyDownX;
    
    
    private LayoutParams screenSize;

    public LayoutParams getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(LayoutParams screenSize) {
        this.screenSize = screenSize;
    }

    public HomeFeatureLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HomeFeatureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeFeatureLayout(Context context) {
        super(context);
    }

    /**
     * Create the homelayout child views from the given list 
     * @param columnViews LinearLayouts components of the home screen layout
     * @param topIcons 
     */
    public void setFeatureItems(List<LinearLayout> columnViews, List<ImageButton> columnIcons, List<List<ImageButton>> topIcons) {
        
        this.btmColumnIcons = columnIcons;
        this.topMenuIcons = topIcons;

        
        //Create the parent wrapper view
        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
        addView(internalWrapper);
        
        //Add the child views
        
        this.childColumns = columnViews;

        for (LinearLayout child : columnViews){
            child.setLayoutParams(screenSize);
            internalWrapper.addView(child);
        }
        
        //Set the touch listener for home layout
        setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("Home-OnTouch", "Event:" + event.toString());

 
                if (homeFlingDetector.onTouchEvent(event)) {
                    // If the user flings then capture the event and update the view then scroll to the correct screen
                    Log.i("Home-OnTouch", "Fling to new screen");
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // If the user scrolls and lets go make it snap to the correct screen if more than half way
                    Log.i("Home-OnTouch", "Action Up snap to nearest screen");

                    int featureWidth = v.getMeasuredWidth();
                    int newActiveColumn = ((getScrollX() + (featureWidth / 2)) / featureWidth);
                    smoothScrollToColumn(newActiveColumn);
                    return true;
                } else {
                    // If it is any other event then let other things handle it
                    Log.i("Home-OnTouch", "Ignore everything else");
                    return false;
                }
            }
        });
        
        //Instantiate the detectors
        homeFlingDetector = new GestureDetector(new HomeFlingDetector());
        verticalScrollGestureDetector = new GestureDetector(new YScrollDetector());
        
        this.post(new Runnable(){

            //Scroll to bible column on first load
            @Override
            public void run() {
                scrollToColumn(1);
            }
            
        });
    }
    
    /**
     * Scroll to a given screen
     * @param activeColumn
     */
    public void scrollToColumn(int activeColumn ){
        

    	//Without requesting focus on something else it would get stuck on editexts as cursor
    	//keeps trying to get focus - so hack to get focus somewhere else
    	btmColumnIcons.get(0).requestFocus();
    	
        //Scroll to the given column
        scrollTo(childColumns.get(0).getWidth()*activeColumn, 0);

        //Update mActiveFeature to new value
        mActiveFeature = activeColumn;
        
        //Update the icons
        updateActiveIcon();
        
    }
    
    /**
     * Scroll to a given screen
     * @param activeColumn
     */
    public void smoothScrollToColumn(int activeColumn ){
    	
    	//Without requesting focus on something else it would get stuck on editexts as cursor
    	//keeps trying to get focus - so hack to get focus somewhere else
    	btmColumnIcons.get(0).requestFocus();

        //Scroll to the given column
        smoothScrollTo(childColumns.get(0).getWidth()*activeColumn, 0);

        //Update mActiveFeature to new value
        mActiveFeature = activeColumn;
        
        //Update the icons
        updateActiveIcon();
        
        
    }
    
    
    
    public void updateActiveIcon(){
        //Update the bottom column icons
        for (ImageButton icon : btmColumnIcons){
            icon.setPressed(false);
        }
        btmColumnIcons.get(mActiveFeature).setPressed(true);
        
        //Update the top menu icons
        for (List<ImageButton> columnsTopIcons : topMenuIcons) {
            for (ImageButton icon : columnsTopIcons){
                icon.setVisibility(GONE);
            }
        }
        
        //Set the ative column icons to visible
        for (ImageButton icon :topMenuIcons.get(mActiveFeature)) {
        	icon.setVisibility(VISIBLE);
        }
    }
    
    /**
     * A gesture listener to to detect flings on the home screen and scroll to the correct home screen
     * @author GLo1
     *
     */
    class HomeFlingDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                Log.i("HomeFeature OnFling", "velocityX: " + Math.abs(velocityX) + "velocityY: " + Math.abs(velocityY) + " distance:" +
                                             (lastKeyDownX - e2.getX()));
                // right to left
                if (lastKeyDownX - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = getMeasuredWidth();
                    int newActiveColumn = (mActiveFeature < (childColumns.size() - 1)) ? mActiveFeature + 1 : childColumns.size() - 1;
                    smoothScrollToColumn(newActiveColumn);
                    return true;
                }
                // left to right
                else if (e2.getX() - lastKeyDownX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("Fling",
                        "There was an fling event distance:" + String.valueOf(e2.getX() - lastKeyDownX) + "speed:" + Math.abs(velocityX));
                    int featureWidth = getMeasuredWidth();
                    int newActiveColumn = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                    smoothScrollToColumn(newActiveColumn);
                    return true;
                }
            } catch (Exception e) {
//                Log.e("HomeFeature", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Save copy of the action down because of future null on e1 of fling and also ignore anomolies of minor horizontals on vertical scrolling
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.i("Home-OnIntercept", "Event: " + ev.toString());

        // The ACTION_DOWN gets intercepted by child ListViews so featurelayout needs to keep a copy of it for flings because e1 becomes nulls
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.i("Home-OnIntercept", "Saving Event OnDown X:" + ev.getX() + " Y:" + ev.getY());
            lastKeyDownX = ev.getX();
        }
        
        //Make slight horizontal views on scrolling smoother - also ignores if it has been press and held for more 100 ms (usually scrolling)
        if (verticalScrollGestureDetector.onTouchEvent(ev) || (ev.getEventTime() - ev.getDownTime() > 100)) {
            //If it is mainly a vertical scrolling the homescreen event should ignore it and let the lists deal with it
//            Log.e("Home-OnIntercept", "Ignored");
            return false;
        } else {
//        	Log.e("Home-OnIntercept", "HomeFeature intecepts");
            //If it is mainly a horizontal scroll view then let the homescreen deal with it
            return super.onInterceptTouchEvent(ev);
        }
    }


    /**
     * Gesture listener to determine if the scroll is mainly a vertical scroll 
     */
    class YScrollDetector extends SimpleOnGestureListener {
    	
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                // nothing
            }
            return true;
        }
    }

    public int getmActiveFeature() {
        return mActiveFeature;
    }

    public void setmActiveFeature(int mActiveFeature) {
        this.mActiveFeature = mActiveFeature;
    }

}
