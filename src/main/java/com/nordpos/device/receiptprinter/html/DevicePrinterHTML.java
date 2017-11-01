package com.nordpos.device.receiptprinter.html;

import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.writter.WritterFile;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class DevicePrinterHTML implements DevicePrinter {

    private String id = LocalDateTime.now().toString().replaceAll("[:.]", "-");
    private WritterFile writterFile;
    private final String edgeLine = "##############################################";
    private StringBuilder lineText = new StringBuilder();


    public DevicePrinterHTML(WritterFile writterFile) {
        this.writterFile = writterFile;
    }

    public DevicePrinterHTML(WritterFile writterFile, String id) {
        this.id = id;
        this.writterFile = writterFile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWritterFile(WritterFile writterFile) {
        this.writterFile = writterFile;
    }

    @Override
    public String getPrinterName() {
        return null;
    }

    @Override
    public String getPrinterDescription() {
        return null;
    }

    @Override
    public JComponent getPrinterComponent() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void beginReceipt() {
        writterFile.write("<div id=\"" + id + "\" class=\"receipt\" onclick=\"del('" + id + "')\">" + System.getProperty("line.separator") + edgeLine + "</br>" + System.getProperty("line.separator"));
    }

    @Override
    public void printImage(BufferedImage image) {
        System.out.println("image not working for now"); //TODO: implement image
    }

    @Override
    public void printBarCode(String type, String position, String code) {
        System.out.println("barcode not working for now"); //TODO: implement barcode
    }

    @Override
    public void beginLine(Integer iTextSize) {
        lineText.setLength(0); //clears the stringbuilder
        lineText.append("# ");
    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
        if (bBold)
            lineText.append("<b>").append(sText).append("</b>");
        else
            lineText.append(sText);
    }

    @Override
    public void endLine() {
        while (lineText.length() < 44)
            lineText.append(" ");
        lineText.append(" #</br>").append(System.getProperty("line.separator"));
        writterFile.write(lineText.toString());
    }

    @Override
    public void endReceipt() {
        writterFile.write(edgeLine + "</br>" + System.getProperty("line.separator") + "</div>" + System.getProperty("line.separator"));
    }

    @Override
    public void cutPaper(boolean complete) {
    }

    @Override
    public void openDrawer() {
    }
}
