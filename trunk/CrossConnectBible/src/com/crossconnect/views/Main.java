package com.crossconnect.views;

import java.util.ArrayList;
import java.util.List;

import musicplayer.MusicActivity;
import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import net.sword.engine.OSISHandler.ParsedDataSet;
import net.sword.engine.sword.SwordContentFacade;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.crossconnect.activity.BookManagerActivity;
import com.crossconnect.activity.MainActivity;
import com.crossconnect.activity.NotesActivity;
import com.crossconnect.model.BibleText;
import com.crossconnect.model.OnlineAudioResource;
import com.crossconnect.model.SwordBibleText;
import com.crossconnect.service.NotesService;

/**
 * @author Gary
 * @deprecated Replaced with fragment activity MainActivity
 */
@Deprecated 
public class Main extends Activity {

    private static final int SERMON_COLUMN = 0;
    private static final int BIBLE_TEXT_COLUMN = 1;
    private static final int AUDIO_BIBLE_COLUMN = 2;
    private static final int NOTES_COLUMN = 3;
    private static final String TAG = "Main";
    
    //The return codes for startactivity for result
    private static final int CHAPTER_SELECT_CODE = 1;
	private static final int WINDOW_SELECT_CODE = CHAPTER_SELECT_CODE+1;
	private static final int TRANSLATION_SELECT_CODE = WINDOW_SELECT_CODE+1;
	private static final int NOTES_SELECT_CODE = TRANSLATION_SELECT_CODE+1;
	private static final int MAIN_SELECT_CODE = NOTES_SELECT_CODE+1;

	private static final int LOADING_DIALOG = 1;
	
	private Handler updateTitleHandler;
	
    private HomeFeatureLayout columns;

    private ListView resourceColumn;

    private ListView audioReadingColumn;

    private BibleTextViewImpl bibleTextView;
    
    private BibleTextScrollView bibleScrollView;
    
    private TextView headTitleTextView;

    private OnClickListener downloadListener;
    
//    private ImageButton sermonBtn; 
    
    private EditText grabFocus;
    
    private int windowId;
    
    private NotesService notesService;
    
    private NotesEditText notesEditText;
    
    private ImageButton notesLock;


        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        //TODO: Load this when it actually scrolls to the notes part instead of onCreate
        notesService = new NotesService(this);        
        
        
        
        Log.e("Main", "OnCreate Main Called");
        setContentView(R.layout.main);
        
//        sermonBtn = (ImageButton) findViewById(R.id.column_icon_sermon);
        
        //Used to grab focus so that the scrollView doesn't gay up
        grabFocus = (EditText) findViewById(R.id.grab_focus);
        
        // Setup views
        columns = (HomeFeatureLayout) findViewById(R.id.home_feature);
        
        /* Test QA */
        final ActionItem first = new ActionItem();
        
        first.setTitle("Dashboard");
        first.setIcon(getResources().getDrawable(R.drawable.dashboard));
        first.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main.this, "Dashboard" , Toast.LENGTH_SHORT).show();
            }
        });
        
        
        final ActionItem second = new ActionItem();
        
        second.setTitle("Users & Groups");
        second.setIcon(getResources().getDrawable(R.drawable.kontak));
        second.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main.this, "User & Group", Toast.LENGTH_SHORT).show();
            }
        });
        
        /* End test QA */

        ((ImageButton) findViewById(R.id.column_icon_sermon)).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                QuickActionVertical qa = new QuickActionVertical(v);
                
                qa.addActionItem(first);
                qa.addActionItem(second);
                
                qa.show();

                columns.smoothScrollToColumn(SERMON_COLUMN);
            }
            
        });
        

//      Test new QA
      final ActionItem playAction = new ActionItem();
      
      playAction.setTitle("Play");
      playAction.setIcon(getResources().getDrawable(R.drawable.kontak));

      final ActionItem downloadAction = new ActionItem();
      
      downloadAction.setTitle("Download");
      downloadAction.setIcon(getResources().getDrawable(R.drawable.kontak));
      
      final ActionItem readAction = new ActionItem();
      
      readAction.setTitle("Read");
      readAction.setIcon(getResources().getDrawable(R.drawable.kontak));
      
      // Setup menus for bottom column icons
        
        ((ImageButton) findViewById(R.id.column_icon_bible_text)).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                columns.smoothScrollToColumn(BIBLE_TEXT_COLUMN);
                
                final QuickActionVertical mQuickAction  = new QuickActionVertical(v);
                
                final String text               = "blah";
                
                
                playAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Main.this, "Play " + text, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Main.this, MainActivity.class);
                        intent.putExtra("Translation", bibleTextView.getBibleText().getTranslation().getInitials());
                        startActivityForResult(intent, MAIN_SELECT_CODE);    

                        
                        mQuickAction.dismiss();
                    }
                });

                downloadAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Main.this, "Accept " + text, Toast.LENGTH_SHORT).show();
                        
                        mQuickAction.dismiss();
                    }
                });
                
                readAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Main.this, "Upload " + text, Toast.LENGTH_SHORT).show();
                        
                        mQuickAction.dismiss();
                    }
                });
                
                mQuickAction.addActionItem(playAction);
                mQuickAction.addActionItem(downloadAction);
                mQuickAction.addActionItem(readAction);
                
                mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
                
                mQuickAction.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
//                        mMoreImage.setImageResource(R.drawable.ic_list_more);
                    }
                });
                
                mQuickAction.show();

            }
            
        });
        
//        End test new QA

        ((ImageButton) findViewById(R.id.column_icon_audio_bible)).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                columns.smoothScrollToColumn(AUDIO_BIBLE_COLUMN);
            }
            
        });
        
        ((ImageButton) findViewById(R.id.column_icon_notes)).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                columns.smoothScrollToColumn(NOTES_COLUMN);
            }
            
        });

        
        
        //Setup listeners for top menu icons
        
        
        //Go to notes browsing view
        ((ImageButton) findViewById(R.id.menu_button_notes)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, NotesActivity.class);
                intent.putExtra("Translation", bibleTextView.getBibleText().getTranslation().getInitials());
                startActivityForResult(intent, NOTES_SELECT_CODE);    
            }
        });        

        //Go to download books
        ((ImageButton) findViewById(R.id.menu_button_translations)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, BookManagerActivity.class);
                intent.putExtra("Translation", bibleTextView.getBibleText().getTranslation().getInitials());
//                intent.putExtra("WindowId", windowId);
                startActivityForResult(intent, TRANSLATION_SELECT_CODE);    
//                overridePendingTransition(R.anim.zoom_enter, 0);

//                QuickActionVertical qa = new QuickActionVertical(v);
//                
//                qa.addActionItem(first);
//                qa.addActionItem(second);
//                
//                qa.show();

            }
        });        
        
        //Go to windows view
        ((ImageButton) findViewById(R.id.menu_button_windows)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, WindowsActivity.class);
                

                //Set the verseNumber to what is currently being viewed
                int verseNumber = bibleTextView.yToVerse(bibleScrollView.getScrollY(), findViewById(R.id.prev_chapter_button).getHeight());
                
                
        		Log.i("Main", "Set BibleTextView verse number " + verseNumber);
                
        		bibleTextView.getBibleText().setVerse(verseNumber);
        		
        		Log.i("Main", "BibleTextView sent " + bibleTextView.getBibleText().getReferenceBookChapterVerse());

        		//TODO: add string preview  from existing rendered text probably just do a substring of boundaries
                intent.putExtra("BibleText", bibleTextView.getBibleText());
                intent.putExtra("WindowId", windowId);
                startActivityForResult(intent, WINDOW_SELECT_CODE);    
                overridePendingTransition(R.anim.zoom_enter, 0);

            }
        });        
        
        ((ImageButton) findViewById(R.id.menu_button_star)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	
                startActivity(new Intent(Main.this, PreferencesFromXml.class));
            }
        });        

            
        ((ImageButton) findViewById(R.id.menu_button_churches)).setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Main.this, ResourceRepositoryActivity.class);
                    startActivity(intent);    
                }
        });
        
        ((ImageButton) findViewById(R.id.menu_button_browse_audio)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, ResourceRepositoryActivity.class);
                startActivity(intent);    
            }
        });

        
        ((ImageButton) findViewById(R.id.menu_button_audio)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, MusicActivity.class);
                startActivity(intent);    
            }
        });        
        
        ((ImageButton) findViewById(R.id.menu_button_browse_resources)).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, MusicActivity.class);
                startActivity(intent);    
            }
        });



        // Setup column view dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        //top and bottom should only be 36+25 dp + 2 dp for some reason
        //25 sp for the notification bar
        //Need to work more on DP to pixel conversion
        int mGestureThreshold = (int) ((63 +25)* scale + 0.5f);
        columns.setScreenSize(new LayoutParams(dm.widthPixels, dm.heightPixels - mGestureThreshold));

        // Setup column view content
        List<LinearLayout> items = new ArrayList<LinearLayout>();
        items.add((LinearLayout) View.inflate(getBaseContext(), R.layout.audio_view, null));
        items.add((LinearLayout) View.inflate(getBaseContext(), R.layout.bible_text_column, null));
        items.add((LinearLayout) View.inflate(getBaseContext(), R.layout.column_audio_view, null));
        
        
        View notesColumn = View.inflate(getBaseContext(), R.layout.notes_column, null);
        notesEditText = ((NotesEditText) notesColumn.findViewById(R.id.notes_edit_text));
        
        items.add((LinearLayout) notesColumn);

        
        //Put all column icons into an arraylist to be passsed to homefeature layout
        List<ImageButton> columnIcons = new ArrayList<ImageButton>();
        columnIcons.add((ImageButton) findViewById(R.id.column_icon_sermon));
        columnIcons.add((ImageButton) findViewById(R.id.column_icon_bible_text));
        columnIcons.add((ImageButton) findViewById(R.id.column_icon_audio_bible));
        columnIcons.add((ImageButton) findViewById(R.id.column_icon_notes));

        //Add top menu icons into the seperate list 
        List<ImageButton> resource_top_icons = new ArrayList<ImageButton>();
        resource_top_icons.add((ImageButton) findViewById(R.id.menu_button_churches));
        resource_top_icons.add((ImageButton) findViewById(R.id.menu_button_browse_resources));

        List<ImageButton> bible_text_top_icons = new ArrayList<ImageButton>();
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_translations));
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_windows));
        bible_text_top_icons.add((ImageButton) findViewById(R.id.menu_button_star));

        List<ImageButton> audio_top_icons = new ArrayList<ImageButton>();
        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_audio));
        audio_top_icons.add((ImageButton) findViewById(R.id.menu_button_browse_audio));

        List<ImageButton> notes_top_icons = new ArrayList<ImageButton>();
        notes_top_icons.add((ImageButton) findViewById(R.id.menu_button_notes));
        notesLock = ((ImageButton) findViewById(R.id.menu_button_notes_lock));
        notes_top_icons.add(notesLock);

        
        //Add the handlers to the icons to homefeaturelayout
        List<List<ImageButton>> topIcons = new ArrayList<List<ImageButton>>();
        topIcons.add(resource_top_icons);
        topIcons.add(bible_text_top_icons);
        topIcons.add(audio_top_icons);
        topIcons.add(notes_top_icons);
        
        
        
        columns.setFeatureItems(items, columnIcons, topIcons);

        
        
        // Find Views After Setup
        headTitleTextView = (TextView) findViewById(R.id.header_title);
        headTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + "Ubuntu-R.ttf"),Typeface.NORMAL);

        

        //Handler created on UI thread to handle updates to title
        updateTitleHandler = new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		
        		final String verseText = msg.getData().getString("Verse_Text");
						headTitleTextView.setText(bibleTextView.getBibleText().getReferenceBookChapter() + verseText);
        	}
        	
        };

        
        bibleScrollView = (BibleTextScrollView) findViewById(R.id.bibleScrollView);
        bibleScrollView.setTitleHandler(updateTitleHandler);
        
        bibleTextView = (BibleTextViewImpl) findViewById(R.id.bible_text);
        bibleTextView.setBibleSrcollView(bibleScrollView);
        
        notesEditText.initialise(notesLock, bibleTextView);
//        notesEditText.setBibleTextView(bibleTextView);

        //Load last opened verse
        String currentBook = getSharedPreferences("APP SETTINGS", MODE_PRIVATE).getString("current_book", "Philipiians");
        String currentChapter = getSharedPreferences("APP SETTINGS", MODE_PRIVATE).getString("current_chapter", "1");
        String currentVerse = getSharedPreferences("APP SETTINGS", MODE_PRIVATE).getString("current_verse", "1");
        String currentTranslation = getSharedPreferences("APP SETTINGS", MODE_PRIVATE).getString("current_translation", "ESV");
        //TODO:make it not assume ESV is installed

        new SwordInitTask().execute(currentBook, currentChapter, currentVerse, currentTranslation);

        
// TODO:       bibleText.selectVerse(1);
        
        resourceColumn = (ListView) findViewById(R.id.audio_list);

        List<OnlineAudioResource> resources = new ArrayList<OnlineAudioResource>();
        OnlineAudioResource resource = new OnlineAudioResource("Blah", "John3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("Sermon Sermon", "Phil 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);
        resource = new OnlineAudioResource("John Piper", "Mark 3:16", "http://www.google.com", "http://www.google.com");
        resources.add(resource);

        resourceColumn.setAdapter(new ResourceAdapter(this, R.layout.audio_row, resources));
//        TODO: make quickaction row based resourceColumn.setOnItemClickListener()
        
        
        audioReadingColumn = (ListView) findViewById(R.id.reading_audio_list);
        audioReadingColumn.setAdapter(new ResourceAdapter(this, R.layout.audio_row, resources));


        // Setup Listeners
        downloadListener = new OnClickListener() {
            public void onClick(View v) {
                    final QuickActionHorizontal mQuickAction  = new QuickActionHorizontal(v);
                    
                    final String text               = "blah";
                    
                    
                    playAction.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(Main.this, "Add " + text, Toast.LENGTH_SHORT).show();
                            
                            mQuickAction.dismiss();
                        }
                    });

                    downloadAction.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(Main.this, "Accept " + text, Toast.LENGTH_SHORT).show();
                            
                            mQuickAction.dismiss();
                        }
                    });
                    
                    readAction.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(Main.this, "Upload " + text, Toast.LENGTH_SHORT).show();
                            
                            mQuickAction.dismiss();
                        }
                    });
                    
                    mQuickAction.addActionItem(playAction);
                    mQuickAction.addActionItem(downloadAction);
                    mQuickAction.addActionItem(readAction);
                    
                    mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
                    
                    mQuickAction.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    });
                    
                    mQuickAction.show();
                }

                
                
                
//                Intent intent = new Intent(Main.this, AudioPlayer.class);
//                intent
//                    .putExtra("audioFileName", "http://www.esvapi.org/v2/rest/passageQuery?key=IP&output-format=mp3&passage=John+3:16-17");
//                startActivity(intent);
        };
        
        findViewById(R.id.header_box).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	
            	//TODO: can we pass the actual BibleText?
                Intent intent = new Intent(Main.this, ChapterSelectionActivity.class);
                intent.putExtra("Book", bibleTextView.getBibleText().getBook());
                intent.putExtra("Chapter", bibleTextView.getBibleText().getChapter());
                intent.putExtra("Translation", bibleTextView.getBibleText().getTranslation().getInitials());
            	startActivityForResult(intent, CHAPTER_SELECT_CODE);
            }
        });

        //Previous chapter button
        ((Button) findViewById(R.id.prev_chapter_button)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
		        //Start thread to get verse
                showDialog(LOADING_DIALOG);
		        new SwordVerseTask().execute(bibleTextView.getBibleText().getPrevChapterRef());
			}
			
		});

        
        //Next chapter button
        ((Button) findViewById(R.id.next_chapter_button)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
		        //Start thread to get verse
                showDialog(LOADING_DIALOG);
		        new SwordVerseTask().execute(bibleTextView.getBibleText().getNextChapterRef());
			}
			
		});

        
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case LOADING_DIALOG: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait while loading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
        }
        return null;
    }

    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == CHAPTER_SELECT_CODE || requestCode == WINDOW_SELECT_CODE || requestCode == NOTES_SELECT_CODE)) {
        	BibleText bibleText = (BibleText) data.getParcelableExtra("BibleText");

			if (requestCode == NOTES_SELECT_CODE) {
				updateNotesView(bibleText);
			} else {
	        	updateBibleTextView(bibleText);
			}
        	
			if (requestCode == WINDOW_SELECT_CODE) {
				windowId = data.getExtras().getInt("WindowId");
				Log.i("Main", "Window ID Received" + windowId);
	        }
        }  else if (resultCode == Activity.RESULT_OK && requestCode == TRANSLATION_SELECT_CODE) {
        	bibleTextView.getBibleText().setTranslation((String) data.getExtras().get("Translation"));
        	new SwordVerseTask().execute(bibleTextView.getBibleText());
        }
        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	//Adjust the selection to position so restore will work properly untested    	
    	int height = findViewById(R.id.prev_chapter_button).getHeight();
    	
    	//Currently crashes on rotation because bibleTextView not initialised if is in background so check for nulls
    	if (bibleTextView != null && bibleTextView.getBibleText() != null) {
    		//Scroll to bibletextcolumn because when we scroll to verse it will move to that column by default but not 
    		//update the active feature inside columns, this will drive the top and bottom buttons
            columns.scrollToColumn(BIBLE_TEXT_COLUMN);
    		bibleTextView.scrollToVerse(bibleTextView.yToVerse(bibleScrollView.getScrollY(), height),height);
    	}
    }
    

    /**
     * Save state of which column - CURRENTLY SAVES BUT DOES NOT RESTORE
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	Log.e("Main", "onSaveCalled");
        outState.putInt("column", columns.getmActiveFeature());
        outState.putInt("scrollY", bibleScrollView.getScrollY());
        saveCurrentPassage(bibleTextView.getBibleText());
        super.onSaveInstanceState(outState);
    }
    
    /**
     * Restore state of which column THIS DOES NOT GET CALLED FOR SOME REASON
     * 
     * Apaprently this does not correspond with onSave, rather this is only called
     * when activity is completely killed i.e. better off doing stuff ononCreate
     * 
     * When not killed probably just rewrite resume
     * 
     */
    @Override    
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    	Log.e("onRestoreInstanceState", "onRestore is called!");

        final int savedColumn = savedInstanceState.getInt("column");
        final int scrollY = savedInstanceState.getInt("scrollY");
        bibleTextView.post(new Runnable(){
            public void run(){
            	Log.e("onRestoreScroll", "Column:" + savedColumn);
                columns.scrollToColumn(savedColumn);
                bibleScrollView.scrollTo(0, scrollY);
//                bibleTextView.scrollToVerse(verse, extra)
            };
        });
    }
    
    
    @Override
    protected void onResume(){
        super.onResume();
        //do not give the editbox focus automatically when activity starts
        Log.d(TAG, "onResume, clearing and requesting focus");
        bibleTextView.clearFocus();
        grabFocus.requestFocus();
    }
    
    /**
     * The adapter to populate the resource column listeview
     * @author glo1
     *
     */
    private class ResourceAdapter extends ArrayAdapter<OnlineAudioResource> {

        private List<OnlineAudioResource> items;

        public ResourceAdapter(Context context, int textViewResourceId, List<OnlineAudioResource> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.audio_row, null);
            }
            OnlineAudioResource o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.audio_reading_title);
                TextView bt = (TextView) v.findViewById(R.id.audio_reading_description);
                ImageButton downloadBtn = (ImageButton) v.findViewById(R.id.audio_download_icon);

                if (o.getAudioURL() == null) {
                    downloadBtn.setVisibility(View.GONE);
                } else {
                    // Stream Button

                    // Download Button
                    downloadBtn.setFocusable(false);
                    downloadBtn.setTag(o.getAudioURL());
                    downloadBtn.setOnClickListener(downloadListener);
                    downloadBtn.setVisibility(View.VISIBLE);
                }

                if (tt != null) {
                    tt.setText(o.getResourceName());
                }
                if (bt != null) {
                    bt.setText(o.getResourceVerse());
                }
            }
            return v;
        }
    }
    
    /**
     * Update bible text with new BibleText TODO: translation is current static
     */
    private void updateBibleTextView(final BibleText bibleText) {
		bibleTextView.setBibleText(bibleText);
		headTitleTextView.setText(bibleText.getReferenceBookChapterVerse());
		
		String note = notesService.getNotes(bibleText.getKey());
		if (note != null) {
			notesEditText.setText(note);
		} else {
			notesEditText.setText("");
		}
		notesEditText.lock();

		
		//Scroll to the required  verse after reload, will be verse 1 if new chapter
        bibleTextView.post(new Runnable(){
            public void run(){
            	try{
	            	dismissDialog(LOADING_DIALOG);
            	} catch (Exception e) {
            		
            	}
//                sermonBtn.requestFocus();
            	bibleTextView.scrollToVerse(bibleText.getVerse(), findViewById(R.id.prev_chapter_button).getHeight());
                //do not give the editbox focus automatically when activity starts
                bibleTextView.clearFocus();
                grabFocus.requestFocus();

            };
        });
        
        //Save the current passage so it can be reloaded //TODO: is it needed here since onRestore has it?
        saveCurrentPassage(bibleText);
    }
    
    
    /**
     * TODO: should use synergy with above method instead of copy and paste currently only difference is that it scroll sto column
     */
    private void updateNotesView(final BibleText bibleText) {
		bibleTextView.setBibleText(bibleText);
		headTitleTextView.setText(bibleText.getReferenceBookChapterVerse());
		
		String note = notesService.getNotes(bibleText.getKey());
		if (note != null) {
			notesEditText.setText(note);
		} else {
			notesEditText.setText("");
		}
		notesEditText.lock();

		
		//Scroll to the required  verse after reload, will be verse 1 if new chapter
        bibleTextView.post(new Runnable(){
            public void run(){
            	try{
	            	dismissDialog(LOADING_DIALOG);
            	} catch (Exception e) {
            		
            	}
//                sermonBtn.requestFocus();
                //do not give the editbox focus automatically when activity starts
                bibleTextView.clearFocus();
                grabFocus.requestFocus();
                columns.smoothScrollToColumn(NOTES_COLUMN);
            };
        });
        
        //Save the current passage so it can be reloaded //TODO: is it needed here since onRestore has it?
        saveCurrentPassage(bibleText);
    }
    
    
    private void saveCurrentPassage(BibleText bibleText) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences("APP SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("current_book", bibleText.getBook());
        editor.putString("current_chapter", String.valueOf(bibleText.getChapter()));

        int verseNumber = 1;
        try {
            //Set the verseNumber to what is currently being viewed
            verseNumber = bibleTextView.yToVerse(bibleScrollView.getScrollY(), findViewById(R.id.prev_chapter_button).getHeight());        	
        } catch (Exception e) {
        	
        }
        
        //TODO: verse restore doens't yet work
        editor.putString("current_verse", String.valueOf(verseNumber));
        editor.putString("current_translation", bibleText.getTranslation().getInitials());

        
        // Commit the edits!
        editor.commit();
    }
    
    ParsedDataSet data;
    
  /**
   * Thread to get verse data  - overrides existing boundaries and text of BibleText
   * @author garylo
   *
   */
  private class SwordVerseTask extends AsyncTask<BibleText,String,BibleText> {

		@Override
		protected BibleText doInBackground(BibleText... params) {
			BibleText bibleText = params[0];
	        SwordContentFacade.getInstance().injectChapterFromJsword(bibleText); 
	        return bibleText;
		}
  	
		@Override
		protected void onPostExecute(BibleText bibleText){
			updateBibleTextView(bibleText);
		}
		
  }
  
  
  /**
   * Thread to initilise Sword without creating a BibleText
   * @author garylo
   *
   */
  private class SwordInitTask extends AsyncTask<String,String,Void> {


		String bibleBook;
		int chapter;
		int verse;
		String translation;

	  
	  @Override
		protected Void doInBackground(String... params) {
			bibleBook = params[0];
			chapter = Integer.valueOf(params[1]);
			verse  = Integer.valueOf(params[2]);
			translation = params[3];
			
			//Initialise the Sword
	        SwordContentFacade.getInstance();
			return null;
		}
  	
		@Override
		protected void onPostExecute(Void blah){
			BibleText bibleText = new SwordBibleText(bibleBook, chapter, verse, translation);
	        SwordContentFacade.getInstance().injectChapterFromJsword(bibleText); 
			updateBibleTextView(bibleText);

//	        new SwordVerseTask().execute(new SwordBibleText(bibleBook, chapter, translation));
	    }
  }
  
}
