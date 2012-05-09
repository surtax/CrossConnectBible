package org.crossconnect.bible.views;

import org.crossconnect.bible.model.BibleText;

import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.ClipboardManager;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import org.crossconnect.bible.R;

/**
 * THis class should always have a scrollview wrapping around it
 * @author garylo
 *
 */
public class BibleTextView extends TextView{

    private static final String TAG = "BibleTextView";

	private BibleText bibleText;
    
    //The scroller surrounding the edittext used for scrolling to verses
    //Needs to be manually set
    private BibleTextScrollView bibleScrollView;
    
    private Context ctx;
    
    //X Y coordinates to determine where to show the popup action menu - updated on each press
    private int lastX = 0;
    private int lastY = 0;
    
    //The popup menu shown on long press
    QuickActionHorizontal mQuickAction;

    private void setLongClick() {
        
        final ActionItem addAction = new ActionItem();
        
        addAction.setTitle("Copy");
        addAction.setIcon(getResources().getDrawable(R.drawable.kontak));

        final ActionItem accAction = new ActionItem();
        
        accAction.setTitle("Share");
        accAction.setIcon(getResources().getDrawable(R.drawable.kontak));
        
        final ActionItem upAction = new ActionItem();
        
        upAction.setTitle("Star");
        upAction.setIcon(getResources().getDrawable(R.drawable.kontak));

        
        
        this.setOnLongClickListener(new OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                
                mQuickAction  = new QuickActionHorizontal(v);
                
                final String text;
                
                if (getSelectionStart() <= 0 && getSelectionEnd() <= 0) {
                	//No text highighted then assume it is using all text
                	text = getText().toString();
                } else {
                	text =  getText().toString().substring(getSelectionStart(),getSelectionEnd());
                }
                
                
                addAction.setOnClickListener(new OnClickListener() {
                    
                    //Copy text action item
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ctx, "Copy " + text, Toast.LENGTH_SHORT).show();
                        
                        ClipboardManager clipboard = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(text);
                        
                        mQuickAction.dismiss();
                    }
                });

                accAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	
                    	//determine verse range highlighted
                    	int start = bibleText.getVerseFromCharPos(getSelectionStart());
                    	int end = bibleText.getVerseFromCharPos(getSelectionEnd());

                    	
                    	
                        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
                        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "CrossConnect Bible Verse");
                        sendMailIntent.putExtra(Intent.EXTRA_TEXT, text + " (" +  bibleText.getReferenceBookChapterVerseVersion(start, end) + ")");
                        sendMailIntent.setType("text/plain");
                        ctx.startActivity(Intent.createChooser(sendMailIntent, "Share using..."));
                        mQuickAction.dismiss();
                    }
                });
                
                upAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ctx, "Upload " + text, Toast.LENGTH_SHORT).show();
                        
                        mQuickAction.dismiss();
                    }
                });
                
                mQuickAction.addActionItem(addAction);
                mQuickAction.addActionItem(accAction);
                mQuickAction.addActionItem(upAction);
                
                mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
                
                mQuickAction.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                });
                
                int[] location = {lastX, lastY};
                mQuickAction.showXY(location);
                return true;
            }});
    }
    
    @Override
    public int getSelectionStart() {
    	return selectionStart;
    }

    @Override
    public int getSelectionEnd() {
    	return selectionEnd;
    }


    public BibleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        setLongClick();
    }
     
    public BibleTextView(Context context, BibleText reference) {
        super(context);
        this.bibleText = reference;
        ctx = context;
        setLongClick();
        
//        //Set 
//        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/Fanwood.ttf");
//        this.setTypeface(tf);
    }
    
    private int selectionStart = -1;
    private int selectionEnd  = -1;
    
    /**
     * Highlight the verse given a character selected on the view    
     * @param characterNumber the character number in which was selected by the click
     * @param currentStart the start of the current selection
     * @param currentEnd the end of the current selection
     */
    public void selectVerse(int characterNumber, int currentStart, int currentEnd) {
        
    	Log.e(TAG, "SelectVerse: From charpos to verse org.crossconnect.bible.utility" + bibleText.getVerseFromCharPos(this.getSelectionStart()));

        
    	int newSelection = bibleText.getVerseFromCharPos(this.getSelectionStart());
    	
    	
    	//Find out which verses are currently selected
    	int currentStartSelected = bibleText.getVerseFromCharPos(currentStart);
    	if (currentEnd <= 0) {
    		currentEnd = 0;
    	} else {
    		currentEnd = currentEnd -1;
    	}
    	int currentEndSelected = bibleText.getVerseFromCharPos(currentEnd);
    	Log.e("SelectVerse", "Current Start and End" + currentStartSelected + " " + currentEndSelected);
    	
    	if (currentStart <= 0) {
    		currentStart = 0;
    	} else {
    		currentStart = currentStart -1;
    	}
    	
    	int conjoiningVerseStart = currentStartSelected -1;
    	int conjoiningVerseEnd = currentEndSelected + 1;
    	
    	if (currentStartSelected <= -0 || currentEndSelected <= -0){
    		//Nothing is currently selected so..
     		//Just select the selected verse
         	setSelection(bibleText.getCharPosStartFromVerse(newSelection), bibleText.getCharPosEndFromVerse(newSelection));
         	
         	//TODO:currently setting the verse bold for custom fails as well as it shifts the actual alignment when bolded, probably better to just do selection
         	//TODO:all of these set selections are common in they get reference.getCharpos etc (verse, verse) so should put into one new method
//         	setBoldVerse(newSelection, newSelection);
     	} else if (newSelection == currentStartSelected && newSelection == currentEndSelected) {
    		// Selecting same verse as currently selected so unselect it
     		removeSelection();
    	} else if (newSelection == conjoiningVerseStart) {
    		//join with previous verse 
        	setSelection(bibleText.getCharPosStartFromVerse(conjoiningVerseStart), bibleText.getCharPosEndFromVerse(currentEndSelected));
    	} else if (newSelection == conjoiningVerseEnd) {
    		//join with the end verse
    		setSelection(bibleText.getCharPosStartFromVerse(currentStartSelected), bibleText.getCharPosEndFromVerse(conjoiningVerseEnd));
    	} else {
    		//Remove currently selected verse and just select the new verse
    		removeSelection();
        	setSelection(bibleText.getCharPosStartFromVerse(newSelection), bibleText.getCharPosEndFromVerse(newSelection));
    	}
    }
    
    /**
     * Highlight a set selection of text
     * @param text
     * @param startPosition
     * @param endPosition
     */
    private void setSelection(int startPosition, int endPosition) {
    	Spannable wordtoSpan = (Spannable) getText();

    	wordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    	setText(wordtoSpan, TextView.BufferType.SPANNABLE);
    	
    	selectionStart = startPosition;
    	selectionEnd = endPosition;
    }
    
    /**
     * Highlight a set selection of text
     * @param text
     * @param startPosition
     * @param endPosition
     */
    public void removeSelection() {
    	Spannable wordtoSpan = (Spannable) getText();
    	BackgroundColorSpan[] backgroundSpans = wordtoSpan.getSpans(0, wordtoSpan.length(), BackgroundColorSpan.class);
    	for (BackgroundColorSpan span : backgroundSpans) {
    		wordtoSpan.removeSpan(span);
    	}
    	setText(wordtoSpan, TextView.BufferType.SPANNABLE);
    	
    	selectionStart = -1;
    	selectionEnd = -1;
    }

    
    /**
     * Bold the given verse range
     * @param startVerse
     * @param endVerse
     */
    public void setBoldVerse(int startVerse, int endVerse){
        SpannableStringBuilder sb = new SpannableStringBuilder(this.getText());
        sb.setSpan(new StyleSpan(Typeface.BOLD), bibleText.getCharPosStartFromVerse(startVerse), bibleText.getCharPosEndFromVerse(endVerse), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(sb);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        //Preserve the selection start and end
        int start = selectionStart;
        int end = selectionEnd;
        Log.d("BibleTextView", "onTouch Current Selection Start:" + start + "End = " + end);
        
        handled = super.onTouchEvent(event);
        //Only select verse if the popup window is not open
        boolean windowShowing = mQuickAction == null ? false : mQuickAction.isShowing();        
        if (event.getAction() == MotionEvent.ACTION_UP && !windowShowing){
        	int characterTouched = touchToCharacterPos((int) event.getX(), (int) event.getY());
        	selectionStart = characterTouched;
        	selectionEnd = characterTouched;
            selectVerse(characterTouched, start, end);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) event.getX();
            lastY = (int) event.getY() - bibleScrollView.getScrollY();
        }        
        return handled;
    }
    
    /**
     * Translate where the screen was touched with the character that is actually returned
     * @param x x-coordinate
     * @param y y-coordinate
     * @return character number
     */
    private int touchToCharacterPos(int x, int y) {
    	Layout layout = this.getLayout();
    	if(layout != null) {
    		int line = layout.getLineForVertical(y);
    		return  layout.getOffsetForHorizontal(line, x);
    		
    	}
    	return 0;
    }
    
    //TODO: Add method which will store and restore the scrolly of the view    
    /**
     * Scrolls the surrounding scrollview to the given verse
     * @param verse the verse number
     * @param scrollView the surrounding scrollview
     * @extra extra the extra y it will add due to the button at the top
     */
    public void scrollToVerse(int verse, int extra) {
    	int pos = bibleText.getTxtBoundaries().get(verse-1);
    	Layout layout = getLayout();
    	
    	if (layout != null) {
        	int line = layout.getLineForOffset(pos);
        	int baseline = layout.getLineBaseline(line);
        	int ascent = layout.getLineAscent(line);
        	float y = baseline + ascent + extra;
        	
        	Log.d("ScrollToVerse", "Scroll to verse: " + verse);

        	//Without requesting focus on something else it would get stuck on editexts as cursor
        	//keeps trying to get focus - so hack to get focus somewhere else
        	//Look in xml focusable needs to be set to true
        	
        	
//        	bibleScrollView.requestFocus();
    //
//        	setSelection(pos);
        	bibleScrollView.scrollTo(0, (int) y);
        	
//        	bibleScrollView.requestFocus();
    	}
    }
    
	/**
     * Finds the verse given a certain y co-ordinate
     * @param current y co-ordinate
     * @extra extra the extra y it will add due to the button at the top
     * @return the verse number currently viewed at the scroll level
     */
    public int yToVerse(int yScroll, int extra) {
    	
    	Layout layout = getLayout();
    	int line, baseline, ascent;
    	int i = 0;
    	float boundaryY;
    	for(Integer pos: bibleText.getTxtBoundaries()) {
        	line = layout.getLineForOffset(pos);
        	baseline = layout.getLineBaseline(line);
        	ascent = layout.getLineAscent(line);
        	boundaryY = baseline + ascent + extra;
        	if (boundaryY > yScroll) {
        		break;
        	}
        	i++;
    	}
    	
    	//At least verse 1 even if it is above
    	i = (i == 0) ? 1 : i;
    	
    	return i;
    }

    
    /**
     * Method to determine and update the header to the current verse being displayed
     * @return
     */
    
    public int getCurrentTopVerse() {
    	//TODO: put a listener determine what the verse is based on amount scrolled rever engineer scrollToVerse
    	return 0;
    }


	public BibleText getBibleText() {
		return bibleText;
	}
	
	/**
	 * Updates the bibleText model as well as the text
	 * @param bibleText
	 */
	public void setBibleText(BibleText bibleText) {
		this.bibleText = bibleText;
		this.setText(bibleText.getSpannableStringBuilder(), TextView.BufferType.SPANNABLE);
	}


	public void setBibleSrcollView(BibleTextScrollView bibleScrollView) {
		this.bibleScrollView = bibleScrollView;		
	}
    
}
