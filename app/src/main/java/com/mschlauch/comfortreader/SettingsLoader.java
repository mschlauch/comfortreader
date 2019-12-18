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
import android.content.res.Resources;
import android.database.Cursor;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SettingsLoader {
	protected Context thiscontext;

	protected String xppoints = "xppointsvalue";
	// protected String wordpoints = "wordpointsvalue";
	protected String readingcharacterstatistics = "readingcharacterstat";
	protected String readingtimestatistics = "readingtimestat";

	protected String minblocksizekey = "minblocksizevalue";
	protected String maxblocksizekey = "maxblocksizevalue";
	protected String wpmkey="wpmvalue";
	protected String helplineskey="helplines";
	protected String textcolorkey = "textcolorvalue";
	protected String focuscolorkey = "focuscolorvalue";
	protected String backgroundcolorkey = "backgroundcolorvalue";
	protected String globalpositionpermillekey = "globalpositionpercentage";//only the one used in Preference Activity
//	protected String globalpositionkey = "globalposition";
	protected String fontsizekey = "fontsizevalue";
	protected String fontnamekey = "fontnamevalue";

	protected String notebooktextkey = "notebook";
	//protected String texttoreadkey = "texttoread";
	//protected String texttoreadtotallengthkey = "texttoreadtotallength";
	protected String orientationkey = "orientationmode";
	// protected String copypastekey = "fromcopyandpaste";
	protected String copiedtextkey = "textfromcopyandpaste";
	protected String precopypasteglobalpositionpermillekey = "precopypastepositionpercentage";
	protected String lastreadskey = "recentreads";

	protected String filepathkey = "filepath";
	protected String insertmanualkey = "inserttextmanually";

	//migrated to database 10-12-2019
	//protected String filepathkeyreal = "filepath2";
	//protected String filepathkey3 = "filepath3";
	//protected String filepathkey4 = "filepath4";
	//protected String filepathkey5 = "filepath5";
	//protected String filepathkey6 = "filepath6";
	//protected String filepathkey7 = "filepath7";

	//protected String globalpositionpermillekeyreal = "globalpositionpercentage2";
	//protected String globalpositionpermillekey3 = "globalpositionpercentage3";
	//protected String globalpositionpermillekey4 = "globalpositionpercentage4";
	//protected String globalpositionpermillekey5 = "globalpositionpercentage5";
	//protected String globalpositionpermillekey6 = "globalpositionpercentage6";
	//protected String globalpositionpermillekey7 = "globalpositionpercentage7";

	//protected String texttoreadkey2 = "texttoread2";
	//protected String texttoreadkey3 = "texttoread3";
	//protected String texttoreadkey4 = "texttoread4";
	//protected String texttoreadkey5 = "texttoread5";
	//protected String texttoreadkey6 = "texttoread6";
	//protected String texttoreadkey7 = "texttoread7";

	private DBManager dbManager;
	protected String currentbookidkey = "currentbookid";
  //  public int globalposition = 1;
  //  public int tickposition = 0;
  //  public int minblocksize = 20;
  //  public int maxblocksize = 110;
	public SharedPreferences preferences = null;
	//public String standarttext = "standarttext";

	public SettingsLoader (SharedPreferences shared, Context context){

		thiscontext = context;
		dbManager = new DBManager(context);
		//dbManager.open();

		preferences = shared;
		int loadedbookid = retrieveNumber(currentbookidkey);
		saveNumber(wpmkey, getWordsPerMinute());
		Log.d("settingslfnew", "bookid: should be same as" + loadedbookid);
		updatemigration();
	}
	public boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public void saveBoolean(String key, boolean value) {
		preferences.edit().putBoolean(key, value).commit();
	}
	public boolean firststart (){
		if (getBoolean("firststart", true)){
			saveBoolean("firststart",false);
			return true;
		}
		else {
			return false;
		}

	}

	private void updatemigration ()
	{
		String filepathkeyreal = "filepath2";
		String filepathkey3 = "filepath3";
		String filepathkey4 = "filepath4";
		String filepathkey5 = "filepath5";
		String filepathkey6 = "filepath6";
		String filepathkey7 = "filepath7";

		String globalpositionpermillekeyreal = "globalpositionpercentage2";
		String globalpositionpermillekey3 = "globalpositionpercentage3";
		String globalpositionpermillekey4 = "globalpositionpercentage4";
		String globalpositionpermillekey5 = "globalpositionpercentage5";
		String globalpositionpermillekey6 = "globalpositionpercentage6";
		String globalpositionpermillekey7 = "globalpositionpercentage7";

		String texttoreadkey2 = "texttoread2";
		String texttoreadkey3 = "texttoread3";
		String texttoreadkey4 = "texttoread4";
		String texttoreadkey5 = "texttoread5";
		String texttoreadkey6 = "texttoread6";
		String texttoreadkey7 = "texttoread7";
		 String copypastekey = "fromcopyandpaste";
		if(preferences.contains(filepathkeyreal)) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.remove(filepathkeyreal);
			editor.remove(filepathkey3);
			editor.remove(filepathkey4);
			editor.remove(filepathkey5);
			editor.remove(filepathkey6);
			editor.remove(filepathkey7);
			editor.remove(copypastekey);

			editor.remove(globalpositionpermillekeyreal);
			editor.remove(globalpositionpermillekey3);
			editor.remove(globalpositionpermillekey4);
			editor.remove(globalpositionpermillekey5);
			editor.remove(globalpositionpermillekey6);
			editor.remove(globalpositionpermillekey7);

			editor.remove(texttoreadkey2);
			editor.remove(texttoreadkey3);
			editor.remove(texttoreadkey4);
			editor.remove(texttoreadkey5);
			editor.remove(texttoreadkey6);
			editor.remove(texttoreadkey7);

			editor.apply();


		}

	}

	private synchronized String retrieve(String variable){
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

	public synchronized void save(String variable, String value){

			SharedPreferences.Editor editor = preferences.edit();

		    	  editor.putString(variable,value);
		    	  editor.apply();

		}

	public synchronized void saveNumber(String variable, int value){

		Integer newinteger = new Integer(value);
		//String valuestring = newinteger.toString();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(variable,value);
		editor.apply();


	}

	public void saveCommitChanges(){

		SharedPreferences.Editor editor = preferences.edit();
		editor.commit();
		dbManager.close();

		Log.d("settingslf", "changes committed (book id:" + retrieveNumber(currentbookidkey));

	}

	/*public void saveReadingCopyTextboolean(boolean bool){
		preferences.edit().putBoolean(copypastekey,bool).commit();
		if (bool == false){
			int newposition = retrieveNumber(precopypasteglobalpositionpermillekey);
			saveNumber(globalpositionpermillekeyreal,newposition);
			adjustGlobalPositionToPercentage(newposition);

		}

	}*/

	public void saveReadingCopyTextString(String text){


		helper_insertnewtextintodatabase("shared or copied text", text);


	}

	/*public boolean getReadingCopyTextOn(){
		return preferences.getBoolean(copypastekey,false);
	}*/

	public boolean getHelplinesOn(){
		return preferences.getBoolean(helplineskey,false);
	}

	protected synchronized int retrieveNumber(String variable){

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
		dbManager.open();
		Cursor cursor = dbManager.fetchwithBookID(retrieveNumber(currentbookidkey));
		if (cursor.getCount() > 0) {
			int number = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper.BOOKPOSITION));
			cursor.close();


				//	dbManager.close();
			return number;
		}
		else{
			//dbManager.close();
		return 0;}
		//int vhelper = retrieveNumber(globalpositionkey);
		//if (vhelper == 0){
		//	vhelper = 0;
		//}

		//return vhelper;
	}
	public int getGlobalPositionSeekbarValue(){


		int position = getGlobalPosition();
		int length = getTexttoReadtotalLength();
		Log.i("Settingloader", "text length is: " + length);

		float newposition = ( (float)position / (float)length) * 1000;
		int positionpermille = Math.round(newposition);

		return positionpermille;


	}

	public String getGlobalPositionPercentString(){
		int permille = getGlobalPositionSeekbarValue();
		float percentage = (float)permille/10;
		String percent = String.format("%.2f", (float)percentage) + "%";
		return percent;


	}



	public synchronized int saveGlobalPosition(int globalposition){
		//TODO getTexttoRead is to heavy on memory
		//int length = getTexttoRead().length();
		int length = getTexttoReadtotalLength();
		Log.i("Settingloader", "text length is: " + length);

		float newposition = ( (float)globalposition / (float)length) * 1000;
		int positionpermille = Math.round(newposition);
		dbManager.open();
		//TODO also add timestamp
		dbManager.updateGlobalPosition(retrieveNumber(currentbookidkey),globalposition);
		//dbManager.close();
		saveNumber(globalpositionpermillekey, positionpermille);
		return positionpermille;
		//saveNumber(globalpositionkey,position);
	}

	public int adjustGlobalPositionToPercentage(int number){
		int maxnumber = 1000;
		//TODO getTexttoRead to heavy on memory
		//int length = getTexttoRead().length();
		int length = getTexttoReadtotalLength();
		Log.i("Settingloader", "text length is: " + length);

		float newposition = length * ( (float) number / (float) maxnumber);
		int adjustedposition = Math.round(newposition);
		Log.i("Settingloader", "position is: " + adjustedposition);
		dbManager.open();
		dbManager.updateGlobalPosition(retrieveNumber(currentbookidkey),adjustedposition);
		//dbManager.close();
		//saveNumber(globalpositionkey,adjustedposition);

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
		int number = 175;
		//float totalwords = getGlobalPosition() / getGlobalPositionSeekbarValue() * 1000;
		//int output = (int) totalwords;
		dbManager.open();
		Cursor cursor = dbManager.fetchwithBookID(retrieveNumber(currentbookidkey));

		if (cursor.getCount() > 0) {
			number = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper.BOOKWPM));
			//return retrieveNumber(texttoreadtotallengthkey);
			cursor.close();



		}
		//dbManager.close();
		return number;


/*
		int vhelper = retrieveNumber(wpmkey);
		if (vhelper == 0){
			vhelper = 230;
		}

		return vhelper;*/
	}
//	public int getWordpoints(){
//
//		int vhelper = retrieveNumber(wordpoints);
//		if (vhelper == 0){
//			vhelper = 0;
//		}
//
//		return vhelper;
//
//	}
//	void saveWordpoints(int number){
//		saveNumber(wordpoints, number);
//
//
//	}

	void saveAddReadCharacters(int tickdistance){
	int formernumber = retrieveNumber(readingcharacterstatistics);
	saveNumber(readingcharacterstatistics, formernumber + tickdistance);

	}
	public int getReadCharacters(){

		int vhelper = retrieveNumber(readingcharacterstatistics);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;

	}

	void saveAddReadingTime(int milliseconds){
	int formernumber = retrieveNumber(readingtimestatistics);
	saveNumber(readingtimestatistics, formernumber + milliseconds);

	}
	public int getReadingTime(){

		int vhelper = retrieveNumber(readingtimestatistics);
		if (vhelper == 0){
			vhelper = 0;
		}

		return vhelper;

	}

	public void resetStatistics(){
		saveNumber(readingtimestatistics, 0);
		saveNumber(readingcharacterstatistics, 0);

	}

	void saveWordsPerMinuteFromSharedPreferences(){
		int vhelper = retrieveNumber(wpmkey);
		if (vhelper == 0){
			vhelper = 230;
		}
		saveWordsPerMinute(vhelper);


	}
	void saveWordsPerMinute(int number){

		dbManager.open();
		//TODO also add timestamp
		dbManager.updateWPM(retrieveNumber(currentbookidkey),number);
		//dbManager.close();
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

		/*if (getReadingCopyTextOn()) {
			newstring = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Comfort Reader/notes_copy_pasted_text.txt";

		}*/


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
		dbManager.open();

		if(preferences.contains(currentbookidkey)){
		Cursor cursor = dbManager.fetchwithBookID(retrieveNumber(currentbookidkey));

		if (cursor.getCount() > 0) {

			String path  = cursor.getString(cursor.getColumnIndex(dbManager.dbHelper.BOOKPATH));
			cursor.close();
			//dbManager.close();
			return path;
		}
		else{
		//	dbManager.close();
			return "intro";}

		}
		else {return "intro";}
	}

	public int getTexttoReadtotalLength(){

		//float totalwords = getGlobalPosition() / getGlobalPositionSeekbarValue() * 1000;
		//int output = (int) totalwords;
		dbManager.open();
		Cursor cursor = dbManager.fetchwithBookID(retrieveNumber(currentbookidkey));
		//dbManager.close();
		if (cursor.getCount() > 0) {
			int number = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper.BOOKLENGTH));
			//return retrieveNumber(texttoreadtotallengthkey);
			cursor.close();
			return number;

		}
		else{
			Resources res = thiscontext.getResources();
			String standart = res.getString(R.string.support_standarttext);
			return standart.length();}


	}
	public String getTexttoRead(){
		String text;



			int bookid = retrieveNumber(currentbookidkey);
			Log.d("settingslf", "trying to load with bookid: " + bookid);
			dbManager.open();
			Cursor cursor = dbManager.fetchwithBookID(bookid);

			if (cursor.getCount() > 0) {
				text = cursor.getString(cursor.getColumnIndex(dbManager.dbHelper.BOOKTEXT));
				Log.d("settingslf", "loading text from database with lenght: " + text.length());
				cursor.close();

			}
			else{
				Resources res = thiscontext.getResources();
				text = res.getString(R.string.support_standarttext);
				}
			//dbManager.close();





		if (text.length()<14){
			Resources res = thiscontext.getResources();
			text = res.getString(R.string.support_standarttext);
		}
		return text;
	}


	public void shiftBooks(){

		dbManager.open();
		Cursor cursor = dbManager.fetchchronological();
		//save(insertmanualkey, " ");

		int number = 0;
		while (cursor.isAfterLast() == false) {
			if (number > 14){
				int bookid = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper._ID));
				dbManager.deleteSingleRow(bookid);
			}


			number = number + 1;
			cursor.moveToNext();
		}
	//	dbManager.close();
//TODO
		//take ordered list

		//delete everything later than 10

/*
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
}*/
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
//TODO connect to SQLite Database
	public CharSequence[] getLastBooks(){
		dbManager.open();
		Cursor cursor = dbManager.fetchchronological();

		List<String> listItems = new ArrayList<String>();
		while (cursor.isAfterLast() == false) {
			String filename = getFileofPath(cursor.getString(cursor.getColumnIndex(dbManager.dbHelper.BOOKPATH)));
			int position  = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper.BOOKPOSITION));
			int length = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper.BOOKLENGTH));


			Log.i("Settingloaderlastbooks", "text length is: " + length);

			float newposition = ( (float)position / (float)length) * 1000;
			int positionpermille = Math.round(newposition);
			String permille = getPercentStringfromPermille(positionpermille);
			listItems.add(""+filename + " " + permille);

			cursor.moveToNext();

		}
	//	dbManager.close();
		final CharSequence[] entries = listItems.toArray(new CharSequence[listItems.size()]);


		/*CharSequence[] entries = { //retrieve(filepathkeyreal) + " " + retrieveNumber(globalpositionpermillekeyreal),
				getFileofPath(retrieve(filepathkey3)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey3)),
				getFileofPath(retrieve(filepathkey4)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey4)),
				getFileofPath(retrieve(filepathkey5)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey5)),
				getFileofPath(retrieve(filepathkey6)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey6)),
				getFileofPath(retrieve(filepathkey7)) + " " + getPercentStringfromPermille(retrieveNumber(globalpositionpermillekey7)) };*/
		return entries;

	}
	//TODO connecto to SQLite Database
	public CharSequence[] getLastBooksValues(){
		dbManager.open();
		Cursor cursor = dbManager.fetchchronological();

		List<String> listItems = new ArrayList<String>();
		while (cursor.isAfterLast() == false) {

			int id  = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper._ID));

			listItems.add(""+id);

			cursor.moveToNext();

		}
		final CharSequence[] entryValues = listItems.toArray(new CharSequence[listItems.size()]);
	//	dbManager.close();


		//CharSequence[] entryValues ={"2", "3", "4", "5","6"};


	return entryValues;

	}



	public void reloadSelectedBook(){ //load book after last read selection may have been changed
		String fromSlotNumber = retrieve(lastreadskey);
		int bookid = 0;
	//	Cursor cursor = dbManager.fetchwithBookID();

		try
		{
			// the String to int conversion happens here
			bookid = Integer.parseInt(fromSlotNumber);

			// print out the value after the conversion
			//System.out.println("int i = " + i);
		}
		catch (NumberFormatException nfe)
		{
			//System.out.println("NumberFormatException: " + nfe.getMessage());

		}


		if (bookid > 0) {
			saveNumber(currentbookidkey, bookid);
			saveCommitChanges();
			Log.i("settings", "aktuelle position in den dritten slot geladen " + getGlobalPositionSeekbarValue());
		}
		save(lastreadskey,"0");

		//String text;
		//String path;
		//int permille;

		//	adjustGlobalPositionToPercentage(permille);








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

	/*	Log.d("settings", "loading book global position 0: " + getGlobalPositionSeekbarValue());

		Log.d("settings", "loading book global position 1: " + getGlobalPositionSeekbarValue());
		preferences.edit().putBoolean(copypastekey,false).commit();


		Log.d("settings", "loading book global position 2: " + getGlobalPositionSeekbarValue());
*/
		helper_insertnewtextintodatabase(path, texttoread);
	}

	public void helper_insertnewcopiedtextintodatabase(String texttoread){
	//	saveCommitChanges();
	//	texttoread = retrieve(insertmanualkey);

		Resources res = thiscontext.getResources();
		String copiedfilepath = res.getString(R.string.settings_insertmanually_filepath);

		dbManager.open();
		Cursor cursor = dbManager.fetchwithBookpath(copiedfilepath);

		if (cursor.getCount() > 0) {

			while (cursor.isAfterLast() == false) {

					int bookid = cursor.getInt(cursor.getColumnIndex(dbManager.dbHelper._ID));
					dbManager.deleteSingleRow(bookid);


				cursor.moveToNext();
			}

		}


		helper_insertnewtextintodatabase(copiedfilepath,texttoread);


	}


	public void helper_insertnewtextintodatabase(String path, String texttoread){
		/*if (texttoread.length()<10){
			Resources res = thiscontext.getResources();
			texttoread = res.getString(R.string.support_standarttext);
		}*/


		int time = (int) (System.currentTimeMillis());
		Timestamp tsTemp = new Timestamp(time);
		long timevalue =  tsTemp.getTime();
		Log.d("settingslf", "loading book text length: " + texttoread.length());
		//TODO insert currentbookid above
		dbManager.open();
		long bookidlong = dbManager.insert(path,texttoread,texttoread.length(),0,timevalue,getWordsPerMinute());
		//	dbManager.close();
		int bookid = (int) bookidlong;
		saveNumber(currentbookidkey,  bookid);
		int loadedbookid = retrieveNumber(currentbookidkey);
		Log.d("settingslf", "bookid: "+ bookid + "should be same as" + loadedbookid);
		saveCommitChanges();
		//save(filepathkeyreal,retrieve(filepathkey));

		//save(texttoreadkey, texttoread);
		//initializeTexttoreadTotalLength(texttoread);
		//saveGlobalPosition(0);
		adjustGlobalPositionToPercentage(0);
		shiftBooks();


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

	/*public void loadRealSettingstoPreferences(){
		saveNumber(globalpositionpermillekey,getGlobalPositionSeekbarValue());
		save(filepathkey,getFilePath());

	}*/


}
