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

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.util.Log.d;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SettingsLoader {

	protected String xppoints = "xppointsvalue";
	protected String wordpoints = "wordpointsvalue";
	protected String minblocksizekey = "minblocksizevalue";
	protected String maxblocksizekey = "maxblocksizevalue";
	protected String wpmkey="wpmvalue";
	protected String textcolorkey = "textcolorvalue";
	protected String focuscolorkey = "focuscolorvalue";
	protected String backgroundcolorkey = "backgroundcolorvalue";
	protected String globalpositionpermillekey = "globalpositionpercentage";//only the one used in Preference Activity
	protected String globalpositionkey = "globalposition";
	protected String fontsizekey = "fontsizevalue";
	protected String fontnamekey = "fontnamevalue";
	protected String filepathkey = "filepath";
	protected String notebooktextkey = "notebook";
	protected String texttoreadkey = "texttoread";
	protected String orientationkey = "orientationmode";
	protected String copypastekey = "fromcopyandpaste";
	protected String copiedtextkey = "textfromcopyandpaste";
	protected String precopypasteglobalpositionpermillekey = "precopypastepositionpercentage";
	protected String lastreadskey = "recentreads";

	protected String filepathkeyreal = "filepath2";
	protected String filepathkey3 = "filepath3";
	protected String filepathkey4 = "filepath4";
	protected String filepathkey5 = "filepath5";
	protected String filepathkey6 = "filepath6";
	protected String filepathkey7 = "filepath7";

	protected String globalpositionpermillekeyreal = "globalpositionpercentage2";
	protected String globalpositionpermillekey3 = "globalpositionpercentage3";
	protected String globalpositionpermillekey4 = "globalpositionpercentage4";
	protected String globalpositionpermillekey5 = "globalpositionpercentage5";
	protected String globalpositionpermillekey6 = "globalpositionpercentage6";
	protected String globalpositionpermillekey7 = "globalpositionpercentage7";

	protected String texttoreadkey2 = "texttoread2";
	protected String texttoreadkey3 = "texttoread3";
	protected String texttoreadkey4 = "texttoread4";
	protected String texttoreadkey5 = "texttoread5";
	protected String texttoreadkey6 = "texttoread6";
	protected String texttoreadkey7 = "texttoread7";


  //  public int globalposition = 1;
  //  public int tickposition = 0;
  //  public int minblocksize = 20;
  //  public int maxblocksize = 110;
	public SharedPreferences preferences = null;
	public String standarttext = "standarttext";

	public SettingsLoader (SharedPreferences shared){




		preferences = shared;
	}


	private String retrieve(String variable){
	//	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
  	String slotnumber = "";
		try{slotnumber = preferences.getString("slotnumber","");}
		catch(Exception e){slotnumber = "slot1-";}

		// variable = variable;
		String value = "";
		try {value = preferences.getString(variable,"");}
		catch(Exception e){}

		Log.i("SettingsLoader", "loading" + variable + " which is:" + value);


  	  	return value;
	}

	public void save(String variable, String value){

			SharedPreferences.Editor editor = preferences.edit();

		    	  editor.putString(variable,value);
		    	  editor.apply();

		}

	public void saveNumber(String variable, int value){

		Integer newinteger = new Integer(value);
		//String valuestring = newinteger.toString();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(variable,value);
		editor.apply();


	}
	public void saveReadingCopyTextboolean(boolean bool){
		preferences.edit().putBoolean(copypastekey,bool).commit();
		if (bool == false){
			int newposition = retrieveNumber(precopypasteglobalpositionpermillekey);
			saveNumber(globalpositionpermillekeyreal,newposition);
			adjustGlobalPositionToPercentage(newposition);

		}

	}

	public void saveReadingCopyTextString(String text){
		save(copiedtextkey,text);
		if(getReadingCopyTextOn() == false){ //only save position if current text was not loaded from copy-paste as well
		int previousposition = getGlobalPositionSeekbarValue();
		saveNumber(precopypasteglobalpositionpermillekey,previousposition);}
		adjustGlobalPositionToPercentage(0);

	}

	public boolean getReadingCopyTextOn(){
		return preferences.getBoolean(copypastekey,false);
	}

	protected int retrieveNumber(String variable){

		int value = 0;
		try {value = preferences.getInt(variable,0);}
		catch(Exception e){}
		return value;
	}

	public String getOrientationMode() {
		String vhelper = retrieve(orientationkey);
		return vhelper;
	}


	public int getMinBlockSize() {
		int vhelper = retrieveNumber(minblocksizekey);
		if (vhelper == 0){
			vhelper = 17;

		}

		return vhelper;
	}
	public int getMaxBlockSize(){
		int vhelper = retrieveNumber(maxblocksizekey);
		if (vhelper == 0){
			vhelper = 50;
		}
		if (getMinBlockSize() > vhelper){
			vhelper = getMinBlockSize();

		}

		return vhelper;
	}
	public int getGlobalPosition(){
		int vhelper = retrieveNumber(globalpositionkey);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;
	}
	public int getGlobalPositionSeekbarValue(){
		int vhelper = retrieveNumber(globalpositionpermillekeyreal);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;
	}

	public String getGlobalPositionPercentString(){
		int permille = getGlobalPositionSeekbarValue();
		float percentage = (float)permille/10;
		String percent = String.format("%.2f", (float)percentage) + "%";
		return percent;


	}



	public int saveGlobalPosition(int globalposition){
		int length = getTexttoRead().length();
		Log.i("Settingloader", "text length is: " + length);

		float newposition = ( (float)globalposition / (float)length) * 1000;
		int adjustedposition = Math.round(newposition);
		saveNumber(globalpositionpermillekeyreal, adjustedposition);
		saveNumber(globalpositionkey,globalposition);
		return adjustedposition;
		//saveNumber(globalpositionkey,position);
	}

	public int adjustGlobalPositionToPercentage(int number){
		int maxnumber = 1000;
		int length = getTexttoRead().length();
		Log.i("Settingloader", "text length is: " + length);

		float newposition = length * ( (float) number / (float) maxnumber);
		int adjustedposition = Math.round(newposition);
		Log.i("Settingloader", "position is: " + adjustedposition);
		saveNumber(globalpositionkey,adjustedposition);
		//saveNumber(globalpositionpermillekey2, number);
		//save(filepathkey2, retrieve(filepathkey));
		return adjustedposition;//is the global position in absolute numbers
	}



	public int getFontSize(){
		int vhelper = retrieveNumber(fontsizekey);
		if (vhelper == 0){
			vhelper = 20;
		}
		return vhelper;
	}
	public String getFontName(){
		return retrieve(fontnamekey);
	}
	public int getTextColor(){
		String hexcode = retrieve(textcolorkey);
		int number = retrieveNumber(textcolorkey);
		//return Color.parseColor(number);
		//if (number == 0 && getBackgroundColor() == 0){
		//	number = Color.parseColor("#ffffff");
		//}
		return number;
	}
	public int getBackgroundColor(){
	//	String hexcode = retrieve(backgroundcolorkey);
		int number = retrieveNumber(backgroundcolorkey);
		//return Color.parseColor(number);



		return number;
	}
	public int getFocusColor(){
	//	String hexcode = retrieve(focuscolorkey);
		int number = retrieveNumber(focuscolorkey);
		//if (number == 0){
		//	number = Color.parseColor("#ffee00");
		//}

		return number;
	}
	public int getWordsPerMinute(){
		int vhelper = retrieveNumber(wpmkey);
		if (vhelper == 0){
			vhelper = 230;
		}

		return vhelper;
	}
	public int getWordpoints(){

		int vhelper = retrieveNumber(wordpoints);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;

	}
	void saveWordpoints(int number){
		saveNumber(wordpoints, number);


	}
	void saveWordsPerMinute(int number){
		saveNumber(wpmkey, number);


	}

	public int getXPpoints(){

		int vhelper = retrieveNumber(xppoints);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;



	}
	void saveXPpoints(int number){
		saveNumber(xppoints, number);


	}
	public float getGamificationLevel(){
		int number = getXPpoints();
		double levelnummer = 0.01 * (float) Math.sqrt( number );
		return (float) levelnummer;
	}

	public String getCurrentNotes(){
		String filepath = getCurrentNotesFilePath();
		File myfile = new File(filepath);
		if (myfile.exists()){
			return loadfromtxtfile(getCurrentNotesFilePath());
		}
		else {
			return "";
		}
		//wenn kein Path, dann keine note
	}

	public String getCurrentNotesFilePath(){
		String filename = getFileofPath(getFilePath());
		//ist da jetzt ein Doppelpunkt dran?
		File directory = new File(Environment.getExternalStorageDirectory(), "Comfort Reader");
		directory.mkdirs();
		if (filename.contains(".")){
		filename = filename.substring(0,filename.lastIndexOf("."));}

		if(filename.length() > 30){
			filename = filename.substring(0, 29); }
		String newstring = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Comfort Reader/notes_" + filename + ".txt";

		if (getReadingCopyTextOn()) {
			newstring = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Comfort Reader/notes_copy_pasted_text.txt";

		}


		return newstring;
	}


	public Boolean addtoCurrentNotes(String note){

		String textwriteout = note;
		String path = getCurrentNotesFilePath();
		Boolean success = false;

		try {
			File myFile = new File(path);
			//if (!myFile.exists()) {
			//	myFile.mkdirs();
			//}
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter =
					new OutputStreamWriter(fOut);
			myOutWriter.append(textwriteout);
			myOutWriter.close();
			fOut.close();
			success = true;

			//Toast.makeText(getBaseContext(),
			//		"Done writing out to 'Comfort Reader/notes" + filename + ".txt",
			//		Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		//	Toast.makeText(getBaseContext(), e.getMessage(),
		//			Toast.LENGTH_SHORT).show();
		}


		return success;



		//wenn kein file am Path, dann neu anlegen
		//sonst Text einfach anhängen


	}





	public String getFilePath(){
		return retrieve (filepathkeyreal);
	}
	public String getTexttoRead(){
		String text;

		if(getReadingCopyTextOn()){
			text = retrieve(copiedtextkey);
		}
		else {
			text = retrieve(texttoreadkey);
		}

		if (text.length()<14){
			text = standarttext;
		}
		return text;
	}


	public void shiftBooks(){

if (retrieve(filepathkey3).equals(getFilePath())==false) {

	Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());
	Log.i("settings", "shifting books");

	String
			text = retrieve(texttoreadkey6);
	save(texttoreadkey7, text);
	text = retrieve(texttoreadkey5);
	save(texttoreadkey6, text);


			text = retrieve(texttoreadkey4);
	save(texttoreadkey5, text);
	text = retrieve(texttoreadkey3);
	save(texttoreadkey4, text);
	text = retrieve(texttoreadkey);
	save(texttoreadkey3, text);
	//text = retrieve(texttoreadkey);
	//save(texttoreadkey2, text);

	text = retrieve(filepathkey6);
	save(filepathkey7, text);
	text = retrieve(filepathkey5);
	save(filepathkey6, text);
	text = retrieve(filepathkey4);
	save(filepathkey5, text);
	text = retrieve(filepathkey3);
	save(filepathkey4, text);
	text = retrieve(filepathkeyreal);
	save(filepathkey3, text);
	//text = retrieve(filepathkey);//TODO wurder ja schon geändert in dem zeitpunkt
	//save(filepathkey2, text);

	int permille = retrieveNumber(globalpositionpermillekey6);
	saveNumber(globalpositionpermillekey7, permille);
			permille = retrieveNumber(globalpositionpermillekey5);
	saveNumber(globalpositionpermillekey6, permille);

	permille = retrieveNumber(globalpositionpermillekey4);
	saveNumber(globalpositionpermillekey5, permille);
	permille = retrieveNumber(globalpositionpermillekey3);
	saveNumber(globalpositionpermillekey4, permille);
	permille = retrieveNumber(globalpositionpermillekeyreal);
	Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());

	saveNumber(globalpositionpermillekey3, permille);
	//permille = retrieveNumber(globalpositionpermillekeyreal);
	//saveNumber(globalpositionpermillekey2, permille);
}
	}

	public String getPercentStringfromPermille(int zahl){
	float percentage = (float)zahl/10;
	return String.format("%.2f", (float)percentage) + "%";

	}
	public String getFileofPath(String path){
		String text = "";
		try{
			text = path.substring(path.lastIndexOf("/")+1);
		}
		catch (Error E){}
		return text;

	}

	public CharSequence[] getLastBooks(){

		CharSequence[] entries = { //retrieve(filepathkeyreal) + " " + retrieveNumber(globalpositionpermillekeyreal),
				getFileofPath(retrieve(filepathkey3)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey3)),
				getFileofPath(retrieve(filepathkey4)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey4)),
				getFileofPath(retrieve(filepathkey5)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey5)),
				getFileofPath(retrieve(filepathkey6)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey6)),
				getFileofPath(retrieve(filepathkey7)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey7)) };
		return entries;

	}
	public CharSequence[] getLastBooksValues(){
		CharSequence[] entryValues ={"2", "3", "4", "5","6"};


	return entryValues;

	}



	public void reloadSelectedBook(){ //load book after last read selection may have been changed


		Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());
		String fromSlotNumber = retrieve(lastreadskey);
		save(lastreadskey,"0");

		String text;
		String path;
		int permille;

		if(fromSlotNumber.equals("1")){
			;
		}
		else if(fromSlotNumber.equals("2")) {
			preferences.edit().putBoolean(copypastekey,false).commit();

			text = retrieve(texttoreadkey3);
			path = retrieve(filepathkey3);


			permille = retrieveNumber(globalpositionpermillekey3);

			shiftBooks();

			save(texttoreadkey,text);
			save(filepathkeyreal,path);
			adjustGlobalPositionToPercentage(permille);
		}
		else if (fromSlotNumber.equals("3")) {
			preferences.edit().putBoolean(copypastekey,false).commit();

			text = retrieve(texttoreadkey4);

			path = retrieve(filepathkey4);


			permille = retrieveNumber(globalpositionpermillekey4);

			shiftBooks();

			save(texttoreadkey,text);
			save(filepathkeyreal,path);
			adjustGlobalPositionToPercentage(permille);
		}
		else if (fromSlotNumber.equals("4")) {
			preferences.edit().putBoolean(copypastekey,false).commit();

			text = retrieve(texttoreadkey5);


			path = retrieve(filepathkey5);
			// Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());

			permille = retrieveNumber(globalpositionpermillekey5);




			shiftBooks();

			save(texttoreadkey,text);
			save(filepathkeyreal,path);
			adjustGlobalPositionToPercentage(permille);

		}
		else if (fromSlotNumber.equals("5")) {
			preferences.edit().putBoolean(copypastekey,false).commit();

			text = retrieve(texttoreadkey6);


			path = retrieve(filepathkey6);
			// Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());

			permille = retrieveNumber(globalpositionpermillekey6);




			shiftBooks();

			save(texttoreadkey,text);
			save(filepathkeyreal,path);
			adjustGlobalPositionToPercentage(permille);

		}
		else if (fromSlotNumber.equals("6")) {
			preferences.edit().putBoolean(copypastekey,false).commit();

			text = retrieve(texttoreadkey7);


			path = retrieve(filepathkey7);
			// Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());

			permille = retrieveNumber(globalpositionpermillekey7);




			shiftBooks();

			save(texttoreadkey,text);
			save(filepathkeyreal,path);
			adjustGlobalPositionToPercentage(permille);

		}






	}



	/*public void loadNewText(String text){
		if (text.length() > 30){
			save(filepathkey,"Clipboard-Paste");
			saveGlobalPosition(0);
			save(texttoreadkey, text);
		}
	}*/

	public void loadTextfromFilePath(String path){
		d("settings", "filename is " + path);
		String extension = "";
		if (path.contains(".")){
			extension = path.substring((path.lastIndexOf(".") + 1), path.length());
		}

		d("settings", "extension is " + extension);
		String texttoread = "";
		if (extension.equals("pdf:")){// the : sign is necessary because of the caracteristics of the filepicker preference that appends always :

			path = path.substring(0,(path.lastIndexOf(".")))+".pdf";
			d("settings", "Loading pdf" + path);
			texttoread = loadfrompdf(path);
		}

		else if (extension.equals("epub:")){
			path = path.substring(0,(path.lastIndexOf(".")))+".epub";
			d("settings", "Loading epub" + path);
			texttoread = loadfromepubfile(path);
		}


		else if (extension.equals("txt:")){

			path = path.substring(0,(path.lastIndexOf(".")))+".txt";
			d("settings", "Loading txt" + path);
			texttoread = loadfromtxtfile(path);
		}
		else {
			texttoread = "text file format not supported";
		}

		Log.d("settings", "loading book global position 0: " + getGlobalPositionSeekbarValue());
		shiftBooks();
		Log.d("settings", "loading book global position 1: " + getGlobalPositionSeekbarValue());
		preferences.edit().putBoolean(copypastekey,false).commit();


		Log.d("settings", "loading book global position 2: " + getGlobalPositionSeekbarValue());

		save(filepathkeyreal,retrieve(filepathkey));

		save(texttoreadkey, texttoread);
		saveGlobalPosition(0);
		adjustGlobalPositionToPercentage(0);

	}



	public String loadfromepubfile (String pathtext) {
		File file2 = new File(pathtext);
		pathtext = file2.getAbsolutePath();


		Reader reader = new Reader();
		reader.setMaxContentPerSection(1000); // Max string length for the current page.
		reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
		try {
			reader.setFullContent(pathtext); // Must call before readSection.
		} catch (ReadingException e) {
			reader = null;
			e.printStackTrace();
			return "Failed to load file";
		}
		StringBuilder text = new StringBuilder();
		int index = 0;
		boolean counton = TRUE;
		while (counton) {


			BookSection bookSection = null;
			try {
				bookSection = reader.readSection(index);
			} catch (ReadingException e) {
				counton = FALSE;
			} catch (OutOfPagesException e) {
				counton = FALSE;
			}
			index = index +1;
			if (bookSection  != null) {
				text.append(bookSection.getSectionTextContent());
			}
		}



		return text.toString();

	}

	public String loadfromtxtfile (String pathtext){

		File file2 = new File(pathtext);
		pathtext = file2.getAbsolutePath();
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new FileReader(pathtext));
		} catch (FileNotFoundException e) {
			reader = null;
			e.printStackTrace();
			return "Failed to load file";
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
			return "Failed to load file";
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed to load file";
		}

		return text.toString();
	}

	public String loadfrompdf (String pathtext){
		//	TextView myOutBox = (TextView) findViewById(R.id.TextViewFilePath);
		//String pathtext = path.getAbsolutePath() + "/" + chosenFile;
		File file2 = new File(pathtext);
		pathtext = file2.getAbsolutePath();
		//	myOutBox.setText("" + pathtext);

		d("settings", "loading from pdf");

		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(pathtext);
		String text = "empty . . . empty empty";


		try {
			text = pdfManager.ToText();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	public void loadRealSettingstoPreferences(){
		saveNumber(globalpositionpermillekey,getGlobalPositionSeekbarValue());
		save(filepathkey,getFilePath());

	}


}
