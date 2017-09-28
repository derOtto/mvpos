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
package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.TableRendererBasic;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.*;
import com.nordpos.device.ticket.TicketParser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import java.awt.Dimension;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JPanelCloseMoney extends JPanel implements JPanelView, BeanFactoryApp {

    private static final String PRINTER_SHEMA = "/com/nordpos/templates/Schema.Printer.xsd";
    private static final String PRINT_CLOSE_CASH = "/com/nordpos/templates/Printer.CloseCash.xml";
    private static final String PRINT_PARTIAL_CASH = "/com/nordpos/templates/Printer.PartialCash.xml";

    private AppView m_App;
    private DataLogicSystem m_dlSystem;

    private PaymentsModel m_PaymentsToClose = null;

    private TicketParser m_TTP;

    private static final Logger logger = Logger.getLogger(JPanelCloseMoney.class.getName());

    /**
     * Creates new form JPanelCloseMoney
     */
    public JPanelCloseMoney() {

        initComponents();
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {

        m_App = app;
        m_dlSystem = (DataLogicSystem) m_App.getBean(DataLogicSystem.class.getName());

        m_jTicketTable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{new FormatsPayment(), Formats.CURRENCY}));
        m_jTicketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollTableTicket.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jTicketTable.getTableHeader().setReorderingAllowed(false);
        m_jTicketTable.setRowHeight(25);
        m_jTicketTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jsalestable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{Formats.STRING, Formats.CURRENCY, Formats.CURRENCY, Formats.CURRENCY}));
        m_jsalestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollSales.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jsalestable.getTableHeader().setReorderingAllowed(false);
        m_jsalestable.setRowHeight(25);
        m_jsalestable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CloseTPV");
    }

    @Override
    public void activate() throws BasicException {
        loadData();
    }

    @Override
    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
    }

    private void loadData() throws BasicException {

        // Reset
        m_jSequence.setText(null);
        m_jMinDate.setText(null);
        m_jMaxDate.setText(null);
        m_jPrintCash.setEnabled(false);
        m_jCloseCash.setEnabled(false);
        m_jCount.setText(null); // AppLocal.getIntString("label.noticketstoclose");
        m_jCash.setText(null);

        m_jSales.setText(null);
        m_jSalesSubtotal.setText(null);
        m_jSalesTaxes.setText(null);
        m_jSalesTotal.setText(null);

        m_jTicketTable.setModel(new DefaultTableModel());
        m_jsalestable.setModel(new DefaultTableModel());

        // LoadData
        m_PaymentsToClose = PaymentsModel.loadInstance(m_App);

        // Populate Data
        m_jSequence.setText(m_PaymentsToClose.printSequence());
        m_jMinDate.setText(m_PaymentsToClose.printDateStart());
        m_jMaxDate.setText(m_PaymentsToClose.printDateEnd());

        if (m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getSales() != 0) {

            m_jPrintCash.setEnabled(true);
            m_jCloseCash.setEnabled(true);

            m_jCount.setText(m_PaymentsToClose.printPayments());
            m_jCash.setText(m_PaymentsToClose.printPaymentsTotal());

            m_jSales.setText(m_PaymentsToClose.printSales());
            m_jSalesSubtotal.setText(m_PaymentsToClose.printSalesBase());
            m_jSalesTaxes.setText(m_PaymentsToClose.printSalesTaxes());
            m_jSalesTotal.setText(m_PaymentsToClose.printSalesTotal());
        }

        m_jTicketTable.setModel(m_PaymentsToClose.getPaymentsModel());

        TableColumnModel jColumns = m_jTicketTable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(200);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(100);
        jColumns.getColumn(1).setResizable(false);

        m_jsalestable.setModel(m_PaymentsToClose.getSalesModel());

        jColumns = m_jsalestable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(200);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(100);
        jColumns.getColumn(1).setResizable(false);
    }

    private void printPayments(String report) throws TicketPrinterException {
        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(report);
        if (schema == null || template == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                m_TTP = new TicketParser(schema, m_App.getDeviceTicket());
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToClose);
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
                throw e;
            }
        }
    }

    private class FormatsPayment extends Formats {

        @Override
        protected String formatValueInt(Object value) {
            return AppLocal.getIntString("transpayment." + (String) value);
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return SwingConstants.LEFT;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        jPanel4 = new JPanel();
        jLabel11 = new JLabel();
        m_jSequence = new JTextField();
        jLabel2 = new JLabel();
        m_jMinDate = new JTextField();
        jLabel3 = new JLabel();
        m_jMaxDate = new JTextField();
        jPanel5 = new JPanel();
        m_jScrollTableTicket = new JScrollPane();
        m_jTicketTable = new JTable();
        jLabel1 = new JLabel();
        m_jCount = new JTextField();
        jLabel4 = new JLabel();
        m_jCash = new JTextField();
        jPanel6 = new JPanel();
        m_jSalesTotal = new JTextField();
        m_jScrollSales = new JScrollPane();
        m_jsalestable = new JTable();
        m_jSalesTaxes = new JTextField();
        m_jSalesSubtotal = new JTextField();
        m_jSales = new JTextField();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel12 = new JLabel();
        jLabel7 = new JLabel();
        m_jCloseCash = new JButton();
        m_jPrintCash = new JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.datestitle"))); // NOI18N

        jLabel11.setText(AppLocal.getIntString("label.sequence")); // NOI18N

        m_jSequence.setEditable(false);
        m_jSequence.setHorizontalAlignment(JTextField.RIGHT);

        jLabel2.setText(AppLocal.getIntString("Label.StartDate")); // NOI18N

        m_jMinDate.setEditable(false);
        m_jMinDate.setHorizontalAlignment(JTextField.RIGHT);

        jLabel3.setText(AppLocal.getIntString("Label.EndDate")); // NOI18N

        m_jMaxDate.setEditable(false);
        m_jMaxDate.setHorizontalAlignment(JTextField.RIGHT);

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSequence, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jMinDate, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jMaxDate, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(m_jSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_jMinDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(m_jMaxDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel5.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.paymentstitle"))); // NOI18N

        m_jScrollTableTicket.setMinimumSize(new Dimension(350, 140));
        m_jScrollTableTicket.setPreferredSize(new Dimension(350, 140));

        m_jTicketTable.setFocusable(false);
        m_jTicketTable.setIntercellSpacing(new Dimension(0, 1));
        m_jTicketTable.setRequestFocusEnabled(false);
        m_jTicketTable.setShowVerticalLines(false);
        m_jScrollTableTicket.setViewportView(m_jTicketTable);

        jLabel1.setText(AppLocal.getIntString("Label.Tickets")); // NOI18N

        m_jCount.setEditable(false);
        m_jCount.setHorizontalAlignment(JTextField.RIGHT);

        jLabel4.setText(AppLocal.getIntString("Label.Cash")); // NOI18N

        m_jCash.setEditable(false);
        m_jCash.setHorizontalAlignment(JTextField.RIGHT);

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jScrollTableTicket, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCount, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCash, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(m_jScrollTableTicket, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(m_jCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(m_jCash, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel6.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.salestitle"))); // NOI18N

        m_jSalesTotal.setEditable(false);
        m_jSalesTotal.setHorizontalAlignment(JTextField.RIGHT);

        m_jsalestable.setFocusable(false);
        m_jsalestable.setIntercellSpacing(new Dimension(0, 1));
        m_jsalestable.setRequestFocusEnabled(false);
        m_jsalestable.setShowVerticalLines(false);
        m_jScrollSales.setViewportView(m_jsalestable);

        m_jSalesTaxes.setEditable(false);
        m_jSalesTaxes.setHorizontalAlignment(JTextField.RIGHT);

        m_jSalesSubtotal.setEditable(false);
        m_jSalesSubtotal.setHorizontalAlignment(JTextField.RIGHT);

        m_jSales.setEditable(false);
        m_jSales.setHorizontalAlignment(JTextField.RIGHT);

        jLabel5.setText(AppLocal.getIntString("label.sales")); // NOI18N

        jLabel6.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N

        jLabel12.setText(AppLocal.getIntString("label.taxcash")); // NOI18N

        jLabel7.setText(AppLocal.getIntString("label.totalcash")); // NOI18N

        GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jScrollSales, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSales, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesSubtotal, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesTaxes, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSalesTotal, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(m_jScrollSales, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(m_jSales, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(m_jSalesSubtotal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(m_jSalesTaxes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(m_jSalesTotal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        m_jCloseCash.setText(AppLocal.getIntString("Button.CloseCash")); // NOI18N
        m_jCloseCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseCashActionPerformed(evt);
            }
        });

        m_jPrintCash.setText(AppLocal.getIntString("Button.PrintCash")); // NOI18N
        m_jPrintCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintCashActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(m_jPrintCash, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCloseCash)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jPrintCash)
                        .addComponent(m_jCloseCash)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jCloseCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseCashActionPerformed
        // TODO add your handling code here:
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannaclosecash"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            Date dNow = new Date();
            Session s = m_App.getSession();

            try {
                // Start transaction
                if (!s.isTransaction()) {
                    s.begin();
                }

                try {
                    // Cerramos la caja si esta pendiente de cerrar.
                    if (m_App.getActiveCashDateEnd() == null) {
                        new StaticSentence(m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING, Datas.STRING}))
                                .exec(new Object[]{dNow, m_App.getProperties().getHost(), m_App.getActiveCashIndex()});
                    }
                } catch (BasicException e) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                    msg.show(this);
                }

                try {
                    // Creamos una nueva caja
                    m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);

                    // creamos la caja activa
                    m_dlSystem.execInsertCash(
                            new Object[]{m_App.getActiveCashIndex(), m_App.getProperties().getHost(), m_App.getActiveCashSequence(), m_App.getActiveCashDateStart(), m_App.getActiveCashDateEnd()});

                    // ponemos la fecha de fin
                    m_PaymentsToClose.setDateEnd(dNow);

                    // print report
                    printPayments(PRINT_CLOSE_CASH);

                    // Mostramos el mensaje
                    JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closecashok"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);

                    // Commiting changes
                    s.commit();

                } catch (TicketPrinterException | BasicException e) {
                    s.rollback();
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                    msg.show(this);
                }

                try {
                    loadData();
                } catch (BasicException e) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("label.noticketstoclose"), e);
                    msg.show(this);
                }
            } catch (java.sql.SQLException e) {
                logger.log(Level.SEVERE, "SQL error accured while process closing ticket", e);
                try {
                    s.rollback();
                } catch (java.sql.SQLException rollbackException) {
                }
            }
        }
    }//GEN-LAST:event_m_jCloseCashActionPerformed

private void m_jPrintCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintCashActionPerformed

    // print report
    try {
        printPayments(PRINT_PARTIAL_CASH);
        // Not important errors
    } catch (TicketPrinterException e) {
    }

}//GEN-LAST:event_m_jPrintCashActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel1;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JPanel jPanel1;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JTextField m_jCash;
    private JButton m_jCloseCash;
    private JTextField m_jCount;
    private JTextField m_jMaxDate;
    private JTextField m_jMinDate;
    private JButton m_jPrintCash;
    private JTextField m_jSales;
    private JTextField m_jSalesSubtotal;
    private JTextField m_jSalesTaxes;
    private JTextField m_jSalesTotal;
    private JScrollPane m_jScrollSales;
    private JScrollPane m_jScrollTableTicket;
    private JTextField m_jSequence;
    private JTable m_jTicketTable;
    private JTable m_jsalestable;
    // End of variables declaration//GEN-END:variables

}
