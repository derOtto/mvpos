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
package com.openbravo.pos.sales;

import com.nordpos.device.ticket.TicketParser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.ticket.DeviceTicketFactory;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.panels.JTicketsFinder;
import com.openbravo.pos.ticket.FindTicketsInfo;

import java.io.InputStream;

/**
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JTicketsBagTicket extends JTicketsBag {

    private static final String PRINTER_SHEMA = "/com/nordpos/templates/Schema.Printer.xsd";
    private static final String PRINT_TICKET_PREVIEW = "/com/nordpos/templates/Printer.TicketPreview.xml";
    private final DeviceTicketFactory m_TP;
    private final JTicketsBagTicketBag m_TicketsBagTicketBag;
    private final JPanelTicketEdits m_panelticketedit;
    protected DataLogicCustomers dlCustomers;
    private TicketParser m_TTP;
    private TicketParser m_TTP2;
    private TicketInfo m_ticket;
    private TicketInfo m_ticketCopy;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup buttonGroup1;
    private JButton jButton1;
    private JButton jButton2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JRadioButton jrbRefunds;
    private JRadioButton jrbSales;
    private JPanel m_jButtons;
    private JButton m_jEdit;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private JPanel m_jOptions;
    private JPanel m_jPanelTicket;
    private JButton m_jPrint;
    private JButton m_jRefund;
    private com.openbravo.editor.JEditorIntegerPositive m_jTicketEditor;
    private JLabel m_jTicketId;
    public JTicketsBagTicket(AppView app, JPanelTicketEdits panelticket) {

        super(app, panelticket);
        m_panelticketedit = panelticket;
        dlCustomers = (DataLogicCustomers) m_App.getBean(DataLogicCustomers.class.getName());

        // Inicializo la impresora...
        m_TP = new DeviceTicketFactory();

        initComponents();

        m_TicketsBagTicketBag = new JTicketsBagTicketBag(this);

        m_jTicketEditor.addEditorKeys(m_jKeys);

        // Este deviceticket solo tiene una impresora, la de pantalla
        m_jPanelTicket.add(m_TP.getDevicePrinter("1").getPrinterComponent(), BorderLayout.CENTER);
    }

    @Override
    public void activate() {

        // precondicion es que no tenemos ticket activado ni ticket en el panel
        m_ticket = null;
        m_ticketCopy = null;

        printTicket();

        m_jTicketEditor.reset();
        m_jTicketEditor.activate();

        m_panelticketedit.setActiveTicket(null, null);

        jrbSales.setSelected(true);

        m_jEdit.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.EditTicket"));
        m_jRefund.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.RefundTicket"));
        m_jPrint.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.PrintTicket"));

        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }

    @Override
    public boolean deactivate() {

        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        m_ticket = null;
        m_ticketCopy = null;
        return true;
        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }

    @Override
    public void deleteTicket() {

        if (m_ticketCopy != null) {
            // Para editar borramos el ticket anterior
            try {
                m_dlSales.deleteTicket(m_ticketCopy, m_App.getInventoryLocation());
            } catch (BasicException eData) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                msg.show(this);
            }
        }

        m_ticket = null;
        m_ticketCopy = null;
        resetToTicket();
    }

    public void canceleditionTicket() {

        m_ticketCopy = null;
        resetToTicket();
    }

    private void resetToTicket() {
        printTicket();
        m_jTicketEditor.reset();
        m_jTicketEditor.activate();
        m_panelticketedit.setActiveTicket(null, null);
    }

    @Override
    protected JComponent getBagComponent() {
        return m_TicketsBagTicketBag;
    }

    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    private void readTicket(int iTicketid, int iTickettype) {

        try {
            TicketInfo ticket = (iTicketid == -1)
                    ? m_dlSales.loadTicket(iTickettype, m_jTicketEditor.getValueInteger())
                    : m_dlSales.loadTicket(iTickettype, iTicketid);

            if (ticket == null) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notexiststicket"));
                msg.show(this);
            } else {
                m_ticket = ticket;
                m_ticketCopy = null; // se asigna al pulsar el boton de editar o devolver
                printTicket();
            }

        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadticket"), e);
            msg.show(this);
        }

        m_jTicketEditor.reset();
        m_jTicketEditor.activate();
    }

    private void printTicket() {

        // imprimo m_ticket
        try {
            m_jEdit.setEnabled(
                    m_ticket != null
                            && (m_ticket.getTicketType() == TicketInfo.RECEIPT_NORMAL || m_ticket.getTicketType() == TicketInfo.RECEIPT_REFUND)
                            && m_dlSales.isCashActive(m_ticket.getActiveCash()));
        } catch (BasicException e) {
            m_jEdit.setEnabled(false);
        }
        m_jRefund.setEnabled(m_ticket != null && m_ticket.getTicketType() == TicketInfo.RECEIPT_NORMAL);
        m_jPrint.setEnabled(m_ticket != null);

        // Este deviceticket solo tiene una impresora, la de pantalla
        m_TP.getDevicePrinter("1").reset();

        if (m_ticket == null) {
            m_jTicketId.setText(null);
        } else {
            m_jTicketId.setText(m_ticket.getName());
            InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
            InputStream template = getClass().getResourceAsStream(PRINT_TICKET_PREVIEW);
            m_TTP = new TicketParser(schema, m_TP);
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticket", m_ticket);
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        buttonGroup1 = new ButtonGroup();
        m_jOptions = new JPanel();
        m_jButtons = new JPanel();
        m_jTicketId = new JLabel();
        jButton2 = new JButton();
        m_jEdit = new JButton();
        m_jRefund = new JButton();
        m_jPrint = new JButton();
        jPanel2 = new JPanel();
        m_jPanelTicket = new JPanel();
        jPanel3 = new JPanel();
        jPanel4 = new JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel5 = new JPanel();
        jButton1 = new JButton();
        m_jTicketEditor = new com.openbravo.editor.JEditorIntegerPositive();
        jPanel1 = new JPanel();
        jrbSales = new JRadioButton();
        jrbRefunds = new JRadioButton();

        setLayout(new BorderLayout());

        m_jOptions.setLayout(new BorderLayout());

        m_jButtons.setLayout(new FlowLayout(FlowLayout.LEFT));

        m_jTicketId.setHorizontalAlignment(SwingConstants.CENTER);
        m_jTicketId.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIManager.getDefaults().getColor("Button.darkShadow")), BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTicketId.setOpaque(true);
        m_jTicketId.setPreferredSize(new Dimension(160, 25));
        m_jTicketId.setRequestFocusEnabled(false);
        m_jButtons.add(m_jTicketId);

        jButton2.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/search.png"))); // NOI18N
        jButton2.setText(AppLocal.getIntString("label.search")); // NOI18N
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.setMargin(new Insets(8, 14, 8, 14));
        jButton2.setRequestFocusEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        m_jButtons.add(jButton2);

        m_jEdit.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/edit.png"))); // NOI18N
        m_jEdit.setText(AppLocal.getIntString("button.edit")); // NOI18N
        m_jEdit.setFocusPainted(false);
        m_jEdit.setFocusable(false);
        m_jEdit.setMargin(new Insets(8, 14, 8, 14));
        m_jEdit.setRequestFocusEnabled(false);
        m_jEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditActionPerformed(evt);
            }
        });
        m_jButtons.add(m_jEdit);

        m_jRefund.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/inbox.png"))); // NOI18N
        m_jRefund.setText(AppLocal.getIntString("button.refund")); // NOI18N
        m_jRefund.setFocusPainted(false);
        m_jRefund.setFocusable(false);
        m_jRefund.setMargin(new Insets(8, 14, 8, 14));
        m_jRefund.setRequestFocusEnabled(false);
        m_jRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jRefundActionPerformed(evt);
            }
        });
        m_jButtons.add(m_jRefund);

        m_jPrint.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/printer.png"))); // NOI18N
        m_jPrint.setText(AppLocal.getIntString("button.print")); // NOI18N
        m_jPrint.setFocusPainted(false);
        m_jPrint.setFocusable(false);
        m_jPrint.setMargin(new Insets(8, 14, 8, 14));
        m_jPrint.setRequestFocusEnabled(false);
        m_jPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintActionPerformed(evt);
            }
        });
        m_jButtons.add(m_jPrint);

        m_jOptions.add(m_jButtons, BorderLayout.WEST);

        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        m_jOptions.add(jPanel2, BorderLayout.CENTER);

        add(m_jOptions, BorderLayout.NORTH);

        m_jPanelTicket.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanelTicket.setLayout(new BorderLayout());
        add(m_jPanelTicket, BorderLayout.CENTER);

        jPanel3.setLayout(new BorderLayout());

        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel4.add(m_jKeys);

        jPanel5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new GridBagLayout());

        jButton1.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png"))); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMargin(new Insets(8, 14, 8, 14));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanel5.add(jButton1, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel5.add(m_jTicketEditor, gridBagConstraints);

        jPanel4.add(jPanel5);

        jPanel3.add(jPanel4, BorderLayout.NORTH);

        buttonGroup1.add(jrbSales);
        jrbSales.setText(AppLocal.getIntString("label.sales")); // NOI18N
        jrbSales.setFocusPainted(false);
        jrbSales.setFocusable(false);
        jrbSales.setRequestFocusEnabled(false);
        jPanel1.add(jrbSales);

        buttonGroup1.add(jrbRefunds);
        jrbRefunds.setText(AppLocal.getIntString("label.refunds")); // NOI18N
        jrbRefunds.setFocusPainted(false);
        jrbRefunds.setFocusable(false);
        jrbRefunds.setRequestFocusEnabled(false);
        jPanel1.add(jrbRefunds);

        jPanel3.add(jPanel1, BorderLayout.CENTER);

        add(jPanel3, BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditActionPerformed

        m_ticketCopy = m_ticket;
        m_TicketsBagTicketBag.showEdit();
        m_panelticketedit.showCatalog();
        m_panelticketedit.setActiveTicket(m_ticket.copyTicket(), null);

    }//GEN-LAST:event_m_jEditActionPerformed

    private void m_jPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintActionPerformed

        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(PRINT_TICKET_PREVIEW);

        if (m_ticket != null) {
            m_TTP2 = new TicketParser(schema, m_App.getDeviceTicket());
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticket", m_ticket);
                script.put("local", new AppLocal());
                m_TTP2.printTicket(template, script);
            } catch (ScriptException | TicketPrinterException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"), e));
            }
        }

    }//GEN-LAST:event_m_jPrintActionPerformed

    private void m_jRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jRefundActionPerformed

        java.util.List aRefundLines = new ArrayList();

        for (int i = 0; i < m_ticket.getLinesCount(); i++) {
            TicketLineInfo newline = new TicketLineInfo(m_ticket.getLine(i));
            aRefundLines.add(newline);
        }

        m_ticketCopy = null;
        m_TicketsBagTicketBag.showRefund();
        m_panelticketedit.showRefundLines(aRefundLines);

        TicketInfo refundticket = new TicketInfo();
        refundticket.setTicketType(TicketInfo.RECEIPT_REFUND);
        refundticket.setCustomer(m_ticket.getCustomer());
        refundticket.setPayments(m_ticket.getPayments());
        m_panelticketedit.setActiveTicket(refundticket, null);

    }//GEN-LAST:event_m_jRefundActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        readTicket(-1, jrbSales.isSelected() ? 0 : 1);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed

        readTicket(-1, jrbSales.isSelected() ? 0 : 1);

    }//GEN-LAST:event_m_jKeysActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JTicketsFinder finder = JTicketsFinder.getReceiptFinder(this, m_dlSales, dlCustomers);
        finder.setVisible(true);
        FindTicketsInfo selectedTicket = finder.getSelectedCustomer();
        if (selectedTicket == null) {
            m_jTicketEditor.reset();
            m_jTicketEditor.activate();
        } else {
            readTicket(selectedTicket.getTicketId(), selectedTicket.getTicketType());
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // End of variables declaration//GEN-END:variables

}
