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

import com.openbravo.basic.BasicException;
import com.openbravo.pos.catalog.CatalogSelector;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JTicketCatalogLines extends javax.swing.JPanel {

    private AppView m_App;
    private JRefundLines m_reflines;
    private CatalogSelector m_catalog;

    /**
     * Creates new form JTicketCatalogLines
     */
    public JTicketCatalogLines(AppView app, JPanelTicketEdits jTicketEdit, PropertiesConfig panelconfig) {

        m_App = app;
        DataLogicSystem dlSystem = null;
        DataLogicSales dlSales = null;
        dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");

        initComponents();

        m_reflines = new JRefundLines(dlSystem, jTicketEdit);
        add(m_reflines, "reflines");

        m_catalog = new JCatalog(dlSales, panelconfig);
        m_catalog.getComponent().setPreferredSize(new Dimension(0, 245));
        // m_catalog.addActionListener(new CatalogListener());        
        add(m_catalog.getComponent(), "catalog");
    }

    public void showCatalog() {
        showView("catalog");
    }

    public void loadCatalog() throws BasicException {
        m_catalog.loadCatalog(m_App);
    }

    public void addActionListener(ActionListener l) {
        m_catalog.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        m_catalog.addActionListener(l);
    }

    public void showRefundLines(List aRefundLines) {
        // anado las lineas de refund
        m_reflines.setLines(aRefundLines);
        showView("reflines");
    }

    private void showView(String sView) {
        CardLayout cl = (CardLayout) (this.getLayout());
        cl.show(this, sView);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new CardLayout());

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
