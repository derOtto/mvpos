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
package com.openbravo.pos.reports;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.design.*;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.EditorCreator;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.util.JRViewer;

public abstract class JPanelReport extends JPanel implements JPanelView, BeanFactoryApp {

    protected AppView m_App;
    protected SentenceList taxsent;
    protected TaxesLogic taxeslogic;
    private JRViewer reportviewer = null;
    private JasperReport jr = null;
    private EditorCreator editor = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton jButton1;
    private JPanel jPanel1;
    private JPanel jPanelFilter;
    private JPanel jPanelHeader;
    private JToggleButton jToggleFilter;

    /**
     * Creates new form JPanelReport
     */
    public JPanelReport() {

        initComponents();
    }

    public void init(AppView app) throws BeanFactoryException {

        m_App = app;
        DataLogicSales dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        taxsent = dlSales.getTaxList();

        editor = getEditorCreator();
        if (editor instanceof ReportEditorCreator) {
            jPanelFilter.add(((ReportEditorCreator) editor).getComponent(), BorderLayout.CENTER);
        }

        reportviewer = new JRViewer(null);

        add(reportviewer, BorderLayout.CENTER);

        try {

            InputStream in = getClass().getResourceAsStream(getReport() + ".ser");
            if (in == null) {
                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(getReport() + ".jrxml"));
                jr = JasperCompileManager.compileReport(jd);
            } else {
                try (ObjectInputStream oin = new ObjectInputStream(in)) {
                    jr = (JasperReport) oin.readObject();
                }
            }
        } catch (JRException | IOException | ClassNotFoundException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
            jr = null;
        }
    }

    public Object getBean() {
        return this;
    }

    protected abstract String getReport();

    protected abstract String getResourceBundle();

    protected abstract BaseSentence getSentence();

    protected abstract ReportFields getReportFields();

    protected EditorCreator getEditorCreator() {
        return null;
    }

    public JComponent getComponent() {
        return this;
    }

    public void activate() throws BasicException {

        setVisibleFilter(true);
        taxeslogic = new TaxesLogic(taxsent.list());
    }

    public boolean deactivate() {

        reportviewer.loadJasperPrint(null);
        return true;
    }

    protected void setVisibleButtonFilter(boolean value) {
        jToggleFilter.setVisible(value);
    }

    protected void setVisibleFilter(boolean value) {
        jToggleFilter.setSelected(value);
        jToggleFilterActionPerformed(null);
    }

    private void launchreport() {

        m_App.waitCursorBegin();

        if (jr != null) {
            try {

                // Archivo de recursos
                String res = getResourceBundle();

                // Parametros y los datos
                Object params = (editor == null) ? null : editor.createValue();
                JRDataSource data = new JRDataSourceBasic(getSentence(), getReportFields(), params);

                // Construyo el mapa de los parametros.
                Map reportparams = new HashMap();
                reportparams.put("ARG", params);
                if (res != null) {
                    reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(res));
                }
                reportparams.put("TAXESLOGIC", taxeslogic);

                JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, data);

                reportviewer.loadJasperPrint(jp);

                setVisibleFilter(false);

            } catch (MissingResourceException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadresourcedata"), e);
                msg.show(this);
            } catch (JRException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfillreport"), e);
                msg.show(this);
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreportdata"), e);
                msg.show(this);
            }
        }

        m_App.waitCursorEnd();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelHeader = new JPanel();
        jPanelFilter = new JPanel();
        jPanel1 = new JPanel();
        jToggleFilter = new JToggleButton();
        jButton1 = new JButton();

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());

        jPanelHeader.setLayout(new BorderLayout());

        jPanelFilter.setLayout(new BorderLayout());
        jPanelHeader.add(jPanelFilter, BorderLayout.CENTER);

        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

        jToggleFilter.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/preferences-filter.png"))); // NOI18N
        jToggleFilter.setSelected(true);
        jToggleFilter.setSelectedIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/preferences-filter.png"))); // NOI18N
        jToggleFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleFilterActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleFilter);

        jButton1.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/applications-office.png"))); // NOI18N
        jButton1.setText(AppLocal.getIntString("Button.ExecuteReport")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jPanelHeader.add(jPanel1, BorderLayout.SOUTH);

        add(jPanelHeader, BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        launchreport();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleFilterActionPerformed

        jPanelFilter.setVisible(jToggleFilter.isSelected());

    }//GEN-LAST:event_jToggleFilterActionPerformed
    // End of variables declaration//GEN-END:variables

}
