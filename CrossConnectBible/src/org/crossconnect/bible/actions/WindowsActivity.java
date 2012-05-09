/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.crossconnect.bible.actions;

import java.util.List;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.Window;
import org.crossconnect.bible.service.WindowsService;

import net.sword.engine.sword.SwordContentFacade;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import org.crossconnect.bible.actions.R;


public class WindowsActivity extends Activity implements TabContentFactory {
    
//    private ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();

    private Gallery windowsGallery;

    SpinnerAdapter adapter;
    
    private static final int CHAPTER_SELECT_CODE = 0;
    
    private static final int GALLERY_COLOR = Color.parseColor("#B68543");
    
    private WindowsService windowsService;
    
    BibleText bibleTextReceived;

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	bibleTextReceived = (BibleText) getIntent().getParcelableExtra("BibleText");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.windows_view);

        windowsGallery = (Gallery) findViewById(R.id.windows_gallery);
        
        //Get existing windows TODO: need to set to current window
//        List<Window> initialWindows = new ArrayList<Window>();
        
        //TODO: mutithread the text retreive in window impl
        windowsService = new WindowsService(this);

    	Log.i("Windows: " , "Retreiving windows");
        List<Window> initialWindows = windowsService.getWindows();
        for (Window window :initialWindows) {
        	Log.i("WindowsActivity " ,"Windows ID:" + window.getId() + " Ref:" +  window.getBibleText().getReferenceBookChapter());
        }
        
        adapter = new WindowAdapter(this, R.layout.window_column, initialWindows);

        windowsGallery.setAdapter(adapter);
        windowsGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                //Set the colors
                for(int i  = 0; i < parent.getChildCount(); i++){
//                    ((TextView) parent.getChildAt(i)).setTextColor(GALLERY_COLOR);
                }
//                ((TextView) v).setTextColor(Color.WHITE);                                
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        
        windowsGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
				Intent i = new Intent ();
				//Put full text in because it is loaded from cache
				BibleText bibleText = ((Window)adapter.getItem(position)).getBibleText();
				
				//TODO: shouldn't need to do this if preview is stored in DB
				SwordContentFacade.getInstance().injectChapterFromJsword(bibleText);
				i.putExtra("BibleText", bibleText);
				Log.i("WindowsActivity", "Sending Window ID" + ((Window)adapter.getItem(position)).getId());
				i.putExtra("WindowId", ((Window)adapter.getItem(position)).getId());
				setResult(RESULT_OK, i);
				finish();
                overridePendingTransition(0, R.anim.zoom_exit_small_to_big);
            }
        });

        
        ImageButton addWindowBtn = (ImageButton) findViewById(R.id.menu_button_add_window);
        addWindowBtn.setOnClickListener(new OnClickListener(){
        	
        	//TODO: Instead of adding new window should start verse selection intent for result of verse reference
        	//and then creating a window with it

        	/**
        	 * Result goes to onactivityresult
        	 */
            @Override
            public void onClick(View v) {
            	addNewWindow(null);
            }}
        );
        
        initialiseWindows();
    }
    
    private void addNewWindow(BibleText bibleText) {
//    	Bundle returnedData = data.getExtras();
    	
    	//TODO: potentially can return favourites here or favourites can call directly
    	
    	//Add Empty window placeholder used to create new window
        ((WindowAdapter) adapter).add(windowsService.getEmptyWindow());

        //Animate to the new window
        MotionEvent e1 = MotionEvent.obtain(
            SystemClock.uptimeMillis(), 
            SystemClock.uptimeMillis(),  
            MotionEvent.ACTION_DOWN, 89.333336f, 265.33334f, 0);
        MotionEvent e2 = MotionEvent.obtain(
            SystemClock.uptimeMillis(), 
            SystemClock.uptimeMillis(), 
            MotionEvent.ACTION_UP, 300.0f, 238.00003f, 0);
        
        // Figure out how wide the screen is 
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Number of windows till end of gallery
        Log.i("Windows Add","Selected" +         windowsGallery.getSelectedItemPosition() + " Total:" + ((WindowAdapter) adapter).getCount());
        int windowsToEnd = ((WindowAdapter) adapter).getCount() - windowsGallery.getSelectedItemPosition();

        
        //Multiply by the number of windows there are and make it the velocity so it should only take 1s
        int totalWidth = windowsToEnd * dm.widthPixels;
        
 
        //Give additional to velocity so that it takes less than 1 second
        //time = widt/velocity
        //We want time to be 750ms so 750ms = width/velocity
        //So velocity = width/0.750 = width * 1000 /750
        int timeAllowed = 400;
        int velocity = totalWidth * 1000 / timeAllowed;
        
        
        //Fling it to the new window
        windowsGallery.onFling(e1, e2, -(velocity), 0);
        windowsGallery.postDelayed(new Runnable(){

			@Override
			public void run() {
				Intent i = new Intent(WindowsActivity.this, ChapterSelectionActivity.class);
				i.putExtra("Translation", bibleTextReceived.getTranslation().getInitials());
		    	startActivityForResult(i, CHAPTER_SELECT_CODE);
                overridePendingTransition(R.anim.zoom_enter_small_to_big, 0);
			}
        	
        }, timeAllowed);
    }
    
    private void closeWindow(int position, final Window window, View v) {
    	
    	Animation out  = AnimationUtils.makeOutAnimation(WindowsActivity.this, true);
    	v.startAnimation(out);
    	v.setVisibility(View.INVISIBLE);
    	
    	
        //Animate to other windows
//        MotionEvent e1 = MotionEvent.obtain(
//            SystemClock.uptimeMillis(), 
//            SystemClock.uptimeMillis(),  
//            MotionEvent.ACTION_DOWN, 89.333336f, 265.33334f, 0);
//        MotionEvent e2 = MotionEvent.obtain(
//            SystemClock.uptimeMillis(), 
//            SystemClock.uptimeMillis(), 
//            MotionEvent.ACTION_UP, 300.0f, 238.00003f, 0);
//        
//        // Figure out how wide the screen is 
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//
//        //Default to move one to the left
//        int move = -1;
//        
//        //If leftmost window
//        if(position == 0) {
//        	
//            Log.i("Windows Removed","Removed" +         windowsGallery.getSelectedItemPosition() + " Total:" + ((WindowAdapter) adapter).getCount());
//            int windowsToEnd = ((WindowAdapter) adapter).getCount() - windowsGallery.getSelectedItemPosition();
//            
//            //If right most window
//            if(windowsToEnd == 0) {
//            	move = 0; // only window
//            } else {
//            	//Has space to move on the right
//            	move = 1;
//            }
//        	
//        }
//        
//        Log.e("WindowsActivity", "DM WidthPixel: " + dm.widthPixels);
//        Log.e("WindowsActivity", "From java code WidthPixel: " + v.getWidth());
//        
//
//        
//        //Multiply by the number of windows there are and make it the velocity so it should only take 1s
//        int totalWidth = move * dm.widthPixels;
//        
// 
//        //Give additional to velocity so that it takes less than 1 second
//        //time = widt/velocity
//        //We want time to be 750ms so 750ms = width/velocity
//        //So velocity = width/0.750 = width * 1000 /750
//        int timeAllowed = 400;
//        int velocity = totalWidth * 1000 / timeAllowed;
//        
//        
//        //Fling it to the new window
//        windowsGallery.onFling(e1, e2, -(velocity), 0);
        windowsGallery.postDelayed(new Runnable(){

			@Override
			public void run() {
		        ((WindowAdapter) adapter).remove(window);
			}
        	
        }, 1000 +200);
    }

    
    
//    private void addHashItems(List strings) {
//        Iterator it = strings.iterator();
//        while (it.hasNext()) {
//            HashMap<String, String> item = new HashMap<String, String>();
//            item.put("line1", (String) it.next());
//            bookList.add(item);
//        }
//    }
    
    private class WindowAdapter extends ArrayAdapter<Window> {

        private List<Window> items;

        public WindowAdapter(Context context, int textViewResourceId, List<Window> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public void removeLast() {
        	//Use the adapter one because it is thread safe
            ((WindowAdapter) adapter).remove(items.get(items.size()-1));
		}
        
        /**
         * Find index of window given the key
         * @param refText
         * @return
         */
        public int findPoistion(int id) {
        	for (int i = 0; i < items.size(); i ++) {
            	if(items.get(i).getId() == id){
            		return i;
            	}
        	}
        	Log.e("WindowsActivity", "Window item does not exist");
        	return -1;
        }

		@Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.window_column, null);
            }
            final View windowView = v;
            Window window = items.get(position);
            if (window != null) {
                TextView tt = (TextView) v.findViewById(R.id.audio_reading_title);
                TextView bt = (TextView) v.findViewById(R.id.audio_reading_description);
                ImageButton closeButton = (ImageButton) v.findViewById(R.id.close_window);
                
                closeButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
		                //Close a window 
						Window window = items.get(position);
		                windowsService.removeWindow(window);
		                closeWindow(position, window, windowView);
					}
                	
                });

                if (tt != null) {
                    tt.setText(window.getBibleText().getReferenceBookChapterVerse());
                }
                if (bt != null) {
                    bt.setText(window.getBibleText().getPreview());
                }
            }
            return v;
        }
        
        @Override
        public void add(Window object){
        	super.add(object);
        }
        
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CHAPTER_SELECT_CODE) {
        	//Chapter has been selected to new window thus open it in bibletextview
        	Window window = windowsService.getNewWindow((BibleText) data.getParcelableExtra("BibleText"));
        	data.putExtra("WindowId", window.getId());        	
			setResult(RESULT_OK, data);
			finish();
            overridePendingTransition(0, R.anim.zoom_exit_small_to_big);
            //TODO: need to add new window to windows manager
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == CHAPTER_SELECT_CODE) {
        	//The user cancelled the chapter select activity thus get rid of the newly added window
        	((WindowAdapter) adapter).removeLast();
        }
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windowsService != null) {
            windowsService.close();
        }
    }
    
    
    
    /**
     *	Set chapter and book to what is viewed
     */
    private void initialiseWindows() {
    	try {
        	int id = getIntent().getIntExtra("WindowId", -1);
        	Log.i("WindowsActivity", id +"");

	    	int position = ((WindowAdapter) adapter).findPoistion(id);

	    	Log.i("WindowsActivity", "BibleText received: " + bibleTextReceived.getReferenceBookChapterVersion());

	
	    	if(bibleTextReceived != null){
	    		//New window does not exist
	    		if (position == -1) {
	    			Window window = windowsService.getNewWindow(bibleTextReceived);
	    			
	    	    	Log.i("WindowsActivity: " , "New Window Does not yet exist - Adding new window from Main");
	    	    	((WindowAdapter) adapter).add(window);
	    			//Scroll to new window
		    		windowsGallery.setSelection(windowsGallery.getCount(), true);	    			


	    		} else {
	    	    	Log.i("WindowsActivity: " , "Existing window so updating pos: " + position);

	    			// Existing window so update and scroll to selection 
	    	    	((Window) ((WindowAdapter) adapter).getItem(position)).setBibleText(bibleTextReceived);
	    	    	windowsService.updateWindow(((Window) ((WindowAdapter) adapter).getItem(position)));
		    		windowsGallery.setSelection(position, true);	    	
	    		}
	    	}
    	} catch (NullPointerException e) {
    		//do nothing if the intents dont exists
    	}
    }

	@Override
	public View createTabContent(String tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabs_bg_text, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(tag);
        return view;
	}

}

