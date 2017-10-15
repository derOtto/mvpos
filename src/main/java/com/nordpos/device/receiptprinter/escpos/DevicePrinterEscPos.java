package com.nordpos.device.receiptprinter.escpos;

import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.translator.UnicodeTranslator;
import com.nordpos.device.translator.UnicodeTranslatorInt;
import com.nordpos.device.writter.Writter;
import com.nordpos.device.writter.WritterFile;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class DevicePrinterEscPos implements DevicePrinter {

    private final Writter writterFile;
    private Codes codes;
    private UnicodeTranslator translator;


    public DevicePrinterEscPos(WritterFile writterFile) throws TicketPrinterException {
        this.writterFile = writterFile;
        codes = new CodesEpson();
        translator = new UnicodeTranslatorInt();

        //Initialization
        writterFile.init(ESCPOS.INIT);
        writterFile.write(ESCPOS.SELECT_PRINTER);
        writterFile.init(codes.getInitSequence());
        writterFile.write(translator.getCodeTable());
        writterFile.flush();
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
        writterFile.write(ESCPOS.SELECT_PRINTER);
        writterFile.write(codes.transImage(image));
    }

    @Override
    public void printBarCode(String type, String position, String code) {
        writterFile.write(ESCPOS.SELECT_PRINTER);
        codes.printBarcode(writterFile, type, position, code);
    }

    @Override
    public void beginLine(Integer iTextSize) {
        writterFile.write(ESCPOS.SELECT_PRINTER);

        if (iTextSize == DevicePrinter.SIZE_0) {
            writterFile.write(codes.getSize0());
        } else if (iTextSize == DevicePrinter.SIZE_1) {
            writterFile.write(codes.getSize1());
        } else if (iTextSize == DevicePrinter.SIZE_2) {
            writterFile.write(codes.getSize2());
        } else if (iTextSize == DevicePrinter.SIZE_3) {
            writterFile.write(codes.getSize3());
        } else {
            writterFile.write(codes.getSize0());
        }
    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
        writterFile.write(ESCPOS.SELECT_PRINTER);
        /*writterFile.write(new byte[]{ 0x1B, 0x74, 40});
        try {
            writterFile.write(sText.getBytes("ISO-8859-15"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        if (bBold) {
            writterFile.write(codes.getBoldSet());
        }
        if (sUnderlineType.equals("true")) {
            writterFile.write(codes.getUnderlineSet());
        }
        writterFile.write(translator.transString(sText));
        if (sUnderlineType.equals("true")) {
            writterFile.write(codes.getUnderlineReset());
        }
        if (bBold) {
            writterFile.write(codes.getBoldReset());
        }
    }

    @Override
    public void endLine() {
        writterFile.write(ESCPOS.SELECT_PRINTER);
        writterFile.write(codes.getNewLine());
    }

    @Override
    public void endReceipt() {
        writterFile.write(ESCPOS.SELECT_PRINTER);

        writterFile.write(codes.getNewLine());
        writterFile.write(codes.getNewLine());
        writterFile.write(codes.getNewLine());
        writterFile.write(codes.getNewLine());
        writterFile.write(codes.getNewLine());

        writterFile.write(codes.getCutReceipt());
        writterFile.flush();
    }

    @Override
    public void cutPaper(boolean complete) {
        /*
        writterFile.write(ESCPOS.SELECT_PRINTER);
        writterFile.write(codes.getCutReceipt());
        writterFile.flush();
        */
    }

    @Override
    public void openDrawer() {
        writterFile.write(ESCPOS.SELECT_PRINTER);
        writterFile.write(codes.getOpenDrawer());
        writterFile.flush();
    }
}
