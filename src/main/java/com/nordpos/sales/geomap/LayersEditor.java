/**
 * NORD POS is a fork of Openbravo POS.
 * <p>
 * Copyright (C) 2009-2016 Nord Trading Ltd. <http://www.nordpos.com>
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
package com.nordpos.sales.geomap;

import com.bric.swing.ColorPicker;
import com.openbravo.basic.BasicException;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class LayersEditor extends JPanel implements EditorRecord {

    private String m_sID;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jColorChooser;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel8;
    private JTextField m_jColor;
    private com.openbravo.data.gui.JImageEditor m_jIcon;
    private JTextField m_jName;
    private JCheckBox m_jVisible;

    public LayersEditor(DirtyManager dirty) {
        initComponents();

        m_jName.getDocument().addDocumentListener(dirty);
        m_jVisible.addActionListener(dirty);
        m_jIcon.addPropertyChangeListener("image", dirty);
        m_jColor.getDocument().addDocumentListener(dirty);

        writeValueEOF();
    }

    @Override
    public final void writeValueEOF() {

        m_sID = null;
        m_jName.setText(null);
        m_jVisible.setSelected(false);
        m_jIcon.setImage(null);
        m_jColor.setText(null);
        m_jColor.setBackground(Color.WHITE);

        m_jName.setEnabled(false);
        m_jVisible.setEnabled(false);
        m_jIcon.setEnabled(false);
        m_jColor.setEnabled(false);
        jColorChooser.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {

        m_sID = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jName.setBackground(COLOR_MANDATORY_FIELD);
        m_jVisible.setSelected(true);
        m_jIcon.setImage(null);
        m_jColor.setText("0x" + Integer.toHexString(0x100 | Color.WHITE.getRed()).substring(1).toUpperCase()
                + Integer.toHexString(0x100 | Color.WHITE.getGreen()).substring(1).toUpperCase()
                + Integer.toHexString(0x100 | Color.WHITE.getBlue()).substring(1).toUpperCase());
        m_jColor.setBackground(Color.WHITE);

        m_jName.setEnabled(true);
        m_jVisible.setEnabled(true);
        m_jIcon.setEnabled(true);
        m_jColor.setEnabled(true);
        jColorChooser.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {

        Object[] layer = (Object[]) value;
        m_sID = Formats.STRING.formatValue(layer[0]);
        m_jName.setText(Formats.STRING.formatValue(layer[1]));
        m_jVisible.setSelected(((Boolean) layer[2]));
        m_jIcon.setImage((BufferedImage) layer[3]);
        m_jColor.setText(Formats.STRING.formatValue(layer[4]));
        m_jColor.setBackground(new Color((int) Integer.decode(m_jColor.getText())));

        m_jName.setEnabled(false);
        m_jVisible.setEnabled(false);
        m_jIcon.setEnabled(false);
        m_jColor.setEnabled(false);
        jColorChooser.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {

        Object[] layer = (Object[]) value;
        m_sID = Formats.STRING.formatValue(layer[0]);
        m_jName.setText(Formats.STRING.formatValue(layer[1]));
        m_jName.setBackground(null);
        m_jVisible.setSelected(((Boolean) layer[2]));
        m_jIcon.setImage((BufferedImage) layer[3]);
        m_jColor.setText(Formats.STRING.formatValue(layer[4]));
        m_jColor.setBackground(new Color((int) Integer.decode(m_jColor.getText())));

        m_jName.setEnabled(true);
        m_jVisible.setEnabled(true);
        m_jIcon.setEnabled(true);
        m_jColor.setEnabled(true);
        jColorChooser.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {

        Object[] layer = new Object[5];

        layer[0] = m_sID;
        layer[1] = m_jName.getText();
        layer[2] = m_jVisible.isSelected();
        layer[3] = m_jIcon.getImage();
        layer[4] = m_jColor.getText();
        return layer;
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

        jLabel3 = new JLabel();
        m_jName = new JTextField();
        jLabel8 = new JLabel();
        m_jVisible = new JCheckBox();
        jLabel4 = new JLabel();
        m_jIcon = new com.openbravo.data.gui.JImageEditor();
        jColorChooser = new JButton();
        jLabel5 = new JLabel();
        m_jColor = new JTextField();

        setAutoscrolls(true);

        jLabel3.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        jLabel8.setText(AppLocal.getIntString("label.visible")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("label.icon")); // NOI18N

        m_jIcon.setMaxDimensions(new java.awt.Dimension(32, 32));

        jColorChooser.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/ColourPick.png"))); // NOI18N
        jColorChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorChooserActionPerformed(evt);
            }
        });

        jLabel5.setText(AppLocal.getIntString("label.color")); // NOI18N

        m_jColor.setEditable(false);
        m_jColor.setBackground(Color.white);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(m_jIcon, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(m_jVisible)
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addComponent(m_jColor, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(jColorChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                                      .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                                      .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jLabel3)
                                                      .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                      .addComponent(m_jVisible)
                                                      .addComponent(jLabel8))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                      .addComponent(jLabel4)
                                                      .addComponent(m_jIcon, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                      .addComponent(jLabel5)
                                                      .addComponent(m_jColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jColorChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                      .addContainerGap(141, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorChooserActionPerformed
        ColorPicker picker = new ColorPicker();
        picker.setColor(Color.WHITE);
        picker.setOpacityVisible(false);
        JOptionPane.showMessageDialog(null, picker, AppLocal.getIntString("dialog.ColorPicker"), JOptionPane.PLAIN_MESSAGE);
        Color newColor = picker.getColor();
        String sColor = "0x" + Integer.toHexString(0x100 | newColor.getRed()).substring(1).toUpperCase()
                + Integer.toHexString(0x100 | newColor.getGreen()).substring(1).toUpperCase()
                + Integer.toHexString(0x100 | newColor.getBlue()).substring(1).toUpperCase();
        m_jColor.setText(sColor);

        m_jColor.setBackground(new Color((int) Integer.decode(sColor)));
    }//GEN-LAST:event_jColorChooserActionPerformed
    // End of variables declaration//GEN-END:variables

}
