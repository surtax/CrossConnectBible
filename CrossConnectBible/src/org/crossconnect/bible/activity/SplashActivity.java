package org.crossconnect.bible.activity;

import java.io.IOException;

import org.crossconnect.bible.model.BibleText;
import org.crossconnect.bible.model.SwordBibleText;

import net.sword.engine.sword.SwordDocumentFacade;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;

import org.crossconnect.bible.R;


public class SplashActivity extends Activity{
	
	private static final int TRANSLATION_SELECT_CODE = 1;

	private static final int LOADING_DIALOG = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO: Update to NDEF when ICS fixes this NFC thing
    	BibleText bibleText = new SwordBibleText("John", 3, 16, "ESV");
        Tag tagFromIntent = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        
        if (tagFromIntent != null) {
            NdefFormatable formattable  = NdefFormatable.get(tagFromIntent);
            try {
    			formattable.format(new NdefMessage(bibleText.getReferenceBookChapterVerse().getBytes()));
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (FormatException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            

        	//            new MifareUltralightTagTester().writeBibleVerse(tagFromIntent, bibleText) ;        	
//            BibleText readFromNFCBibleText = new MifareUltralightTagTester().readBibleVerse(tagFromIntent) ;
//            
//            
//            SharedPreferences settings = getSharedPreferences("APP SETTINGS", MODE_PRIVATE);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString(SharedPreferencesHelper.CURRENT_BOOK, readFromNFCBibleText.getBook());
//            editor.putString(SharedPreferencesHelper.CURRENT_CHAPTER, String.valueOf(readFromNFCBibleText.getChapter()));
//            editor.putString(SharedPreferencesHelper.CURRENT_VERSE, String.valueOf(readFromNFCBibleText.getVerse()));
//            editor.putString(SharedPreferencesHelper.CURRENT_TRANSLATION, readFromNFCBibleText.getTranslation().getInitials());
//
//            
//            // Commit the edits!
//            editor.commit();
        }
        
        setContentView(R.layout.splash_screen_view);
        showDialog(LOADING_DIALOG);
        new SwordInitTask().execute();
	}
    
    
    /**
     * Thread to initilise Sword without creating a BibleText
     * @author garylo
     *
     */
    private class SwordInitTask extends AsyncTask<Void,Void,Void> {

  	  @Override
  		protected Void doInBackground(Void... params) {
  			//Initialise the Sword
  	        SwordDocumentFacade.getInstance();
  			return null;
  		}
    	
  		@Override
  		protected void onPostExecute(Void blah){
  			if (SwordDocumentFacade.getInstance().getBibles().size()==0) {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//                intent.putExtra("WindowId", windowId);
                startActivityForResult(intent, TRANSLATION_SELECT_CODE);    
  			} else {
  	  	        Intent i = new Intent(SplashActivity.this, MainActivity.class);
  	  	        startActivity(i);
  			}
  	    }
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

}
