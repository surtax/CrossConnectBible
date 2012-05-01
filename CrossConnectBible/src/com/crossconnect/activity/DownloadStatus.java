package com.crossconnect.activity;


import net.sword.engine.sword.SwordDocumentFacade;

import org.crosswire.common.progress.Progress;

import utility.SharedPreferencesHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crossconnect.service.ReportListenerService;
import com.crossconnect.util.RequestResultCodes;
import com.crossconnect.actions.R;

public class DownloadStatus extends ProgressActivityBase {
	private static final String TAG = "DownloadStatus";
	
	private boolean mIsOkayButtonEnabled = true;
	private Button mOkayButton;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BibleDataHelper.installJSwordErrorReportListener();
        
        ReportListenerService.getInstance(this);
        
        
        Log.i(TAG, "Displaying "+TAG+" view");
        setContentView(R.layout.download_status);
        
		mOkayButton = (Button)findViewById(R.id.okButton);
        enableOkay();

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


	/** called on job finishing and must be accurate
	 */
	private void enableOkay() {
		mIsOkayButtonEnabled = isAllJobsFinished();
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
    	Log.i(TAG, "CLICKED onMore");
    	Intent resultIntent = new Intent(this, DownloadStatus.class);
    	setResult(10, resultIntent);
    	finish();    
    }

    public void onOkay(View v) {
    	
        SharedPreferences settings = getSharedPreferences("APP SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SharedPreferencesHelper.CURRENT_BOOK, "Philipiians");
        editor.putString(SharedPreferencesHelper.CURRENT_CHAPTER, "1");
        editor.putString(SharedPreferencesHelper.CURRENT_VERSE, "1");
        editor.putString(SharedPreferencesHelper.CURRENT_TRANSLATION, SwordDocumentFacade.getInstance().getDocuments().get(0).getInitials());

        
        // Commit the edits!
        editor.commit();

    	
    	
    	Log.i(TAG, "OK CLICKED");
    	Intent resultIntent = new Intent(this, DownloadStatus.class);
    	setResult(RequestResultCodes.SUCCESSFUL_DOWNLOAD, resultIntent);
    	finish();    
    }
}
