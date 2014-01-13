package com.vps.twilioplayrecordedaudio;

import java.io.IOException;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	String audioURL="http://api.twilio.com/2010-04-01/Accounts/AC6e8bbd2580054bfc9c0858cedad700f1/Recordings/RE9d5bfc94c9a23e7c97adf84405513200.mp3";
	private MediaPlayer mPlayer;
	private SeekBar seekBar;
	private Button pButton;
	private Thread playThread;
	private TextView audioMax;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pButton=(Button)findViewById(R.id.playPauseBtn);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		audioMax = (TextView) findViewById(R.id.AudioMax);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPlayer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    protected void onPause() {
    	super.onPause();
    	if (mPlayer != null) {
    		mPlayer.release();
    	}
    }
    
    private void getPlayer() {
    	Uri audURI=Uri.parse(this.audioURL);
    	
    	mPlayer = new MediaPlayer();
    	//mPlayer.setOnErrorListener(android.media.MediaPlayer.OnErrorListener);
    	try {
			mPlayer.setDataSource(this, audURI);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	mPlayer.setOnPreparedListener(new OnPreparedListener() {
    	    @Override
    	    public void onPrepared(MediaPlayer mp) {
    	    	Log.d("duration", String.valueOf(mp.getDuration()));
    	    	seekBar.setMax(mp.getDuration());
    	    	audioMax.setText(mp.getDuration()/1000 + "s");
    	        playThread = new Thread(new Runnable() {
    	        	public void run() {
    	        		try {
    	        			while(mPlayer!=null && mPlayer.getCurrentPosition() < mPlayer.getDuration())
    	        			{
    	        				seekBar.setProgress(mPlayer.getCurrentPosition());
    	                        Message msg=new Message();
    	                        int millis = mPlayer.getCurrentPosition();

    	                        msg.obj=millis/1000;

    	                        try {
    	                           Thread.sleep(100);
    	                        } 
    	                        catch (InterruptedException e) {
    	                        	e.printStackTrace();
    	                            Log.e("mplayer","interrupt exeption" +e);
    	                           }
    	                      }
    	        			
    	                  }catch(Exception e){
    	                      e.printStackTrace();
    	                      
    	                  }
    	              }
    	      });
    	        playThread.start();
    	    	pButton.setText("Play");
    	        pButton.setClickable(true);
    	        
    	        
    	    }
    	});
    	
    	mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				//playThread.interrupt();
				mPlayer.seekTo(0);
				pButton.setText("Play");
				
			}
    		
    	});
    	
    	
    	
    	mPlayer.setLooping(false);
    	mPlayer.prepareAsync();
    	
    	pButton.setOnClickListener(this);
    	pButton.setClickable(false);
    	
    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    	    @Override
    	    public void onProgressChanged(SeekBar seekBar, int progress,
    	            boolean fromUser) {
    	        if (fromUser) {
    	            mPlayer.seekTo(progress);
    	        }
    	    }

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    	});
    }
    
    
    
    private void play(){
    	mPlayer.start();
    	pButton.setText("Pause");
    		
    }
    
    private void pause(){
    	mPlayer.pause();
    	pButton.setText("Play");
    }
    
    private void playPause(){
    	if(mPlayer.isPlaying()) {
    		pause();
    	}
    	else {
    		play();
    	}
    }

	@Override
	public void onClick(View v){
		if(v.getId() == R.id.playPauseBtn){
			playPause();
		}
	}

}
