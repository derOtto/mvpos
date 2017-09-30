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
package com.openbravo.pos.inventory;

import java.awt.Component;
import java.util.UUID;

import com.openbravo.basic.BasicException;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;

/**
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class LocationsView extends javax.swing.JPanel implements EditorRecord {

    private String m_sID;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField m_jAddress;
    private javax.swing.JCheckBox m_jClose;
    private javax.swing.JTextField m_jName;

    public LocationsView(DirtyManager dirty) {
        initComponents();

        m_jName.getDocument().addDocumentListener(dirty);
        m_jAddress.getDocument().addDocumentListener(dirty);
        m_jClose.addActionListener(dirty);

        writeValueEOF();
    }

    @Override
    public void writeValueEOF() {

        m_sID = null;
        m_jName.setText(null);
        m_jAddress.setText(null);
        m_jClose.setSelected(false);

        m_jName.setEnabled(false);
        m_jAddress.setEnabled(false);
        m_jClose.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {

        m_sID = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jName.setBackground(COLOR_MANDATORY_FIELD);
        m_jAddress.setText(null);
        m_jClose.setSelected(false);

        m_jName.setEnabled(true);
        m_jAddress.setEnabled(true);
        m_jClose.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {

        Object[] location = (Object[]) value;
        m_sID = Formats.STRING.formatValue(location[0]);
        m_jName.setText(Formats.STRING.formatValue(location[1]));
        m_jAddress.setText(Formats.STRING.formatValue(location[2]));
        m_jClose.setSelected(((Boolean) location[3]));

        m_jName.setEnabled(false);
        m_jAddress.setEnabled(false);
        m_jClose.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {

        Object[] location = (Object[]) value;
        m_sID = Formats.STRING.formatValue(location[0]);
        m_jName.setText(Formats.STRING.formatValue(location[1]));
        m_jName.setBackground(null);
        m_jAddress.setText(Formats.STRING.formatValue(location[2]));
        m_jClose.setSelected(((Boolean) location[3]));

        m_jName.setEnabled(true);
        m_jAddress.setEnabled(true);
        m_jClose.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] location = new Object[4];
        location[0] = m_sID;
        location[1] = m_jName.getText();
        location[2] = m_jAddress.getText();
        location[3] = m_jClose.isSelected();
        return location;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jAddress = new javax.swing.JTextField();
        m_jClose = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();

        jLabel2.setText(AppLocal.getIntString("label.locationname")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.locationaddress")); // NOI18N

        jLabel12.setText(AppLocal.getIntString("label.locationclose")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                      .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                      .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                      .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addComponent(m_jClose, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                      .addContainerGap(67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                      .addComponent(jLabel2)
                                                      .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                      .addComponent(jLabel3)
                                                      .addComponent(m_jAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                      .addComponent(m_jClose)
                                                      .addComponent(jLabel12))
                                      .addContainerGap(93, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // End of variables declaration//GEN-END:variables

}
