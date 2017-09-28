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
package com.openbravo.data.gui;

import com.openbravo.data.loader.LocalRes;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class JImageEditor extends javax.swing.JPanel {

    private Dimension m_maxsize;
    private final ZoomIcon m_icon;
    private BufferedImage m_Img = null;

    private static File m_fCurrentDirectory = null;
    private static final NumberFormat m_percentformat = new DecimalFormat("#,##0.##%");

    public JImageEditor() {
        initComponents();

        m_Img = null;
        m_maxsize = null;
        m_icon = new ZoomIcon();
        m_jImage.setIcon(m_icon);
        m_jPercent.setText(m_percentformat.format(m_icon.getZoom()));
        privateSetEnabled(isEnabled());
    }

    public void setMaxDimensions(Dimension size) {
        m_maxsize = size;
    }

    public Dimension getMaxDimensions() {
        return m_maxsize;
    }

    @Override
    public void setEnabled(boolean value) {

        privateSetEnabled(value);
        super.setEnabled(value);
    }

    private void privateSetEnabled(boolean value) {
        m_jbtnopen.setEnabled(value);
        m_jbtnclose.setEnabled(value && (m_Img != null));
        m_jbtnzoomin.setEnabled(value && (m_Img != null));
        m_jbtnzoomout.setEnabled(value && (m_Img != null));
        m_jPercent.setEnabled(value && (m_Img != null));
        m_jScr.setEnabled(value && (m_Img != null));
    }

    public void setImage(BufferedImage img) {
        BufferedImage oldimg = m_Img;
        m_Img = img;
        m_icon.setIcon(m_Img == null ? null : new ImageIcon(m_Img));

        m_jPercent.setText(m_percentformat.format(m_icon.getZoom()));

        m_jImage.revalidate();
        m_jScr.revalidate();
        m_jScr.repaint();

        privateSetEnabled(isEnabled());

        firePropertyChange("image", oldimg, m_Img);
    }

    public BufferedImage getImage() {
        return m_Img;
    }

    public double getZoom() {
        return m_icon.getZoom();
    }

    public void setZoom(double zoom) {
        double oldzoom = m_icon.getZoom();
        m_icon.setZoom(zoom);

        m_jPercent.setText(m_percentformat.format(m_icon.getZoom()));

        m_jImage.revalidate();
        m_jScr.revalidate();
        m_jScr.repaint();

        firePropertyChange("zoom", oldzoom, zoom);
    }

    public void incZoom() {
        double zoom = m_icon.getZoom();
        setZoom(zoom > 4.0 ? 8.0 : zoom * 2.0);
    }

    public void decZoom() {
        double zoom = m_icon.getZoom();
        setZoom(zoom < 1.0 ? 0.25 : zoom / 2.0);
    }

    public void doLoad() {
        JFileChooser fc = new JFileChooser(m_fCurrentDirectory);

        fc.addChoosableFileFilter(new ExtensionsFilter(LocalRes.getIntString("label.imagefiles"), "png", "gif", "jpg", "jpeg", "bmp"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());
                if (img != null) {
                    // compruebo que no exceda el tamano maximo.
                    if (m_maxsize != null && (img.getHeight() > m_maxsize.height || img.getWidth() > m_maxsize.width)) {
                        if (JOptionPane.showConfirmDialog(this, LocalRes.getIntString("message.resizeimage"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            // Redimensionamos la imagen para que se ajuste
                            img = resizeImage(img);
                        }
                    }
                    setImage(img);
                    m_fCurrentDirectory = fc.getCurrentDirectory();
                }
            } catch (IOException eIO) {
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage img) {

        int myheight = img.getHeight();
        int mywidth = img.getWidth();

        if (mywidth > m_maxsize.width || myheight > m_maxsize.height) {
            if (mywidth > myheight) {
                img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, m_maxsize.height);
            } else {
                img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, m_maxsize.width);
            }
        }

        return img;
    }

    public static class ZoomIcon implements Icon {

        private Icon ico;
        private double zoom;

        public ZoomIcon() {
            this.ico = null;
            this.zoom = 1.0;
        }

        @Override
        public int getIconHeight() {
            return ico == null ? 0 : (int) (zoom * ico.getIconHeight());
        }

        @Override
        public int getIconWidth() {
            return ico == null ? 0 : (int) (zoom * ico.getIconWidth());
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (ico != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                AffineTransform oldt = g2d.getTransform();
                g2d.transform(AffineTransform.getScaleInstance(zoom, zoom));
                ico.paintIcon(c, g2d, (int) (x / zoom), (int) (y / zoom));
                g2d.setTransform(oldt);
            }
        }

        public void setIcon(Icon ico) {
            this.ico = ico;
        }

        public void setZoom(double zoom) {
            this.zoom = zoom;
        }

        public double getZoom() {
            return zoom;
        }
    }

    private static class ExtensionsFilter extends FileFilter {

        private final String message;
        private final String[] extensions;

        public ExtensionsFilter(String message, String... extensions) {
            this.message = message;
            this.extensions = extensions;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                String sFileName = f.getName();
                int ipos = sFileName.lastIndexOf('.');
                if (ipos >= 0) {
                    String sExt = sFileName.substring(ipos + 1);
                    for (String s : extensions) {
                        if (s.equalsIgnoreCase(sExt)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        @Override
        public String getDescription() {
            return message;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jScr = new javax.swing.JScrollPane();
        m_jImage = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnopen = new javax.swing.JButton();
        m_jbtnclose = new javax.swing.JButton();
        m_jbtnzoomin = new javax.swing.JButton();
        m_jPercent = new javax.swing.JLabel();
        m_jbtnzoomout = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        m_jImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jScr.setViewportView(m_jImage);

        add(m_jScr, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        m_jbtnopen.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        m_jbtnopen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnopenActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnopen);

        m_jbtnclose.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/fileclose.png"))); // NOI18N
        m_jbtnclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtncloseActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnclose);

        m_jbtnzoomin.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/viewmag+.png"))); // NOI18N
        m_jbtnzoomin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnzoominActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnzoomin);

        m_jPercent.setBackground(java.awt.Color.white);
        m_jPercent.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jPercent.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPercent.setOpaque(true);
        jPanel2.add(m_jPercent);

        m_jbtnzoomout.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/viewmag-.png"))); // NOI18N
        m_jbtnzoomout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnzoomoutActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnzoomout);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnzoomoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnzoomoutActionPerformed

        decZoom();

    }//GEN-LAST:event_m_jbtnzoomoutActionPerformed

    private void m_jbtnzoominActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnzoominActionPerformed

        incZoom();

    }//GEN-LAST:event_m_jbtnzoominActionPerformed

    private void m_jbtncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtncloseActionPerformed

        setImage(null);

    }//GEN-LAST:event_m_jbtncloseActionPerformed

    private void m_jbtnopenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnopenActionPerformed

        doLoad();

    }//GEN-LAST:event_m_jbtnopenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel m_jImage;
    private javax.swing.JLabel m_jPercent;
    private javax.swing.JScrollPane m_jScr;
    private javax.swing.JButton m_jbtnclose;
    private javax.swing.JButton m_jbtnopen;
    private javax.swing.JButton m_jbtnzoomin;
    private javax.swing.JButton m_jbtnzoomout;
    // End of variables declaration//GEN-END:variables

}
