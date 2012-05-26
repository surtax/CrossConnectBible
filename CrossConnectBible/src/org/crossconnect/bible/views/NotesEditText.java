package org.crossconnect.bible.views;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crossconnect.bible.service.NotesService;

import net.londatiga.android.QuickActionVertical;
import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.ClipboardManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import org.crossconnect.bible.R;

/**
 * A custom EditText that draws lines between each line of text that is
 * displayed.
 */

public class NotesEditText extends EditText {
	private Rect mRect;
	private Paint mPaint;
	private Context  ctx;
	protected NotesService notesService;
	private BibleTextView bibleTextView;
	
	// we need this constructor for LayoutInflater
	public NotesEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(0x800000FF);
		initialiseLinkListeners();
		this.notesService = new NotesService(context);
	}
	
	private ImageButton lockBtn;
	
	/**
	 * Add hooks into the bibletextview for the verse reference and the UI lock button to update
	 * @param lockBtn
	 * @param bibleTextView
	 */
	public void initialise(ImageButton lockBtn, final BibleTextView bibleTextView) {
		setLockBtn(lockBtn);
    	this.bibleTextView = bibleTextView;
    	setEnabled(false); //by default set it to be disabled
		lockBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                
                //
                if(isEnabled()) {
                    //If moving from edit to lock mode then save the contents
                    notesService.insertUpdate(bibleTextView.getBibleText().getKey(), getText().toString(), getReferences(), getLabels());
                } 
                edit();
                if(isEnabled()) {
                    requestFocus();
                    requestFocusFromTouch();
                }
                
//              TODO: menu for notes
//                Intent intent = new Intent(Main.this, ResourceRepositoryActivity.class);
//                startActivity(intent);    
            }
        });        
	}
	


	
	protected void setLockBtn(ImageButton lockBtn) {
		this.lockBtn = lockBtn;
	}

//    @Override
//    protected void onDraw(Canvas canvas) {
//        int count = getLineCount();
//        Rect r = mRect;
//        Paint paint = mPaint;
//
//        for (int i = 0; i < count; i++) {
//            int baseline = getLineBounds(i, r);
//
//            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
//        }
//
//        super.onDraw(canvas);
//    }
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		int linesToPaint = getLineCount();
		
		
		//At least paint 20 lines
		linesToPaint = (linesToPaint < 20) ? 20 : linesToPaint;
		
		Rect r = mRect;
		Paint paint = mPaint;
		
		int baseline = getLineBounds(0, r);
		for (int i = 0; i < linesToPaint; i++) {
			canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
			baseline = baseline + getLineHeight();
		}

		super.onDraw(canvas);
	}
	
	@Override
    protected void onTextChanged(CharSequence text,
            int start, int before, int after) {
//		linkify();
		super.onTextChanged(text, start, before, after);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("NotesEdit", "KeyDown");
		return super.onKeyDown(keyCode, event);
	}
	
	
	//should match all things with #[a-Z][0-9]:[0-9] till space 
	//convert this to bible books then determine if it falls within valid range
	//extend textview with notes view and override onTextChanged
	private static final String bookChapterVerseRangeRegex = "@\\w*\\d+:\\d+-\\d+";    	
	private static final String bookChapterVerseRegex = "@\\w*\\d+:\\d+";
	private static final String bookChapterRegex = "@\\w*\\d+";
	private static final String labelsRegex = "#\\w+\\s{1}";

	
	public List<String> getReferences() {
		List<String> references = new ArrayList<String>();
    	Pattern rangePattern = Pattern.compile(bookChapterVerseRangeRegex);
    	Matcher m = rangePattern.matcher(getText());
    	while(m.find()) {
    		references.add(m.group());
    	}
    	rangePattern = Pattern.compile(bookChapterVerseRegex);
    	m = rangePattern.matcher(getText());
    	while(m.find()) {
    		references.add(m.group());
    	}
    	rangePattern = Pattern.compile(bookChapterRegex);
    	m = rangePattern.matcher(getText());
    	while(m.find()) {
    		references.add(m.group());
    	}
    	
    	for (String s : references) {
    		Log.d("NotesEditText", "getReferences" + s);
    	}
    	
    	return references;
	}
	
	public List<String> getLabels() {
		List<String> labels = new ArrayList<String>();
    	Pattern rangePattern = Pattern.compile(labelsRegex);
    	Matcher m = rangePattern.matcher(getText());
    	while(m.find()) {
    		labels.add(m.group());
    	}

    	for (String s : labels) {
    		Log.d("NotesEditText", "getLabels: " + s);
    	}
    	
    	return labels;
	}


	
	public void linkify() {
		SpannableStringBuilder sb = new SpannableStringBuilder(getText());
    	Pattern rangePattern = Pattern.compile(bookChapterVerseRangeRegex);
    	Matcher m = rangePattern.matcher(getText());
    	while(m.find()) {
    		Log.e("Found", m.group());
    		sb.setSpan(new InternalURLSpan(referenceClickListener),m.start(),m.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	
    	
    	Pattern bookChapterVerseRegexpattern = Pattern.compile(bookChapterVerseRegex);
    	m = bookChapterVerseRegexpattern.matcher(getText());
    	while(m.find()) {
    		Log.e("Found", m.group());
    		sb.setSpan(new InternalURLSpan(referenceClickListener),m.start(),m.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}

    	Pattern bookChapterRegexPattern = Pattern.compile(bookChapterRegex);
    	m = bookChapterRegexPattern.matcher(getText());
    	while(m.find()) {
    		Log.e("Found", m.group());
    		sb.setSpan(new InternalURLSpan(referenceClickListener),m.start(),m.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	
    	Pattern labelsRegexPattern = Pattern.compile(labelsRegex);
    	m = labelsRegexPattern.matcher(getText());
    	while(m.find()) {
    		Log.e("Found", m.group());
    		sb.setSpan(new InternalURLSpan(referenceClickListener),m.start(),m.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	
    	
    	
  
    	this.setText(sb);
    	
    	addLinkMovementMethod(this);
//    	this.setFocusable(true);
	}
    	//Find the first edittext
//    	editText = (EditText) findViewById(R.id.editText1);
//    	setLinksClickable(true);
//    	setAutoLinkMask(Linkify.ALL);

    private void addLinkMovementMethod(TextView t) {
        t.setMovementMethod(LinkMovementMethod.getInstance());
        this.setEnabled(false);
    	        MovementMethod m = t.getMovementMethod();

        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (t.getLinksClickable()) {
                t.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
    
    private boolean locked = false;
    
    
    public void edit() {
    	if (locked) {
    		//Unlock it
    		setMovementMethod(ArrowKeyMovementMethod.getInstance());
    		SpannableStringBuilder sb = new SpannableStringBuilder(getText());
    		sb.clearSpans();
    		this.setText(sb);    		
    		this.setEnabled(true);
    	} else {
    		linkify();
    	}
        locked = !locked;
        
        updateLockBtn();
        
    }
    
    private void updateLockBtn() {
        if (locked) {
        	lockBtn.setBackgroundResource(R.drawable.icon_pen);
        } else {
        	lockBtn.setBackgroundResource(R.drawable.icon_save);
        }
    }
    
    public void lock() {
    	linkify();
        locked = true;
        updateLockBtn();
    }
    
    /**
     * Called by on tabs changed to save any unsaved changes
     */
    public void save() {
        notesService.insertUpdate(bibleTextView.getBibleText().getKey(), getText().toString(), getReferences(), getLabels());
        edit();
    }

    
	
	static class InternalURLSpan extends ClickableSpan {
		OnClickListener mListener;
		
		public InternalURLSpan(OnClickListener listener) {
			mListener= listener;
		}

		@Override
		public void onClick(View widget) {
			mListener.onClick(widget);
			
		}
	}



	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
    //The popup menu shown on link press
    private QuickActionHorizontal mQuickAction;
    
    private OnClickListener referenceClickListener;
    private OnClickListener labelListener;


    private void initialiseLinkListeners() {
        
        final ActionItem addAction = new ActionItem();
        
        addAction.setTitle("Copy");
        addAction.setIcon(getResources().getDrawable(R.drawable.icon_copy));

        final ActionItem accAction = new ActionItem();
        
        accAction.setTitle("Share");
        accAction.setIcon(getResources().getDrawable(R.drawable.ic_menu_share));
        
        final ActionItem upAction = new ActionItem();
        
        upAction.setTitle("Star");
        upAction.setIcon(getResources().getDrawable(R.drawable.icon_support));

        
        
        referenceClickListener =(new OnClickListener(){

			@Override
			public void onClick(View v) {
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
                        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
                        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "§CrossConnect Bible Verse");
                        sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);
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
                
                //TODO: Not sure why but the popup appears a bit too much to the bottom manual 
                //hack to adjust it by hardcoded 40 of pixels
                int[] location = {lastX,lastY-40};
                mQuickAction.showXY(location);

			
			}});
        
        labelListener =(new OnClickListener(){

			@Override
			public void onClick(View v) {
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
                        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
                        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "§CrossConnect Bible Verse");
                        sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);
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
                
                mQuickAction.show();

			
			}});


    
    }
    
	private int lastX = 0;
	private int lastY = 0;
	
	/**
	 * Remember last X and Y so that the popups show in the right place
	 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) event.getX();
            lastY = (int) event.getY();
//            lastY = (int) event.getY() - bibleScrollView.getScrollY();
        }        
        return handled;
    }

}
