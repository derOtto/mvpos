package com.nordpos.device.receiptprinter.escpos;

import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.ticket.DeviceTicketFactory;
import com.nordpos.device.writter.Writter;

import java.awt.image.BufferedImage;

public abstract class Codes {

    /**
     * Creates a new instance of Codes
     */
    public Codes() {
    }

    public abstract byte[] getInitSequence();

    public abstract byte[] getSize0();

    public abstract byte[] getSize1();

    public abstract byte[] getSize2();

    public abstract byte[] getSize3();

    public abstract byte[] getBoldSet();

    public abstract byte[] getBoldReset();

    public abstract byte[] getUnderlineSet();

    public abstract byte[] getUnderlineReset();

    public abstract byte[] getOpenDrawer();

    public abstract byte[] getCutReceipt();

    public abstract byte[] getNewLine();

    public abstract byte[] getImageHeader();

    public abstract int getImageWidth();

    /**
     *
     * @param writterFile file to write the bytes
     * @param type only works with ean13 (feel free to implement)
     * @param position position
     * @param code barcode
     */
    public void printBarcode(Writter writterFile, String type, String position, String code) {

        if (DevicePrinter.BARCODE_EAN13.equals(type)) {

            writterFile.write(getNewLine());

            writterFile.write(ESCPOS.BAR_HEIGHT);
            if (DevicePrinter.POSITION_NONE.equals(position)) {
                writterFile.write(ESCPOS.BAR_POSITIONNONE);
            } else {
                writterFile.write(ESCPOS.BAR_POSITIONDOWN);
            }
            writterFile.write(ESCPOS.BAR_HRIFONT1);
            writterFile.write(ESCPOS.BAR_CODE02);
            writterFile.write(DeviceTicketFactory.transNumber(DeviceTicketFactory.alignBarCode(code, 13).substring(0, 12)));
            writterFile.write(new byte[]{0x00});

            writterFile.write(getNewLine());
        }
    }

    public byte[] transImage(BufferedImage image) {

        CenteredImage centeredimage = new CenteredImage(image, getImageWidth());

        // Imprimo los par\u00e1metros en cu\u00e1druple
        int iWidth = (centeredimage.getWidth() + 7) / 8; // n\u00famero de bytes
        int iHeight = centeredimage.getHeight();

        // Array de datos
        byte[] bData = new byte[getImageHeader().length + 4 + iWidth * iHeight];

        // Comando de impresion de imagen
        System.arraycopy(getImageHeader(), 0, bData, 0, getImageHeader().length);

        int index = getImageHeader().length;

        // Dimension de la imagen
        bData[index++] = (byte) (iWidth % 256);
        bData[index++] = (byte) (iWidth / 256);
        bData[index++] = (byte) (iHeight % 256);
        bData[index++] = (byte) (iHeight / 256);

        // Raw data
        int iRGB;
        int p;
        for (int i = 0; i < centeredimage.getHeight(); i++) {
            for (int j = 0; j < centeredimage.getWidth(); j = j + 8) {
                p = 0x00;
                for (int d = 0; d < 8; d++) {
                    p = p << 1;
                    if (centeredimage.isBlack(j + d, i)) {
                        p = p | 0x01;
                    }
                }

                bData[index++] = (byte) p;
            }
        }
        return bData;
    }

    protected class CenteredImage {

        private BufferedImage image;
        private int width;

        public CenteredImage(BufferedImage image, int width) {
            this.image = image;
            this.width = width;
        }

        public int getHeight() {
            return image.getHeight();
        }

        public int getWidth() {
            return width;
        }

        public boolean isBlack(int x, int y) {

            int centeredx = x + (image.getWidth() - width) / 2;
            if (centeredx < 0 || centeredx >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                return false;
            } else {
                int rgb = image.getRGB(centeredx, y);

                int gray = (int) (0.30 * ((rgb >> 16) & 0xff) +
                        0.59 * ((rgb >> 8) & 0xff) +
                        0.11 * (rgb & 0xff));

                return gray < 128;
            }
        }
    }
}
