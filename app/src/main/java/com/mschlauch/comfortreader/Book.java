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

    public int backgroundColor;
    public int textColor;
    public int emphasisColor;
    public String previewColorHex;

    public int hour, minute;
    public double second;
    public String textToRead = "uaeiaeu . iaeuaie uiaeiuae uia";
    public String segmenters = ".,)(:;。、としにが・，।";
    public int globalPosition = 1;
    public int globalPositionBefore = 1;
    //private int globalPositionOffset;
    public int tickPosition = 0;
    public int minBlocksize = 20;
    public int maxBlocksize = 110;
    public boolean finished;
    //public boolean begin;
    public int standardFontSize = 20;
    public int fontNumber = 1;
    public int maxCharactersPerLine = 15;
    public int brightTheme = 0;
    public int lenseEffect = 0;
    public boolean HtmlOptionActive = false;
    public ArrayList<String> loadedPreHtmlStrings;
    CountDownLatch latch = new CountDownLatch(1);
    private boolean preloadRunning = false;
    private String messageBegin = "Tap the screen to start reading";
    private String messageEnd = "  ";

    private StringSegmenter segmenter;
    private StringSegmenter segmenterWhileLoading = new StringSegmenter();
    private boolean manualInvoke = false;
    //private String loadedString;
    //private ArrayList<String> loadedTokens;
    private String tokenizedString;
    //private boolean tickTurnSegment = false;


    public Book() {
        //load
    }


    public void loadTextToRead(String text) {
        textToRead = text + "      .__________.END OF TEXT.__________.";
    }

    public void loadPreviewColorString() {
        //int red = Math.round(((float)Color.red(backgroundColor)+(float)Color.red(textColor))/2);
        //int green = Math.round(((float)Color.green(backgroundColor)+(float)Color.green(textColor))/2);
        //int blue = Math.round(((float)Color.blue(backgroundColor)+(float)Color.blue(textColor))/2);

        int centerColor = (Integer) new ArgbEvaluator().evaluate((float) 0.5, backgroundColor, textColor);
        previewColorHex = String.format("#%06X", (0xFFFFFF & centerColor));

        //previewColorHex = "#"+Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }

    public void waitOnFinishedPreload() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void invokeNextSegmentWaitingOnThread() {

        if (preloadRunning) {
            waitOnFinishedPreload();
        }

        if (manualInvoke) {//if there was a manual call, don't use segmenter cache
            segmenter = loadNextSegment();
            manualInvoke = false;
            segmenterWhileLoading.loaded = false;
        } else {
            segmenter = new StringSegmenter(segmenterWhileLoading);
            //bisheriger segmenter in Ladung wird einfach auf den jetzigen verschoben, geht schneller

            //bei manuellem Vor- oder Rückspulen wird der segmenterWhileLoading obsolet
            segmenterWhileLoading.loaded = false;//zeit an, dass neuer segmenter noch nicht geladen wurde
        }

        //derweil gebe neuen segmenter in Auftrag
        new Thread(() -> {
            preloadRunning = true;
            segmenterWhileLoading = loadNextSegment();
/*
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
            latch.countDown();
            //     preloadRunning = false;
        }).start();
        incrementGlobalPosition(segmenter.globalPositionOffset);
    }

    public void invokeNextSegment() {
/*
        if (segmenterWhileLoading.loaded == false) {
            segmenterWhileLoading = loadNextSegment();
        }
        segmenterWhileLoading = loadNextSegment();
*/
        manualInvoke = true;
        segmenter = loadNextSegment();
        segmenterWhileLoading.loaded = false;
/*
        try {
            segmenter = (StringSegmenter) segmenterWhileLoading.clone();

        } catch (CloneNotSupportedException e) {
            //	segmenter = loadNextSegment();
            segmenter = new StringSegmenter();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // segmenter = new StringSegmenter(segmenterWhileLoading);
        // segmenterWhileLoading.loaded = false;
*/

        incrementGlobalPosition(segmenter.globalPositionOffset); //muss getrennt von loadNextSegment aufgerufen werden falls segmentwhileloading verworfen wird
    }

    public String loadSegmentStringBeforePosition(int position) {
        int end = position;
        String loadedstring = loadSegmentStringAfterPosition(position - maxBlocksize, 0);
        if (loadedstring.length() < maxBlocksize - minBlocksize) {
            position = position - maxBlocksize + loadedstring.length();

        } else {
            position = position - maxBlocksize;
        }

/*
        if (globalPosition < 1) {
            begin = true;
            globalPosition = 0;
            globalPositionBefore = 0;
        } else {
            begin = false;
        }
*/

        try {
            loadedstring = textToRead.substring(position, end);
        } catch (Exception e) {
            loadedstring = "  ";
        }

        try {
            loadedstring = loadedstring.substring(loadedstring.indexOf(" "));
        } catch (Exception ignored) {

        }

        return loadedstring;
    }

    public void invokePreviousSegment() {
/*
        if (segmenterWhileLoading.loaded == false) {
            segmenterWhileLoading = loadNextSegment();
        }
*/
        manualInvoke = true;
/*
        int offset = 0;
        try {
            offset = segmenter.globalPositionOffset;
        } catch (Exception e) {
            offset = 0;
        }

        reduceglobalposition(offset + minBlocksize);
*/

        segmenter = loadPreviousSegment();

        // incrementGlobalPosition(segmenter.globalPositionOffset);
        // segmenter = loadNextSegment();

        segmenterWhileLoading.loaded = false;
    }

    public String loadSegmentStringAfterPosition(int position, int minimumBlocksize) {

        int srcEnd = position + maxBlocksize;

        String loadedstring = "";

        try {
            loadedstring = textToRead.substring(position, srcEnd);
        } catch (Exception e) {
            loadedstring = messageEnd;
        }

        int maxCutter = loadedstring.lastIndexOf(" ");
        if (maxCutter > 0) {
            loadedstring = loadedstring.substring(0, maxCutter);
        }

        //look for the next occurrence of one of the segmenters
        int positionOfSegmenter = loadedstring.length();
        //implement minimum block size feature
        String referenceString = loadedstring;

        //implementation of minimum blocksize...
        //if loadedstring is already too small, minBlocksize doesn't matter
        if (loadedstring.length() >= minimumBlocksize) {
            String placeholder = "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh";
            placeholder = placeholder.substring(0, minimumBlocksize);

            referenceString = placeholder + loadedstring.substring(minimumBlocksize);
        }

        for (int x = 0; x < segmenters.length(); x = x + 1) {

            char indexString = segmenters.charAt(x);
            //	indexString = '.';
            int p = referenceString.indexOf(indexString);

            if (p < positionOfSegmenter && p > 0) {
                positionOfSegmenter = p;
            }
        }

        //extract the actual block to read
        if (positionOfSegmenter < loadedstring.length() - 1) {
            loadedstring = loadedstring.substring(0, positionOfSegmenter + 1);
            //loadedstring = loadedstring.substring(0, positionOfSegmenter);

        } else {
            loadedstring = loadedstring; //important that he doesn't ask to cut it off beyond the length
            // s does not contain '-'
        }

        // parts = loadedstring.split(",");
        // loadedstring = parts[0];

        //kontrollieren ob das Ende erreicht ist..

        return loadedstring;

    }

    public void incrementGlobalPosition(int offset) {
        globalPositionBefore = globalPosition;
        globalPosition = globalPosition + offset;

        if (globalPositionBefore + maxBlocksize > textToRead.length()) {
            finished = true;

            if (globalPosition > textToRead.length()) {
                globalPosition = textToRead.length();
            }
        } else {
            finished = false;
        }
    }

    public StringSegmenter loadNextSegment() {

        tickPosition = 0;
        //load new String with actual position, chop some off to facilitate elaboration
        //String s = textToRead;
        String loadedstring = "";

        loadedstring = loadSegmentStringAfterPosition(globalPosition, minBlocksize);

        if (finished) {
            loadedstring = loadSegmentStringBeforePosition(textToRead.length());
        }

        tokenizedString = tokenize(loadedstring);
        //return loadedString;  // segen

        StringSegmenter segment = new StringSegmenter();

        segment.globalPositionOffset = loadedstring.length(); //oo
        if (!finished) {
            segment.previewNextString = loadSegmentStringAfterPosition(globalPosition + segment.globalPositionOffset, minBlocksize);
            segment.previewlastString = loadSegmentStringAfterPosition(globalPositionBefore, minBlocksize);
            if (globalPosition + segment.globalPositionOffset + maxBlocksize > textToRead.length()) {
                segment.previewNextString = loadSegmentStringBeforePosition(textToRead.length());
            }
        } else {
            //segment.previewNextString = loadSegmentStringBeforePosition(textToRead.length());
        }
        //this is double calculation done already before. optimize?
        // segment.segmenters = segmenters;
        // segment.tickPosition = tickPosition;
        segment.previewcolorhex = previewColorHex;
        segment.minblocksize = minBlocksize;
        segment.maxblocksize = maxBlocksize;
        segment.textcolor = textColor;
        segment.backgroundcolor = backgroundColor;
        segment.emphasiscolor = emphasisColor;

        // segment.finished = finished;
        // segment.begin = begin;
        // segment.standardFontSize = standardFontSize;
        // segment.maxCharactersPerLine = maxCharactersPerLine;
        segment.brighttheme = brightTheme;
        // segment.lenseEffect = lenseEffect;
        segment.tokenizedstring = tokenizedString;

        segment.loadedprehtmlstrings = loadedPreHtmlStrings;//calculated at book creation
        segment.loaded = true;
        segment.fontnumber = fontNumber;
        segment.helplinesinhtml = HtmlOptionActive;
        segment.loadAllTicks();

        return segment;
    }

    public StringSegmenter loadPreviousSegment() {
        tickPosition = 0;
        String loadedstring = "";
        //load new String with actual position, chop some off to facilitate elaboration
        //String s = textToRead;

        //kontrollieren ob das Ende erreicht ist..
        if (globalPosition < 1) {
            loadedstring = messageBegin;
            globalPosition = 0;
            globalPositionBefore = 0;
        } else {
            loadedstring = loadSegmentStringBeforePosition(globalPositionBefore);
            globalPosition = globalPositionBefore;
            globalPositionBefore = globalPositionBefore - loadedstring.length();
        }

        tokenizedString = tokenize(loadedstring);

        StringSegmenter segment = new StringSegmenter();
        segment.previewcolorhex = previewColorHex;
        segment.globalPositionOffset = loadedstring.length();
        segment.previewNextString = loadSegmentStringAfterPosition(globalPosition, minBlocksize);
        segment.previewlastString = loadSegmentStringBeforePosition(globalPositionBefore); //this is double calculation done already before. optimize?

        segment.textcolor = textColor;
        segment.backgroundcolor = backgroundColor;
        segment.emphasiscolor = emphasisColor;
        segment.helplinesinhtml = HtmlOptionActive;
        // segment.segmenters = segmenters;
        // segment.tickPosition = tickPosition;
        segment.minblocksize = minBlocksize;
        segment.maxblocksize = maxBlocksize;
        // segment.finished = finished;
        // segment.begin = begin;
        // segment.standardFontSize = standardFontSize;
        // segment.maxCharactersPerLine = maxCharactersPerLine;
        segment.brighttheme = brightTheme;
        // segment.lenseEffect = lenseEffect;
        segment.tokenizedstring = tokenizedString;
        segment.loadedprehtmlstrings = loadedPreHtmlStrings;//calculated at book creation
        segment.loaded = true;

        //	int radius = 20;
        //   if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
        //    	radius = 5;
        // }
        //	segmenter.radius = 5;

        //	segmenter.loadAllPreHtmls();

        segment.loadAllTicks();// zum buffern
        //get positionofnextblock

        //update globalPosition

        return segment;
    }

    public void updateGlobalPosition(double percent) {
        double position = percent * textToRead.length() / 1000;
        globalPosition = (int) Math.round(position);
        //(int) Math.round(position);
    }

    public int calculateProgress(int progressBarMax) {
        double position = (float) globalPosition / textToRead.length();
        position = position * progressBarMax;

        int newposition = (int) position;
        // newposition = (int) position;

        return newposition;
    }

    public String tokenize(String test) {
        // String test = "";
        // test.replaceAll(System.getProperty ("line.separator")," ");

        StringBuilder localtokenizedstring = new StringBuilder(" ");
        // ArrayList<String> tokens = new ArrayList<String>();

        //no breakups...¿
        Scanner tokenize;
        // String newline = "\n";
        // String newline = "<br>";
        String newline = "◜";

        // tokenize = new Scanner(loadedString); // tokenize = new Scanner(test);
        // while (tokenize.hasNext()) {
        // tokens.add(tokenize.next());
        // test = test + tokenize.next() + " ";
        //}

        test = test.replaceAll("-\n", "");
        test = test.replaceAll("\n", " ");

        int maxcharacters = maxCharactersPerLine;//actually its characters...

        for (int numberi = 0; numberi < test.length(); numberi++) {
            String addstring = test.substring(numberi, numberi + 1);
            localtokenizedstring.append(addstring);
            if (numberi > maxcharacters && addstring.equals(" ")) {
                localtokenizedstring.append(newline);

                maxcharacters = numberi + maxCharactersPerLine;
            }
        }
        localtokenizedstring.append("   ");
/*
        loadedTokens = tokens;
        Iterator<String> it = loadedTokens.iterator();
        while (it.hasNext()) {
            tokenizedString = "" + it.toString();
        }
*/

        // tokenize= null;
        return localtokenizedstring.toString();
    }

    public String getSegmentOutput(int tick) {
        Log.i("Book", "is asking segmenter for tick" + tick);
        //	return segmenter.getsegmenthtml(tick);
        return segmenter.loadSegmentOutput(tick); //wenn wir buffern wollen
    }

    public String getSegmentOutputNextTick(int tickdistance) {
        if (tokenizedString == null) {
            invokeNextSegment();
        }

        //	String output = "";
        int position = tickPosition;
        if (segmenter.maxticks < position) {
            position = segmenter.maxticks;
        }

        String output = getSegmentOutput(position);
        tickPosition = tickPosition + tickdistance;

        if (segmenter.maxticks - 2 < tickPosition) {
            // tickTurnSegment = false;
            // output = getSegmentOutput (tickPosition - 1);
            // output = getSegmentOutput(tickPosition);

            invokeNextSegmentWaitingOnThread();
            Log.i("Book", "is self-invoking next segment thread at position:" + globalPosition);
            // preloadRunning = true;

            // invokeNextSegment();
            // tickPosition = 0;

        } else {
            // this is here, because during the tick turn it should stay there a while for the interpunctuation
        }
        return output;
    }

    public void loadAllPreHtmls() {

        int radius = 20;

        loadedPreHtmlStrings = new ArrayList<>();

        for (int i = 0; i < radius; i++) {
            String insert = getPreHtml(i, radius);
            loadedPreHtmlStrings.add(insert);
            Log.i("Book", "prehtml" + i + insert + "fontsize" + standardFontSize);
        }
        // loadedhtmlstrings.add("");
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

    public String getPreHtml(int distance, int radius) {
        int r1 = Color.red(textColor);
        int g1 = Color.green(textColor);
        int b1 = Color.blue(textColor);

        int r2 = Color.red(emphasisColor);
        int g2 = Color.green(emphasisColor);
        int b2 = Color.blue(emphasisColor);

        int red = Math.round(((float) r1 + (float) r2) / 2);
        int green = Math.round(((float) g1 + (float) g2) / 2);
        int blue = Math.round(((float) b1 + (float) b2) / 2);

        String hextextcolor = "#" + getHexSingleColor(r1) + getHexSingleColor(g1) + getHexSingleColor(b1);
        String hexfocuscolor = "#" + getHexSingleColor(r2) + getHexSingleColor(g2) + getHexSingleColor(b2);
        // String hexcentercolor = "#" + getHexSingleColor(red) + getHexSingleColor(green) + getHexSingleColor(blue);

        int centercolor = (Integer) new ArgbEvaluator().evaluate((float) 0.5, emphasisColor, textColor);
        String hexcentercolor = String.format("#%06X", (0xFFFFFF & centercolor));

        float fraction = (float) distance / (float) radius;

        int newcolor = (Integer) new ArgbEvaluator().evaluate(fraction, emphasisColor, textColor);
        String colorname = String.format("#%06X", (0xFFFFFF & newcolor));


        // bcolorvalue = b2 + (b1-b2) *distance/radius;
        // String bhex = Integer.toHexString(bcolor);String rhex = Integer.toHexString(rcolor);String ghex = Integer.toHexString(gcolor);

/*
        int bcolor = Math.round(bcolorvalue);
        if (bcolor > 255) {
            bcolor = 255;
        }

        float rcolorvalue = r2 + (r1 - r2) * distance / radius;
        int rcolor = Math.round(rcolorvalue);
        if (rcolor > 255) {
            rcolor = 255;
        }

        float gcolorvalue = g2 + (g1 - g2) * distance / radius;
        int gcolor = Math.round(gcolorvalue);
        if (rcolor > 255) {
            bcolor = 255;
        }
*/

        // String colorname = "rgb(" + rcolor + "," + gcolor + "," + bcolor + ")";
        // Log.i("StringSegmenter as Book", "prehtml load" + colorname);

        // String colorname = "#" + rhex + ghex + bhex;
        //if (abstand == 0) {colorname = "rgb(255,0,0)";}

        String prehtml; // = "<span style=\"font-size:"+fontsize+"px ; color:"+colorname+";\">";
        prehtml = "<font color=\"" + colorname + "\">";

        //if (distance < 5){prehtml = prehtml + "<u>"+ "" ;}
        //else if (distance < 10){prehtml = prehtml + ""+ "" ;}
        if (distance < 2) {
            prehtml = "<font size=\"" + "50" + "\" color=\"" + emphasisColor + "\" >";
        }

        if (distance < 1) {
            prehtml = "<font size=\"" + "50" + "\" color=\"" + hexcentercolor + "\" >";
        }

        //if (distance < 1) {prehtml = "<font size=\"" + "50" + "\" color=\"" + hextextcolor + "\" >";}

        return prehtml;
    }
}
