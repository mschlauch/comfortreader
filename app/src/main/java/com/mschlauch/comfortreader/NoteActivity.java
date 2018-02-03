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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
//import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteActivity extends Activity {


	public SettingsLoader settingsload = null ;
	// public String savednotebook;
	public String prefix;
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

		settingsload = new SettingsLoader (PreferenceManager.getDefaultSharedPreferences(this));
		//EditText
		inputTextView = (TextView) findViewById(R.id.editTextInput);
		prefixTextView = (TextView) findViewById(R.id.textViewPrefix);
		composedTextView = (TextView) findViewById(R.id.textViewComposed);

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


		prefixTextView.setText("Original text: " + extract);
		//composedTextView.setText((CharSequence)savednotebook);
		//composednotebook = savednotebook;

	}


	/*public void copy(View view) {

		String thetext = prefix + inputTextView.getText().toString();

		// Gets a handle to the clipboard service.
		ClipboardManager clipboard = (ClipboardManager)
		        getSystemService(Context.CLIPBOARD_SERVICE);
		// Creates a new text clip to put on the clipboard
		ClipData clip = ClipData.newPlainText("Comfort Reader note",thetext);
		// Set the clipboard's primary clip.
		clipboard.setPrimaryClip(clip);


	}*/

	private void saveTextfile(String text) {

        Boolean success = settingsload.addtoCurrentNotes(text);
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


		finish();
		/*File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Comfort Reader");
		directory.mkdirs();


		String filename = retrieve("filename");
    	if(filename.length() > 30){
    	filename = filename.substring(0, 29); }
		String newstring = "mnt/sdcard/Comfort Reader/notes" + filename + ".txt";


		try {
            File myFile = new File(newstring);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                                    new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }*/
	}

	/*public void deleteall(View view) {
		savednotebook = "";
		composednotebook = "";
		inputTextView.setText("");
		composedTextView.setText((CharSequence) composednotebook);
		//composedTextView.set
		save("savednotebook",composednotebook);

	}*/
/*
	public void delete(View view){
		composedTextView.setText("");
		enter(null);

	}*/

	public void enter(View view) {

		InputMethodManager imm = (InputMethodManager)getSystemService(
		      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(composedTextView.getWindowToken(), 0);

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

		Uri uri = Uri.parse("file://"+settingsload.getCurrentNotesFilePath());
		intent.setDataAndType(uri, "text/plain");
		startActivity(intent);

		/*String thetext = composednotebook;

		// Gets a handle to the clipboard service.
		ClipboardManager clipboard = (ClipboardManager)
		        getSystemService(Context.CLIPBOARD_SERVICE);
		// Creates a new text clip to put on the clipboard
		ClipData clip = ClipData.newPlainText("Comfort Reader note",thetext);
		// Set the clipboard's primary clip.
		clipboard.setPrimaryClip(clip);

		saveTextfile(composednotebook);*/
	}
/*
	 public void save(String variable, String value){


			//value = wpmTextBox.getText();

			   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


				String slotnumber = preferences.getString("slotnumber","");
		  	  	if (slotnumber == "")
		  	  	{
		  	  		slotnumber = "slot1-";
		  	  	}
				variable = slotnumber + variable;


			   SharedPreferences.Editor editor = preferences.edit();
		    	  editor.putString(variable,value);
		    	  editor.apply();

		}


		public String retrieve(String variable){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	  	  	String slotnumber = preferences.getString("slotnumber","");
	  	  	if (slotnumber == "")
	  	  	{
	  	  		slotnumber = "slot1-";
	  	  	}
			variable = slotnumber + variable;
			String value = preferences.getString(variable,"");

			Log.d("notactivity", "retreived " + variable + " " + value);


	  	  	return value;
		}

		public int retrieveNumber(String variable){
			String value = retrieve(variable);
			if (value == ""){
				value = "0";
			}

			int number=Integer.parseInt(value.replaceAll("[\\D]",""));

			//value = wpmTextBox.getText();
			return number;
		}


		public boolean onKeyDown(int keyCode, KeyEvent event) {
	        super.onKeyDown(keyCode, event);
	        //VOLUME KEY DOWN
	   if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	   {

		   this.finish();

	       return true;
	   }
	   //VOLUME KEY UP
	   if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
	   {
		   enter(inputTextView);

	       return true;
	   }




	    if (keyCode == KeyEvent.KEYCODE_MENU)
	    {

	    	this.finish();


	        return true;
	    }



	    return true;
	    }
*/
}
