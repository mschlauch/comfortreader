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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

//import android.support.v7.app.ActionBarActivity;

public class NoteActivity extends Activity {


    public SettingsLoader settingsload = null;
    // public String savednotebook;
    public String prefix;
    public String extract;
    public String filename;
    public String note;
    public String composednotebook;
    private String position;
    private TextView inputTextView;
    private TextView prefixTextView;
    private TextView composedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        settingsload = new SettingsLoader(PreferenceManager.getDefaultSharedPreferences(this), this);
        //EditText
        inputTextView = findViewById(R.id.editTextInput);
        prefixTextView = findViewById(R.id.textViewPrefix);
        //	composedTextView = (TextView) findViewById(R.id.textViewComposed);

		/*
		// savednotebook = settingsload.getCurrentNotes();//TODO call settingsloader instead
		position = settingsload.getGlobalPositionPercentString();

		String texttoread = settingsload.getTexttoRead();//TODO ""
		int globalposition = settingsload.getGlobalPosition();
		String filepath = settingsload.getFilePath();//TODO ""
		filename = settingsload.getFileofPath(filepath);

		//String pathtofile = ("" + filepath + "/" + filename);

		String datetext = " " + android.text.format.DateFormat.format("yyyy-MM-dd hh:mm", new java.util.Date());


		int textlength = texttoread.length();
		int begin = globalposition - 100 - settingsload.getMaxBlockSize();
		int end = globalposition + 100;
		if (begin < 1){begin = 0;}
		if (end > textlength || end < 0){
			end = textlength;
		}
		String preextract = texttoread.substring(begin,end);


		//get extracted text as words
		textlength = preextract.length();
		begin = 0;
		end = 0;
		begin = preextract.indexOf(" ");
		end = preextract.lastIndexOf(" ");
		if (begin < 1){begin = 0;}
		if (end > preextract.length() || end < 0){
			end = preextract.length() ;
		}

		String extract = " ";
		extract = preextract.substring(begin, end);
		extract =	extract.replaceAll("-\n","");

		   extract =	extract.replaceAll("\n"," ");

		extract = "[…]" + extract + "[…]";
		//linebreak bereinigung
		//extract.replace(System.getProperty ("line.separator"), "");
		//geht nicht TODO line.separator bereinigen...später

	//	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
  	 // 	position = preferences.getString("positiontextfornote","");

		//position = "position";
		//current position
		//percentage

		prefix = "\n____________________\nQUOTATION:" + "\nFile:"+ filepath + "\nPosition: " +  position + "\nDate:" + datetext + "\nOriginal text:"  + "\n \"" + extract + "\" \n\n" + "COMMENT:\n//" ;



		//composedTextView.setText((CharSequence)savednotebook);
		//composednotebook = savednotebook;

		*/

        String eins = " ";
        new AsyncTask<String, Void, String>() {


            @Override
            protected String doInBackground(String... urlStr) {
                // do stuff on non-UI thread

                NoteComposer notec = new NoteComposer();
                extract = notec.getExtract(settingsload);


                return extract;

                // String chunk1 = rawoutput.substring(rawoutput.indexOf(". "));
                // String chunk2 = chunk1.substring(chunk1.indexOf(". "));

                // return chunk2;

                // return htmlCode.toString();
            }


            @Override
            protected void onPostExecute(String htmlCode) {
                // do stuff on UI thread with the html
                prefixTextView.setText("Original text: " + extract);


            }
        }.execute(eins);


    }


    private void saveTextfile(String text) {

        Boolean success = settingsload.addtoCurrentNotes(text);
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


        finish();
    }


    public void enter(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);

        //CharSequence note = composedTextView.getText();
        //	note = (String) inputTextView.getText();
        note = "hallo";
        note = inputTextView.getText().toString();

        NoteComposer notec = new NoteComposer();
        composednotebook = notec.getcomposedNote(note, settingsload);
        //	composedTextView.setText((CharSequence)composednotebook);
        //	save("savednotebook",composednotebook);

        saveTextfile(composednotebook);
    }

    public void opennotebook(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        Uri uri = Uri.parse("file://" + settingsload.getCurrentNotesFilePath());
        intent.setDataAndType(uri, "text/plain");
        startActivity(intent);

    }
}
