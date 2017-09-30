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

package com.openbravo.pos.config;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.JPanelView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class JPanelConfiguration extends JPanel implements JPanelView {

    private List<PanelConfig> m_panelconfig;

    private AppConfig config;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnRestore;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JTabbedPane m_jConfigOptions;

    public JPanelConfiguration(AppView oApp) {
        this(oApp.getProperties());
    }

    public JPanelConfiguration(AppProperties props) {

        config = new AppConfig(props.getConfigFile());

        initComponents();

        // Inicio lista de paneles
        m_panelconfig = new ArrayList<>();
        m_panelconfig.add(new JPanelConfigGeneral());
        m_panelconfig.add(new JPanelConfigDatabase());
        m_panelconfig.add(new JPanelConfigHardware());
        m_panelconfig.add(new JPanelConfigLocale());
        m_panelconfig.add(new JPanelConfigPayment(props));
        m_panelconfig.add(new JPanelConfigServer());

        // paneles auxiliares
        for (PanelConfig c : m_panelconfig) {
            m_jConfigOptions.addTab(c.getPanelConfigName(), c.getConfigComponent());
        }
    }

    private void restoreProperties() {

        if (config.delete()) {
            loadProperties();
        } else {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotdeleteconfig")));
        }
    }

    private void loadProperties() {

        config.load();

        // paneles auxiliares
        for (PanelConfig c : m_panelconfig) {
            c.loadProperties(config);
        }
    }

    private void saveProperties() {

        // paneles auxiliares
        for (PanelConfig c : m_panelconfig) {
            c.saveProperties(config);
        }

        try {
            config.save();
            JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.restartchanges"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotsaveconfig"), e));
        }
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Configuration");
    }

    @Override
    public void activate() throws BasicException {
        loadProperties();
    }

    @Override
    public boolean deactivate() {

        boolean haschanged = false;
        for (PanelConfig c : m_panelconfig) {
            if (c.hasChanged()) {
                haschanged = true;
            }
        }


        if (haschanged) {
            int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannasave"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveProperties();
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        m_jConfigOptions = new javax.swing.JTabbedPane();
        jbtnCancel = new javax.swing.JButton();
        jbtnRestore = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();

        m_jConfigOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(m_jConfigOptions);

        jbtnCancel.setText(AppLocal.getIntString("Button.Restore")); // NOI18N
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jbtnRestore.setText(AppLocal.getIntString("Button.Factory")); // NOI18N
        jbtnRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRestoreActionPerformed(evt);
            }
        });

        jbtnSave.setText(AppLocal.getIntString("Button.Save")); // NOI18N
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                                                      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                                  .addGap(0, 362, Short.MAX_VALUE)
                                                                                                                  .addComponent(jbtnSave)
                                                                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                  .addComponent(jbtnRestore)
                                                                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                  .addComponent(jbtnCancel)))
                                      .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                  .addContainerGap()
                                                                                  .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                                                                                  .addGap(18, 18, 18)
                                                                                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                  .addComponent(jbtnCancel)
                                                                                                  .addComponent(jbtnRestore)
                                                                                                  .addComponent(jbtnSave))
                                                                                  .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed

        if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.configrestore"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            loadProperties();
        }

    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void jbtnRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRestoreActionPerformed

        if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.configfactory"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            restoreProperties();
        }

    }//GEN-LAST:event_jbtnRestoreActionPerformed

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed

        saveProperties();

    }//GEN-LAST:event_jbtnSaveActionPerformed
    // End of variables declaration//GEN-END:variables

}
