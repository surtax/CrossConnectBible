package com.crossconnect.service;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ReporterEvent;
import org.crosswire.common.util.ReporterListener;

import android.content.Context;
import android.util.Log;

public class ReportListenerService {
	
	private static ReporterListener listener;
	
	private static ReportListenerService singleton;
	
	private static Context context;
	
	public static ReportListenerService getInstance(Context ctx) {
		if (singleton == null) {
			singleton = new ReportListenerService();
		}
		context = ctx;
		clearListeners();
		installJSwordErrorReportListener();
		return singleton;
	}
	
	private static void clearListeners() {
		if (listener != null) {
			Reporter.removeReporterListener(listener);
		}
	}

	private ReportListenerService() {
		
	}
	
    /** 
     * JSword calls back to this listener in the event of some types of error
     * 
     */
    public static void installJSwordErrorReportListener() {
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
				final String msg = "Doh! Something's gone wrong, sorry about this :(";
				
				

//				if (ev==null) {
//					msg = "Doh! Something's gone wrong, sorry about this :(";
//				} //else if (!StringUtils.isEmpty(ev.getMessage())) {
//					msg = ev.getMessage();
//				} else if (ev.getException()!=null && StringUtils.isEmpty(ev.getException().getMessage())) {
//					msg = ev.getException().getMessage();
//				} else 
//				{
//					msg = "Doh! Something's gone wrong, sorry about this :(";
//				}
				
				Log.e("Install Reporter", msg);
			

//			    	new AlertDialog.Builder(context)
//					   .setMessage(msg)
//				       .setCancelable(false)
//				       .show();
			}
        });
    }


}
