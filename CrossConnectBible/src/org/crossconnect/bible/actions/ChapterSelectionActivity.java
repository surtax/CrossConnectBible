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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sword.engine.sword.SwordContentFacade;

import org.crossconnect.bible.activity.BookManagerActivity;
import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;
import org.crossconnect.bible.util.BibleDataHelper;
import org.crosswire.jsword.passage.NoSuchKeyException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.crossconnect.bible.actions.R;

public class ChapterSelectionActivity extends Activity {
    
    private ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> chapterList = new ArrayList<HashMap<String, String>>();

    private Gallery bookGallery;
    private Gallery chapterGallery;
    private BookTopicScrollView topicScrollView;
    private AutoCompleteTextView chapterSelectionTextView;
    private Button translationButton;
    private ImageButton goBtn;
 

    private SimpleAdapter chapterAdatper;
    
    final List<Integer> chapters = BibleDataHelper.getChapters();
    
    private final int TRANSLATION_SELECT_CODE = 2;

    
    private int galleryMove = 0;
    
    private List<String> bibleBooks = BibleDataHelper.getBooks();
    
    private ArrayAdapter<String> autoCompleteAdapter;
    
    private static final int INVALID_VERSE_DIALOG = 1;
    
    private static final String TAG = "ChapterSelectionActivity";
    
    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        // See assets/res/any/layout/translucent_background.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.chapter_selection_view);

        topicScrollView = (BookTopicScrollView) findViewById(R.id.book_type_scrollview);
        bookGallery = (Gallery) findViewById(R.id.book_gallery);

        
//        Set the touch listener for home layout
        
        //Notify that topicGallery which is moving
        topicScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                galleryMove = R.id.book_type_scrollview;
                return false;
            }
        });
        
        //Notify that it is the bookGallery which is moving
        bookGallery.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                galleryMove = R.id.book_gallery;
                return false;
            }
        });
        
        
        // Setup column view content
        ArrayList<LinearLayout> items = new ArrayList<LinearLayout>();
        
        items.add(generateTopicColumn("Law"));
        items.add(generateTopicColumn("Former\nProphets"));
        items.add(generateTopicColumn("Writings"));
        items.add(generateTopicColumn("Later\nProphets"));
        items.add(generateTopicColumn("Gospels"));
        items.add(generateTopicColumn("Paul's\nEpistles"));
        items.add(generateTopicColumn("Pastoral\nEpistles"));
        items.add(generateTopicColumn("General\nEpistles"));
        
        //TODO: use post runnable instead of this
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        // TOPIC COLUMN WIDTH IN DP from topic_column.xml calculated for padding
        int COLUMN_WIDTH = 50;
        int halfColumn = (int) (COLUMN_WIDTH/2 * scale + 0.5f);
        
        topicScrollView.setFeatureItems(items, dm.widthPixels/2, halfColumn);
        
        addHashItems(bibleBooks);

        SpinnerAdapter adapter = new SimpleAdapter(this, bookList, R.layout.gallery_row, new String[] { "line1" }, new int[] { R.id.text1 });

        bookGallery.setAdapter(adapter);
        bookGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View v, int bookPosition, long id) {
                //Set the colors
                for(int i  = 0; i < parent.getChildCount(); i++){
                    ((TextView) parent.getChildAt(i)).setTextColor(Color.RED);
                }
                
                //Set highlighted color
                ((TextView) v).setTextColor(Color.BLACK);
                              
                //Re-Populate chapters with corresponding book
                addChapterItems(chapters.get(bookPosition));
                chapterAdatper.notifyDataSetChanged();
                chapterGallery.setSelection(0);
                
                // Calculate where to update the topicScrollView to and scroll there
                if (galleryMove == R.id.book_gallery) {
                    topicScrollView.scrollToBookPosition(bookPosition);
                    updateHeaderText();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        
        topicScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                // Only actioned when it is touched not when it is indirectly scrolled by other scrollbar
                if(galleryMove == R.id.book_type_scrollview){
                    // Calculate where to update the bookGallery to and scroll there                                      
                    bookGallery.setSelection(topicScrollView.getBookPosition());
                    updateHeaderText();
                    
                }
            }
            
        });
        
        chapterAdatper = new SimpleAdapter(this, chapterList, R.layout.gallery_row, new String[] { "line1" }, new int[] { R.id.text1 });

        chapterGallery = (Gallery) findViewById(R.id.chapter_gallery);
        chapterGallery.setAdapter(chapterAdatper);

        chapterGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                //Set the colors
                for(int i  = 0; i < parent.getChildCount(); i++){
                    ((TextView) parent.getChildAt(i)).setTextColor(Color.RED);
                }
                
                //Set highlighted color
                ((TextView) v).setTextColor(Color.BLACK);
                
                //Update header text to reflect new selection
                updateHeaderText();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        
        goBtn = (ImageButton) findViewById(R.id.go_btn);
        goBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent();
                BibleText bibleText;
                try {
                    bibleText = new SwordBibleText(chapterSelectionTextView.getText().toString(), translationButton.getText().toString());
                    // BibleText bibleText = new SwordBibleText(bibleBooks.get(bookGallery.getSelectedItemPosition()),
                    // chapterGallery.getSelectedItemPosition()+1, translationTextView.getText().toString());
                    SwordContentFacade.getInstance().injectChapterFromJsword(bibleText);
                    i.putExtra("BibleText", bibleText);
                    setResult(RESULT_OK, i);
                    finish();
                } catch (NoSuchKeyException e) {
                    showDialog(INVALID_VERSE_DIALOG);
                }
            }

        });
        
        chapterSelectionTextView = (AutoCompleteTextView) findViewById(R.id.chapter_selection_textview);
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bibleBooks.toArray(new String[0]));
        autoCompleteAdapter.setNotifyOnChange(true);
        chapterSelectionTextView.setAdapter(autoCompleteAdapter);
        final String book = getIntent().getExtras().getString("Book");
    	int chapter = getIntent().getExtras().getInt("Chapter");
    	final String translation = getIntent().getExtras().getString("Translation");

        chapterSelectionTextView.setText(book + " " + chapter);
        translationButton = (Button) findViewById(R.id.header_translation);
        translationButton.setText(translation);
        
        chapterSelectionTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                try {
                    
                Log.d(TAG, "Clear adapter");
                autoCompleteAdapter.clear();
                String query = s.toString().trim().toLowerCase();
                    List<String> books = BibleDataHelper.getBooks();
                    int numBooks = 0;
                    
                    //Adapter seems to have some delay and cant retreive things just added so hard save it
                    String bookAdded = null;
                    String directMatch = null;
                    for(String book:books){
                        if(book.toLowerCase().contains(query)){
                            Log.d(TAG, "Add matching books:" + book);
                            numBooks++;
                            bookAdded = book;
                            autoCompleteAdapter.add(book);
                            if (book.toLowerCase().equals(query)){
                                directMatch = book;
                            }
                        }
                    }

                    if (directMatch != null) {

                        Log.d(TAG, "Direct match so add chapters:" + directMatch);
                        int index = BibleDataHelper.getBooks().indexOf(directMatch);
                        if (index != -1) {
                            int numChapters = BibleDataHelper.getChapters().get(index);
                            for (int i = 1; i <= numChapters; i++) {
                                autoCompleteAdapter.add(directMatch + " " + i);
                            }
                        }
                    } else if (numBooks == 1 && bookAdded != null) {
                        // Add chapters to suggestions when just 1 option
                        Log.d(TAG, "Only one match so add chapters");
                        int index = BibleDataHelper.getBooks().indexOf(bookAdded);
                        if (index != -1) {
                            int numChapters = BibleDataHelper.getChapters().get(index);
                            for (int i = 1; i <= numChapters; i++) {
                                autoCompleteAdapter.add(bookAdded + " " + i);
                            }
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    //Randomly throws this on redeploys but doesn't do anything just catch and ignore
                }

                }
            
        });
        
        translationButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
                Intent intent = new Intent(ChapterSelectionActivity.this, BookManagerActivity.class);
                intent.putExtra("Translation", translation);
//                intent.putExtra("WindowId", windowId);
                startActivityForResult(intent, TRANSLATION_SELECT_CODE);    
			}
        	
        });
        
        initliseGallery();

        
        
        
    }
    
    private void updateHeaderText() {
    	chapterSelectionTextView.setText(bibleBooks.get(bookGallery.getSelectedItemPosition()) + " " + (chapterGallery.getSelectedItemPosition()+1));    	
    }
    
    
    /**
     *	Set chapter and book to what is viewed
     */
    private void initliseGallery() {
    	try {
	    	final String book = getIntent().getExtras().getString("Book");
	    	int chapter = getIntent().getExtras().getInt("Chapter");
	    	
	    	Log.i("chapterselectionactivity", "book received: " + book);
	    	Log.i("chapterselectionactivity", "chapter received: " + chapter);

	    	//TODO:handle translations
	    	String translation = getIntent().getExtras().getString("Translation");
	    	
	
	    	if(book != null && bibleBooks.contains(book)){
				topicScrollView.post(new Runnable(){

		            //Scroll to bible column on first load
		            @Override
		            public void run() {
						topicScrollView.scrollToBookPosition(bibleBooks.indexOf(book));
		            }
		            
		        });
				bookGallery.setSelection(bibleBooks.indexOf(book), false);

				if(chapters.get(bibleBooks.indexOf(book)) >= chapter){
					chapterGallery.setSelection(chapter-1, false);
				}
	    	}
    	} catch (NullPointerException e) {
    		//do nothing if the intents dont exists
    	}
    	
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case INVALID_VERSE_DIALOG:
            return new AlertDialog.Builder(ChapterSelectionActivity.this)
                .setTitle("Oops..")
                .setMessage("We couldn't find the chapter reference you entered, could you please check it and try again?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    }
                }).create();
        }
        return null;
    }

    


	private void addHashItems(List strings) {
        Iterator it = strings.iterator();
        while (it.hasNext()) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("line1", (String) it.next());
            bookList.add(item);
        }
    }
    
    private void addChapterItems(int s) {
        chapterList.clear();
        for (int i = 1; i <= s; i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("line1", Integer.toString(i));
            chapterList.add(item);
        }
    }


    
    private LinearLayout generateTopicColumn(String topicText){
        LinearLayout topicColumn = (LinearLayout) View.inflate(getBaseContext(), R.layout.topic_column, null);
        ((TextView) topicColumn.findViewById(R.id.topic_column_text)).setText(topicText);    
        topicColumn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
            	topicScrollView.scrollToBookPosition(v);
            	galleryMove = R.id.book_type_scrollview;
            };
        });
        return topicColumn;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK && requestCode == TRANSLATION_SELECT_CODE) {
    		translationButton.setText((String) data.getExtras().get("Translation"));
        }
        
    }

}
