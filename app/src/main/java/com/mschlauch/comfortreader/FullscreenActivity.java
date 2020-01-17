/**
 * This file is part of Comfort Reader.
 * <p>
 * LICENSE
 * Copyright 2014-2017 Michael Schlauch
 * <p>
 * Comfort Reader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Comfort Reader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Comfort Reader.  If not, see <http://www.gnu.org/licenses/>.>.
 */
package com.mschlauch.comfortreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;

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

    public SettingsLoader settingsload = null;
    public Book segmenterObject;
    private Toast toast;
    private Boolean switchOfAllMenus = false;
    //my own methods
    private SeekBar mSeekBar;
    private int wordsPerMinute = 250;
    private int tickDistance = 1;
    private boolean started = false;
    private Handler handler = new Handler();
    private TextView contentView;
    //  private View controlsView;
    private View controlsView2;
    private View controlsView3tap;
    private Boolean goldversion = true;
    private Boolean running = false;
    private Boolean preferencesInProcessOfCommitment = false;
    private ProgressBar spinner;
    private int longRewindVelocity = 10;
    private int longForwardVelocity = 10;
    private double charactersPerWord = 6; //here should be only 5, I don't know why it gets 3 times faster on my phone, wegen der streckung im stringsegmenter

    //	private GestureDetector mGestureDetector;
    //  private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    final Runnable runnable = () -> {
        if (started) {
            start();
            Log.i("Fullscreen reading", "started from runnable");
        }
    };

    public void stop() {
        handler.removeCallbacks(runnable);
        started = false;
        Log.i("Fullscreen reading", "reading stopped");

        //respectfully request feedback
        DefaultLayoutPromptView promptView = findViewById(R.id.prompt_view);

        Amplify.getSharedInstance().promptIfReady(promptView);
        //textHasChanged();

        String eins = "";
        if (!preferencesInProcessOfCommitment) {
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... urlStr) {

                    preferencesInProcessOfCommitment = true;
                    Log.i("Fullscreen reading", "saving commit begin");
                    settingsload.saveCommitChanges();
                    Log.i("Fullscreen reading", "saving commited");

                    return "";
                }

                @Override
                protected void onPostExecute(String htmlCode) {
                    preferencesInProcessOfCommitment = false;
                }
            }.execute(eins);
        }
    }

    public void start() {
        handler.removeCallbacks(runnable);
        started = true;
        Log.i("Fullscreen reading", "reading started");

        //  double charactersPerWord = 6; //here should be only 5, I don't know why it gets 3 times faster on my phone, wegen der streckung im stringsegmenter
        double charactersPerMinute = wordsPerMinute * charactersPerWord;
        tickDistance = 1 + (int) Math.round(charactersPerMinute / 1440);//24 frames/second, 41.6 milliseconds max interval
        if (tickDistance < 1) {
            tickDistance = 1;
        }

        double steptime = 60000 / charactersPerMinute * tickDistance;

        //double characters = 3;
        int milliseconds = (int) Math.round(steptime);

        Log.i("Fullscreenactivity", "next step time" + milliseconds);

        //  worker.schedule(runnable, milliseconds, TimeUnit.MICROSECONDS);

        //  ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        //  long delay = milliseconds; // the period between successive executions
        //  exec.scheduleAtFixedRate(new MyTask(), 0, period, TimeUnit.MICROSECONDS);
        //  long delay = 100; //the delay between the termination of one execution and the commencement of the next
        //  exec.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MICROSECONDS);

        handler.postDelayed(runnable, milliseconds);
        //zum timing wichtig, dass der rechenintensive befehl darunter steht
        playAutomated(milliseconds);
    }

    @Override
    public void onStop() {
        //TODO commit erwirken der shared preferences

        super.onStop();  // Always call the superclass method first
        stop();

        //overridePendingTransition(0, 0);
        //startActivity(getIntent());
        //overridePendingTransition(0, 0);
        // Intent refresh = new Intent(this, FullscreenActivity.class);
        // startActivity(refresh);
        Log.i("Fullscreen reading", "on stop");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        stop();
        Log.i("Fullscreen reading", "on pause");
    }

    @Override
    public void onRestart() {
        // spinner = findViewById(R.id.spinnerProgress);
        // spinner.setVisibility(View.VISIBLE);
        super.onRestart();
/*
        finish();
        Intent refresh = new Intent(this, FullscreenActivity.class);
        startActivity(refresh);
*/

        Log.i("Fullscreen reading", "on restart");
        segmenterObject = new Book();
        retrieveSavedOptions();

/*
        segmenterObject = new Book();
        //How to avoid to repeat same operation at resum?
        retrieveSavedOptions();
*/
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.i("Fullscreen reading", "on resume");

        segmenterObject = new Book();
        //settingsload = new SettingsLoader (PreferenceManager.getDefaultSharedPreferences(this));
        retrieveSavedOptions();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	super.onCreate(null);
        //save
        boolean isFirstStart = false;
        //  If the activity has never started before...
        if (isFirstStart) {
            Intent inent = new Intent("com.mschlauch.comfortreader.ComfortReaderIntro");

            startActivity(inent);
        }

        setContentView(R.layout.activity_fullscreen);
        spinner = findViewById(R.id.spinnerProgress);
        controlsView2 = findViewById(R.id.topcontrolbar);
        controlsView3tap = findViewById(R.id.fullscreen_content2);
        contentView = findViewById(R.id.fullscreen_content);
        mSeekBar = findViewById(R.id.reading_progress_bar);
/*
        mSeekBar.setOnTouchListener((v, event) -> true);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // TODO Auto-generated method stub
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
*/

        View previous = findViewById(R.id.previousbutton);
        previous.setOnLongClickListener(view -> {
            previousButtonLongClicked(view);
            return true;
        });

        View nextb = findViewById(R.id.nextbutton);
        nextb.setOnLongClickListener(view -> {
            nextButtonLongClicked(view);
            return true;
        });

        View middlebutton = findViewById(R.id.playbutton);
        middlebutton.setOnLongClickListener(view -> {
            //TODO refactor!!!

            NoteComposer notec = new NoteComposer();

            Boolean success = settingsload.addToCurrentNotes(notec.getComposedNote("", settingsload));
            if (success) {
                String newpath = settingsload.getCurrentNotesFilePath();
                newpath = newpath.substring(newpath.lastIndexOf("/") + 1);

                Toast.makeText(getBaseContext(),
                        getString(R.string.notes_message_done_saving_note) + "Comfort Reader/" + newpath,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(),
                        "Error",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        controlsView3tap.setOnClickListener(this::playButtonClicked);
        settingsload = new SettingsLoader(PreferenceManager.getDefaultSharedPreferences(this), this);

       /* Context context = getApplicationContext();
        CharSequence text = "comfortreader dev 3.01.17";
        int duration = Toast.LENGTH_SHORT;*/
        segmenterObject = new Book();
    }

    @Override
    public void onStart() {
        super.onStart();  // Always call the superclass method first
        Log.i("Fullscreen reading", "on start");

        if (settingsload.firststart()) {
/*
            Toast.makeText(getBaseContext(),
                    "First start",
                    Toast.LENGTH_SHORT).show();
*/
            //for some reason, the activity must be closed and restarted because otherwise it will skip frames or crash
        }

        retrieveSavedOptions();
        textHasChanged();
    }

    /*
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    */
    public void playAutomated(int milliseconds) {
        Log.i("Fullscreen", "automatic loading webview");
        String html = segmenterObject.getSegmentOutputNextTick(tickDistance);
        contentView.setText(Html.fromHtml(html));
        settingsload.saveAddReadCharacters(tickDistance);
        settingsload.saveAddReadingTime(milliseconds);
        if (segmenterObject.tickPosition == 0) {
            textHasChanged();
        }
        if (segmenterObject.finished) {
            stop();
            controlsView2.setVisibility(View.VISIBLE);
        }
    }

    public void playButtonClicked(View view) {
        resetLongVelocities();

        if (!switchOfAllMenus) {

            if (started || segmenterObject.finished) {
                stop();
                controlsView2.setVisibility(View.VISIBLE);

            } else {
                View decorView = getWindow().getDecorView();
                // Hide both the navigation bar and the status bar.
                // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
                // a general rule, you should design your app to hide the status bar whenever you
                // hide the navigation bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);

                start();
                controlsView2.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void nextButtonClicked(View view) {
        resetLongVelocities();
        if (!switchOfAllMenus) {

            boolean restart = false;
            if (started) {
                stop();
                restart = true;
            }

            segmenterObject.invokeNextSegment();
            String html = segmenterObject.getSegmentOutputNextTick(tickDistance);
            contentView.setText(Html.fromHtml(html));

            textHasChanged();
            if (restart) {
                start();
            }
        }
    }

    public void nextButtonLongClicked(View view) {

        if (!switchOfAllMenus) {

            boolean restart = false;
            if (started) {
                stop();
                restart = true;
            }

            int N = longForwardVelocity;
            if (longForwardVelocity < 50) {
                longForwardVelocity = longForwardVelocity * 2;
            }
            for (int i = 0; i < N; i++) {
                segmenterObject.invokeNextSegment();
            }

            String html = segmenterObject.getSegmentOutputNextTick(tickDistance);
            contentView.setText(Html.fromHtml(html));

            textHasChanged();
            if (restart) {
                start();
            }

            int duration = Toast.LENGTH_SHORT;
            Context context = getApplicationContext();
            CharSequence text = N + " x >>";
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void resetLongVelocities() {
        longForwardVelocity = 10;
        longRewindVelocity = 10;
    }

    public void previousButtonClicked(View view) {
        if (!switchOfAllMenus) {
            resetLongVelocities(); //make sure that the next long press is reset
            boolean restart = false;
            if (started) {
                stop();
                restart = true;

            }
            segmenterObject.finished = false;
            segmenterObject.invokePreviousSegment();

            String html = segmenterObject.getSegmentOutputNextTick(tickDistance);

            contentView.setText(Html.fromHtml(html));

            textHasChanged();
            if (restart) {
                start();
            }
        }
    }

    public void previousButtonLongClicked(View view) {
        if (!switchOfAllMenus) {

            boolean restart = false;
            if (started) {
                stop();
                restart = true;

            }
            segmenterObject.finished = false;
            int N = longRewindVelocity;
            if (longRewindVelocity < 50) {
                longRewindVelocity = longRewindVelocity * 2;
            }
            for (int i = 0; i < N; i++) {
                segmenterObject.invokePreviousSegment();
            }

            String html = segmenterObject.getSegmentOutputNextTick(tickDistance);

            contentView.setText(Html.fromHtml(html));

            textHasChanged();
            if (restart) {
                start();
            }

            int duration = Toast.LENGTH_SHORT;
            Context context = getApplicationContext();
            CharSequence text = N + " x <<";
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void textHasChanged() {
        if (!switchOfAllMenus) {

            int progress = segmenterObject.calculateProgress(1000);
            float percentage = (float) progress / 10;
            String percent = String.format("%.2f", (float) percentage) + "%";
            String filename = "";

            filename = settingsload.getFileOfPath(settingsload.getFilePath());

            if (filename.length() > 11) {
                filename = filename.substring(0, 10);
            }

            if (wordsPerMinute == 0) {
                wordsPerMinute = 1;
            }

            float minutesToGo = (settingsload.getTextToReadTotalLength() * (1 - percentage / 100) / (5 * wordsPerMinute));
            String minutes = String.format("%.1f", minutesToGo) + "min";
            int position = segmenterObject.globalPosition;
            int progressSeekbar = settingsload.saveGlobalPosition(position);
            Log.i("Fullscreen reading", "position saved: " + progressSeekbar + "   " + position);

            mSeekBar.setProgress(progressSeekbar);
            String toshow = filename + "\n" + percent + " ~" + minutes;

            TextView myOutBox = findViewById(R.id.textViewStatus);
            myOutBox.setText(toshow);
        }
    }

    public void noteButtonClicked(View view) {
        if (!switchOfAllMenus) {

            stop();
            //for some reason, the activity must be closed and restarted because otherwise it will skip frames or crash
            finish();
            Intent refresh = new Intent(this, FullscreenActivity.class);
            startActivity(refresh);

            // int progress = segmenterObject.calculateProgress(1000);
            // float percentage = (float) progress / 10;
            // String percent = String.format("%.2f", (float) percentage) + "%";
            // settingsload.save("positiontextfornote", percent + " character: " + segmenterObject.globalPosition);
            Intent inent = new Intent("com.mschlauch.comfortreader.NoteActivity");
            startActivity(inent);
        }
    }

    public void menuButtonClicked(View view) {
        if (!switchOfAllMenus) {

            stop();
            //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            //	settingsload.loadRealSettingsToPreferences();

            //for some reason, the activity must be closed and restarted because otherwise it will skip frames or crash
            finish();
            Intent refresh = new Intent(this, FullscreenActivity.class);
            startActivity(refresh);

            Intent i = new Intent(this, CRPreferenceActivity.class);
            //startActivityForResult(i, 1);
            startActivity(i);
        }
    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
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

    public void retrieveSavedOptions() {
        switchOfAllMenus = true;
        spinner.setVisibility(View.VISIBLE);
        String eins = "";

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urlStr) {
                // do stuff on non-UI thread
                settingsload.reloadSelectedBook();

                wordsPerMinute = settingsload.getWordsPerMinute();
                segmenterObject.minBlocksize = settingsload.getMinBlockSize();
                segmenterObject.maxBlocksize = settingsload.getMaxBlockSize();
                segmenterObject.textColor = settingsload.getTextColor();
                segmenterObject.emphasisColor = settingsload.getFocusColor();
                segmenterObject.backgroundColor = settingsload.getBackgroundColor();
                segmenterObject.HtmlOptionActive = settingsload.getHelplinesOn();

                segmenterObject.maxCharactersPerLine = settingsload.getMaxBlockSize();
                segmenterObject.loadPreviewColorString();

                int actual = settingsload.getGlobalPosition();
                Log.i("fullscreen", "global position loaded: " + actual);
                segmenterObject.globalPosition = actual;
                segmenterObject.globalPositionBefore = actual;
                //Load Content
                String text = settingsload.getTextToRead() + "";
                //String textdefault = getString(R.string.support_standardtext);
                Log.i("fullscreen", "text  is: " + text);

                segmenterObject.loadTextToRead(text);
/*
                if (text.equals("standarttext")) {
                    segmenterObject.loadTextToRead(textdefault);
                    segmenterObject.globalPosition = 0;
                    segmenterObject.emphasisColor = Color.parseColor("#ffee00");
                    segmenterObject.textColor = Color.parseColor("#ffffff");
                } else {
                    segmenterObject.loadTextToRead(text);
                }
*/
                Log.i("fullscreen 2", " real text  is: " + segmenterObject.textToRead);

                segmenterObject.loadAllPreHtmls();

                return "";
            }

            @Override
            protected void onPostExecute(String htmlCode) {
                // do stuff on UI thread with the html

                contentView.setTextSize(settingsload.getFontSize());
                contentView.setBackgroundColor(settingsload.getBackgroundColor());

                String parole = settingsload.getOrientationMode();

                Log.i("Fullscreen", "orientation loading" + parole);
                switch (parole) {
                    case "1":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        break;
                    case "2":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        break;
                    case "0":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        break;
                }
                //TODO here setup availability of new font
                final Typeface openDyslexic = Typeface.createFromAsset(getAssets(), "fonts/OpenDyslexic-Regular.otf");

                parole = settingsload.getFontName();

                Log.i("Fullscreen", "orientation loading" + parole);
                switch (parole) {
                    case "sans":
                        contentView.setTypeface(Typeface.SANS_SERIF);
                        break;
                    case "serif":
                        contentView.setTypeface(Typeface.SERIF);
                        break;
                    case "mono":
                        contentView.setTypeface(Typeface.MONOSPACE);
                        break;
                    case "openDyslexic":
                        contentView.setTypeface(openDyslexic);
                        break;
                }

                spinner.setVisibility(View.GONE);

                switchOfAllMenus = false;
                previousButtonClicked(null);
                nextButtonClicked(null);
            }
        }.execute(eins);

        //Line Spacing...
        // contentView.setLineSpacing(0,(float) 1.28);
        // actual = retrieveNumber("maxlinelength");
        // settingsload.adjustGlobalPositionToPercentage(settingsload.getGlobalPositionSeekbarValue());
        // Log.i("fullscreen", "globalPosition:" + actual);
        //get the seekbar etc right...
        // startdialog();

    }

/*
    public boolean onTouchEvent(MotionEvent event) {
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
    }
*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int duration = Toast.LENGTH_SHORT;

        //VOLUME KEY DOWN
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            String middletext = " wpm (";
            int plus = -1;

            if (started) {
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

            return true;
        }

        //VOLUME KEY UP
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            Context context = getApplicationContext();
            CharSequence text = "➤";

            if (started) {
                text = "❙❙";
                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            playButtonClicked(contentView);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            menuButtonClicked(contentView);
            return true;
        }
        super.onKeyDown(keyCode, event);

        return true;
    }
}
