package com.mschlauch.comfortreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;

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

public class BroadcastReceiverOpenInFileManager extends Activity {
    private String TAG = "TagOpenTxt";
    private String uri = "";
    private Uri uri2;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  setContentView(R.layout.activity_main);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            //uri = intent.getStringExtra("URI");
            uri2 = intent.getData();
            uri = uri2.getPath() + ":";

            File myFile = new File(uri2.getPath());
            uri = myFile.getAbsolutePath();
            Log.i("TextBroadcaster", "loaded: " + uri);

            final SettingsLoader settingsLoad = new SettingsLoader(PreferenceManager.getDefaultSharedPreferences(this), this);
            /* String path = data.getPath();*/
            String eins = " ";

            if (uri.contains(".")) {
                final String extension = uri.substring(uri.lastIndexOf("."));
                Log.i("TextBroadcaster", "extension is " + extension);

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... urlStr) {
                        // do stuff on non-UI thread

                        switch (extension) {
                            case ".pdf":
                                Log.i("TextBroadcaster", "loaded pdf:" + uri);
                                settingsLoad.helper_insertNewTextIntoDatabase(uri, settingsLoad.loadFromPdfFile(uri));
                                break;
                            case ".txt":
                                Log.i("TextBroadcaster", "loaded txt: " + uri);
                                settingsLoad.helper_insertNewTextIntoDatabase(uri, settingsLoad.loadFromTxtFile(uri));
                                break;
                            case ".epub":
                                Log.i("TextBroadcaster", "loaded epub:" + uri);
                                settingsLoad.helper_insertNewTextIntoDatabase(uri, settingsLoad.loadFromEpubFile(uri));
                                break;
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String htmlCode) {
                        // do stuff on UI thread with the html
                    }
                }.execute(eins);

                Intent i = new Intent(this, FullscreenActivity.class);
                finish();
                startActivity(i);
            }
        }

        //TODO make sure text gets saved in sqlite table with fake path
        // settingsLoad.helper_insertNewTextIntoDatabase(uri, toread);
        // settingsLoad.saveReadingCopyTextBoolean(true);

        // TextView textView = (TextView) findViewById(R.id.textView);
        // textView.setText(uri);
        // now you call whatever function your app uses
        // to consume the txt file whose location you now know
    }
}
