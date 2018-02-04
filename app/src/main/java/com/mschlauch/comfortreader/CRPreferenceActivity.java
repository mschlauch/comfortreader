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
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.view.FilePickerPreference;

import java.util.Random;

import static android.content.ClipDescription.MIMETYPE_TEXT_HTML;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


public class CRPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SettingsLoader settingsload = null;
    private Toast toast;
    //boolean filepathmanualchange = false;
   // private ProgressDialog progressDialog;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        settingsload = new SettingsLoader (PreferenceManager.getDefaultSharedPreferences(this));
       // settingsload.loadRealSettingstoPreferences();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Log.i("CPPreferenceActivity", "was created now ");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       // Log.i("CPPreferenceActivity", "focus color: " + settingsload.getFocusColor() + "text color" + settingsload.getTextColor() + "background color" + settingsload.getBackgroundColor() );
       // filepathmanualchange = false;

      //  progressDialog = ProgressDialog.show(this, "", "Loading...");
        if (key.equals(settingsload.filepathkey)){

           /*
            //TODO test if notefile exists, if yes load into notes, if not create one
                */
            setResult(RESULT_OK, null);
            finish();

        }

        else if (key.equals(settingsload.globalpositionpermillekey)){
            // int value = settingsload.getGlobalPositionSeekbarValue();

          // sharedPreferences.getInt()
        //    Log.i("CPPreferenceActivity", "new globalposition percentage set: " + value );
        //    Log.i("CPPreferenceActivity", "new globalposition percentage set: " + position);

            }
/*
        else if (key.equals(settingsload.minblocksizekey)){

            

        }
        else if (key.equals(settingsload.maxblocksizekey)){

        }*/

        else if (key.equals(settingsload.lastreadskey)){
            setResult(RESULT_OK, null);
            finish();
        }


        else if (key.equals("fromcopyandpaste")){
                if(settingsload.getReadingCopyTextOn()){ //copied text will be loaded
                  //  settingsload.saveReadingCopyTextboolean(false);



                    String copiedtext = readFromClipboard();
                    settingsload.saveReadingCopyTextString(copiedtext);

                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.settings_loadcopyandpaste_outputmessage_on);
                    int duration = Toast .LENGTH_SHORT;
                    toast = Toast.makeText(context, text, duration);
                    toast.show();





                }else{
                    //settingsload.saveReadingCopyTextboolean(false);
                    int newposition = settingsload.retrieveNumber(settingsload.precopypasteglobalpositionpermillekey);
                    settingsload.saveNumber(settingsload.globalpositionpermillekey,newposition);
                    settingsload.adjustGlobalPositionToPercentage(newposition);

                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.settings_loadcopyandpaste_outputmessage_off) + " " + settingsload.getFileofPath(settingsload.getFilePath());
                    int duration = Toast.LENGTH_SHORT;
                    toast = Toast.makeText(context, text, duration);
                    toast.show();


                }

            ;

        }
        else if (key.equals("gamificationswitch")){
            Context context = getApplicationContext();
            CharSequence text = "\uD83D\uDE01";
            int duration = Toast.LENGTH_SHORT;
            toast = Toast.makeText(context, text, duration);
            toast.show();

        }

       // progressDialog.dismiss();
        // getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        //TODO fix this Bug: activity crashes everytime one changes position slider after having changed device orientation. Above line responsible for that.


    }





    public static class MyPreferenceFragment extends PreferenceFragment
    {


        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            Log.i("preference fragment", "shared preferences called ");

            addPreferencesFromResource(R.xml.preferences);

            Log.i("preference fragment", "shared preferences still called ");

            updateAppearance();

            final SettingsLoader settingslolo = new SettingsLoader(this.getPreferenceManager().getSharedPreferences());

            final Preference wpmpref = (Preference) findPreference("wpmvalue");
            wpmpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    preference.setSummary(getString(R.string.settings_wpn_summary) + " " + newValue + "" );
                     return true;
                }

            });


            final Preference maxblockpref = (Preference) findPreference("maxblocksizevalue");
            maxblockpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    String einstring = "" + newValue;
                    int zahl = Integer.valueOf(einstring);
                    if (settingslolo.getMinBlockSize() > zahl){
                        zahl = settingslolo.getMinBlockSize();
                    }

                    preference.setSummary(getString(R.string.settings_maxwords_summary) + " " + zahl + " " + getString(R.string.settings_maxwords_summary2) );
                     return true;
                }

            });

            final Preference minblockpref = (Preference) findPreference("minblocksizevalue");
            minblockpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    preference.setSummary(getString(R.string.settings_minwords_summary) + " " + newValue + " " + getString(R.string.settings_minwords_summary2) );
                    return true;
                }

            });

            final Preference fontsizepref = (Preference) findPreference("fontsizevalue");
            fontsizepref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    preference.setSummary(getString(R.string.settings_fontname_summary) + " " + newValue + "" );
                    return true;
                }

            });



            final Preference gamificationpreference = findPreference("gamificationswitch");
            gamificationpreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick    (Preference preference) {
// fontsizepref.setSummary("ciao");

                    if(preference.getKey().equals("gamificationswitch")){
                        int wordpoints = settingslolo.getWordpoints();

                        int xpgain = 0;
                        if (wordpoints >0) {
                            int difference = (int) Math.round((float) wordpoints / 9) + 10;


                            settingslolo.saveWordpoints(settingslolo.getWordpoints() - difference);

                            Random rand = new Random();
                            int newrand = rand.nextInt() % 6;//count = 2 for your case

                            double factor = 1.00;
                            switch(newrand) {
                                case 0:
                                    gamificationpreference.setIcon(R.drawable.ic_college);
                                    factor = 1.5;
                                    break;
                                case 1:
                                    gamificationpreference.setIcon(R.drawable.ic_sadsmiley);
                                    factor = 0.5;
                                    break;
                                case 2:
                                    gamificationpreference.setIcon(R.drawable.ic_happysmiley);
                                    factor = 0.25;
                                    break;
                                case 3:
                                    gamificationpreference.setIcon(R.drawable.ic_heart);
                                    factor = 2.0;
                                    break;
                                case 4:
                                    gamificationpreference.setIcon(R.drawable.ic_notes);
                                    factor = -3.0;
                                    break;
                                case 5:
                                    gamificationpreference.setIcon(R.drawable.ic_typefacesize);
                                    factor = 5.0;
                                    break;
                            }

                            xpgain = (int) Math.round(difference*factor);
                            settingslolo.saveXPpoints(settingslolo.getXPpoints() + xpgain);


                        }
                        String gamification_summary = getString(R.string.settings_gamification_summary) + " " + settingslolo.getWordpoints() + " Level: " + String.format("%.3f", settingslolo.getGamificationLevel()) + " (" + xpgain + " added)";
                        gamificationpreference.setSummary(gamification_summary);

                     /*   // get a reference to the already created main layout
                        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.preferences);

                        // inflate the layout of the popup window
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_window, null);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        // show the popup window
                        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                        // dismiss the popup window when touched
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
*/

                    }
                    return true;
                }
            } );



            final Preference positionpreference = findPreference("globalpositionpercentage");
            positionpreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                     if(preference.getKey().equals("globalpositionpercentage"))
                    {
                       // String einstring = "" + newValue;
                       // int zahl = Integer.valueOf(einstring);
                        int value = (Integer) newValue;
                        float percentage = (float) value/10;
                       String einvalue = String.format("%.2f", (float)percentage) + "%";
                     float totalwords = settingslolo.getTexttoRead().length() / 5;
                        int totalnumberofwords = (int) totalwords;
                        int numberofwordsread = (int) (totalwords * value / 1000);
                        String wordcount = "~" + numberofwordsread + "/" + totalnumberofwords + " " + getString(R.string.words);
                      //  Preference pref = (Preference) this;
                      //  SettingsLoader settingslolo = new SettingsLoader(positionpreference.getPreferenceManager().getSharedPreferences());
                        positionpreference.setTitle(getString(R.string.settings_positionslider_title) + ": " + einvalue + " " + wordcount);
  //  positionpreference.setSummary(wordcount);


                        int position = settingslolo.adjustGlobalPositionToPercentage(value);
                        settingslolo.saveGlobalPosition(position);//save to real global position


                    }
                    return true;
                }

            });

            final ListPreference listPreference = (ListPreference) findPreference("recentreads");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceData(listPreference);



            listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(listPreference);
                    return false;
                }
            });

           final FilePickerPreference pickerPreference = (FilePickerPreference) findPreference("filepath");

           pickerPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {



                    if(preference.getKey().equals("filepath"))
                    {

                        final Preference picker = preference;
                        final String value=(String) newValue;

                        Log.i("Preference Fragment", "processing new path: " + value );

                        final SettingsLoader settingslolo = new SettingsLoader(preference.getPreferenceManager().getSharedPreferences());

                        String eins = " ";

                        preference.setTitle(getString(R.string.fullscreen_text_while_loading));
                        preference.setSummary(getString(R.string.settings_filepicker_loadingtext));

                        new AsyncTask<String, Void, String>() {


                            @Override
                            protected String doInBackground(String... urlStr) {
                                // do stuff on non-UI thread

                                Log.d("settings", "loading book global position 0: " + settingslolo.getGlobalPositionSeekbarValue());
                                settingslolo.loadTextfromFilePath(value);

                                String out = "";
                                return out;

                                // String chunk1 = rawoutput.substring(rawoutput.indexOf(". "));
                                // String chunk2 = chunk1.substring(chunk1.indexOf(". "));

                                // return chunk2;

                                // return htmlCode.toString();
                            }


                            @Override
                            protected void onPostExecute(String htmlCode) {
                                // do stuff on UI thread with the html
                                picker.setSummary(value);

                                settingslolo.save("filepath",value);



                            }
                        }.execute(eins);









                    }
                    return false;
                }

            });




        }



        protected void updateAppearance() {

            //filepath und appearance trennen...updaten bevor listener registriert wird.
            String eins = "";
            final Preference preferencetochange1 = (Preference) findPreference("filepath");
            final SettingsLoader settingslolo = new SettingsLoader(preferencetochange1.getPreferenceManager().getSharedPreferences());


       /*     new AsyncTask<String, Void, String[]>() {


                @Override
                protected String [] doInBackground(String... urlStr) {*/
                    String globalposition_title;
                    String filepath;
                    String wpm_summary;
                    String maxblocksize_summary;
                    String minblocksize_summary;
                    String fontsize_summary;

                    // do stuff on non-UI thread
                    filepath = settingslolo.getFilePath();
                    wpm_summary = getString(R.string.settings_wpn_summary) + " " + settingslolo.getWordsPerMinute() + "";
                    maxblocksize_summary = getString(R.string.settings_maxwords_summary) + " " + settingslolo.getMaxBlockSize() + " " + getString(R.string.settings_maxwords_summary2);
                    minblocksize_summary = getString(R.string.settings_minwords_summary) + " " + settingslolo.getMinBlockSize() + " " + getString(R.string.settings_minwords_summary2);
                    fontsize_summary = getString(R.string.settings_fontname_summary) + " " + settingslolo.getFontSize() + "";
            float totalwords = settingslolo.getTexttoRead().length() / 5;
            int totalnumberofwords = (int) totalwords;
            int numberofwordsread = (int) (settingslolo.getGlobalPosition() / 5);
            String wordcount = "~" + numberofwordsread + "/" + totalnumberofwords + " " + getString(R.string.words);


                    globalposition_title = getString(R.string.settings_positionslider_title) + ": " + settingslolo.getGlobalPositionPercentString() + " " + wordcount;

            String gamification_summary = getString(R.string.settings_gamification_summary) + " " + settingslolo.getWordpoints() + " Level: " + String.format("%.3f", settingslolo.getGamificationLevel());

                 /*   String [] strinarray= {globalposition_title,filepath,wpm_summary,maxblocksize_summary,minblocksize_summary,fontsize_summary};
                    return strinarray;

                }

                    @Override
                    protected void onPostExecute(String [] strinarray) {*/
                     /*   String globalposition_title = strinarray[0];
                        String filepath = strinarray[1];
                        String wpm_summary = strinarray[2];
                        String maxblocksize_summary = strinarray[3];
                        String minblocksize_summary = strinarray[4];
                        String fontsize_summary = strinarray[5];*/
                        // do stuff on UI thread with the html
                        preferencetochange1.setSummary( filepath );

                        Preference preferencetochange2 = (Preference) findPreference(settingslolo.globalpositionpermillekey);
                        preferencetochange2.setTitle(globalposition_title);

                        Preference preferencetochange3 = (Preference) findPreference(settingslolo.wpmkey);
                        preferencetochange3.setSummary(wpm_summary);
                        Preference preferencetochange4 = (Preference) findPreference(settingslolo.maxblocksizekey);
                        preferencetochange4.setSummary(maxblocksize_summary);

                        Preference preferencetochange5 = (Preference) findPreference(settingslolo.minblocksizekey);
                        preferencetochange5.setSummary(minblocksize_summary);
                        Preference preferencetochange6 = (Preference) findPreference(settingslolo.fontsizekey);
                        preferencetochange6.setSummary(fontsize_summary);

            Preference preferencetochange7 = (Preference) findPreference("gamificationswitch");
            preferencetochange7.setSummary(gamification_summary);

                        Preference preferencetochange = (Preference) findPreference(settingslolo.orientationkey);
                        String parole = settingslolo.getOrientationMode();
                        if (parole.equals("1")) {
                            preferencetochange.setSummary(getString(R.string.settings_deviceorientation_landscape) + "" );}
                        else if (parole.equals("2")){
                            preferencetochange.setSummary(getString(R.string.settings_deviceorientation_portrait) + "" );}
                        else if (parole.equals("0")){
                            preferencetochange.setSummary(getString(R.string.settings_deviceorientation_default) + "" );}
          /*          }
                }.execute(eins);
*/




            //   settingslolo.loadRealSettingstoPreferences();

           // Preference preferencetochange1 = (Preference) findPreference(settingslolo.filepathkey);


          //  NumberPickerPreference numberpreference = (NumberPickerPreference) findPreference(settingslolo.maxblocksizekey);
          //  numberpreference.setMinimum(settingslolo.getMinBlockSize());
            //TODO set number picker min and max values according to Min and Max words...
        }


        protected static void setListPreferenceData(ListPreference lp) {
            SettingsLoader settingslolo = new SettingsLoader(lp.getPreferenceManager().getSharedPreferences());
            CharSequence[] entries = settingslolo.getLastBooks();
            CharSequence[] entryValues = settingslolo.getLastBooksValues();//{"2", "3", "4"};
            lp.setEntries(entries);
            lp.setDefaultValue("0");
            lp.setEntryValues(entryValues);
        }

}


    public String readFromClipboard() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";


        if (!(clipboard.hasPrimaryClip())) {
            //disabled

            Log.i("Options", "has primaty clip" + pasteData);


        } else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_HTML) || clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {

            Log.i("Options", "plain text");



            // This enables the paste menu item, since the clipboard contains plain text.
            //enabled
            //copy stuff
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

// Gets the clipboard as text.
            pasteData = item.getText().toString();

        }


        //      Log.i("Reading from Clipboard", pasteData);

        Log.i("Options", "THIS IS" + pasteData);


        return pasteData;
    }



}