package com.mschlauch.comfortreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
//import static com.mschlauch.comfortreader.R.id.dialog;

public class BroadcastReceiverWebLink extends Activity {
    private String TAG = "TagOpenTxt";
    private String uri = "";
    private Uri uri2;
    private boolean Shouldload = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  setContentView(R.layout.activity_main);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Shouldload) {
            if (Intent.ACTION_SEND.equals(action)) {
                //uri = intent.getStringExtra("URI");
                uri2 = intent.getData();
                String website = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
                String websiteescaped = website;
            /*try {
                websiteescaped = URLEncoder.encode(website, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
               // throw new RuntimeException(ex.getCause());
            }*/

                Log.i("WebLinkBroadcaster", "loaded: " + website);
                final SettingsLoader settingsload = new SettingsLoader(PreferenceManager.getDefaultSharedPreferences(this), this);
                final Intent i = new Intent(this, FullscreenActivity.class);

                if (isValid(websiteescaped)) {
                    new AsyncTask<String, Void, String>() {


                        @Override
                        protected String doInBackground(String... urlStr) {
                            // do stuff on non-UI thread
                            StringBuffer htmlCode = new StringBuffer();
                            try {
                                URL url = new URL(urlStr[0]);
                                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                                String inputLine;

                                while ((inputLine = in.readLine()) != null) {
                                    htmlCode.append(inputLine);
                                    Log.d(TAG, "html: " + inputLine);
                                }

                                in.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error: " + e.getMessage());
                                Log.d(TAG, "HTML CODE: " + htmlCode);
                            }
                            String preoutput = Jsoup.clean(htmlCode.toString(), Whitelist.basic());
                            String rawoutput = Html.fromHtml(preoutput).toString();

                            return rawoutput;

                            // String chunk1 = rawoutput.substring(rawoutput.indexOf(". "));
                            // String chunk2 = chunk1.substring(chunk1.indexOf(". "));

                            // return chunk2;

                            // return htmlCode.toString();
                        }


                        @Override
                        protected void onPostExecute(String htmlCode) {
                            // do stuff on UI thread with the html

                            /* String path = data.getPath();*/
                            String toread = htmlCode;
                            if (toread.length() > 10) {
                                Log.i("WebLinkBroadcaster", "loaded: " + toread);
                                Shouldload = false;
                                //TODO set imported text with invented bookpath
                                settingsload.helper_insertnewcopiedtextintodatabase(toread);
                                // settingsload.saveReadingCopyTextboolean(true);

                            } else {
                                Toast.makeText(getBaseContext(),
                                        "Text missing - check your internet connection",
                                        Toast.LENGTH_SHORT).show();
                            }

                            finish();
                            startActivity(i);

                        }
                    }.execute(websiteescaped);


                } else {
                    Toast.makeText(getBaseContext(),
                            "Invalid URL",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


            //  TextView textView = (TextView)findViewById(R.id.textView);
            //  textView.setText(uri);
            // now you call whatever function your app uses
            // to consume the txt file whose location you now know
        } else {
            Log.d(TAG, "intent was something else: " + action);
        }
    }

    private boolean isValid(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException e) {

        }

        return false;
    }

}
