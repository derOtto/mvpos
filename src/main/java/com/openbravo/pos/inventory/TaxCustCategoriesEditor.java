//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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
///    Foundation, Inc., 51 Franklin Street, Fifth floor, Boston, MA  02110-1301  USA

package com.openbravo.pos.inventory;

import java.awt.Component;
import java.util.UUID;
import javax.swing.*;

import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppLocal;

public class TaxCustCategoriesEditor extends JPanel implements EditorRecord {
    
    private Object m_oId;
    
    /** Creates new form taxEditor */
    public TaxCustCategoriesEditor(DirtyManager dirty) {
        initComponents();

        m_jName.getDocument().addDocumentListener(dirty);
        
        writeValueEOF();
    }
    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);
        m_jName.setEnabled(false);
    }
    public void writeValueInsert() {
        m_oId = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jName.setBackground(COLOR_MANDATORY_FIELD);
        m_jName.setEnabled(true);
    }
    public void writeValueDelete(Object value) {

        Object[] taxcustcat = (Object[]) value;
        m_oId = taxcustcat[0];
        m_jName.setText(Formats.STRING.formatValue(taxcustcat[1]));
        m_jName.setEnabled(false);
    }    
    public void writeValueEdit(Object value) {

        Object[] taxcustcat = (Object[]) value;
        m_oId = taxcustcat[0];
        m_jName.setText(Formats.STRING.formatValue(taxcustcat[1]));
        m_jName.setBackground(null);
        m_jName.setEnabled(true);
    }

    public Object createValue() throws BasicException {
        
        Object[] taxcustcat = new Object[2];

        taxcustcat[0] = m_oId;
        taxcustcat[1] = m_jName.getText();

        return taxcustcat;
    }    
     
    public Component getComponent() {
        return this;
    }
    
    public void refresh() {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new JLabel();
        m_jName = new JTextField();

        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_jName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel2;
    private JTextField m_jName;
    // End of variables declaration//GEN-END:variables

}