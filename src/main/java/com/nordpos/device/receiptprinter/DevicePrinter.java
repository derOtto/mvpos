/**
 * NORD POS is a fork of Openbravo POS.
 * <p>
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
 * <p>
 * This file is part of NORD POS.
 * <p>
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.device.receiptprinter;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public interface DevicePrinter {

    public static final int SIZE_0 = 0;
    public static final int SIZE_1 = 1;
    public static final int SIZE_2 = 2;
    public static final int SIZE_3 = 3;

    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_UNDERLINE = 2;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;

    public static final String BARCODE_EAN8 = "EAN8";
    public static final String BARCODE_EAN13 = "EAN13";
    public static final String BARCODE_CODE39 = "CODE39";
    public static final String BARCODE_CODE128 = "CODE128";
    public static final String BARCODE_DATAMATRIX = "DATAMATRIX";
    public static final String BARCODE_QRCODE = "QRCODE";

    public static final String POSITION_TOP = "top";
    public static final String POSITION_BOTTOM = "bottom";
    public static final String POSITION_NONE = "none";

    // INTERFAZ DESCRIPCION
    public String getPrinterName();

    public String getPrinterDescription();

    public JComponent getPrinterComponent();

    public void reset();

    public void beginReceipt();

    public void printImage(BufferedImage image);

    public void printBarCode(String type, String position, String code);

    public void beginLine(Integer iTextSize);

    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText);

    public void endLine();

    public void endReceipt();

    public void cutPaper(boolean complete);

    public void openDrawer();
}
