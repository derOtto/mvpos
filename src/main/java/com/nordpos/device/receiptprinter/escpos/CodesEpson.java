package com.nordpos.device.receiptprinter.escpos;

public class CodesEpson extends Codes {

    public static final byte[] BOLD_SET = {0x1B, 0x45, 0x01};
    public static final byte[] BOLD_RESET = {0x1B, 0x45, 0x00};
    public static final byte[] UNDERLINE_SET = {0x1B, 0x2D, 0x01};
    public static final byte[] UNDERLINE_RESET = {0x1B, 0x2D, 0x00};
    private static final byte[] INITSEQUENCE = {};
    private static final byte[] CHAR_SIZE_0 = {0x1D, 0x21, 0x00};
    private static final byte[] CHAR_SIZE_1 = {0x1D, 0x21, 0x01};
    private static final byte[] CHAR_SIZE_2 = {0x1D, 0x21, 0x30};
    private static final byte[] CHAR_SIZE_3 = {0x1D, 0x21, 0x31};
    private static final byte[] OPEN_DRAWER = {0x1B, 0x70, 0x00, 0x32, -0x06};
    private static final byte[] PARTIAL_CUT_1 = {0x1B, 0x6D};
    private static final byte[] IMAGE_HEADER = {0x1D, 0x76, 0x30, 0x03};
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return

    /**
     * Creates a new instance of CodesEpson
     */
    public CodesEpson() {
    }

    public byte[] getInitSequence() {
        return INITSEQUENCE;
    }

    public byte[] getSize0() {
        return CHAR_SIZE_0;
    }

    public byte[] getSize1() {
        return CHAR_SIZE_1;
    }

    public byte[] getSize2() {
        return CHAR_SIZE_2;
    }

    public byte[] getSize3() {
        return CHAR_SIZE_3;
    }

    public byte[] getBoldSet() {
        return BOLD_SET;
    }

    public byte[] getBoldReset() {
        return BOLD_RESET;
    }

    public byte[] getUnderlineSet() {
        return UNDERLINE_SET;
    }

    public byte[] getUnderlineReset() {
        return UNDERLINE_RESET;
    }

    public byte[] getOpenDrawer() {
        return OPEN_DRAWER;
    }

    public byte[] getCutReceipt() {
        return PARTIAL_CUT_1;
    }

    public byte[] getNewLine() {
        return NEW_LINE;
    }

    public byte[] getImageHeader() {
        return IMAGE_HEADER;
    }

    public int getImageWidth() {
        return 256;
    }
}
