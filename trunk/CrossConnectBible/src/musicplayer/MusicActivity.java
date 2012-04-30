/*   
 * Copyright (C) 2011 The Android Open Source Project
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

package musicplayer;

import java.util.TimerTask;

import musicplayer.MusicService.LocalBinder;

import com.crossconnect.views.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;


/** 
 * Main activity: shows media player buttons. This activity shows the media player buttons and
 * lets the user click them. No media handling is done here -- everything is done by passing
 * Intents to our {@link MusicService}.
 * */
public class MusicActivity extends Activity implements OnClickListener {
    /**
     * The URL we suggest as default when adding by URL. This is just so that the user doesn't
     * have to find an URL to test this sample.
     */
    final String SUGGESTED_URL = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

    Button mPlayButton;
    Button mPauseButton;
    Button mSkipButton;
    Button mRewindButton;
    Button mStopButton;
    Button mEjectButton;
    SeekBar mSeekBar;
    TextView mTimeText;
    TextView mTitleText;

    /**
     * Called when the activity is first created. Here, we simply set the event listeners and
     * start the background service ({@link MusicService}) that will handle the actual media
     * playback.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_view);

        mPlayButton = (Button) findViewById(R.id.playbutton);
        mPauseButton = (Button) findViewById(R.id.pausebutton);
        mSkipButton = (Button) findViewById(R.id.skipbutton);
        mRewindButton = (Button) findViewById(R.id.rewindbutton);
        mStopButton = (Button) findViewById(R.id.stopbutton);
        mEjectButton = (Button) findViewById(R.id.ejectbutton);
        mSeekBar = (SeekBar) findViewById(R.id.SeekBar);
		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		mTitleText = (TextView) findViewById(R.id.songTitleText);
        mTimeText = (TextView) findViewById(R.id.timeText);


        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        mRewindButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mEjectButton.setOnClickListener(this);
        
        //Start service with initilise, else if bind service was the thing that started
        //then the service will kill itself once unBound
        startService(new Intent(MusicService.ACTION_INITIALISE));
        
    }

    @Override
    public void onClick(View target) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        if (target == mPlayButton) 
            startService(new Intent(MusicService.ACTION_PLAY));
        else if (target == mPauseButton)
            startService(new Intent(MusicService.ACTION_PAUSE));
        else if (target == mSkipButton)
            startService(new Intent(MusicService.ACTION_SKIP));
        else if (target == mRewindButton)
            startService(new Intent(MusicService.ACTION_REWIND));
        else if (target == mStopButton) 
            startService(new Intent(MusicService.ACTION_STOP));
        else if (target == mEjectButton) {
            showUrlDialog();
        }
    }


	/** 
     * Shows an alert dialog where the user can input a URL. After showing the dialog, if the user
     * confirms, sends the appropriate intent to the {@link MusicService} to cause that URL to be
     * played.
     */
    void showUrlDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Manual Input");
        alertBuilder.setMessage("Enter a URL (must be http://)");
        final EditText input = new EditText(this);
        alertBuilder.setView(input);

        input.setText(SUGGESTED_URL);

        alertBuilder.setPositiveButton("Play!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int whichButton) {
                // Send an intent with the URL of the song to play. This is expected by
                // MusicService.
                Intent i = new Intent(MusicService.ACTION_URL);
                Uri uri = Uri.parse(input.getText().toString());
                i.setData(uri);
                startService(i);
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int whichButton) {}
        });

        alertBuilder.show();
    }
    
    
    //Begin binding
    MusicService mService;
    boolean mBound = false;
        
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	Log.e("ServiceConnection", "BOUND SERVICE!");
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    
    class UpdateTimeTask extends TimerTask {

		@Override
		public void run() {
			mService.getCurrentPosition();
			mService.getDuration();
		}
    	
    }
    
    private Handler mHandler = new Handler();
    
    private static Runnable mUpdateTimeTask;
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d("MainActivity", "Starting Refresh Timer");
        mUpdateTimeTask = new Runnable() {

			@Override
			public void run() {
//		    	Log.e("MainActivity", "RUN JOB!");
				if (mBound){
					int duration = (int) Math.round(((double) mService.getDuration()) / 1000);
					int curPosition = (int) Math.round(((double) mService.getCurrentPosition()) / 1000);;
//					Log.e("RefreshTimer", "Duration: " + duration + " CurrentPosition:" + curPosition);
					
					
					if (duration != 0 && curPosition != 0) {
						mSeekBar.setMax(duration);
						mSeekBar.setProgress(curPosition);
						mTitleText.setText(mService.getSongTitle());
						mTimeText.setText(secondsToText(curPosition) + "/" + secondsToText(duration));
						
					} else {
						
						//TODO: probably should just make it so that stop and start works rather than change this
//						Log.e("MainActivity", "Duration fail");
//						mTitleText.setText("");
					}
					
				} else {
			        Intent intent = new Intent(MusicActivity.this, MusicService.class);
			        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				}
				mHandler.postDelayed(this, 500);
			}
        	
        };
        mUpdateTimeTask.run();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	//Stop the looping
    	mHandler.removeCallbacks(mUpdateTimeTask);
    }

    
	// Seek using seek bar
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser == true) {
				Log.e("MainActivity", "Progress:" + progress);
				mService.seekTo(mService.getDuration() * progress / seekBar.getMax());
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	};

    
    /**
     * utility method to convert seconds to readable text
     * @param seconds
     * @return
     */
	private String secondsToText(int seconds) {
		// If single digit need to pad the seconds with a zero
		String zero = "";
		if ((seconds % 60) < 10) {
			zero = "0";
		}
		return (String.valueOf(seconds / 60) + ":" + zero + String.valueOf((seconds % 60)));
	}

    
    
    
}
