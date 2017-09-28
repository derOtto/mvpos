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
package com.openbravo.pos.admin;

import java.awt.Component;
import javax.swing.*;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.Hashcypher;
import java.awt.image.BufferedImage;
import java.util.UUID;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public final class PeopleView extends JPanel implements EditorRecord {

    private static final String LOCATION_ID_KEY = "user.location.id";

    private Object m_oId;
    private String m_sPassword;

    private final DirtyManager m_Dirty;

    private final SentenceList m_sentrole;
    private ComboBoxValModel m_RoleModel;

    private final SentenceList m_warehouses;
    private ComboBoxValModel m_WarehouseModel;

    private final AppView m_App;

    private Properties properties;

    public PeopleView(AppView app, DataLogicAdmin dlAdmin, DirtyManager dirty) {
        initComponents();
        
        DataLogicSales dlSales = (DataLogicSales) app.getBean(DataLogicSales.class.getName());

        m_App = app;

        // El modelo de roles
        m_sentrole = dlAdmin.getRolesList();
        m_RoleModel = new ComboBoxValModel();
        m_warehouses = dlSales.getLocationsList();
        m_WarehouseModel = new ComboBoxValModel();
        
        m_Dirty = dirty;
        m_jName.getDocument().addDocumentListener(dirty);
        m_jRole.addActionListener(dirty);
        m_jVisible.addActionListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        jcboWarehoseLocation.addActionListener(dirty);

        properties = new Properties();

        writeValueEOF();
    }

    @Override
    public void writeValueEOF() {
        m_oId = null;
        properties = new Properties();
        m_jName.setText(null);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_WarehouseModel.setSelectedKey(null);
        m_jVisible.setSelected(false);
        jcard.setText(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jRole.setEnabled(false);
        jcboWarehoseLocation.setEnabled(false);
        m_jVisible.setEnabled(false);
        jcard.setEnabled(false);
        m_jImage.setEnabled(false);
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_oId = null;
        properties = new Properties();
        m_jName.setText(null);
        m_jName.setBackground(COLOR_MANDATORY_FIELD);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_WarehouseModel.setSelectedKey(null);
        m_jVisible.setSelected(true);
        jcard.setText(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(true);
        m_jRole.setEnabled(true);
        jcboWarehoseLocation.setEnabled(true);
        m_jVisible.setEnabled(true);
        jcard.setEnabled(true);
        m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_sPassword = Formats.STRING.formatValue(people[2]);
        m_RoleModel.setSelectedKey(people[3]);
        m_jVisible.setSelected(((Boolean) people[4]));
        jcard.setText(Formats.STRING.formatValue(people[5]));
        m_jImage.setImage((BufferedImage) people[6]);
        if (people[7] == null) {
            properties = new Properties();
        } else {
            try {
                properties.loadFromXML(new ByteArrayInputStream((byte[]) people[7]));
            } catch (IOException ex) {
                properties = new Properties();
            }
        }
        m_WarehouseModel.setSelectedKey(properties.getProperty(LOCATION_ID_KEY));

        m_jName.setEnabled(false);
        m_jRole.setEnabled(false);
        jcboWarehoseLocation.setEnabled(false);
        m_jVisible.setEnabled(false);
        jcard.setEnabled(false);
        m_jImage.setEnabled(false);
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_jName.setBackground(null);
        m_sPassword = Formats.STRING.formatValue(people[2]);
        m_RoleModel.setSelectedKey(people[3]);
        m_jVisible.setSelected(((Boolean) people[4]));
        jcard.setText(Formats.STRING.formatValue(people[5]));
        m_jImage.setImage((BufferedImage) people[6]);
        properties = new Properties();
        if (people[7] != null) {
            try {
                properties.loadFromXML(new ByteArrayInputStream((byte[]) people[7]));
            } catch (IOException ex) {
                properties = new Properties();
            }
        }
        m_WarehouseModel.setSelectedKey(properties.getProperty(LOCATION_ID_KEY));

        m_jName.setEnabled(true);
        m_jRole.setEnabled(true);
        jcboWarehoseLocation.setEnabled(true);
        m_jVisible.setEnabled(true);
        jcard.setEnabled(true);
        m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] people = new Object[8];
        people[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        people[1] = Formats.STRING.parseValue(m_jName.getText());
        people[2] = Formats.STRING.parseValue(m_sPassword);
        people[3] = m_RoleModel.getSelectedKey();
        people[4] = m_jVisible.isSelected();
        people[5] = Formats.STRING.parseValue(jcard.getText());
        people[6] = m_jImage.getImage();
        if(m_WarehouseModel.getSelectedKey() != null){
            properties.setProperty(LOCATION_ID_KEY, (String) m_WarehouseModel.getSelectedKey());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            properties.storeToXML(outputStream, "User properties", "UTF-8");
        } catch (IOException ex) {
            throw new BasicException(ex);
        }
        people[7] = outputStream.toByteArray();
        return people;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public void activate() throws BasicException {

        m_RoleModel = new ComboBoxValModel(m_sentrole.list());
        m_jRole.setModel(m_RoleModel);
        
        m_WarehouseModel = new ComboBoxValModel(m_warehouses.list());
        jcboWarehoseLocation.setModel(m_WarehouseModel);
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

        jButton3 = new JButton();
        jLabel1 = new JLabel();
        m_jName = new JTextField();
        m_jVisible = new JCheckBox();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jButton1 = new JButton();
        m_jRole = new JComboBox();
        jLabel2 = new JLabel();
        jcard = new JTextField();
        jButton2 = new JButton();
        jLabel5 = new JLabel();
        jcboWarehoseLocation = new JComboBox();
        jLabel6 = new JLabel();

        jButton3.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/fileclose.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText(AppLocal.getIntString("label.peoplename")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.peoplevisible")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("label.peopleimage")); // NOI18N

        m_jImage.setMaxDimensions(new java.awt.Dimension(32, 32));

        jButton1.setText(AppLocal.getIntString("button.peoplepassword")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText(AppLocal.getIntString("label.role")); // NOI18N

        jButton2.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/gnome-tali.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText(AppLocal.getIntString("label.card")); // NOI18N

        jLabel6.setText(AppLocal.getIntString("label.warehouse")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jVisible)
                    .addComponent(m_jRole, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jcard, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))))
                    .addComponent(jcboWarehoseLocation, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jImage, GroupLayout.PREFERRED_SIZE, 276, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addContainerGap()
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jcard, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(m_jRole, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(jcboWarehoseLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(m_jVisible))
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(m_jImage, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                                                            .addContainerGap(44, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String sNewPassword = Hashcypher.changePassword(this);
        if (sNewPassword != null) {
            m_sPassword = sNewPassword;
            m_Dirty.setDirty(true);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.cardnew"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            jcard.setText(m_App.getUserCard() + StringUtils.getCardNumber());
            m_Dirty.setDirty(true);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.cardremove"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            jcard.setText(null);
            m_Dirty.setDirty(true);
        }

    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JTextField jcard;
    private JComboBox jcboWarehoseLocation;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private JTextField m_jName;
    private JComboBox m_jRole;
    private JCheckBox m_jVisible;
    // End of variables declaration//GEN-END:variables

}
