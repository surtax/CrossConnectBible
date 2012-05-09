package org.crossconnect.bible.actions;

import org.crossconnect.bible.actions.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BibleTextScrollView extends ScrollView {
	
    
    //Handler required to be created on the UI thread - because sometimes the stupid text doesn't update if just used above handle
	private Handler updateTitleHandler;

	public BibleTextScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);

//		Log.e("BibleTextScrollView", "Textview Before :" + headTitleTextView.getText());

				
		final BibleTextView bibleTextView = (BibleTextView) findViewById(R.id.bible_text);
		View prevBtn = findViewById(R.id.prev_chapter_button);
		
		
		//If views hasn't yet been drawn then just end it here
		if (prevBtn == null || bibleTextView == null || bibleTextView.getBibleText() == null || bibleTextView.getBibleText().getTxtBoundaries() == null) {
			return;
		}
		
		int verseNumber = bibleTextView.yToVerse(y, prevBtn.getHeight());
		String verseText = "";
		if (verseNumber == -1) {
			//Error on getting verse number
			verseText = "";
		} else if (verseNumber == 0) {
			verseText = "";			
		} else {
			verseText = ":" + verseNumber;
		}
		

		//Send updated verseText to updateTitleHandler
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("Verse_Text", verseText);
		msg.setData(bundle);
		updateTitleHandler.sendMessage(msg);
		
//		Log.e("BibleTextScrollView", "onScrollChanged verse number:" + verseText);
//		Log.e("BibleTextScrollView", "Textview is now :" + headTitleTextView.getText());
		
		
		
    }

	public void setTitleHandler(Handler updateTitleHandler) {
		this.updateTitleHandler = updateTitleHandler;
	}





}
