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
package com.openbravo.pos.inventory;

import java.awt.Component;
import javax.swing.*;

import com.openbravo.pos.forms.AppLocal;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public final class CategoriesEditor extends JPanel implements EditorRecord {

    private final SentenceList m_sentcat;
    private final SentenceExec m_sentadd;
    private final SentenceExec m_sentdel;

    private ComboBoxValModel m_CategoryModel;

    private Object m_id;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JButton m_jCatalogAdd;
    private JButton m_jCatalogDelete;
    private JComboBox m_jCategory;
    private JTextField m_jCode;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private JTextField m_jName;

    public CategoriesEditor(AppView app, DirtyManager dirty) {

        DataLogicSales dlSales = (DataLogicSales) app.getBean(DataLogicSales.class.getName());

        initComponents();

        m_sentcat = dlSales.getCategoriesList();

        m_sentadd = dlSales.getCatalogCategoryAdd();
        m_sentdel = dlSales.getCatalogCategoryDel();

        m_CategoryModel = new ComboBoxValModel();

        m_jName.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);

        writeValueEOF();
    }

    @Override
    public void refresh() {

        List a;

        try {
            a = m_sentcat.list();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotloadlists"), eD);
            msg.show(this);
            a = new ArrayList();
        }

        a.add(0, null); // The null item
        m_CategoryModel = new ComboBoxValModel(a);
        m_jCategory.setModel(m_CategoryModel);
    }

    @Override
    public void writeValueEOF() {
        m_id = null;
        m_jName.setText(null);
        m_jCode.setText(null);
        m_CategoryModel.setSelectedKey(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jCatalogDelete.setEnabled(false);
        m_jCatalogAdd.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_id = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jName.setBackground(COLOR_MANDATORY_FIELD);

        int code = 1;

        try {
            if (m_sentcat.list() != null) {
                code = m_sentcat.list().size() + 1;
            }
        } catch (BasicException ex) {
            Logger.getLogger(CategoriesEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sCode = Integer.toString(code);

        for (int i = sCode.length(); i < 4; i++) {
            sCode = "0".concat(sCode);
        }

        m_jCode.setText(sCode);
        m_CategoryModel.setSelectedKey(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jCatalogDelete.setEnabled(false);
        m_jCatalogAdd.setEnabled(false);
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] cat = (Object[]) value;
        m_id = cat[0];
        m_jName.setText(Formats.STRING.formatValue(cat[1]));
        m_jCode.setText(Formats.STRING.formatValue(cat[2]));
        m_CategoryModel.setSelectedKey(cat[3]);
        m_jImage.setImage((BufferedImage) cat[4]);
        m_jName.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jCatalogDelete.setEnabled(false);
        m_jCatalogAdd.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] cat = (Object[]) value;
        m_id = cat[0];
        m_jName.setText(Formats.STRING.formatValue(cat[1]));
        m_jCode.setText(Formats.STRING.formatValue(cat[2]));
        m_CategoryModel.setSelectedKey(cat[3]);
        m_jImage.setImage((BufferedImage) cat[4]);
        m_jName.setEnabled(true);
        m_jName.setBackground(null);
        m_jCode.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jCatalogDelete.setEnabled(true);
        m_jCatalogAdd.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {

        Object[] cat = new Object[5];

        cat[0] = m_id;
        cat[1] = m_jName.getText();
        cat[2] = m_jCode.getText();
        cat[3] = m_CategoryModel.getSelectedKey();
        cat[4] = m_jImage.getImage();
        return cat;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new JLabel();
        m_jName = new JTextField();
        jLabel3 = new JLabel();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        m_jCatalogAdd = new JButton();
        m_jCatalogDelete = new JButton();
        jLabel5 = new JLabel();
        m_jCategory = new JComboBox();
        m_jCode = new JTextField();
        jLabel4 = new JLabel();

        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.image")); // NOI18N

        m_jImage.setMaxDimensions(new java.awt.Dimension(256, 256));

        m_jCatalogAdd.setText(AppLocal.getIntString("button.catalogadd")); // NOI18N
        m_jCatalogAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCatalogAddActionPerformed(evt);
            }
        });

        m_jCatalogDelete.setText(AppLocal.getIntString("button.catalogdel")); // NOI18N
        m_jCatalogDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCatalogDeleteActionPerformed(evt);
            }
        });

        jLabel5.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("Label.Code")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jCategory, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jImage, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jCode, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(m_jCatalogAdd, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jCatalogDelete, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))
                                      .addGap(101, 101, 101))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                      .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel2)
                                                      .addComponent(m_jCatalogAdd))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                      .addComponent(m_jCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel4)
                                                      .addComponent(m_jCatalogDelete))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                      .addComponent(m_jCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel5))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(m_jImage, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel3))
                                      .addContainerGap(61, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jCatalogDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCatalogDeleteActionPerformed

        try {
            m_sentdel.exec(m_id);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e));
        }

    }//GEN-LAST:event_m_jCatalogDeleteActionPerformed

    private void m_jCatalogAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCatalogAddActionPerformed

        try {
            Object param = m_id;
            m_sentdel.exec(param); // primero borramos
            m_sentadd.exec(param); // y luego insertamos lo que queda
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e));
        }

    }//GEN-LAST:event_m_jCatalogAddActionPerformed
    // End of variables declaration//GEN-END:variables

}
