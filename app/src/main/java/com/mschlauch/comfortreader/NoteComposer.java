package com.mschlauch.comfortreader;


import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by michael on 02.02.18.
 */

public class NoteComposer {


    // public String savednotebook;
    public String prefix;
    public String filename;
    public String note;
    public String composednotebook;
    private String position;


    public String getExtract(SettingsLoader settingsload) {

        // savednotebook = settingsload.getCurrentNotes();//TODO call settingsloader instead
        position = settingsload.getGlobalPositionPercentString();

        String texttoread = settingsload.getTexttoRead();//TODO ""
        int globalposition = settingsload.getGlobalPosition();

        //String pathtofile = ("" + filepath + "/" + filename);

        String datetext = " " + DateFormat.format("yyyy-MM-dd hh:mm", new Date());


        int textlength = texttoread.length();
        int begin = globalposition - 100 - settingsload.getMaxBlockSize();
        int end = globalposition + 100;
        if (begin < 1) {
            begin = 0;
        }
        if (end > textlength || end < 0) {
            end = textlength;
        }

        String preextract = "empty";
        try {
            preextract = texttoread.substring(begin, end);
        } catch (IndexOutOfBoundsException ignored) {

        }


        //get extracted text as words
        textlength = preextract.length();
        begin = 0;
        end = 0;
        begin = preextract.indexOf(" ");
        end = preextract.lastIndexOf(" ");
        if (begin < 1) {
            begin = 0;
        }
        if (end > preextract.length() || end < 0) {
            end = preextract.length();
        }

        String extract = " ";

        try {
            extract = preextract.substring(begin, end);
        } catch (IndexOutOfBoundsException ignored) {

        }

        extract = extract.replaceAll("-\n", "");

        extract = extract.replaceAll("\n", " ");

        extract = "[…]" + extract + "[…]";
        return extract;


    }

    public String getPrefix(SettingsLoader settingsload) {

        // savednotebook = settingsload.getCurrentNotes();//TODO call settingsloader instead
        position = settingsload.getGlobalPositionPercentString();

        String texttoread = settingsload.getTexttoRead();//TODO ""
        int globalposition = settingsload.getGlobalPosition();
        String filepath = settingsload.getFilePath();//TODO ""
        filename = settingsload.getFileofPath(filepath);

        //String pathtofile = ("" + filepath + "/" + filename);

        String datetext = " " + DateFormat.format("yyyy-MM-dd hh:mm", new Date());
/*

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

        extract = "[…]" + extract + "[…]";*/

        String extract;
        extract = getExtract(settingsload);
        //linebreak bereinigung
        //extract.replace(System.getProperty ("line.separator"), "");
        //geht nicht TODO line.separator bereinigen...später

        //	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // 	position = preferences.getString("positiontextfornote","");

        //position = "position";
        //current position
        //percentage

        prefix = "\n____________________\nQUOTATION:" + "\nFile:" + filepath + "\nPosition: " + position + "\nDate:" + datetext + "\nOriginal text:" + "\n \"" + extract + "\" \n\n" + "COMMENT:\n//";
        return prefix;

    }

    public String getcomposedNote(String note, SettingsLoader settingsload) {
        prefix = getPrefix(settingsload);
        return composednotebook = settingsload.getCurrentNotes() + "" + prefix + note + "";

    }
}
