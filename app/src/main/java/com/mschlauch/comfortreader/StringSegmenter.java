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

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

public class StringSegmenter {


    //   	public String segmentors =".,)(:;";

    public int backgroundcolor;
    public int textcolor;
    public int emphasiscolor;
    public String previewcolorhex;
    //public String hextextcolor;

    public int r1 = 255;
    public int g1 = 255;
    public int b1 = 255;

    public int r2 = 150;
    public int g2 = 150;
    public int b2 = 0;


    public int fontnumber = 1;
    public boolean helplinesinhtml = false;
    //    public int tickposition = 0;
    public int minblocksize = 20;
    public int maxblocksize = 110;
    //    public boolean finished;
    //    public boolean begin;
    //    public int standartfontsize = 15;
    //    public int maxcharactersperline = 15;
    public int brighttheme = 0;
    //    public int lenseeffect = 0;
    public int globalpositionoffset = 0;
    public boolean loaded = false;
    public int radius = 19;
    public String previewnextString = "";
    public String previewlastString = "";
    public ArrayList<String> loadedprehtmlstrings;
    public String tokenizedstring;
    public int maxticks = 0;
    private String loadedstring;
    private ArrayList<String> loadedhtmlstrings;
    private int saltaitickinbuffer = 1; //If this variable is more than 1, only half the ticks get calculated and visualized (possible option on low performance devices)
    private boolean tickturnsegment = false;

    public StringSegmenter(StringSegmenter point) {

        //	this.segmentors = point.segmentors;
        //	this.tickposition = point.tickposition;
        this.minblocksize = point.minblocksize;
        this.maxblocksize = point.maxblocksize;
        //	this.finished = point.finished;
        //	this.begin = point.begin;
        //	this.standartfontsize = point.standartfontsize;
        //	this. maxcharactersperline = point.maxcharactersperline;
        this.brighttheme = point.brighttheme;
        //	this.lenseeffect = point.lenseeffect;
        this.globalpositionoffset = point.globalpositionoffset + 0;
        this.loaded = point.loaded;
        this.radius = point.radius;
        this.loadedstring = point.loadedstring;
        this.tokenizedstring = point.tokenizedstring + "";
        this.tickturnsegment = point.tickturnsegment;
        this.maxticks = point.maxticks;

        try {
            this.loadedprehtmlstrings = new ArrayList<>(point.loadedprehtmlstrings);
        } catch (Exception ignored) {
        }

        try {
            this.loadedhtmlstrings = new ArrayList<>(point.loadedhtmlstrings);
        } catch (Exception ex) {
            this.loadallticks();
        }


    }

    public StringSegmenter() {

        // TODO Auto-generated constructor stub
    }

    //	    public StringSegmenter()
//	    {
//	       globalposition=0;
//	    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void loadallticks() {
        //loadallprehtmls(); //for some reason we have to load them twice?

        Log.i("StringSegmenter", "start loadingallticks");


        getsegmenthtml(0);//load maxticks
        loadedhtmlstrings = new ArrayList<>();
        int saltaticker = 0;
        for (int i = 0; i < maxticks + 1; i++) {
            saltaticker = saltaticker + 1;
            if (saltaticker == saltaitickinbuffer) { //segment has ended
                saltaticker = 0;
                loadedhtmlstrings.add(getsegmenthtml(i));
            }
            //  Log.i("MyActivity", "loadingallticks in loop" + i);

        }
        Log.i("StringSegmenter", "all ticks loaded: total:" + loadedhtmlstrings.size());

    }


    public String loadsegmentoutput(int tick) {
        //return loadedhtmlstrings.get(tick);

        int newtick = Math.round(tick / saltaitickinbuffer);
        String output = "";
        try {
            output = loadedhtmlstrings.get(newtick);
        } catch (Exception ex) {
            // Display exception.
            output = getsegmenthtml(tick);
        }
        return output;

    }


    public String getsegmenthtml(int tick) {


        int r1 = Color.red(textcolor);
        int g1 = Color.green(textcolor);
        int b1 = Color.blue(textcolor);

        int r2 = Color.red(emphasiscolor);
        int g2 = Color.green(emphasiscolor);
        int b2 = Color.blue(emphasiscolor);


        String hextextcolor = "#" + Integer.toHexString(r1) + Integer.toHexString(g1) + Integer.toHexString(b1);


        //   Log.i("StringSegmenter", "heavy string rendering");
        StringBuilder allhtml = new StringBuilder();
        int ihelper = 0;
        int mind = tick - radius;
        if (mind < 0) {
            mind = 0;
        }
        ihelper = mind;
        //ihelper variable signals the right distance to center

        //first, render the stuff that doesn't have special colors
        String anteriore = tokenizedstring.substring(0, mind);
        anteriore = anteriore.replaceAll("◜", "");

        anteriore = "<font color=\"" + hextextcolor + "\" >" + anteriore + "</font>";


        allhtml.append(anteriore);
        int max = tick + radius;
        if (max > tokenizedstring.length() - 2) {
            max = tokenizedstring.length() - 2;
        }
        int rest = tokenizedstring.length() - 2 - max;
        String posteriore = tokenizedstring.substring(max, max + rest);
        posteriore = posteriore.replaceAll("◜", "");
        posteriore = "<font color=\"" + hextextcolor + "\" >" + posteriore + "</font>";

        int feinheitsgrad = 1; //1, if bigger then 1 groups of letter will be processed at once instead of single letters

        //loop over all emphasized letters
        for (int numberi = mind; numberi < max; numberi = numberi + feinheitsgrad) {

            String obj = tokenizedstring.substring(numberi, numberi + feinheitsgrad);
            if (obj.contains("◜")) {
                obj = obj.replaceAll("◜", "");
            }


            String prehtml = "";
            String backhtml = "";

            int abstand = ihelper - tick;
            if (abstand < 0) {
                abstand = (abstand) * (-1);
            }
            if (abstand < radius + 1) {

                try {
                    prehtml = loadedprehtmlstrings.get(abstand);
                } catch (Exception ex) {
                    // Display exception.
                    prehtml = "<span style=\"font-size:" + 17 + "px ; color:white" + ";\">";

                }
                //  Log.i("stringsegmenter", "prehtml gotten from array" + arraysize + " " + abstand + " inhalt:" + prehtml);


                backhtml = "</font>";
            }


            allhtml.append(prehtml);
            allhtml.append(obj);
            allhtml.append(backhtml);


            ihelper = ihelper + 1;

        }

        ihelper = ihelper + rest;
        allhtml.append(posteriore);

        maxticks = ihelper;

        String result = allhtml.toString();

        String prefixdiv = "";
		/*  String fontfilename = "";
		  
		  if (fontnumber == 0){
			  fontfilename = "Crimson-Roman.ttf";
		  }
		  else if (fontnumber == 1){
			  fontfilename = "LiberationSans-Regular.ttf";
		  }
		  else if (fontnumber == 2){
			  fontfilename = "LiberationMono-Regular.ttf";
		  }*/

        String backgroundimagename = "";


        String anfang;
        anfang = "";


        String colorstandart = "rgb(" + r1 + "," + g1 + "," + b1 + ")";

        //implemented helplines as optional function 12112019
        String packagedpreviewnext = "";
        String packagedpreviewlast = "";
        if (helplinesinhtml) {

            packagedpreviewnext = "<br><font color=\"" + previewcolorhex + "\" >" + previewnextString + "</font>";
            packagedpreviewlast = "<font color=\"" + previewcolorhex + "\" >" + previewlastString + "</font><br>";
        }

        result = anfang + "<body><div  style=\"color:" + hextextcolor + ";" + "px ;text-align: center ;" + prefixdiv + "\">" + packagedpreviewlast + result + "" + packagedpreviewnext + "</div>" + "</body></html>";
        // Spanned htmlText = Html.fromHtml(allhtml);
        return result;

    }


}
