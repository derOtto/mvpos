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
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.ticket.ProductFilterSales;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.ProductRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * @author adrianromero
 */
public class JProductFinder extends JDialog {

    public final static int PRODUCT_ALL = 0;
    public final static int PRODUCT_NORMAL = 1;
    public final static int PRODUCT_AUXILIAR = 2;
    private ProductInfoExt m_ReturnProduct;
    private ListProvider lpr;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jButton3;
    private JList jListProducts;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JButton jcmdCancel;
    private JButton jcmdOK;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private JPanel m_jProductSelect;

    /**
     * Creates new form JProductFinder
     */
    private JProductFinder(Frame parent, boolean modal) {
        super(parent, modal);
    }


    /**
     * Creates new form JProductFinder
     */
    private JProductFinder(Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    public static ProductInfoExt showMessage(Component parent, DataLogicSales dlSales) {
        return showMessage(parent, dlSales, PRODUCT_ALL);
    }

    public static ProductInfoExt showMessage(Component parent, DataLogicSales dlSales, int productsType) {

        Window window = getWindow(parent);

        JProductFinder myMsg;
        if (window instanceof Frame) {
            myMsg = new JProductFinder((Frame) window, true);
        } else {
            myMsg = new JProductFinder((Dialog) window, true);
        }
        return myMsg.init(dlSales, productsType);
    }

    private ProductInfoExt init(DataLogicSales dlSales, int productsType) {

        initComponents();

        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));

        //ProductFilter jproductfilter = new ProductFilter(app);
        ProductFilterSales jproductfilter = new ProductFilterSales(dlSales, m_jKeys);
        jproductfilter.activate();
        m_jProductSelect.add(jproductfilter, BorderLayout.CENTER);
        switch (productsType) {
            case PRODUCT_NORMAL:
                lpr = new ListProviderCreator(dlSales.getProductListNormal(), jproductfilter);
                break;
            case PRODUCT_AUXILIAR:
                lpr = new ListProviderCreator(dlSales.getProductListAuxiliar(), jproductfilter);
                break;
            default: // PRODUCT_ALL
                lpr = new ListProviderCreator(dlSales.getProductList(), jproductfilter);
                break;

        }

        jListProducts.setCellRenderer(new ProductRenderer());

        getRootPane().setDefaultButton(jcmdOK);

        m_ReturnProduct = null;

        //show();
        setVisible(true);

        return m_ReturnProduct;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel2 = new JPanel();
        m_jProductSelect = new JPanel();
        jPanel3 = new JPanel();
        jButton3 = new JButton();
        jPanel5 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jListProducts = new JList();
        jPanel1 = new JPanel();
        jcmdOK = new JButton();
        jcmdCancel = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("form.productslist")); // NOI18N

        jPanel4.setLayout(new BorderLayout());
        jPanel4.add(m_jKeys, BorderLayout.NORTH);

        getContentPane().add(jPanel4, BorderLayout.LINE_END);

        jPanel2.setLayout(new BorderLayout());

        m_jProductSelect.setLayout(new BorderLayout());

        jButton3.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/system-search.png"))); // NOI18N
        jButton3.setText(AppLocal.getIntString("button.executefilter")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3);

        m_jProductSelect.add(jPanel3, BorderLayout.SOUTH);

        jPanel2.add(m_jProductSelect, BorderLayout.NORTH);

        jPanel5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jListProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListProducts.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListProductsValueChanged(evt);
            }
        });
        jListProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListProductsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListProducts);

        jPanel5.add(jScrollPane1, BorderLayout.CENTER);

        jPanel2.add(jPanel5, BorderLayout.CENTER);

        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

        jcmdOK.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png"))); // NOI18N
        jcmdOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        jcmdOK.setEnabled(false);
        jcmdOK.setMargin(new Insets(8, 16, 8, 16));
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdOK);

        jcmdCancel.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_cancel.png"))); // NOI18N
        jcmdCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        jcmdCancel.setMargin(new Insets(8, 16, 8, 16));
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdCancel);

        jPanel2.add(jPanel1, BorderLayout.SOUTH);

        getContentPane().add(jPanel2, BorderLayout.CENTER);

        setSize(new Dimension(665, 565));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jListProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListProductsMouseClicked

        if (evt.getClickCount() == 2) {
            m_ReturnProduct = (ProductInfoExt) jListProducts.getSelectedValue();
            dispose();
        }

    }//GEN-LAST:event_jListProductsMouseClicked

    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed

        m_ReturnProduct = (ProductInfoExt) jListProducts.getSelectedValue();
        dispose();

    }//GEN-LAST:event_jcmdOKActionPerformed

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed

        dispose();

    }//GEN-LAST:event_jcmdCancelActionPerformed

    private void jListProductsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListProductsValueChanged

        jcmdOK.setEnabled(jListProducts.getSelectedValue() != null);

    }//GEN-LAST:event_jListProductsValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        try {
            jListProducts.setModel(new MyListData(lpr.loadData()));
            if (jListProducts.getModel().getSize() > 0) {
                jListProducts.setSelectedIndex(0);
            }
        } catch (BasicException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private static class MyListData extends AbstractListModel {

        private java.util.List m_data;

        public MyListData(java.util.List data) {
            m_data = data;
        }

        public Object getElementAt(int index) {
            return m_data.get(index);
        }

        public int getSize() {
            return m_data.size();
        }
    }
    // End of variables declaration//GEN-END:variables

}
