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
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import com.nordpos.device.ticket.TicketParser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;

import java.awt.Component;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class PaymentsEditor extends javax.swing.JPanel implements EditorRecord {

    private static final String PRINTER_SHEMA = "/com/nordpos/templates/Schema.Printer.xsd";
    private static final String PRINT_PAYMENT_EDIT = "/com/nordpos/templates/Printer.PaymentEdit.xml";

    private final ComboBoxValModel m_ReasonModel;
    private final AppView m_App;
    private final DataLogicSystem m_dlSystem;
    private String m_sId;
    private String m_sPaymentId;
    private Date datenew;
    private TicketParser m_TTP;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private com.openbravo.editor.JEditorCurrency jTotal;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JComboBox m_jreason;

    public PaymentsEditor(AppView oApp, DirtyManager dirty) {

        m_App = oApp;
        m_dlSystem = (DataLogicSystem) m_App.getBean(DataLogicSystem.class.getName());

        initComponents();

        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(new PaymentReasonPositive("cashin", AppLocal.getIntString("transpayment.cashin")));
        m_ReasonModel.add(new PaymentReasonNegative("cashout", AppLocal.getIntString("transpayment.cashout")));
        m_jreason.setModel(m_ReasonModel);

        jTotal.addEditorKeys(m_jKeys);

        m_jreason.addActionListener(dirty);
        jTotal.addPropertyChangeListener("Text", dirty);

        writeValueEOF();
    }

    @Override
    public void writeValueEOF() {
        m_sId = null;
        m_sPaymentId = null;
        datenew = null;
        setReasonTotal(null, null);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_sId = null;
        m_sPaymentId = null;
        datenew = null;
        setReasonTotal("cashin", null);
        m_jreason.setEnabled(true);
        jTotal.setEnabled(true);
        jTotal.activate();
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] payment = (Object[]) value;
        m_sId = (String) payment[0];
        datenew = (Date) payment[2];
        m_sPaymentId = (String) payment[3];
        setReasonTotal(payment[4], payment[5]);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] payment = (Object[]) value;
        m_sId = (String) payment[0];
        datenew = (Date) payment[2];
        m_sPaymentId = (String) payment[3];
        setReasonTotal(payment[4], payment[5]);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
        jTotal.activate();
    }

    @Override
    public Object createValue() throws BasicException {
        try {
            printPaymentReason(new PaymentReasonRecord(datenew == null ? new Date() : datenew, m_ReasonModel.getSelectedKey().toString(), jTotal.getDoubleValue()));
            Object[] payment = new Object[6];
            payment[0] = m_sId == null ? UUID.randomUUID().toString() : m_sId;
            payment[1] = m_App.getActiveCashIndex();
            payment[2] = datenew == null ? new Date() : datenew;
            payment[3] = m_sPaymentId == null ? UUID.randomUUID().toString() : m_sPaymentId;
            payment[4] = m_ReasonModel.getSelectedKey();
            PaymentReason reason = (PaymentReason) m_ReasonModel.getSelectedItem();
            Double dtotal = jTotal.getDoubleValue();
            payment[5] = reason == null ? dtotal : reason.addSignum(dtotal);
            return payment;
        } catch (TicketPrinterException ex) {
            Logger.getLogger(PaymentsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
    }

    private void setReasonTotal(Object reasonfield, Object totalfield) {

        m_ReasonModel.setSelectedKey(reasonfield);

        PaymentReason reason = (PaymentReason) m_ReasonModel.getSelectedItem();

        if (reason == null) {
            jTotal.setDoubleValue((Double) totalfield);
        } else {
            jTotal.setDoubleValue(reason.positivize((Double) totalfield));
        }
    }

    private void printPaymentReason(PaymentReasonRecord reasonrec) throws TicketPrinterException {
        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(PRINT_PAYMENT_EDIT);
        if (schema == null || template == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            m_TTP = new TicketParser(schema, m_App.getDeviceTicket());
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payment", reasonrec);
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
                Logger.getLogger(PaymentsEditor.class.getName()).log(Level.SEVERE, null, e);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
                Logger.getLogger(PaymentsEditor.class.getName()).log(Level.SEVERE, null, e);
                throw e;
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

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        m_jreason = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTotal = new com.openbravo.editor.JEditorCurrency();
        jPanel2 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();

        setLayout(new java.awt.BorderLayout());

        jLabel5.setText(AppLocal.getIntString("label.paymentreason")); // NOI18N

        m_jreason.setFocusable(false);

        jLabel3.setText(AppLocal.getIntString("label.paymenttotal")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                           .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                           .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                           .addComponent(m_jreason, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                           .addComponent(jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                           .addComponent(jLabel5)
                                                                           .addComponent(m_jreason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                           .addComponent(jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                           .addComponent(jLabel3))
                                                    .addContainerGap(316, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(m_jKeys, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private static abstract class PaymentReason implements IKeyed {

        private final String m_sKey;
        private final String m_sText;

        public PaymentReason(String key, String text) {
            m_sKey = key;
            m_sText = text;
        }

        @Override
        public Object getKey() {
            return m_sKey;
        }

        public abstract Double positivize(Double d);

        public abstract Double addSignum(Double d);

        @Override
        public String toString() {
            return m_sText;
        }
    }

    private static class PaymentReasonPositive extends PaymentReason {

        public PaymentReasonPositive(String key, String text) {
            super(key, text);
        }

        @Override
        public Double positivize(Double d) {
            return d;
        }

        @Override
        public Double addSignum(Double d) {
            if (d == null) {
                return null;
            } else if (d < 0.0) {
                return -d;
            } else {
                return d;
            }
        }
    }

    private static class PaymentReasonNegative extends PaymentReason {

        public PaymentReasonNegative(String key, String text) {
            super(key, text);
        }

        @Override
        public Double positivize(Double d) {
            return d == null ? null : -d;
        }

        @Override
        public Double addSignum(Double d) {
            if (d == null) {
                return null;
            } else if (d > 0.0) {
                return -d;
            } else {
                return d;
            }
        }
    }
    // End of variables declaration//GEN-END:variables
}
