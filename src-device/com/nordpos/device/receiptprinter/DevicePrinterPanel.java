//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.nordpos.device.receiptprinter;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class DevicePrinterPanel extends javax.swing.JPanel implements DevicePrinter {

    private final String m_sName;

    private final JTicketContainer m_jTicketContainer;
    private BasicTicket m_ticketcurrent;

    public DevicePrinterPanel() {
        initComponents();

        m_sName = "ReceiptPrinter.Screen";

        m_ticketcurrent = null;

        m_jTicketContainer = new JTicketContainer();
        m_jScrollView.setViewportView(m_jTicketContainer);
    }

    @Override
    public String getPrinterName() {
        return m_sName;
    }

    @Override
    public String getPrinterDescription() {
        return null;
    }

    @Override
    public JComponent getPrinterComponent() {
        return this;
    }

    @Override
    public void reset() {
        m_ticketcurrent = null;
        m_jTicketContainer.removeAllTickets();
        m_jTicketContainer.repaint();
    }

    // INTERFAZ PRINTER 2
    @Override
    public void beginReceipt() {
        m_ticketcurrent = new BasicTicketForScreen();
    }

    @Override
    public void printImage(BufferedImage image) {
        m_ticketcurrent.printImage(image);
    }

    @Override
    public void printBarCode(String type, String position, String code) {
        m_ticketcurrent.printBarCode(type, position, code);
    }

    @Override
    public void beginLine(Integer iTextSize) {
        m_ticketcurrent.beginLine(iTextSize);
    }

    @Override
    public void printText(Integer iCharacterSize, String sUnderlineType, Boolean bBold, String sText) {
        m_ticketcurrent.printText(iCharacterSize, sText);
    }

    @Override
    public void endLine() {
        m_ticketcurrent.endLine();
    }

    @Override
    public void endReceipt() {
        m_jTicketContainer.addTicket(new JTicket(m_ticketcurrent));
        m_ticketcurrent = null;
    }

    @Override
    public void openDrawer() {
        // Una simulacion
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void cutPaper(boolean complete) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jScrollView = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(m_jScrollView, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane m_jScrollView;
    // End of variables declaration//GEN-END:variables

}
