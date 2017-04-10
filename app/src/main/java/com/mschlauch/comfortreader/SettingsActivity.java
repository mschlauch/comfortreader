/**
This file is part of Comfort Reader.

Copyright 2014,2015 Michael Schlauch
Comfort Reader is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Comfort Reader is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Comfort Reader.  If not, see <http://www.gnu.org/licenses/>.
 *//*

package com.mschlauch.comfortreader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mschlauch.comfortreader.PDFManager;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar.OnSeekBarChangeListener;

//EBOOK-IMPORT NOT YET IMPLEMENTED
//import nl.siegmann.epublib.*;
//import nl.siegmann.epublib.domain.Book;
//import nl.siegmann.epublib.domain.TOCReference;
//import nl.siegmann.epublib.epub.EpubReader;


public class SettingsActivity extends ActionBarActivity {

	public SettingsLoader settingsload = null ;
	 
	 
	private SeekBar mSeekBar;
	private TextView previewcontent;
	//FROM SIMPLE FILE EXPLORE
	// Stores names of traversed directories
		ArrayList<String> str = new ArrayList<String>();
		 
		// Check if the first level of the directory structure is the one showing
		private Boolean firstLvl = true;

		private static final String TAG = "F_PATH";

		private Item[] fileList;
		private File path = new File("/");
				
				//new File(Environment.getRootDirectory() + "");
		private String chosenFile;
		private static final int DIALOG_LOAD_FILE = 1000;

		ListAdapter adapter;
		//END VARIABLES SIMPLE FILE EXPLORE
	
	
	
	private EditText wpmTextBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		settingsload = new SettingsLoader (PreferenceManager.getDefaultSharedPreferences(this));
	      
		
		mSeekBar = (SeekBar)findViewById(R.id.reading_progress_bar_settings);
		previewcontent = (TextView)findViewById(R.id.textViewcontentpreview);
	       
        mSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
        { 
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
        	if (fromUser){
                 // TODO Auto-generated method stub
        	double percentage = progress;
        //	Integer position = new Integer ((int) Math.round(percentage));
        	
        	String texttoread = retrieve("texttoread");
				Log.d("seekbarsettings", "load text with size:" + texttoread.length());



				if (texttoread.length()<14){
					texttoread = settingsload.standarttext;

				}
        	double position = percentage * texttoread.length() / 1000;
	    	int newposition = (int) Math.round(position) - 5;
	    	if (0 > newposition){newposition = 0;}
        	int end = newposition + 400;
        	if (end > texttoread.length()){
        		end = texttoread.length()-2;
        	}
	    	
        	 position = percentage * texttoread.length() / 1000;
	    	int newposition2 = (int) Math.round(position);
        	int globalposition = newposition2;
        	saveNumber("globalposition",globalposition);
        	Log.d("seekbarsettings", "position" + globalposition);
        	
	    	 String contentpreview = texttoread.substring(newposition, end);
	    	Log.d("seekbarsettings", "contentpreview" + contentpreview);
        	
         previewcontent.setText(""+percentage/10 + "% :" + contentpreview + "..." );
        //	previewcontent.setText("adsfadsf");
        	//TextView myOutBox = (TextView) findViewById(R.id.textViewWPM);
    		//myOutBox.setText(globalposition + "");
        	
        	}                 
//             	
//               
//          };
//		
        }
        
        
      
         public void onStartTrackingTouch(SeekBar seekBar) {
          //   mTrackingText.setText(getString(R.string.seekbar_tracking_on));
         }
      
         public void onStopTrackingTouch(SeekBar seekBar) {
           //  mTrackingText.setText(getString(R.string.seekbar_tracking_off));
         }
        
        
        });
		
		
		retreiveSavedOptions();
		
		
		 
	}

	
	
	public void loadtext(View view) {
		
	   File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,"comfortreader/comfortreader.txt");

		//Read text from file
		StringBuilder text = new StringBuilder();

	try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		
	//	TextView myOutBox = (TextView) findViewById(R.id.myOutBox);
		//  myOutBox.setText(text.substring(0,40));
		save("texttoread",text.toString());
		  
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    public void save(String variable, String value){

		settingsload.save(variable, value);
		
		
	}
	public String retrieve(String variable){
			
		String value = "";//settingsload.retrieve(variable);
		  	return value;
	}
	
	public void saveNumber(String variable, int value){

		settingsload.saveNumber(variable, value);
		
	}
	
	public int retrieveNumber(String variable){
		
		int number = 0;//settingsload.retrieveNumber(variable);
		
		return number;
	}
	
	
	public void retreiveSavedOptions(){

		//String slot = settingsload.getSlotNumber();
		String filepath = retrieve("filepath");
		chosenFile = retrieve("filename");



		String texttoread = retrieve("texttoread");
		int globalposition = retrieveNumber ("globalposition");

		int number=globalposition;
		int divisor = texttoread.length();
		if (divisor == 0){divisor = 1;}
		double position = (float)number / (divisor);
		//das 0.0000 because we shouldnt divide with zero
		int newposition = (int) Math.round(position * 1000) - 1;
		mSeekBar.setProgress(newposition);
		int percent = (int) Math.round(position * 100);
		String positiondisplay = "" + percent + " "+  "% " + "" ;
 String slot = "";


		if (slot.equals("slot1-")){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioBookSlot1);
			radioColorButton.setChecked(true);
			radioColorButton.setText("#1:" + chosenFile + positiondisplay);
		}
		else if (slot.equals("slot2-")){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioBookSlot2);
			radioColorButton.setChecked(true);
			radioColorButton.setText("#2:" + chosenFile + positiondisplay);
		}
		else if (slot.equals("slot3-")){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioBookSlot3);
			radioColorButton.setChecked(true);
			radioColorButton.setText("#3:" + chosenFile + positiondisplay);
		}
		else if (slot.equals("slot4-")){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioBookSlot4);
			radioColorButton.setChecked(true);
			radioColorButton.setText("#4:" + chosenFile + positiondisplay);
		}
		else {
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioBookSlot1);
			radioColorButton.setChecked(true);
			radioColorButton.setText("#1:" + chosenFile);
		}


		int actual = retrieveNumber ("wpm");
		TextView myOutBox = (TextView) findViewById(R.id.textViewWPM);
		myOutBox.setText(actual + "");
		
		actual = retrieveNumber ("minblock");
		myOutBox = (TextView) findViewById(R.id.TextViewMinBlock);
		myOutBox.setText(actual + "");
		
		actual = retrieveNumber ("maxblock");
		myOutBox = (TextView) findViewById(R.id.TextViewMaxBlock);
		myOutBox.setText(actual + "");
		
		actual = retrieveNumber ("fontsize");
		myOutBox = (TextView) findViewById(R.id.TextViewFontSize);
		myOutBox.setText(actual + "");
		
		actual = retrieveNumber ("maxlinelength");
	//	if(actual == 0){ //default values given in FullsrceenActivity
			//default value
		//	actual = 15;
	//	}
	//	myOutBox = (TextView) findViewById(R.id.TextViewPerLine);
	//	myOutBox.setText(actual + "");
	//	actual = retrieveNumber ("maxlinelength");
		
	//	actual = retrieveNumber ("lenseeffect");

	//	 myOutBox = (TextView) findViewById(R.id.TextViewLenseEffect);
	//	myOutBox.setText(actual + "");
		//myOutBox.setText(actual + "");


	//	myOutBox = (TextView) findViewById(R.id.TextViewFilePath);
	//	myOutBox.setText("" + filepath + "/" + chosenFile);
	
		actual = retrieveNumber ("brighttheme");
		if (actual == 1){
			 RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioColorBright);
			radioColorButton.setChecked(true);
		}
		else {
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioColorDark);
			radioColorButton.setChecked(true);
			
		}
		
		actual = retrieveNumber ("typeface");
		if (actual == 1){
			 RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioTypefaceSans);
			radioColorButton.setChecked(true);
		}
		else if (actual == 2){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioTypefaceMono);
			radioColorButton.setChecked(true);
			
		}
		else if (actual == 0){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioTypefaceSerif);
			radioColorButton.setChecked(true);
			
		}
		
		actual = retrieveNumber ("orientationmode");
		if (actual == 0){
			 RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioOrientationAuto);
			radioColorButton.setChecked(true);
		}
		else if (actual == 1){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioOrientationLandscape);
			radioColorButton.setChecked(true);
			
		}
		else if (actual == 2){
			RadioButton radioColorButton = (RadioButton) findViewById(R.id.radioOrientationPortrait);
			radioColorButton.setChecked(true);
			
		}
		

		
		
		
		
		

     
			
			
		*/
/*//*
/set it up also for the Custom View...
		actual = retrieveNumber ("orientationmode");
		 if (actual==1) {
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		 }
		 else if (actual == 2){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		 }
		 else if (actual == 0){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		 }
		*//*

	}
	
	
	
	public void wpmPlusButtonPressed (View view){
		int actualwpm = retrieveNumber ("wpm");
		int increment = 25;
		if (actualwpm < 200){
			increment = 10;
			if (actualwpm < 100){
				increment = 5;
			}
		}
		int newwpm = actualwpm + increment;
		int maxwpm = 10000;
		if (newwpm > maxwpm){
			newwpm = maxwpm;
		}
		saveNumber ("wpm", newwpm);
		TextView myOutBox = (TextView) findViewById(R.id.textViewWPM);
		myOutBox.setText(newwpm + "");
		
	}
	public void wpmMinusButtonPressed (View view){
		int actualwpm = retrieveNumber ("wpm");
		int increment = 25;
		if (actualwpm < 200){
			increment = 10;
			if (actualwpm < 100){
				increment = 5;
			}
		}
		int newwpm = actualwpm - increment;
		int minwpm = 10;
		if (newwpm < minwpm){
			newwpm = minwpm;
		}
		saveNumber ("wpm", newwpm);
		TextView myOutBox = (TextView) findViewById(R.id.textViewWPM);
		myOutBox.setText(newwpm + "");
	}
	public void minPlusButtonPressed (View view){
		int actualminblock = retrieveNumber ("minblock");
		int newminblock = actualminblock + 1;
		int maxminblock = 10000;
		if (newminblock > maxminblock){
			newminblock = maxminblock;
		}
		saveNumber ("minblock", newminblock);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewMinBlock);
		myOutBox.setText(newminblock + "");
				
	}
	public void minMinusButtonPressed (View view){
		int actualminblock = retrieveNumber ("minblock");
		int newminblock = actualminblock - 1;
		int maxminblock = 1; //actually minminblock...
		if (newminblock < maxminblock){
			newminblock = maxminblock;
		}
		saveNumber ("minblock", newminblock);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewMinBlock);
		myOutBox.setText(newminblock + "");
		
	}
	public void maxPlusButtonPressed (View view){
		int actualmaxblock = retrieveNumber ("maxblock");
		int newmaxblock = actualmaxblock + 1;
		int maxmaxblock = 250;
		if (newmaxblock > maxmaxblock){
			newmaxblock = maxmaxblock;
		}
		saveNumber ("maxblock", newmaxblock);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewMaxBlock);
		myOutBox.setText(newmaxblock + "");
		
		
	}
	public void maxMinusButtonPressed (View view){
		int actualmaxblock = retrieveNumber ("maxblock");
		int newmaxblock = actualmaxblock - 1;
		int minmaxblock = 2; 
		if (newmaxblock < minmaxblock){
			newmaxblock = minmaxblock;
		}
		saveNumber ("maxblock", newmaxblock);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewMaxBlock);
		myOutBox.setText(newmaxblock + "");
	}
	public void fsizePlusButtonPressed (View view){
		
		int actualfontsize = retrieveNumber ("fontsize");
		int newfontsize = actualfontsize + 1;
		int maxfontsize = 100;
		if (newfontsize > maxfontsize){
			newfontsize = maxfontsize;
		}
		saveNumber ("fontsize", newfontsize);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewFontSize);
		myOutBox.setText(newfontsize + "");
		
		
	}
	public void fsizeMinusButtonPressed (View view){
		int actualfontsize = retrieveNumber ("fontsize");
		int newfontsize = actualfontsize - 1;
		int minfontsize = 2; 
		if (newfontsize < minfontsize){
			newfontsize = minfontsize;
		}
		saveNumber ("fontsize", newfontsize);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewFontSize);
		myOutBox.setText(newfontsize + "");
	}
	
	*/
/*public void perLinePlusButtonPressed (View view){
		
		int actualfontsize = retrieveNumber ("maxlinelength");
		int newfontsize = actualfontsize + 1;
		int maxfontsize = 100;
		if (newfontsize > maxfontsize){
			newfontsize = maxfontsize;
		}
		saveNumber ("maxlinelength", newfontsize);
	//	TextView myOutBox = (TextView) findViewById(R.id.TextViewPerLine);
		myOutBox.setText(newfontsize + "");
		
		
	}*//*

	*/
/*
	public void perLineMinusButtonPressed (View view){
		int actualfontsize = retrieveNumber ("maxlinelength");
		int newfontsize = actualfontsize - 1;
		int minfontsize = 4; 
		if (newfontsize < minfontsize){
			newfontsize = minfontsize;
		}
		saveNumber ("maxlinelength", newfontsize);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewPerLine);
		myOutBox.setText(newfontsize + "");
	}
	*//*

	*/
/*
	public void lenseeffectPlusButtonPressed (View view){
		
		int actualfontsize = retrieveNumber ("lenseeffect");
		int newfontsize = actualfontsize + 1;
		int maxfontsize = 25;
		if (newfontsize > maxfontsize){
			newfontsize = maxfontsize;
		}
		saveNumber ("lenseeffect", newfontsize);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewLenseEffect);
		myOutBox.setText(newfontsize + "");
		
		
	}
	public void lenseeffectMinusButtonPressed (View view){
		int actualfontsize = retrieveNumber ("lenseeffect");
		int newfontsize = actualfontsize - 1;
		int minfontsize = 0; 
		if (newfontsize < minfontsize){
			newfontsize = minfontsize;
		}
		saveNumber ("lenseeffect", newfontsize);
		TextView myOutBox = (TextView) findViewById(R.id.TextViewLenseEffect);
		myOutBox.setText(newfontsize + "");
	}
	*//*

	public void colorsRadioButtonPressed (View view){
		
		RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioGroupColors);
		//btnDisplay = (Button) findViewById(R.id.btnDisplay);
	 
		int selectedId = radioSexGroup.getCheckedRadioButtonId();
		 
		// find the radiobutton by returned id
	   RadioButton radioColorButton = (RadioButton) findViewById(selectedId);
		
	    if (radioColorButton ==  (RadioButton) findViewById(R.id.radioColorBright)){
	    	saveNumber ("brighttheme", 1);
	    	
	    }
	    else {
	    	
	    	saveNumber ("brighttheme", 0);
	    }
	}
	
public void typefaceRadioButtonPressed (View view){
		
		RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioGroupTypeface);
		//btnDisplay = (Button) findViewById(R.id.btnDisplay);
	 
		int selectedId = radioSexGroup.getCheckedRadioButtonId();
		 
		// find the radiobutton by returned id
	   RadioButton radioColorButton = (RadioButton) findViewById(selectedId);
		
	   if (radioColorButton ==  (RadioButton) findViewById(R.id.radioTypefaceSerif)){
	    	saveNumber ("typeface", 0);
	    }
	   else  if (radioColorButton ==  (RadioButton) findViewById(R.id.radioTypefaceSans)){
	    	saveNumber ("typeface", 1);
	    }
	    else if (radioColorButton ==  (RadioButton) findViewById(R.id.radioTypefaceMono)){
	    	saveNumber ("typeface", 2);
	    }
	}
	
public void orientationRadioButtonPressed (View view){
	
	RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioGroupOrientation);
	//btnDisplay = (Button) findViewById(R.id.btnDisplay);
 
	int selectedId = radioSexGroup.getCheckedRadioButtonId();
	 
	// find the radiobutton by returned id
   RadioButton radioColorButton = (RadioButton) findViewById(selectedId);
	
   if (radioColorButton ==  (RadioButton) findViewById(R.id.radioOrientationAuto)){
    	saveNumber ("orientationmode", 0);
    }
   else  if (radioColorButton ==  (RadioButton) findViewById(R.id.radioOrientationLandscape)){
    	saveNumber ("orientationmode", 1);
    }
    else if (radioColorButton ==  (RadioButton) findViewById(R.id.radioOrientationPortrait)){
    	saveNumber ("orientationmode", 2);
    }
}

public void resettoDefaultButtonAveragePressed(View view){
	saveNumber ("orientationmode", 0);
	saveNumber ("typeface", 0);
	saveNumber ("brighttheme", 0);
	saveNumber ("lenseeffect", 4);
	saveNumber ("maxlinelength", 10);
	saveNumber ("fontsize", 19);
	saveNumber ("minblock", 30);
	saveNumber ("maxblock", 100);
	saveNumber ("wpm", 260);
	//save("texttoread",null);
	//saveNumber("globalposition",1);
	retreiveSavedOptions();
}
public void resettoDefaultButtonFasterPressed(View view){
	saveNumber ("orientationmode", 1);
	saveNumber ("typeface", 1);
	saveNumber ("brighttheme", 0);
	saveNumber ("lenseeffect", 3);
	saveNumber ("maxlinelength", 40);
	saveNumber ("fontsize", 16);
	saveNumber ("minblock", 50);
	saveNumber ("maxblock", 230);
	saveNumber ("wpm", 450);
	//save("texttoread",null);
	//saveNumber("globalposition",1);
	retreiveSavedOptions();
}


public void bookslot1 (View view){
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	  
	SharedPreferences.Editor editor = preferences.edit();
	
	  editor.putString("slotnumber","slot1-");
	  editor.apply();
	retreiveSavedOptions();	
}
public void bookslot2 (View view){
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	  
	SharedPreferences.Editor editor = preferences.edit();
	
	  editor.putString("slotnumber","slot2-");
	  editor.apply();
	retreiveSavedOptions();	
}
public void bookslot3 (View view){
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	  
	SharedPreferences.Editor editor = preferences.edit();
	
	  editor.putString("slotnumber","slot3-");
	  editor.apply();
	retreiveSavedOptions();	
}
public void bookslot4 (View view){
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	  
	SharedPreferences.Editor editor = preferences.edit();
	
	  editor.putString("slotnumber","slot4-");
	  editor.apply();
	retreiveSavedOptions();	
}




	public void browseButtonPressed (View view){
		loadFileList();

		showDialog(DIALOG_LOAD_FILE);
		Log.d(TAG, path.getAbsolutePath());
	}
	
	//METHOD COPY PASTE
	public void pastefromclipboard (View view){
		
		//final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		//String text = clipBoard.getText().toString();
		 String text = "";
			//String text = "aeueai uiae uiuae uai uaie uaie uaie uiae uaie uaie uaie uia eiuae uiae ";	 
				 if (text.length() > 30){
					 save("texttoread",text);
					    save("filename","Clipboard-Paste");
						save("filepath","");
						 saveNumber("globalposition",0);
						 retreiveSavedOptions();
		 }
		 
		    
	}
	@SuppressLint("NewApi")

	
        
	
	//METHODS FILE EXPLORE
	
		private void loadFileList() {
			try {
				path.mkdirs();
			} catch (SecurityException e) {
				Log.e(TAG, "unable to write on the sd card ");
			}

			// Checks whether path exists
			if (path.exists()) {
				FilenameFilter filter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File sel = new File(dir, filename);
						// Filters based on whether the file is hidden or not
						return (sel.isFile() || sel.isDirectory())
								&& !sel.isHidden();

					}
				};

				String[] fList = path.list(filter);
				fileList = new Item[fList.length];
				for (int i = 0; i < fList.length; i++) {
					fileList[i] = new Item(fList[i], R.drawable.file_icon);

					// Convert into file path
					File sel = new File(path, fList[i]);

					// Set drawables
					if (sel.isDirectory()) {
						fileList[i].icon = R.drawable.directory_icon;
						Log.d("DIRECTORY", fileList[i].file);
					} else {
						Log.d("FILE", fileList[i].file);
					}
				}

				if (!firstLvl) {
					Item temp[] = new Item[fileList.length + 1];
					for (int i = 0; i < fileList.length; i++) {
						temp[i + 1] = fileList[i];
					}
					temp[0] = new Item("Up", R.drawable.directory_up);
					fileList = temp;
				}
			} else {
				Log.e(TAG, "path does not exist");
			}

			adapter = new ArrayAdapter<Item>(this,
					android.R.layout.select_dialog_item, android.R.id.text1,
					fileList) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// creates view
					View view = super.getView(position, convertView, parent);
					TextView textView = (TextView) view
							.findViewById(android.R.id.text1);

					// put the image on the text view
					textView.setCompoundDrawablesWithIntrinsicBounds(
							fileList[position].icon, 0, 0, 0);

					// add margin between image and text (support various screen
					// densities)
					int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
					textView.setCompoundDrawablePadding(dp5);

					return view;
				}
			};

		}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		};
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;
					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
					//	Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						//Log.d(TAG, path.getAbsolutePath());

					}
					// File picked
					else {
						// Perform action with file picked
							
						
						
						Log.d("settings", "chosen: " + path.getAbsolutePath());
						
						String filename = chosenFile;
						Log.d("settings", "filename is " + filename);
						String extension = filename.substring((filename.lastIndexOf(".") + 1), filename.length());
						Log.d("settings", "extension is " + extension);
						
						if (extension.equals("pdf")){
					loadfrompdf();
						}
						else {
					loadfromtxtfile();
						}
						
						
					}

				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}

	public void loadfromtxtfile (){
		save("filename",chosenFile);
		save("filepath",path.getAbsolutePath());
		
	//	TextView myOutBox = (TextView) findViewById(R.id.TextViewFilePath);
		
		String pathtext = path.getAbsolutePath() + "/" + chosenFile;
		
	//	myOutBox.setText("" + pathtext);
		
		
		//Routine for IMPORTING String from txt.
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(pathtext));
		} catch (FileNotFoundException e) {
			reader = null;
			e.printStackTrace();
		}
		
		
	    StringBuilder text = new StringBuilder();
	    String line;
	    try {
			while((line = reader.readLine()) != null)
			{
			    text.append(line);
			    text.append("\n");//BECAUSE ITS A LINE :D
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			text.append("No data found.");
			e.printStackTrace();
		}
	    try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    saveNumber("globalposition",0);
	    save("texttoread",text.toString());
		retreiveSavedOptions();
	}
	
	public void loadfrompdf (){
	//	TextView myOutBox = (TextView) findViewById(R.id.TextViewFilePath);
		String pathtext = path.getAbsolutePath() + "/" + chosenFile;
		
	//	myOutBox.setText("" + pathtext);
				 
		Log.d("settings", "loading from pdf");
		
		save("filename",chosenFile);
		save("filepath",path.getAbsolutePath());
		
		
		String pathtext2 = path.getAbsolutePath() + "/" + chosenFile;
		
		
	  		  
		 PDFManager pdfManager = new PDFManager();
	     pdfManager.setFilePath(pathtext2);
	      String text = "empty . . . empty empty";
	      
	     
	  		    try {
			text = pdfManager.ToText();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  		   saveNumber("globalposition",0);
		    save("texttoread",text);  
		    retreiveSavedOptions();
	}
	
	
}
*/
