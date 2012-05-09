package org.crossconnect.bible.views;

import java.util.ArrayList;

import org.crossconnect.bible.util.BibleDataHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * ScrollView used to display the categories of books in the Bible
 * @author glo1
 *
 */
public class BookTopicScrollView extends HorizontalScrollView {

    private ArrayList<LinearLayout> topicViews;
    
    private int halfScreen;
    
//    public int getHorizontalWidth() {
//        int width = 0;
//        int lastViewWidth=0;
//        for (LinearLayout view : topicViews) {
//            width += view.getWidth();
//            lastViewWidth = view.getWidth();
//        }
//        return width-lastViewWidth-(2*halfScreen);
//    }
    
    int columnWidth = -1;
    
    public int getColumnWidth() {
        if(columnWidth == -1) {
            columnWidth = topicViews.get(1).getWidth();
        }
        return columnWidth;
    }
    
    public BookTopicScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mOnScrollChangedListener.onScrollChanged();
//        call listener method update the chapter view
    }
    
    private OnScrollChangedListener mOnScrollChangedListener;
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been selected.
     *
     * @param listener The callback that will run
     */
    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    
    public BookTopicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    public BookTopicScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Create the homelayout child views from the given list 
     * @param items LinearLayouts components of the home screen layout
     * @param halfScreen used to calculate padding required on ends
     * @param halfColumn used for padding
     */
    public void setFeatureItems(ArrayList<LinearLayout> items, int halfScreen, int halfColumn) {
        this.setHorizontalScrollBarEnabled(false);
        //Create the parent wrapper view
        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setGravity(Gravity.CENTER_VERTICAL);
        internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
        addView(internalWrapper);
        
        //Add the child views
        
        this.topicViews = items;

        for (LinearLayout child : items){
            internalWrapper.addView(child);
        }
        this.halfScreen = halfScreen;
        
        //Add padding to either side of the scroll bar
        internalWrapper.getChildAt(0).setPadding((halfScreen-halfColumn), 0,5, 0);
        internalWrapper.getChildAt(internalWrapper.getChildCount()-1).setPadding(5, 0, (halfScreen+halfColumn), 0);
    }
    
    //TODO: rewrite as enums
    /**
     * Given a book position of the bible it will update the position to the current category
     * @param bookPosition the position of the book of the bible i.e. Genesis  - 1
     */
    public void scrollToBookPosition(int bookPosition) {
        
        double numSections = BibleDataHelper.getSectionPosition(bookPosition);
        int scrollX = (int) (numSections * getColumnWidth());
        
        smoothScrollTo(scrollX,0);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = super.onTouchEvent(ev);
        this.setFocusable(true);
        Log.d("Event",ev.toString());
        return handled;
    }

    public int getBookPosition() {
        double section = (double) this.getScrollX()/ (double) getColumnWidth();
        return BibleDataHelper.getBookPosition(section);
    }

    /**
     * Scroll to the given view usually the one which is clicked
     * @param v
     */
	public void scrollToBookPosition(View v) {
		for (int i = 0; i < topicViews.size(); i++) {
			if(topicViews.get(i).equals(v)) {
				smoothScrollTo(i*getColumnWidth(), 0);
		        mOnScrollChangedListener.onScrollChanged();
				break;
			}
		}
		
	}
    
}


