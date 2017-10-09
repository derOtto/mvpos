package com.nordpos.device.receiptprinter;

import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.writter.Writter;
import com.nordpos.device.writter.WritterFile;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class DevicePrinterEscPos implements DevicePrinter {

    private final byte[] bEndOfLine;

    private final Writter writterFile;

    public DevicePrinterEscPos(WritterFile writterFile, byte[] bEndOfLine) throws TicketPrinterException {
        this.writterFile = writterFile;
        this.bEndOfLine = bEndOfLine;
    }

    @Override
    public String getPrinterName() {
        return "label.ReceiptPrinterEscPos";
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

    }

    @Override
    public void printImage(BufferedImage image) {

    }

    @Override
    public void printBarCode(String type, String position, String code) {

    }

    @Override
    public void beginLine(Integer iTextSize) {

    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
        //TODO: implement
    }

    @Override
    public void endLine() {
        writterFile.write(bEndOfLine);
    }

    @Override
    public void endReceipt() {

    }

    @Override
    public void cutPaper(boolean complete) {

    }

    @Override
    public void openDrawer() {

    }
}
