package org.crossconnect.bible.activity;


import net.sword.engine.sword.SwordDocumentFacade;

import org.crossconnect.bible.R;
import org.crossconnect.bible.util.RequestResultCodes;
import org.crossconnect.bible.utility.SharedPreferencesHelper;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ReporterEvent;
import org.crosswire.common.util.ReporterListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeDownloadStatus extends ProgressActivityBase {
	private static final String TAG = "DownloadStatus";
	
	private boolean mIsOkayButtonEnabled = true;
	private Button mOkayButton;
	private TextView mRetryText;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Reporter.addReporterListener(new ReporterListener() {
			@Override
			public void reportException(final ReporterEvent ev) {
				showMsg(ev);
			}

			@Override
			public void reportMessage(final ReporterEvent ev) {
				showMsg(ev);
			}
			
			private void showMsg(ReporterEvent ev) {
//				final String msg = "Doh! Something's gone wrong, sorry about this :(";
				String msg;
				
				if (ev==null) {
					msg = "Doh! Something's gone wrong, sorry about this :(";
				} else if (ev.getMessage() != null && ev.getMessage().length() > 0) {
					msg = ev.getMessage();
				} else if (ev.getException() !=null && ev.getException().getMessage() != null && ev.getException().getMessage().length() > 0) {
					msg = ev.getException().getMessage();
				} else {
					msg = "Doh! Something's gone wrong, sorry about this :(";
				}
				
				Log.e("DownloadStatus", msg);

			}
        });

        
        
        
        Log.i(TAG, "Displaying "+TAG+" view");
        setContentView(R.layout.download_status);
        
		mOkayButton = (Button)findViewById(R.id.okButton);
        
        mRetryText = (TextView) findViewById(R.id.retry_text);
        

        Log.d(TAG, "Finished displaying Download Status view");
    }
    
    @Override
	protected void jobFinished(Progress job) {
		super.jobFinished(job);
		enableOkay();
	}


	@Override
	protected void updateProgress(Progress prog) {
		super.updateProgress(prog);
		fastDisableOkay();
	}


	boolean fail = false;
	
	/** called on job finishing and must be accurate
	 */
	private void enableOkay() {
		mIsOkayButtonEnabled = isAllJobsFinished();
		if (SwordDocumentFacade.getInstance().getDocuments().size() == 0){
			fail = true;
			mRetryText.setVisibility(View.VISIBLE);
		}
   		mOkayButton.setEnabled(mIsOkayButtonEnabled);
   		
   		
    }
	/** called in tight loop so must be quick and ensure disabled
	 */
	private void fastDisableOkay() {
		if (mIsOkayButtonEnabled) {
			mIsOkayButtonEnabled = isAllJobsFinished();
	   		mOkayButton.setEnabled(mIsOkayButtonEnabled);
		}
    }
	
    protected void setMainText(String text) {
    	((TextView)findViewById(R.id.progressStatusMessage)).setText(text);
    }
    
    public void onMore(View v) {
//    	Log.i(TAG, "CLICKED onMore");
//    	Intent resultIntent = new Intent(this, DownloadStatus.class);
//    	setResult(10, resultIntent);
//    	finish();    
    }

    public void onOkay(View v) {
    	
    	if (!fail) {
            SharedPreferences settings = getSharedPreferences("APP SETTINGS", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SharedPreferencesHelper.CURRENT_BOOK, "Philipiians");
            editor.putString(SharedPreferencesHelper.CURRENT_CHAPTER, "1");
            editor.putString(SharedPreferencesHelper.CURRENT_VERSE, "1");
            editor.putString(SharedPreferencesHelper.CURRENT_TRANSLATION, SwordDocumentFacade.getInstance().getDocuments().get(0).getInitials());

            
            // Commit the edits!
            editor.commit();
    	}
    	

    	
    	
    	Log.i(TAG, "OK CLICKED");
    	Intent resultIntent = new Intent(this, SplashActivity.class);
    	startActivity(resultIntent);
    }
}
