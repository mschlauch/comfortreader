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

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;


public class Book {

    public int backgroundcolor;
    public int textcolor;
    public int emphasiscolor;
    public String previewcolorhex;

    public int hour, minute;
    public double second;
    public String texttoread = "uaeiaeu . iaeuaie uiaeiuae uia";
    public String segmentors = ".,)(:;。、としにが・，।";
    public int globalposition = 1;
    public int globalpositionbefore = 1;
    //	private int globalpositionoffset;
    public int tickposition = 0;
    public int minblocksize = 20;
    public int maxblocksize = 110;
    public boolean finished;
    //   public boolean begin;
    public int standartfontsize = 20;
    public int fontnumber = 1;
    public int maxcharactersperline = 15;
    public int brighttheme = 0;
    public int lenseeffect = 0;
    public boolean htmloptionactive = false;
    public ArrayList<String> loadedprehtmlstrings;
    CountDownLatch latch = new CountDownLatch(1);
    private boolean preloadrunning = false;
    private String messagebegin = "tap the screen to start reading";
    private String messageend = "  ";

    private StringSegmenter segmenter;
    private StringSegmenter segmenterwhileloading = new StringSegmenter();
    private boolean manualinvoke = false;
    //    private String loadedstring;
    //    private ArrayList<String> loadedtokens;
    private String tokenizedstring;
    //    private boolean tickturnsegment = false;


    public Book() {
        //load

    }


    public void loadTexttoRead(String text) {
        texttoread = text + "      .__________.END OF TEXT.__________";
    }

    public void loadPreviewcolorString() {
        //int red = Math.round(((float)Color.red(backgroundcolor)+(float)Color.red(textcolor))/2);
        //int green = Math.round(((float)Color.green(backgroundcolor)+(float)Color.green(textcolor))/2);
        //int blue = Math.round(((float)Color.blue(backgroundcolor)+(float)Color.blue(textcolor))/2);


        int centercolor = (Integer) new ArgbEvaluator().evaluate((float) 0.5, backgroundcolor, textcolor);
        previewcolorhex = String.format("#%06X", (0xFFFFFF & centercolor));

        //previewcolorhex = "#"+Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);


    }

    public void waitonfinishedpreload() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void invokenextsegmentwaitingonthread() {

        if (preloadrunning) {
            waitonfinishedpreload();
        }


        if (manualinvoke) {//if there was a manual call, don't use segmenter cach
            segmenter = loadnextsegment();
            manualinvoke = false;
            segmenterwhileloading.loaded = false;
        } else {
            segmenter = new StringSegmenter(segmenterwhileloading);
            //bisheriger segmenter in Ladung wird einfach auf den jetzigen verschoben, geht schneller

            //bei manuellem Vor- oder Rückspulen wird der segmenterwhileloading obsolet
            segmenterwhileloading.loaded = false;//zeit an, dass neuer segmenter noch nicht geladen wurde
        }


        //derweil gebe neuen segmenter in Auftrag
        new Thread(() -> {
            preloadrunning = true;
            segmenterwhileloading = loadnextsegment();
            /*try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            latch.countDown();
            //     preloadrunning = false;
        }).start();
        incrementglobalposition(segmenter.globalpositionoffset);
    }

    public void invokenextsegment() {
        //	if (segmenterwhileloading.loaded == false){segmenterwhileloading = loadnextsegment();
        //		}
        //segmenterwhileloading = loadnextsegment();
        manualinvoke = true;
        segmenter = loadnextsegment();
        segmenterwhileloading.loaded = false;
        //			try {
//				segmenter = (StringSegmenter) segmenterwhileloading.clone();
//				
//			
//			} catch (CloneNotSupportedException e) {
//			//	segmenter = loadnextsegment();
//				segmenter = new StringSegmenter();
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

        //	segmenter = new StringSegmenter(segmenterwhileloading);
        //	segmenterwhileloading.loaded = false;


        incrementglobalposition(segmenter.globalpositionoffset); //muss getrennt von loadnextsegment aufgerufen werden falls segmentwhileloading verworfen wird
//

    }

    public String loadStringofsegmentbeforePosition(int position) {
        int end = position;
        String loadedstring = loadStringofsegmentafterPosition(position - maxblocksize, 0);
        if (loadedstring.length() < maxblocksize - minblocksize) {
            position = position - maxblocksize + loadedstring.length();

        } else {
            position = position - maxblocksize;
        }
/*

		if (globalposition < 1){
			begin = true;
			globalposition = 0;
			globalpositionbefore = 0;
		}
		else {
			begin = false;
		}
*/


        try {
            loadedstring = texttoread.substring(position, end);
        } catch (Exception e) {
            loadedstring = "  ";
        }


        try {
            loadedstring = loadedstring.substring(loadedstring.indexOf(" "));
        } catch (Exception ignored) {

        }


        return loadedstring;
    }

    public void invokeprevioussegment() {
        //	if (segmenterwhileloading.loaded == false){segmenterwhileloading = loadnextsegment();
        //			}
        manualinvoke = true;
		/*int offset = 0;
		try {offset = segmenter.globalpositionoffset;

		}
		catch (Exception e){offset = 0;}

		reduceglobalposition(offset + minblocksize);*/

        segmenter = loadprevioussegment();


        //	incrementglobalposition(segmenter.globalpositionoffset);
        //	segmenter = loadnextsegment();


        segmenterwhileloading.loaded = false;


    }


    public String loadStringofsegmentafterPosition(int position, int minimumblocksize) {

        int srcEnd = position + maxblocksize;

        String loadedstring = "";


        try {
            loadedstring = texttoread.substring(position, srcEnd);
        } catch (Exception e) {
            loadedstring = messageend;
        }

        int maxcutter = loadedstring.lastIndexOf(" ");
        if (maxcutter > 0) {
            loadedstring = loadedstring.substring(0, maxcutter);
        }


        //look for the next occurance of one of the segmentors
        int positionofsegmenter = loadedstring.length();
        //implement minimum block size feature
        String referencestring = loadedstring;

        //implementation of minimum blocksize...
        //if loadedstring already too small, minblocksize doesn't matter
        if (loadedstring.length() >= minimumblocksize) {
            String placeholder = "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh";
            placeholder = placeholder.substring(0, minimumblocksize);

            referencestring = placeholder + loadedstring.substring(minimumblocksize);
        }

        for (int x = 0; x < segmentors.length(); x = x + 1) {

            char indexstring = segmentors.charAt(x);
            //	indexstring = '.';
            int p = referencestring.indexOf(indexstring);

            if (p < positionofsegmenter && p > 0) {
                positionofsegmenter = p;
            }

        }


        //extract the actual block to read
        if (positionofsegmenter < loadedstring.length() - 1) {
            loadedstring = loadedstring.substring(0, positionofsegmenter + 1);
            //loadedstring = loadedstring.substring(0, positionofsegmenter);

        } else {
            loadedstring = loadedstring; //important that he doesn't ask to cut it off beyond the length
            // s does not contain '-'
        }

//
//	    	parts = loadedstring.split(",");
//	    	loadedstring = parts[0];

        //kontrollieren ob das Ende erreicht ist..

        return loadedstring;

    }

    public void incrementglobalposition(int offset) {
        globalpositionbefore = globalposition;
        globalposition = globalposition + offset;


        if (globalpositionbefore + maxblocksize > texttoread.length()) {


            finished = true;

            if (globalposition > texttoread.length()) {
                globalposition = texttoread.length();
            }
        } else {
            finished = false;
        }


    }


    public StringSegmenter loadnextsegment() {


        tickposition = 0;
        //load new String with actual position, chop some off to facilitate elaboration
        //String s = texttoread;
        String loadedstring = "";

        loadedstring = loadStringofsegmentafterPosition(globalposition, minblocksize);

        if (finished) {
            loadedstring = loadStringofsegmentbeforePosition(texttoread.length());
        }


        tokenizedstring = tokenize(loadedstring);
        //return loadedstring;  // segen


        StringSegmenter segment = new StringSegmenter();

        segment.globalpositionoffset = loadedstring.length(); //oo
        if (!finished) {
            segment.previewnextString = loadStringofsegmentafterPosition(globalposition + segment.globalpositionoffset, minblocksize);
            segment.previewlastString = loadStringofsegmentafterPosition(globalpositionbefore, minblocksize);
            if (globalposition + segment.globalpositionoffset + maxblocksize > texttoread.length()) {
                segment.previewnextString = loadStringofsegmentbeforePosition(texttoread.length());
            }
        } else {
            // segment.previewnextString = loadStringofsegmentbeforePosition(texttoread.length());
        }
        //this is double calculation done already before. optimize?
        //	segment.segmentors = segmentors;
        //	segment.tickposition = tickposition;
        segment.previewcolorhex = previewcolorhex;
        segment.minblocksize = minblocksize;
        segment.maxblocksize = maxblocksize;
        segment.textcolor = textcolor;
        segment.backgroundcolor = backgroundcolor;
        segment.emphasiscolor = emphasiscolor;


        //	segment.finished = finished;
        //	segment.begin = begin;
        //segment.standartfontsize = standartfontsize;
        //segment.maxcharactersperline = maxcharactersperline;
        segment.brighttheme = brighttheme;
        //segment.lenseeffect = lenseeffect;
        segment.tokenizedstring = tokenizedstring;

        segment.loadedprehtmlstrings = loadedprehtmlstrings;//calculated at book creation
        segment.loaded = true;
        segment.fontnumber = fontnumber;
        segment.helplinesinhtml = htmloptionactive;
        segment.loadallticks();


        return segment;
    }

    public StringSegmenter loadprevioussegment() {
        tickposition = 0;
        String loadedstring = "";
        //load new String with actual position, chop some off to facilitate elaboration
        //String s = texttoread;


        //kontrollieren ob das Ende erreicht ist..
        if (globalposition < 1) {
            loadedstring = messagebegin;
            globalposition = 0;
            globalpositionbefore = 0;
        } else {
            loadedstring = loadStringofsegmentbeforePosition(globalpositionbefore);
            globalposition = globalpositionbefore;
            globalpositionbefore = globalpositionbefore - loadedstring.length();


        }


        tokenizedstring = tokenize(loadedstring);


        StringSegmenter segment = new StringSegmenter();
        segment.previewcolorhex = previewcolorhex;
        segment.globalpositionoffset = loadedstring.length();
        segment.previewnextString = loadStringofsegmentafterPosition(globalposition, minblocksize);
        segment.previewlastString = loadStringofsegmentbeforePosition(globalpositionbefore); //this is double calculation done already before. optimize?

        segment.textcolor = textcolor;
        segment.backgroundcolor = backgroundcolor;
        segment.emphasiscolor = emphasiscolor;
        segment.helplinesinhtml = htmloptionactive;
        //	segment.segmentors = segmentors;
        //	segment.tickposition = tickposition;
        segment.minblocksize = minblocksize;
        segment.maxblocksize = maxblocksize;
        //	segment.finished = finished;
        //	segment.begin = begin;
        //	segment.standartfontsize = standartfontsize;
        //	segment.maxcharactersperline = maxcharactersperline;
        segment.brighttheme = brighttheme;
        //	segment.lenseeffect = lenseeffect;
        segment.tokenizedstring = tokenizedstring;
        segment.loadedprehtmlstrings = loadedprehtmlstrings;//calculated at book creation
        segment.loaded = true;

        //	int radius = 20;
        //   if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
        //    	radius = 5;
        // }
        //	segmenter.radius = 5;


        //	segmenter.loadallprehtmls();

        segment.loadallticks();// zum buffern
        //get positionofnextblock


        //update globalposition


        return segment;


    }

    public void updateglobalposition(double percent) {

        double position = percent * texttoread.length() / 1000;
        globalposition = (int) Math.round(position);
        //(int) Math.round(position);

    }

    public int calculateprogress(int progressbarmax) {


        double position = (float) globalposition / texttoread.length();
        position = position * progressbarmax;


        int newposition = (int) position;
        // newposition = (int) position;

        return newposition;


    }

    public String tokenize(String test) {
        //String test = "";
        //	test.replaceAll(System.getProperty ("line.separator")," ");

        StringBuilder localtokenizedstring = new StringBuilder(" ");
        //ArrayList<String> tokens = new ArrayList<String>();

        //no breakups...¿
        Scanner tokenize;
        //String newline = "\n";
        //	String newline = "<br>";
        String newline = "◜";

        //	tokenize = new Scanner(loadedstring);//	tokenize = new Scanner(test);
        //while (tokenize.hasNext()) {
        //  tokens.add(tokenize.next());
        //	test = test + tokenize.next() + " ";
        //}
        test = test.replaceAll("-\n", "");

        test = test.replaceAll("\n", " ");

        int maxcharacters = maxcharactersperline;//actually its characters...

        for (int numberi = 0; numberi < test.length(); numberi++) {
            String addstring = test.substring(numberi, numberi + 1);
            localtokenizedstring.append(addstring);
            if (numberi > maxcharacters && addstring.equals(" ")) {
                localtokenizedstring.append(newline);

                maxcharacters = numberi + maxcharactersperline;
            }

        }
        localtokenizedstring.append("   ");
        //	loadedtokens = tokens;
        //   	Iterator<String> it = loadedtokens.iterator();
        //  	while(it.hasNext()){
        //		tokenizedstring = "" + it.toString();

        //}

        //	tokenize= null;
        return localtokenizedstring.toString();
    }

    public String getsegmentoutput(int tick) {

        Log.i("Book", "is asking segmenter for tick" + tick);
        //	return segmenter.getsegmenthtml(tick);
        return segmenter.loadsegmentoutput(tick); //wenn wir buffern wollen

    }

    public String getsegmentoutputNextTick(int tickdistance) {
        if (tokenizedstring == null) {
            invokenextsegment();


        }

        //	String output = "";
        int position = tickposition;
        if (segmenter.maxticks < position) {
            position = segmenter.maxticks;
        }

        String output = getsegmentoutput(position);
        tickposition = tickposition + tickdistance;

        if (segmenter.maxticks - 2 < tickposition) {
            //	tickturnsegment = false;
            //	output = getsegmentoutput (tickposition - 1);
            //	output = getsegmentoutput(tickposition);

            invokenextsegmentwaitingonthread();
            Log.i("Book", "is self-invoking next segment thread at position:" + globalposition);
            //preloadrunning = true;


            //invokenextsegment();
            //tickposition = 0;


        } else {
            // this is here, because during the tick turn it should stay there a while for the interpunctuation


        }

        return output;

    }

    public void loadallprehtmls() {

        int radius = 20;

        loadedprehtmlstrings = new ArrayList<>();

        for (int i = 0; i < radius; i++) {
            String insert = getprehtml(i, radius);
            loadedprehtmlstrings.add(insert);
            Log.i("Book", "prehtml" + i + insert + "fontsize" + standartfontsize);
        }
        //     loadedhtmlstrings.add("");
    }

    public float balancedAverageTwoNumbers(int number1, int number2) {
        float low;
        float high;

        if (number1 > number2) {
            low = (float) number2;
            high = (float) number1;
        } else {
            high = (float) number2;
            low = (float) number1;
        }


        return low + ((high - low) / 2);
    }

    private String getHexSingleColor(int color) {
        if (color > 255) {
            color = 255;
        }
        return Integer.toHexString(color);

    }


    public String getprehtml(int distance, int radius) {
        int r1 = Color.red(textcolor);
        int g1 = Color.green(textcolor);
        int b1 = Color.blue(textcolor);

        int r2 = Color.red(emphasiscolor);
        int g2 = Color.green(emphasiscolor);
        int b2 = Color.blue(emphasiscolor);


        int red = Math.round(((float) r1 + (float) r2) / 2);
        int green = Math.round(((float) g1 + (float) g2) / 2);
        int blue = Math.round(((float) b1 + (float) b2) / 2);


        String hextextcolor = "#" + getHexSingleColor(r1) + getHexSingleColor(g1) + getHexSingleColor(b1);
        String hexfocuscolor = "#" + getHexSingleColor(r2) + getHexSingleColor(g2) + getHexSingleColor(b2);
        //String hexcentercolor = "#" + getHexSingleColor(red) + getHexSingleColor(green) + getHexSingleColor(blue);

        int centercolor = (Integer) new ArgbEvaluator().evaluate((float) 0.5, emphasiscolor, textcolor);
        String hexcentercolor = String.format("#%06X", (0xFFFFFF & centercolor));

        float fraction = (float) distance / (float) radius;

        int newcolor = (Integer) new ArgbEvaluator().evaluate(fraction, emphasiscolor, textcolor);
        String colorname = String.format("#%06X", (0xFFFFFF & newcolor));


        //	bcolorvalue = b2 + (b1-b2) *distance/radius;
        //	String bhex = Integer.toHexString(bcolor);String rhex = Integer.toHexString(rcolor);String ghex = Integer.toHexString(gcolor);
/*
		int bcolor = Math.round(bcolorvalue);
		if (bcolor > 255){bcolor = 255;}


		float rcolorvalue =  r2 + (r1-r2) *distance/radius;
		int rcolor = Math.round(rcolorvalue);
		if (rcolor > 255){rcolor = 255;}



		float gcolorvalue =  g2 + (g1-g2) *distance/radius;
		int gcolor = Math.round(gcolorvalue);
		if (rcolor > 255){bcolor = 255;}
		*/

        //	String colorname = "rgb(" + rcolor +"," + gcolor + "," + bcolor + ")";
        //	Log.i("StringSegmenter as Book", "prehtml load" + colorname);


        //String colorname = "#"+rhex + ghex + bhex ;
        // if (abstand == 0){colorname="rgb(255,0,0)";}

        String prehtml; // = "<span style=\"font-size:"+fontsize+"px ; color:"+colorname+";\">";
        prehtml = "<font color=\"" + colorname + "\">";

        //if (distance < 5){prehtml = prehtml + "<u>"+ "" ;}
        //else if (distance < 10){prehtml = prehtml + ""+ "" ;}
        if (distance < 2) {
            prehtml = "<font size=\"" + "50" + "\" color=\"" + emphasiscolor + "\" >";
        }

        if (distance < 1) {
            prehtml = "<font size=\"" + "50" + "\" color=\"" + hexcentercolor + "\" >";
        }


        //if (distance < 1){prehtml = "<font size=\"" + "50" + "\" color=\""+ hextextcolor + "\" >";}

        return prehtml;
    }


}
