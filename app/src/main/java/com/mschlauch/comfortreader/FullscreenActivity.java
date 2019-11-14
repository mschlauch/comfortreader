/**
 This file is part of Comfort Reader.

 LICENSE
 Copyright 2014-2017 Michael Schlauch

 Comfort Reader is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Comfort Reader is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Comfort Reader.  If not, see <http://www.gnu.org/licenses/>.>.
 */
package com.mschlauch.comfortreader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.mschlauch.comfortreader.Book;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
//import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

/**d.annotation.TargetApi;
import android.app.d.annotation.TargetApi;
import android.app.d.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import androidd.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import androidActivity;
import android.app.Dialog;
import androidd.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import androidd.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import androidActivity;
import android.app.Dialog;
import android
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 *
 */



public class FullscreenActivity extends Activity {
	
	
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

   
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
   
    /**
     * The flags to pass to {link SystemUiHider#getInstance}.
     */
  
	private static final String DEBUG_TAG = "Comfort Reader";

    //final View contentView = findViewById(R.id.fullscreen_content);

    public SettingsLoader settingsload = null ;
	
	private Toast toast;
	private Boolean switchofallmenus = false;
    //my own methods
    private SeekBar mSeekBar;
    private int wordsperminute = 250;
    private int tickdistance = 1;
    public Book segmenterObject;
    private boolean started = false;
    private Handler handler = new Handler();
    private TextView contentView ;
  //  private View controlsView;
    private View controlsView2;
    private View controlsView3tap;
    private Boolean goldversion = true;
    private Boolean running = false;
	private ProgressBar spinner;
	private int longrewindvelocity = 10;
	private int longforwardvelocity = 10;
	private double charactersperword = 6; //here should be only 5, I don't know why it gets 3 times faster on my phone, wegen der streckung im stringsegmenter

//	private GestureDetector mGestureDetector;
//    private static final ScheduledExecutorService worker =
//    		  Executors.newSingleThreadScheduledExecutor();
//
    final Runnable runnable = new Runnable() {        
        @Override
        public void run() {
            
            if(started) {
                start();
				Log.i("Fullscreen reading", "started from runnable");
            }
        }
    };

    public void stop() {
		handler.removeCallbacks(runnable);
        started = false;
		Log.i("Fullscreen reading", "reading stopped");

		//respectfully request feedback
		DefaultLayoutPromptView promptView
				= (DefaultLayoutPromptView) findViewById(R.id.prompt_view);

		Amplify.getSharedInstance().promptIfReady(promptView);
    }

    public void start() {
		handler.removeCallbacks(runnable);
    	started = true;
		Log.i("Fullscreen reading", "reading started");
    	
    	
      //  double charactersperword = 6; //here should be only 5, I don't know why it gets 3 times faster on my phone, wegen der streckung im stringsegmenter
        double charactersperminute = wordsperminute * charactersperword;
        tickdistance = 1 + (int) Math.round(charactersperminute/1440);//24 frames/second, 41.6 milliseconds max interval
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
        	//  tickdistance = (int) Math.round(charactersperminute/1090);//18 frames/second, 55 milliseconds mac interval
        	  tickdistance = 1 + (int) Math.round(charactersperminute/1400);//10 frames/second, 100 milliseconds mac interval
              
        }
         if (tickdistance < 1){tickdistance = 1;}
        
        
        double steptime = 60000/charactersperminute * tickdistance;
        
          //double characters = 3;
        int milliseconds = (int) Math.round(steptime);
       
        
       Log.i("Fullscreenactivity", "next step time" + milliseconds);
      
     //  worker.schedule(runnable, milliseconds, TimeUnit.MICROSECONDS);
       
   //    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    //   long delay = milliseconds; // the period between successive executions
     //  exec.scheduleAtFixedRate(new MyTask(), 0, period, TimeUnit.MICROSECONDS);
     //  long delay = 100; //the delay between the termination of one execution and the commencement of the next
     //  exec.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MICROSECONDS);
        
      handler.postDelayed(runnable, milliseconds);  
        //zum timing wichtig, dass der rechenintensive befehl darunter steht
        playAutomated(milliseconds);
    }

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first


		stop();
	}
	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first

		segmenterObject = new Book();
		retreiveSavedOptions();
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


   	    setContentView(R.layout.activity_fullscreen);
		spinner = (ProgressBar)findViewById(R.id.spinnerProgress);
        controlsView2 = findViewById(R.id.topcontrolbar);
        controlsView3tap = findViewById(R.id.fullscreen_content2);
        contentView = (TextView) findViewById(R.id.fullscreen_content);
		mSeekBar = (SeekBar)findViewById(R.id.reading_progress_bar);
	/*	mSeekBar.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
        { 
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
        	if (fromUser){
                 // TODO Auto-generated method stub
        	}
        	
        }*/

    /*    public void onStartTrackingTouch(SeekBar seekBar)
        {
           // TODO Auto-generated method stub
                                                        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
         // TODO Auto-generated method stub
                                                        }
        });
*/


		View previous = findViewById(R.id.previousbutton);
		previous.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View view) {
				previousButtonLongClicked(view);
				return true;}
			});

		View nextb = findViewById(R.id.nextbutton);

		nextb.setOnLongClickListener(new View.OnLongClickListener(){
				@Override
				public boolean onLongClick(View view) {
					nextButtonLongClicked(view);
					return true;}
				});
		View middlebutton = findViewById(R.id.playbutton);

		middlebutton.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View view) {
//refactor!!!

				NoteComposer notec = new NoteComposer ();


				Boolean success = settingsload.addtoCurrentNotes(notec.getcomposedNote("", settingsload));
				if (success) {
					String newpath = settingsload.getCurrentNotesFilePath();
					newpath = newpath.substring(newpath.lastIndexOf("/")+1);

					Toast.makeText(getBaseContext(),
							getString(R.string.notes_message_done_saving_note) + "Comfort Reader/" + newpath,
							Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getBaseContext(),
							"Error" ,
							Toast.LENGTH_SHORT).show();
				}




				return true;}
		});



		controlsView3tap.setOnClickListener(new View.OnClickListener() {
        	 	@Override
            public void onClick(View view) {
            	playButtonClicked(view);
            }
        });
		settingsload = new SettingsLoader (PreferenceManager.getDefaultSharedPreferences(this));

       /* Context context = getApplicationContext();
        CharSequence text = "comfortreader dev 3.01.17";
        int duration = Toast.LENGTH_SHORT;*/
    }



    /*
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }
 
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
 
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
    */
    public void playAutomated(int milliseconds){

        Log.i("Fullscreen", "automatic loading webview");
        String html =  segmenterObject.getsegmentoutputNextTick(tickdistance);
        contentView.setText(Html.fromHtml(html));
		settingsload.saveAddReadCharacters(tickdistance);
		settingsload.saveAddReadingTime(milliseconds);
		if (segmenterObject.tickposition == 0){
		texthaschanged();}
    	if (segmenterObject.finished){
    		stop();
			controlsView2.setVisibility(View.VISIBLE);
    	}
    	
    }
    
    public void playButtonClicked (View view){
		resetlongvelocities();

		if (switchofallmenus == false) {


    	if (started || segmenterObject.finished){

			stop();



			controlsView2.setVisibility(View.VISIBLE);

			/*View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			decorView.setSystemUiVisibility(uiOptions);*/


    	}
    	else {
			View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN |  View.SYSTEM_UI_FLAG_IMMERSIVE;
			decorView.setSystemUiVisibility(uiOptions);

			start();
        	controlsView2.setVisibility(View.INVISIBLE);
   	}

    }}
    
    
    
    
    public void nextButtonClicked (View view){
    	resetlongvelocities();
		if (switchofallmenus == false) {

    	boolean restart = false;
    	if (started == true){
        	stop();
        	restart = true;
        	    	}

    	segmenterObject.invokenextsegment();
    	String html =  segmenterObject.getsegmentoutputNextTick(tickdistance);
		contentView.setText(Html.fromHtml(html));

		texthaschanged();
		if (restart){
			start();
		}
    	}
	}

	public void nextButtonLongClicked (View view){

		if (switchofallmenus == false) {

			boolean restart = false;
			if (started == true){
				stop();
				restart = true;
			}

			int N = longforwardvelocity;
			if (longforwardvelocity < 50){
			longforwardvelocity = longforwardvelocity * 2;}
			for (int i = 0; i < N; i++) {
				segmenterObject.invokenextsegment();
			}

			String html =  segmenterObject.getsegmentoutputNextTick(tickdistance);
			contentView.setText(Html.fromHtml(html));

			texthaschanged();
			if (restart){
				start();
			}


			int duration = Toast.LENGTH_SHORT;
			Context context = getApplicationContext();
			CharSequence text = N + " x >>";
			toast = Toast.makeText(context, text, duration);
			toast.show();



		}
	}
	public void resetlongvelocities (){
		longforwardvelocity = 10;
		longrewindvelocity = 10;

	}

    
    public void previousButtonClicked (View view){if (switchofallmenus == false) {
	resetlongvelocities(); //make sure that the next long press is reseted
	boolean restart = false;
	if (started == true){
    	stop();
    	restart = true;
    	
	}
	segmenterObject.finished = false;
	segmenterObject.invokeprevioussegment();

	String html =  segmenterObject.getsegmentoutputNextTick(tickdistance);

	contentView.setText(Html.fromHtml(html));

	texthaschanged();
	if (restart){
	start();	
	}	
}}


	public void previousButtonLongClicked (View view){if (switchofallmenus == false) {

		boolean restart = false;
		if (started == true){
			stop();
			restart = true;

		}
		segmenterObject.finished = false;
		int N = longrewindvelocity;
		if (longrewindvelocity < 50){
		longrewindvelocity = longrewindvelocity * 2;}
		for (int i = 0; i < N; i++) {
			segmenterObject.invokeprevioussegment();
		}


		String html =  segmenterObject.getsegmentoutputNextTick(tickdistance);

		contentView.setText(Html.fromHtml(html));

		texthaschanged();
		if (restart){
			start();
		}


		int duration = Toast.LENGTH_SHORT;
		Context context = getApplicationContext();
		CharSequence text = N + " x <<";
		toast = Toast.makeText(context, text, duration);
		toast.show();



	}}





	public void texthaschanged() {
		if (switchofallmenus == false) {

			int progress = segmenterObject.calculateprogress(1000);
			float percentage = (float) progress / 10;
			String percent = String.format("%.2f", (float) percentage) + "%";
			String filename = "";
			if (settingsload.getReadingCopyTextOn()) {
				filename = getString(R.string.fullscreen_copied_title);
			} else {
				filename = settingsload.getFileofPath(settingsload.getFilePath());
			}
			if (filename.length() > 11) {
				filename = filename.substring(0, 10);
			}

			if (wordsperminute == 0) {
				wordsperminute = 1;
			}

			float minutestogo = (settingsload.getTexttoReadtotalLength() * (1 - percentage / 100) / (5 * wordsperminute));
			String minutes = String.format("%.1f", minutestogo) + "min";

			int progressseekbar = settingsload.saveGlobalPosition(segmenterObject.globalposition);
			mSeekBar.setProgress(progressseekbar);
			String toshow = filename + "\n" + percent + " ~" + minutes;


			TextView myOutBox = (TextView) findViewById(R.id.textViewStatus);
			myOutBox.setText(toshow);
		}
		else{

		}
    }
    
    public void noteButtonClicked (View view){
		if (switchofallmenus == false) {


			stop();
			//for some reason, the activity must be closed and restarted because otherwise it will skip frames or crash
			finish();
			Intent refresh = new Intent(this, FullscreenActivity.class);
			startActivity(refresh);

			// int progress = segmenterObject.calculateprogress(1000);
			// float percentage = (float) progress / 10;
			// String percent = String.format("%.2f", (float) percentage) + "%";
			// settingsload.save("positiontextfornote", percent + " character: " + segmenterObject.globalposition);
			Intent inent = new Intent("com.mschlauch.comfortreader.NoteActivity");
			startActivity(inent);
		}
    }
    
    public void menuButtonClicked (View view){
		if (switchofallmenus == false) {

			stop();
		//	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		//	settingsload.loadRealSettingstoPreferences();

			//for some reason, the activity must be closed and restarted because otherwise it will skip frames or crash
			finish();
			Intent refresh = new Intent(this, FullscreenActivity.class);
			startActivity(refresh);

			Intent i = new Intent(this, CRPreferenceActivity.class);
			//startActivityForResult(i, 1);
			startActivity(i);

		}
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){

			Intent refresh = new Intent(this, FullscreenActivity.class);
			startActivity(refresh);

			this.finish();

		}
	}
*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }


	
	public void retreiveSavedOptions(){
		switchofallmenus = true;
		spinner.setVisibility(View.VISIBLE);
		String eins = "";
		new AsyncTask<String, Void, String>() {


			@Override
			protected String doInBackground(String... urlStr) {
				// do stuff on non-UI thread
				settingsload.reloadSelectedBook();

				wordsperminute = settingsload.getWordsPerMinute() ;
				segmenterObject.minblocksize = settingsload.getMinBlockSize();
				segmenterObject.maxblocksize = settingsload.getMaxBlockSize();
				segmenterObject.textcolor=settingsload.getTextColor();
				segmenterObject.emphasiscolor=settingsload.getFocusColor();
				segmenterObject.backgroundcolor=settingsload.getBackgroundColor();
				segmenterObject.htmloptionactive = settingsload.getHelplinesOn();

				segmenterObject.maxcharactersperline = settingsload.getMaxBlockSize();
				segmenterObject.loadPreviewcolorString();

				int actual = settingsload.getGlobalPosition();
				segmenterObject.globalposition = actual;
				segmenterObject.globalpositionbefore = actual;
				//Load Content
				String text = settingsload.getTexttoRead() + "";
				String textdefault = getString(R.string.support_standarttext);
				Log.i("fullscreen", "text  is: " + text);

				segmenterObject.loadTexttoRead(textdefault);
				if (text.equals("standarttext")){
					segmenterObject.loadTexttoRead(textdefault);
					segmenterObject.globalposition = 0;
					segmenterObject.emphasiscolor = Color.parseColor("#ffee00");
					segmenterObject.textcolor=Color.parseColor("#ffffff");
				}
				else {
					segmenterObject.loadTexttoRead(text);
				}

				if (text.length() > 16){

				}
				else {
					//it is importanted that the default text is already segmentable.

				}
				Log.i("fullscreen 2", " real text  is: " + segmenterObject.texttoread);


				segmenterObject.loadallprehtmls();


				String out = "";
				return out;

			}


			@Override
			protected void onPostExecute(String htmlCode) {
				// do stuff on UI thread with the html

				contentView.setTextSize(settingsload.getFontSize());
				contentView.setBackgroundColor(settingsload.getBackgroundColor());

				String parole = settingsload.getOrientationMode();

				Log.i("Fullscreen", "orientation loading" + parole);
				if (parole.equals("1")) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);}
				else if (parole.equals("2")){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);}
				else if (parole.equals("0")){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);}

				parole = settingsload.getFontName();

				Log.i("Fullscreen", "orientation loading" + parole);
				if (parole.equals("sans")) {
					contentView.setTypeface(Typeface.SANS_SERIF);}
				else if (parole.equals("serif")){
					contentView.setTypeface(Typeface.SERIF);}
				else if (parole.equals("mono")){
					contentView.setTypeface(Typeface.MONOSPACE);}

				spinner.setVisibility(View.GONE);

				switchofallmenus = false;
				previousButtonClicked(null);
				nextButtonClicked(null);


			}
		}.execute(eins);




		










		 //Line Spacing...
		// contentView.setLineSpacing(0,(float) 1.28);
	 






		
		//actual = retrieveNumber("maxlinelength");


		//settingsload.adjustGlobalPositionToPercentage(settingsload.getGlobalPositionSeekbarValue());





		//Log.i("fullscreen", "globalposition:" + actual);



		// get the seekbar etc right...


	//	startdialog();
		
	}
    

	/*public boolean onTouchEvent(MotionEvent event) {
	    int eventaction = event.getAction();

	    switch (eventaction) {
	        case MotionEvent.ACTION_DOWN: 
	            // finger touches the screen
	            break;

	        case MotionEvent.ACTION_MOVE:
	            // finger moves on the screen
	            break;

	        case MotionEvent.ACTION_UP:   
	            // finger leaves the screen
	        	playButtonClicked(contentView);
	            break;
	            
	            
	    }

	    // tell the system that we handled the event and no further processing is required
	    return false; 
	}*/
	

    public boolean onKeyDown(int keyCode, KeyEvent event) {

		int duration = Toast.LENGTH_SHORT;
        
        //VOLUME KEY DOWN
   if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
   {
   	  String middletext = " wpm (";
	   int plus = -1;

	   if(started==true){

		   plus = 1;
			middletext = " wpm (+";
	   }


int wpm = settingsload.getWordsPerMinute();
wpm = wpm + plus;
settingsload.saveWordsPerMinute(wpm);



	   Context context = getApplicationContext();
	   CharSequence text = wpm + middletext + plus + ")";


	   toast = Toast.makeText(context, text, duration);
	   toast.show();

   	keyCode = -900000;
       return true;
   }
   //VOLUME KEY UP
   else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
   {

	   Context context = getApplicationContext();
	   CharSequence text = "➤";

	   if(started==true){

		   text = "❙❙";
		   toast = Toast.makeText(context, text, duration);
		   toast.show();

	   }






	   playButtonClicked(contentView);
	   keyCode = -900000;
       return true;
       
   }
   
   
 
    
   else if (keyCode == KeyEvent.KEYCODE_MENU)
    {
    	menuButtonClicked(contentView);
        return true;
    }
    super.onKeyDown(keyCode, event);
    
    
    return true;
    }

}


