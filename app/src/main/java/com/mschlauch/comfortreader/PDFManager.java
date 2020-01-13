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

import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PDFManager {

    private PDFParser parser;
    private PDFTextStripper pdfStripper;
    private PDDocument pdDoc;
    private COSDocument cosDoc;

    private String Text;
    private String filePath;
    private File file;

    public PDFManager() {

    }

    public String ToText() throws IOException {

        this.pdfStripper = null;
        this.pdDoc = null;
        this.cosDoc = null;

        file = new File(filePath);


        PDDocument document = null;
        try {
            document = PDDocument.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }


        pdDoc = document;
        pdfStripper = new PDFTextStripper();
        pdDoc.getNumberOfPages();
        pdfStripper.setStartPage(1);
        // pdfStripper.setEndPage(3);
        pdfStripper.setEndPage(pdDoc.getNumberOfPages());

      /* int max = pdDoc.getNumberOfPages();

       StringBuilder textbuild = new StringBuilder();
       for (int i = 1; i < max; i++) {
           pdfStripper.setStartPage(i);
           pdfStripper.setEndPage(i+1);
           textbuild.append(pdfStripper.getText(pdDoc));
           Log.d("PDFManager", "is loading pdf at page" + i);
       }*/
        // reading text from page 1 to 10
        // if you want to get text from full pdf file use this code
        // pdfStripper.setEndPage(pdDoc.getNumberOfPages());
        // Text = textbuild.toString();
        Text = pdfStripper.getText(pdDoc);
        return Text;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


}
