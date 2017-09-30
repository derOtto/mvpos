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
package com.openbravo.pos.sales.shared;

import com.openbravo.pos.ticket.TicketInfo;

import java.util.*;
import javax.swing.*;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.forms.*;

public class JTicketsBagShared extends JTicketsBag {

    private String m_sCurrentTicket = null;
    private DataLogicReceipts dlReceipts = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JButton m_jDelTicket;
    private JButton m_jListTickets;
    private JButton m_jNewTicket;

    /**
     * Creates new form JTicketsBagShared
     */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);

        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");

        initComponents();
    }

    public void activate() {

        // precondicion es que no tenemos ticket activado ni ticket en el panel
        m_sCurrentTicket = null;
        selectValidTicket();

        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));

        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }

    public boolean deactivate() {

        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        saveCurrentTicket();

        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);

        return true;

        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }

    public void deleteTicket() {
        m_sCurrentTicket = null;
        selectValidTicket();
    }

    protected JComponent getBagComponent() {
        return this;
    }

    protected JComponent getNullComponent() {
        return new JPanel();
    }

    private void saveCurrentTicket() {

        // save current ticket, if exists,
        if (m_sCurrentTicket != null) {
            try {
                dlReceipts.insertSharedTicket(m_sCurrentTicket, m_panelticket.getActiveTicket());
                checkLayaways();
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }

    private void setActiveTicket(String id) throws BasicException {

        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null) {
            // Does not exists ???
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            m_panelticket.setActiveTicket(ticket, null);
        }
        checkLayaways();
        // END TRANSACTION
    }

    private void checkLayaways() throws BasicException {
        List<SharedTicketInfo> listST = dlReceipts.getSharedTicketList();
        if (!listST.isEmpty() && listST.size() > 0) {
            m_jListTickets.setVisible(true);
            m_jListTickets.setEnabled(true);
            m_jListTickets.setText(Integer.toString(listST.size()));
        } else {
            m_jListTickets.setVisible(false);
            m_jListTickets.setEnabled(false);
        }
    }

    private void selectValidTicket() {

        try {
            List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            if (l.size() == 0) {
                newTicket();
            } else {
                setActiveTicket(l.get(0).getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
            newTicket();
        }
    }

    private void newTicket() {

        saveCurrentTicket();

        TicketInfo ticket = new TicketInfo();
        m_sCurrentTicket = UUID.randomUUID().toString(); // m_fmtid.format(ticket.getId());
        m_panelticket.setActiveTicket(ticket, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        m_jNewTicket = new JButton();
        m_jDelTicket = new JButton();
        m_jListTickets = new JButton();

        setLayout(new java.awt.BorderLayout());

        m_jNewTicket.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/document-new.png"))); // NOI18N
        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jNewTicket);

        m_jDelTicket.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/list-remove.png"))); // NOI18N
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jDelTicket);

        m_jListTickets.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/unsortedList.png"))); // NOI18N
        m_jListTickets.setFocusPainted(false);
        m_jListTickets.setFocusable(false);
        m_jListTickets.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jListTickets.setRequestFocusEnabled(false);
        m_jListTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListTicketsActionPerformed(evt);
            }
        });
        jPanel1.add(m_jListTickets);

        add(jPanel1, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jListTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListTicketsActionPerformed

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();

                    JTicketsBagSharedList listDialog = JTicketsBagSharedList.newJDialog(JTicketsBagShared.this);
                    String id = listDialog.showTicketsList(l);

                    if (id != null) {
                        saveCurrentTicket();
                        setActiveTicket(id);
                    }
                } catch (BasicException e) {
                    new MessageInf(e).show(JTicketsBagShared.this);
                    newTicket();
                }
            }
        });

    }//GEN-LAST:event_m_jListTicketsActionPerformed

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            deleteTicket();
        }

    }//GEN-LAST:event_m_jDelTicketActionPerformed

    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed

        newTicket();

    }//GEN-LAST:event_m_jNewTicketActionPerformed
    // End of variables declaration//GEN-END:variables

}
