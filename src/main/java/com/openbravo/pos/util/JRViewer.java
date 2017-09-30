/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net
 * Carlton Moore - cmoore79@users.sourceforge.net
 *  Petr Michalek - pmichalek@users.sourceforge.net
 */

//    Portions:
//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//    author adrian romero
//    This class is a copy of net.sf.jasperreports.view.JRViewer
//    The modifications are:
//    The loadJasperPrint() method 
//    And the redesign of the design properties of the toolbar
//    Nothing else.

/*    Update for support library jasperreports 6.2.0 for NORD POS
 *    @author Andrey Svininykh <svininykh@gmail.com>
 *    http://www.nordpos.mobi
 */

package com.openbravo.pos.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.ImageMapRenderable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRPrintXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;
import net.sf.jasperreports.view.JRHyperlinkListener;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.SaveContributorUtils;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represents the built-in visual component for JasperReports.
 * <p>
 * This class is different from the rest of the classes listed previously in that it is more like a
 * pluggable component than a utility class. It can be used in Swing-based
 * applications to view the reports generated by the JasperReports library.
 * </p><p>
 * This visual component was included like a demo
 * component to show how the core printing functionality can be used to display the reports
 * in Swing-based applications.
 * </p><p>
 * The preferred way to adapt this component to a particular application is by subclassing it.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated Replaced by {@link net.sf.jasperreports.swing.JRViewer}.
 */
public class JRViewer extends JPanel implements JRHyperlinkListener {
    /**
     * Maximum size (in pixels) of a buffered image that would be used by {@link JRViewer JRViewer} to render a report page.
     * <p>
     * If rendering a report page would require an image larger than this threshold
     * (i.e. image width x image height > maximum size), the report page will be rendered directly on the viewer component.
     * </p>
     * <p>
     * If this property is zero or negative, buffered images will never be user to render a report page.
     * By default, this property is set to 0.
     * </p>
     */
    public static final String VIEWER_RENDER_BUFFER_MAX_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "viewer.render.buffer.max.size";
    /**
     * The DPI of the generated report.
     */
    public static final int REPORT_RESOLUTION = 72;
    /**
     *
     */
    protected static final int TYPE_FILE_NAME = 1;
    protected static final int TYPE_INPUT_STREAM = 2;
    protected static final int TYPE_OBJECT = 3;
    private static final Log log = LogFactory.getLog(JRViewer.class);
    private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
    protected final float MIN_ZOOM = 0.5f;
    protected final float MAX_ZOOM = 10f;
    protected int zooms[] = {50, 75, 100, 125, 150, 175, 200, 250, 400, 800};
    protected int defaultZoomIndex = 2;

    protected int type = TYPE_FILE_NAME;
    protected boolean isXML;
    protected String reportFileName;
    protected float zoom;
    /**
     * the zoom ration adjusted to the screen resolution.
     */
    protected float realZoom;
    protected JasperReportsContext jasperReportsContext;
    protected LocalJasperReportsContext localJasperReportsContext;
    protected List<JRSaveContributor> saveContributors = new ArrayList<JRSaveContributor>();
    protected File lastFolder;
    protected JRSaveContributor lastSaveContributor;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JToggleButton btnActualSize;
    protected javax.swing.JButton btnFirst;
    protected javax.swing.JToggleButton btnFitPage;
    protected javax.swing.JToggleButton btnFitWidth;
    protected javax.swing.JButton btnLast;
    protected javax.swing.JButton btnNext;
    protected javax.swing.JButton btnPrevious;
    protected javax.swing.JButton btnPrint;
    protected javax.swing.JButton btnReload;
    protected javax.swing.JButton btnSave;
    protected javax.swing.JButton btnZoomIn;
    protected javax.swing.JButton btnZoomOut;
    protected javax.swing.JComboBox cmbZoom;
    protected JLabel lblStatus;
    protected javax.swing.JTextField txtGoTo;
    JasperPrint jasperPrint;
    private int pageIndex;
    private boolean pageError;
    private JRGraphics2DExporter exporter;
    /**
     * the screen resolution.
     */
    private int screenResolution = REPORT_RESOLUTION;
    private DecimalFormat zoomDecimalFormat;
    private ResourceBundle resourceBundle;
    private int downX;
    private int downY;
    private boolean pnlTabsChangeListenerEnabled = true;
    private List<JRHyperlinkListener> hyperlinkListeners = new ArrayList<JRHyperlinkListener>();
    private Map<JPanel, JRPrintHyperlink> linksMap = new HashMap<JPanel, JRPrintHyperlink>();
    private MouseListener mouseListener =
            new java.awt.event.MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    hyperlinkClicked(evt);
                }
            };
    private JLabel jLabel1;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPanel jPanel8;
    private JPanel jPanel9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar;
    private PageRenderer lblPage;
    private JPanel pnlInScroll;
    private JPanel pnlLinks;
    private JPanel pnlMain;
    private JPanel pnlPage;
    private javax.swing.JScrollPane scrollPane;
    protected KeyListener keyNavigationListener =
            new KeyListener() {
                public void keyTyped(KeyEvent evt) {
                }

                public void keyPressed(KeyEvent evt) {
                    keyNavigate(evt);
                }

                public void keyReleased(KeyEvent evt) {
                }
            };

    /**
     * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
     */
    public JRViewer(String fileName, boolean isXML) throws JRException {
        this(fileName, isXML, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
     */
    public JRViewer(InputStream is, boolean isXML) throws JRException {
        this(is, isXML, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
     */
    public JRViewer(JasperPrint jrPrint) {
        this(jrPrint, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
     */
    public JRViewer(String fileName, boolean isXML, Locale locale) throws JRException {
        this(fileName, isXML, locale, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
     */
    public JRViewer(InputStream is, boolean isXML, Locale locale) throws JRException {
        this(is, isXML, locale, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
     */
    public JRViewer(JasperPrint jrPrint, Locale locale) {
        this(jrPrint, locale, null);
    }

    /**
     * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
     */
    public JRViewer(String fileName, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException {
        this(
                DefaultJasperReportsContext.getInstance(),
                fileName,
                isXML,
                locale,
                resBundle
        );
    }

    /**
     * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
     */
    public JRViewer(InputStream is, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException {
        this(
                DefaultJasperReportsContext.getInstance(),
                is,
                isXML,
                locale,
                resBundle
        );
    }

    /**
     * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
     */
    public JRViewer(JasperPrint jrPrint, Locale locale, ResourceBundle resBundle) {
        this(
                DefaultJasperReportsContext.getInstance(),
                jrPrint,
                locale,
                resBundle
        );
    }

    /**
     *
     */
    public JRViewer(
            JasperReportsContext jasperReportsContext,
            String fileName,
            boolean isXML,
            Locale locale,
            ResourceBundle resBundle
    ) throws JRException {
        this.jasperReportsContext = jasperReportsContext;

        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(fileName, isXML);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    /**
     *
     */
    public JRViewer(
            JasperReportsContext jasperReportsContext,
            InputStream is,
            boolean isXML,
            Locale locale,
            ResourceBundle resBundle
    ) throws JRException {
        this.jasperReportsContext = jasperReportsContext;

        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(is, isXML);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    /**
     *
     */
    public JRViewer(
            JasperReportsContext jasperReportsContext,
            JasperPrint jrPrint,
            Locale locale,
            ResourceBundle resBundle
    ) {
        this.jasperReportsContext = jasperReportsContext;

        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(jrPrint);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    public void loadJasperPrint(JasperPrint jrPrint) {

        loadReport(jrPrint);
        setZoomRatio(zooms[defaultZoomIndex] / 100f);
        cmbZoomItemStateChanged(null);
        refreshPage();
    }

    private void setScreenDetails() {
        screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
    }

    /**
     *
     */
    public void clear() {
        emptyContainer(this);
        jasperPrint = null;
    }

    /**
     *
     */
    protected void setZooms() {
    }

    /**
     *
     */
    public void addSaveContributor(JRSaveContributor contributor) {
        saveContributors.add(contributor);
    }

    /**
     *
     */
    public void removeSaveContributor(JRSaveContributor contributor) {
        saveContributors.remove(contributor);
    }

    /**
     *
     */
    public JRSaveContributor[] getSaveContributors() {
        return saveContributors.toArray(new JRSaveContributor[saveContributors.size()]);
    }

    /**
     * Replaces the save contributors with the ones provided as parameter.
     */
    public void setSaveContributors(JRSaveContributor[] saveContribs) {
        this.saveContributors = new ArrayList<JRSaveContributor>();
        if (saveContribs != null) {
            this.saveContributors.addAll(Arrays.asList(saveContribs));
        }
    }

    /**
     *
     */
    public void addHyperlinkListener(JRHyperlinkListener listener) {
        hyperlinkListeners.add(listener);
    }

    /**
     *
     */
    public void removeHyperlinkListener(JRHyperlinkListener listener) {
        hyperlinkListeners.remove(listener);
    }

    /**
     *
     */
    public JRHyperlinkListener[] getHyperlinkListeners() {
        return hyperlinkListeners.toArray(new JRHyperlinkListener[hyperlinkListeners.size()]);
    }

    /**
     *
     */
    protected void initResources(Locale locale, ResourceBundle resBundle) {
        //FIXME in theory, the setLocale method could be called after current Component was created, in which case all below should be reloaded
        if (locale != null) {
            setLocale(locale);
        } else {
            setLocale(Locale.getDefault());
        }
        if (resBundle == null) {
            this.resourceBundle = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", getLocale());
        } else {
            this.resourceBundle = resBundle;
        }

        zoomDecimalFormat = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(getLocale()));
    }

    /**
     *
     */
    protected JasperReportsContext getJasperReportsContext() {
        return jasperReportsContext;
    }

    /**
     *
     */
    protected String getBundleString(String key) {
        return resourceBundle.getString(key);
    }

    /**
     *
     */
    protected void initSaveContributors() {
        List<JRSaveContributor> builtinContributors = SaveContributorUtils.createBuiltinContributors(
                jasperReportsContext, getLocale(), resourceBundle);
        saveContributors.addAll(builtinContributors);
    }

    /**
     *
     */
    public void gotoHyperlink(JRPrintHyperlink hyperlink) {
        switch (hyperlink.getHyperlinkTypeValue()) {
            case REFERENCE: {
                if (isOnlyHyperlinkListener()) {
                    System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
                    System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
                }
                break;
            }
            case LOCAL_ANCHOR: {
                if (hyperlink.getHyperlinkAnchor() != null) {
                    Map<String, JRPrintAnchorIndex> anchorIndexes = jasperPrint.getAnchorIndexes();
                    JRPrintAnchorIndex anchorIndex = anchorIndexes.get(hyperlink.getHyperlinkAnchor());
                    if (anchorIndex.getPageIndex() != pageIndex) {
                        setPageIndex(anchorIndex.getPageIndex());
                        refreshPage();
                    }
                    Container container = pnlInScroll.getParent();
                    if (container instanceof JViewport) {
                        JViewport viewport = (JViewport) container;

                        int newX = (int) (anchorIndex.getElementAbsoluteX() * realZoom);
                        int newY = (int) (anchorIndex.getElementAbsoluteY() * realZoom);

                        int maxX = pnlInScroll.getWidth() - viewport.getWidth();
                        int maxY = pnlInScroll.getHeight() - viewport.getHeight();

                        if (newX < 0) {
                            newX = 0;
                        }
                        if (newX > maxX) {
                            newX = maxX;
                        }
                        if (newY < 0) {
                            newY = 0;
                        }
                        if (newY > maxY) {
                            newY = maxY;
                        }

                        viewport.setViewPosition(new Point(newX, newY));
                    }
                }

                break;
            }
            case LOCAL_PAGE: {
                int page = pageIndex + 1;
                if (hyperlink.getHyperlinkPage() != null) {
                    page = hyperlink.getHyperlinkPage().intValue();
                }

                if (page >= 1 && page <= jasperPrint.getPages().size() && page != pageIndex + 1) {
                    setPageIndex(page - 1);
                    refreshPage();
                    Container container = pnlInScroll.getParent();
                    if (container instanceof JViewport) {
                        JViewport viewport = (JViewport) container;
                        viewport.setViewPosition(new Point(0, 0));
                    }
                }

                break;
            }
            case REMOTE_ANCHOR: {
                if (isOnlyHyperlinkListener()) {
                    System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
                    System.out.println("Hyperlink anchor    : " + hyperlink.getHyperlinkAnchor());
                    System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
                }
                break;
            }
            case REMOTE_PAGE: {
                if (isOnlyHyperlinkListener()) {
                    System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
                    System.out.println("Hyperlink page      : " + hyperlink.getHyperlinkPage());
                    System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
                }
                break;
            }
            case CUSTOM: {
                if (isOnlyHyperlinkListener()) {
                    System.out.println("Hyperlink of type " + hyperlink.getLinkType());
                    System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
                }
                break;
            }
            case NONE:
            default: {
                break;
            }
        }
    }

    protected boolean isOnlyHyperlinkListener() {
        int listenerCount;
        if (hyperlinkListeners == null) {
            listenerCount = 0;
        } else {
            listenerCount = hyperlinkListeners.size();
            if (hyperlinkListeners.contains(this)) {
                --listenerCount;
            }
        }
        return listenerCount == 0;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new JPanel();
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        pnlInScroll = new JPanel();
        pnlPage = new JPanel();
        jPanel4 = new JPanel();
        pnlLinks = new JPanel();
        jPanel5 = new JPanel();
        jPanel6 = new JPanel();
        jPanel7 = new JPanel();
        jPanel8 = new JPanel();
        jLabel1 = new JLabel();
        jPanel9 = new JPanel();
        lblPage = new PageRenderer(this);
        jToolBar = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnActualSize = new javax.swing.JToggleButton();
        btnFitPage = new javax.swing.JToggleButton();
        btnFitWidth = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnZoomIn = new javax.swing.JButton();
        cmbZoom = new javax.swing.JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < zooms.length; i++) {
            model.addElement("" + zooms[i] + "%");
        }
        cmbZoom.setModel(model);
        btnZoomOut = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        txtGoTo = new javax.swing.JTextField();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        lblStatus = new JLabel();

        setMinimumSize(new Dimension(450, 150));
        setPreferredSize(new Dimension(450, 150));
        setLayout(new java.awt.BorderLayout());

        pnlMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlMainComponentResized(evt);
            }
        });
        pnlMain.setLayout(new java.awt.BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        pnlInScroll.setLayout(new java.awt.GridBagLayout());

        pnlPage.setMinimumSize(new Dimension(100, 100));
        pnlPage.setPreferredSize(new Dimension(100, 100));
        pnlPage.setLayout(new java.awt.BorderLayout());

        jPanel4.setMinimumSize(new Dimension(100, 120));
        jPanel4.setPreferredSize(new Dimension(100, 120));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        pnlLinks.setMinimumSize(new Dimension(5, 5));
        pnlLinks.setOpaque(false);
        pnlLinks.setPreferredSize(new Dimension(5, 5));
        pnlLinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                pnlLinksMousePressed(evt);
            }

            public void mouseReleased(MouseEvent evt) {
                pnlLinksMouseReleased(evt);
            }
        });
        pnlLinks.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                pnlLinksMouseDragged(evt);
            }
        });
        pnlLinks.setLayout(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(pnlLinks, gridBagConstraints);

        jPanel5.setBackground(Color.gray);
        jPanel5.setMinimumSize(new Dimension(5, 5));
        jPanel5.setPreferredSize(new Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setMinimumSize(new Dimension(5, 5));
        jPanel6.setPreferredSize(new Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel7.setMinimumSize(new Dimension(5, 5));
        jPanel7.setPreferredSize(new Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel7, gridBagConstraints);

        jPanel8.setMinimumSize(new Dimension(5, 5));
        jPanel8.setPreferredSize(new Dimension(5, 5));

        jLabel1.setText("jLabel1");
        jPanel8.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel8, gridBagConstraints);

        jPanel9.setMinimumSize(new Dimension(5, 5));
        jPanel9.setPreferredSize(new Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jPanel9, gridBagConstraints);

        lblPage.setBackground(Color.white);
        lblPage.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        lblPage.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(lblPage, gridBagConstraints);

        pnlPage.add(jPanel4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInScroll.add(pnlPage, gridBagConstraints);

        scrollPane.setViewportView(pnlInScroll);

        pnlMain.add(scrollPane, java.awt.BorderLayout.CENTER);

        jToolBar.setFloatable(false);
        jToolBar.setOpaque(false);

        btnSave.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        btnSave.setToolTipText(getBundleString("save"));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar.add(btnSave);

        btnPrint.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/printer.png"))); // NOI18N
        btnPrint.setToolTipText(getBundleString("print"));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar.add(btnPrint);

        btnReload.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        btnReload.setToolTipText(getBundleString("reload"));
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });
        jToolBar.add(btnReload);
        jToolBar.add(jSeparator1);

        btnActualSize.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/mime.png"))); // NOI18N
        btnActualSize.setToolTipText(getBundleString("actual.size"));
        btnActualSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualSizeActionPerformed(evt);
            }
        });
        jToolBar.add(btnActualSize);

        btnFitPage.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/mime2.png"))); // NOI18N
        btnFitPage.setToolTipText(getBundleString("fit.page"));
        btnFitPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitPageActionPerformed(evt);
            }
        });
        jToolBar.add(btnFitPage);

        btnFitWidth.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/mime3.png"))); // NOI18N
        btnFitWidth.setToolTipText(getBundleString("fit.width"));
        btnFitWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitWidthActionPerformed(evt);
            }
        });
        jToolBar.add(btnFitWidth);
        jToolBar.add(jSeparator2);

        btnZoomIn.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/viewmag+.png"))); // NOI18N
        btnZoomIn.setToolTipText(getBundleString("zoom.in"));
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        jToolBar.add(btnZoomIn);

        cmbZoom.setEditable(true);
        cmbZoom.setToolTipText(getBundleString("zoom.ratio"));
        cmbZoom.setMaximumSize(new Dimension(80, 23));
        cmbZoom.setMinimumSize(new Dimension(80, 23));
        cmbZoom.setPreferredSize(new Dimension(80, 23));
        cmbZoom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbZoomItemStateChanged(evt);
            }
        });
        cmbZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbZoomActionPerformed(evt);
            }
        });
        jToolBar.add(cmbZoom);

        btnZoomOut.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/viewmag-.png"))); // NOI18N
        btnZoomOut.setToolTipText(getBundleString("zoom.out"));
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        jToolBar.add(btnZoomOut);
        jToolBar.add(jSeparator3);

        btnFirst.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/2leftarrow.png"))); // NOI18N
        btnFirst.setToolTipText(getBundleString("first.page"));
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        jToolBar.add(btnFirst);

        btnPrevious.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/1leftarrow.png"))); // NOI18N
        btnPrevious.setToolTipText(getBundleString("previous.page"));
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        jToolBar.add(btnPrevious);

        txtGoTo.setToolTipText(getBundleString("go.to.page"));
        txtGoTo.setMaximumSize(new Dimension(40, 23));
        txtGoTo.setMinimumSize(new Dimension(40, 23));
        txtGoTo.setPreferredSize(new Dimension(40, 23));
        txtGoTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGoToActionPerformed(evt);
            }
        });
        jToolBar.add(txtGoTo);

        btnNext.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/1rightarrow.png"))); // NOI18N
        btnNext.setToolTipText(getBundleString("next.page"));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jToolBar.add(btnNext);

        btnLast.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/2rightarrow.png"))); // NOI18N
        btnLast.setToolTipText(getBundleString("last.page"));
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        jToolBar.add(btnLast);
        jToolBar.add(jSeparator4);

        lblStatus.setText("Page i of n");
        jToolBar.add(lblStatus);

        pnlMain.add(jToolBar, java.awt.BorderLayout.NORTH);

        add(pnlMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    void txtGoToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGoToActionPerformed
        try {
            int pageNumber = Integer.parseInt(txtGoTo.getText());
            if (
                    pageNumber != pageIndex + 1
                            && pageNumber > 0
                            && pageNumber <= jasperPrint.getPages().size()
                    ) {
                setPageIndex(pageNumber - 1);
                refreshPage();
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_txtGoToActionPerformed

    void cmbZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbZoomItemStateChanged
        // Add your handling code here:
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);
    }//GEN-LAST:event_cmbZoomItemStateChanged

    void pnlMainComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlMainComponentResized
        // Add your handling code here:
        if (btnFitPage.isSelected()) {
            fitPage();
            btnFitPage.setSelected(true);
        } else if (btnFitWidth.isSelected()) {
            setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth() - 20f) / getPageFormat().getPageWidth());
            btnFitWidth.setSelected(true);
        }

    }//GEN-LAST:event_pnlMainComponentResized

    void btnActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualSizeActionPerformed
        // Add your handling code here:
        if (btnActualSize.isSelected()) {
            btnFitPage.setSelected(false);
            btnFitWidth.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            setZoomRatio(1);
            btnActualSize.setSelected(true);
        }
    }//GEN-LAST:event_btnActualSizeActionPerformed

    void btnFitWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitWidthActionPerformed
        // Add your handling code here:
        if (btnFitWidth.isSelected()) {
            btnActualSize.setSelected(false);
            btnFitPage.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth() - 20f) / getPageFormat().getPageWidth());
            btnFitWidth.setSelected(true);
        }
    }//GEN-LAST:event_btnFitWidthActionPerformed

    void btnFitPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitPageActionPerformed
        // Add your handling code here:
        if (btnFitPage.isSelected()) {
            btnActualSize.setSelected(false);
            btnFitWidth.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            fitPage();
            btnFitPage.setSelected(true);
        }
    }//GEN-LAST:event_btnFitPageActionPerformed

    void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Add your handling code here:

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setLocale(this.getLocale());
        fileChooser.updateUI();
        for (int i = 0; i < saveContributors.size(); i++) {
            fileChooser.addChoosableFileFilter(saveContributors.get(i));
        }

        if (saveContributors.contains(lastSaveContributor)) {
            fileChooser.setFileFilter(lastSaveContributor);
        } else if (saveContributors.size() > 0) {
            fileChooser.setFileFilter(saveContributors.get(0));
        }

        if (lastFolder != null) {
            fileChooser.setCurrentDirectory(lastFolder);
        }

        int retValue = fileChooser.showSaveDialog(this);
        if (retValue == JFileChooser.APPROVE_OPTION) {
            FileFilter fileFilter = fileChooser.getFileFilter();
            File file = fileChooser.getSelectedFile();

            lastFolder = file.getParentFile();

            JRSaveContributor contributor = null;

            if (fileFilter instanceof JRSaveContributor) {
                contributor = (JRSaveContributor) fileFilter;
            } else {
                int i = 0;
                while (contributor == null && i < saveContributors.size()) {
                    contributor = saveContributors.get(i++);
                    if (!contributor.accept(file)) {
                        contributor = null;
                    }
                }

                if (contributor == null) {
                    contributor = new JRPrintSaveContributor(this.jasperReportsContext, getLocale(), this.resourceBundle);
                }
            }

            lastSaveContributor = contributor;

            try {
                contributor.save(jasperPrint, file);
            } catch (JRException e) {
                if (log.isErrorEnabled()) {
                    log.error("Save error.", e);
                }
                JOptionPane.showMessageDialog(this, getBundleString("error.saving"));
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    void pnlLinksMouseDragged(MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseDragged
        // Add your handling code here:

        Container container = pnlInScroll.getParent();
        if (container instanceof JViewport) {
            JViewport viewport = (JViewport) container;
            Point point = viewport.getViewPosition();
            int newX = point.x - (evt.getX() - downX);
            int newY = point.y - (evt.getY() - downY);

            int maxX = pnlInScroll.getWidth() - viewport.getWidth();
            int maxY = pnlInScroll.getHeight() - viewport.getHeight();

            if (newX < 0) {
                newX = 0;
            }
            if (newX > maxX) {
                newX = maxX;
            }
            if (newY < 0) {
                newY = 0;
            }
            if (newY > maxY) {
                newY = maxY;
            }

            viewport.setViewPosition(new Point(newX, newY));
        }
    }//GEN-LAST:event_pnlLinksMouseDragged

    void pnlLinksMouseReleased(MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseReleased
        // Add your handling code here:
        pnlLinks.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_pnlLinksMouseReleased

    void pnlLinksMousePressed(MouseEvent evt) {//GEN-FIRST:event_pnlLinksMousePressed
        // Add your handling code here:
        pnlLinks.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        downX = evt.getX();
        downY = evt.getY();
    }//GEN-LAST:event_pnlLinksMousePressed

    void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
    {//GEN-HEADEREND:event_btnPrintActionPerformed

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        try {

                            btnPrint.setEnabled(false);
                            JRViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            JasperPrintManager.printReport(jasperPrint, true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(JRViewer.this, getBundleString("error.printing"));
                        } finally {
                            JRViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            btnPrint.setEnabled(true);
                        }
                    }
                }
        );

    }//GEN-LAST:event_btnPrintActionPerformed

    void btnLastActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLastActionPerformed
    {//GEN-HEADEREND:event_btnLastActionPerformed
        // Add your handling code here:
        setPageIndex(jasperPrint.getPages().size() - 1);
        refreshPage();
    }//GEN-LAST:event_btnLastActionPerformed

    void btnNextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNextActionPerformed
    {//GEN-HEADEREND:event_btnNextActionPerformed
        // Add your handling code here:
        setPageIndex(pageIndex + 1);
        refreshPage();
    }//GEN-LAST:event_btnNextActionPerformed

    void btnPreviousActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPreviousActionPerformed
    {//GEN-HEADEREND:event_btnPreviousActionPerformed
        // Add your handling code here:
        setPageIndex(pageIndex - 1);
        refreshPage();
    }//GEN-LAST:event_btnPreviousActionPerformed

    void btnFirstActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFirstActionPerformed
    {//GEN-HEADEREND:event_btnFirstActionPerformed
        // Add your handling code here:
        setPageIndex(0);
        refreshPage();
    }//GEN-LAST:event_btnFirstActionPerformed

    void btnReloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnReloadActionPerformed
    {//GEN-HEADEREND:event_btnReloadActionPerformed
        // Add your handling code here:
        if (type == TYPE_FILE_NAME) {
            try {
                loadReport(reportFileName, isXML);
            } catch (JRException e) {
                if (log.isErrorEnabled()) {
                    log.error("Reload error.", e);
                }
                jasperPrint = null;
                setPageIndex(0);
                refreshPage();

                JOptionPane.showMessageDialog(this, getBundleString("error.loading"));
            }

            forceRefresh();
        }
    }//GEN-LAST:event_btnReloadActionPerformed

    protected void forceRefresh() {
        zoom = 0;//force pageRefresh()
        realZoom = 0f;
        setZoomRatio(1);
    }

    void btnZoomInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomInActionPerformed
    {//GEN-HEADEREND:event_btnZoomInActionPerformed
        // Add your handling code here:
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);

        int newZoomInt = (int) (100 * getZoomRatio());
        int index = Arrays.binarySearch(zooms, newZoomInt);
        if (index < 0) {
            setZoomRatio(zooms[-index - 1] / 100f);
        } else if (index < cmbZoom.getModel().getSize() - 1) {
            setZoomRatio(zooms[index + 1] / 100f);
        }
    }//GEN-LAST:event_btnZoomInActionPerformed

    void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomOutActionPerformed
    {//GEN-HEADEREND:event_btnZoomOutActionPerformed
        // Add your handling code here:
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);

        int newZoomInt = (int) (100 * getZoomRatio());
        int index = Arrays.binarySearch(zooms, newZoomInt);
        if (index > 0) {
            setZoomRatio(zooms[index - 1] / 100f);
        } else if (index < -1) {
            setZoomRatio(zooms[-index - 2] / 100f);
        }
    }//GEN-LAST:event_btnZoomOutActionPerformed

    void cmbZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbZoomActionPerformed
    {//GEN-HEADEREND:event_cmbZoomActionPerformed
        // Add your handling code here:
        float newZoom = getZoomRatio();

        if (newZoom < MIN_ZOOM) {
            newZoom = MIN_ZOOM;
        }

        if (newZoom > MAX_ZOOM) {
            newZoom = MAX_ZOOM;
        }

        setZoomRatio(newZoom);
    }//GEN-LAST:event_cmbZoomActionPerformed

    /**
     */
    void hyperlinkClicked(MouseEvent evt) {
        JPanel link = (JPanel) evt.getSource();
        JRPrintHyperlink element = linksMap.get(link);
        hyperlinkClicked(element);
    }

    protected void hyperlinkClicked(JRPrintHyperlink hyperlink) {
        try {
            JRHyperlinkListener listener = null;
            for (int i = 0; i < hyperlinkListeners.size(); i++) {
                listener = hyperlinkListeners.get(i);
                listener.gotoHyperlink(hyperlink);
            }
        } catch (JRException e) {
            if (log.isErrorEnabled()) {
                log.error("Hyperlink click error.", e);
            }
            JOptionPane.showMessageDialog(this, getBundleString("error.hyperlink"));
        }
    }

    /**
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     */
    private void setPageIndex(int index) {
        if (
                jasperPrint != null &&
                        jasperPrint.getPages() != null &&
                        jasperPrint.getPages().size() > 0
                ) {
            if (index >= 0 && index < jasperPrint.getPages().size()) {
                pageIndex = index;
                pageError = false;
                btnFirst.setEnabled((pageIndex > 0));
                btnPrevious.setEnabled((pageIndex > 0));
                btnNext.setEnabled((pageIndex < jasperPrint.getPages().size() - 1));
                btnLast.setEnabled((pageIndex < jasperPrint.getPages().size() - 1));
                txtGoTo.setEnabled(btnFirst.isEnabled() || btnLast.isEnabled());
                txtGoTo.setText("" + (pageIndex + 1));
                lblStatus.setText(
                        MessageFormat.format(
                                getBundleString("page"),
                                new Object[]{Integer.valueOf(pageIndex + 1), Integer.valueOf(jasperPrint.getPages().size())}
                        )
                );
            }
        } else {
            btnFirst.setEnabled(false);
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnLast.setEnabled(false);
            txtGoTo.setEnabled(false);
            txtGoTo.setText("");
            lblStatus.setText("");
        }
    }

    /**
     */
    private PrintPageFormat getPageFormat() {
        return jasperPrint.getPageFormat(pageIndex);
    }

    /**
     */
    protected void loadReport(String fileName, boolean isXmlReport) throws JRException {
        if (isXmlReport) {
            jasperPrint = JRPrintXmlLoader.loadFromFile(jasperReportsContext, fileName);
        } else {
            jasperPrint = (JasperPrint) JRLoader.loadObjectFromFile(fileName);
        }

        type = TYPE_FILE_NAME;
        this.isXML = isXmlReport;
        reportFileName = fileName;

        SimpleFileResolver fileResolver = new SimpleFileResolver(Arrays.asList(new File[]{new File(fileName).getParentFile(), new File(".")}));
        fileResolver.setResolveAbsolutePath(true);
        if (localJasperReportsContext == null) {
            localJasperReportsContext = new LocalJasperReportsContext(jasperReportsContext);
            jasperReportsContext = localJasperReportsContext;
        }
        localJasperReportsContext.setFileResolver(fileResolver);

        btnReload.setEnabled(true);
        setPageIndex(0);
    }

    /**
     */
    protected void loadReport(InputStream is, boolean isXmlReport) throws JRException {
        if (isXmlReport) {
            jasperPrint = JRPrintXmlLoader.load(jasperReportsContext, is);
        } else {
            jasperPrint = (JasperPrint) JRLoader.loadObject(is);
        }

        type = TYPE_INPUT_STREAM;
        this.isXML = isXmlReport;
        btnReload.setEnabled(false);
        setPageIndex(0);
    }

    /**
     */
    protected void loadReport(JasperPrint jrPrint) {
        jasperPrint = jrPrint;
        type = TYPE_OBJECT;
        isXML = false;
        btnReload.setEnabled(false);
        setPageIndex(0);
    }

    /**
     */
    protected void refreshPage() {
        if (
                jasperPrint == null ||
                        jasperPrint.getPages() == null ||
                        jasperPrint.getPages().size() == 0
                ) {
            pnlPage.setVisible(false);
            btnSave.setEnabled(false);
            btnPrint.setEnabled(false);
            btnActualSize.setEnabled(false);
            btnFitPage.setEnabled(false);
            btnFitWidth.setEnabled(false);
            btnZoomIn.setEnabled(false);
            btnZoomOut.setEnabled(false);
            cmbZoom.setEnabled(false);

            if (jasperPrint != null) {
                JOptionPane.showMessageDialog(this, getBundleString("no.pages"));
            }

            return;
        }

        pnlPage.setVisible(true);
        btnSave.setEnabled(true);
        btnPrint.setEnabled(true);
        btnActualSize.setEnabled(true);
        btnFitPage.setEnabled(true);
        btnFitWidth.setEnabled(true);
        btnZoomIn.setEnabled(zoom < MAX_ZOOM);
        btnZoomOut.setEnabled(zoom > MIN_ZOOM);
        cmbZoom.setEnabled(true);

        PrintPageFormat pageFormat = getPageFormat();

        Dimension dim = new Dimension(
                (int) (pageFormat.getPageWidth() * realZoom) + 8, // 2 from border, 5 from shadow and 1 extra pixel for image
                (int) (pageFormat.getPageHeight() * realZoom) + 8
        );
        pnlPage.setMaximumSize(dim);
        pnlPage.setMinimumSize(dim);
        pnlPage.setPreferredSize(dim);

        long maxImageSize = JRPropertiesUtil.getInstance(jasperReportsContext).getLongProperty(VIEWER_RENDER_BUFFER_MAX_SIZE);
        boolean renderImage;
        if (maxImageSize <= 0) {
            renderImage = false;
        } else {
            long imageSize = ((int) (pageFormat.getPageWidth() * realZoom) + 1) * ((int) (pageFormat.getPageHeight() * realZoom) + 1);
            renderImage = imageSize <= maxImageSize;
        }

        ((PageRenderer) lblPage).setRenderImage(renderImage);

        if (renderImage) {
            setPageImage();
        }

        pnlLinks.removeAll();
        linksMap = new HashMap<JPanel, JRPrintHyperlink>();

        createHyperlinks();

        if (!renderImage) {
            lblPage.setIcon(null);

            pnlMain.validate();
            pnlMain.repaint();
        }
    }

    protected void setPageImage() {
        Image image;
        if (pageError) {
            image = getPageErrorImage();
        } else {
            try {
                image = JasperPrintManager.getInstance(jasperReportsContext).printToImage(jasperPrint, pageIndex, realZoom);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Print page to image error.", e);
                }
                pageError = true;

                image = getPageErrorImage();
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString("error.displaying"));
            }
        }
        ImageIcon imageIcon = new ImageIcon(image);
        lblPage.setIcon(imageIcon);
    }

    protected Image getPageErrorImage() {
        PrintPageFormat pageFormat = getPageFormat();
        Image image = new BufferedImage(
                (int) (pageFormat.getPageWidth() * realZoom) + 1,
                (int) (pageFormat.getPageHeight() * realZoom) + 1,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D grx = (Graphics2D) image.getGraphics();
        AffineTransform transform = new AffineTransform();
        transform.scale(realZoom, realZoom);
        grx.transform(transform);

        drawPageError(grx);

        return image;
    }

    protected void createHyperlinks() {
        List<JRPrintPage> pages = jasperPrint.getPages();
        JRPrintPage page = pages.get(pageIndex);
        createHyperlinks(page.getElements(), 0, 0);
    }

    protected void createHyperlinks(List<JRPrintElement> elements, int offsetX, int offsetY) {
        if (elements != null && elements.size() > 0) {
            for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext(); ) {
                JRPrintElement element = it.next();

                ImageMapRenderable imageMap = null;
                if (element instanceof JRPrintImage) {
                    Renderable renderer = ((JRPrintImage) element).getRenderable();
                    if (renderer instanceof ImageMapRenderable) {
                        imageMap = (ImageMapRenderable) renderer;
                        if (!imageMap.hasImageAreaHyperlinks()) {
                            imageMap = null;
                        }
                    }
                }
                boolean hasImageMap = imageMap != null;

                JRPrintHyperlink hyperlink = null;
                if (element instanceof JRPrintHyperlink) {
                    hyperlink = (JRPrintHyperlink) element;
                }
                boolean hasHyperlink = !hasImageMap
                        && hyperlink != null && hyperlink.getHyperlinkTypeValue() != HyperlinkTypeEnum.NONE;
                boolean hasTooltip = hyperlink != null && hyperlink.getHyperlinkTooltip() != null;

                if (hasHyperlink || hasImageMap || hasTooltip) {
                    JPanel link;
                    if (hasImageMap) {
                        Rectangle renderingArea = new Rectangle(0, 0, element.getWidth(), element.getHeight());
                        link = new ImageMapPanel(renderingArea, imageMap);
                    } else //hasImageMap
                    {
                        link = new JPanel();
                        if (hasHyperlink) {
                            link.addMouseListener(mouseListener);
                        }
                    }

                    if (hasHyperlink) {
                        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }

                    link.setLocation(
                            (int) ((element.getX() + offsetX) * realZoom),
                            (int) ((element.getY() + offsetY) * realZoom)
                    );
                    link.setSize(
                            (int) (element.getWidth() * realZoom),
                            (int) (element.getHeight() * realZoom)
                    );
                    link.setOpaque(false);

                    String toolTip = getHyperlinkTooltip(hyperlink);
                    if (toolTip == null && hasImageMap) {
                        toolTip = "";//not null to register the panel as having a tool tip
                    }
                    link.setToolTipText(toolTip);

                    pnlLinks.add(link);
                    linksMap.put(link, hyperlink);
                }

                if (element instanceof JRPrintFrame) {
                    JRPrintFrame frame = (JRPrintFrame) element;
                    int frameOffsetX = offsetX + frame.getX() + frame.getLineBox().getLeftPadding().intValue();
                    int frameOffsetY = offsetY + frame.getY() + frame.getLineBox().getTopPadding().intValue();
                    createHyperlinks(frame.getElements(), frameOffsetX, frameOffsetY);
                }
            }
        }
    }

    protected String getHyperlinkTooltip(JRPrintHyperlink hyperlink) {
        String toolTip;
        toolTip = hyperlink.getHyperlinkTooltip();
        if (toolTip == null) {
            toolTip = getFallbackTooltip(hyperlink);
        }
        return toolTip;
    }

    protected String getFallbackTooltip(JRPrintHyperlink hyperlink) {
        String toolTip = null;
        switch (hyperlink.getHyperlinkTypeValue()) {
            case REFERENCE: {
                toolTip = hyperlink.getHyperlinkReference();
                break;
            }
            case LOCAL_ANCHOR: {
                if (hyperlink.getHyperlinkAnchor() != null) {
                    toolTip = "#" + hyperlink.getHyperlinkAnchor();
                }
                break;
            }
            case LOCAL_PAGE: {
                if (hyperlink.getHyperlinkPage() != null) {
                    toolTip = "#page " + hyperlink.getHyperlinkPage();
                }
                break;
            }
            case REMOTE_ANCHOR: {
                toolTip = "";
                if (hyperlink.getHyperlinkReference() != null) {
                    toolTip = toolTip + hyperlink.getHyperlinkReference();
                }
                if (hyperlink.getHyperlinkAnchor() != null) {
                    toolTip = toolTip + "#" + hyperlink.getHyperlinkAnchor();
                }
                break;
            }
            case REMOTE_PAGE: {
                toolTip = "";
                if (hyperlink.getHyperlinkReference() != null) {
                    toolTip = toolTip + hyperlink.getHyperlinkReference();
                }
                if (hyperlink.getHyperlinkPage() != null) {
                    toolTip = toolTip + "#page " + hyperlink.getHyperlinkPage();
                }
                break;
            }
            default: {
                break;
            }
        }
        return toolTip;
    }

    /**
     */
    private void emptyContainer(Container container) {
        Component[] components = container.getComponents();

        if (components != null) {
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof Container) {
                    emptyContainer((Container) components[i]);
                }
            }
        }

        components = null;
        container.removeAll();
        container = null;
    }

    /**
     */
    private float getZoomRatio() {
        float newZoom = zoom;

        try {
            newZoom =
                    zoomDecimalFormat.parse(
                            String.valueOf(cmbZoom.getEditor().getItem())
                    ).floatValue() / 100f;
        } catch (ParseException e) {
        }

        return newZoom;
    }

    /**
     */
    public void setZoomRatio(float newZoom) {
        if (newZoom > 0) {
            cmbZoom.getEditor().setItem(
                    zoomDecimalFormat.format(newZoom * 100) + "%"
            );

            if (zoom != newZoom) {
                zoom = newZoom;
                realZoom = zoom * screenResolution / REPORT_RESOLUTION;

                refreshPage();
            }
        }
    }

    /**
     */
    private void setRealZoomRatio(float newZoom) {
        if (newZoom > 0 && realZoom != newZoom) {
            zoom = newZoom * REPORT_RESOLUTION / screenResolution;
            realZoom = newZoom;

            cmbZoom.getEditor().setItem(
                    zoomDecimalFormat.format(zoom * 100) + "%"
            );

            refreshPage();
        }
    }

    /**
     *
     */
    public void setFitWidthZoomRatio() {
        setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth() - 20f) / getPageFormat().getPageWidth());

    }

    public void setFitPageZoomRatio() {
        setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getHeight() - 20f) / getPageFormat().getPageHeight());
    }

    /**
     *
     */
    protected JRGraphics2DExporter getGraphics2DExporter() throws JRException {
        return new JRGraphics2DExporter(jasperReportsContext);
    }

    /**
     *
     */
    protected void paintPage(Graphics2D grx) {
        if (pageError) {
            paintPageError(grx);
            return;
        }

        try {
            if (exporter == null) {
                exporter = getGraphics2DExporter();
            } else {
                exporter.reset();
            }

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
            output.setGraphics2D((Graphics2D) grx.create());
            exporter.setExporterOutput(output);
            SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
            configuration.setPageIndex(pageIndex);
            configuration.setZoomRatio(realZoom);
            configuration.setOffsetX(1); //lblPage border
            configuration.setOffsetY(1);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Page paint error.", e);
            }
            pageError = true;

            paintPageError(grx);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(JRViewer.this, getBundleString("error.displaying"));
                }
            });
        }

    }

    protected void paintPageError(Graphics2D grx) {
        AffineTransform origTransform = grx.getTransform();

        AffineTransform transform = new AffineTransform();
        transform.translate(1, 1);
        transform.scale(realZoom, realZoom);
        grx.transform(transform);

        try {
            drawPageError(grx);
        } finally {
            grx.setTransform(origTransform);
        }
    }

    protected void drawPageError(Graphics grx) {
        PrintPageFormat pageFormat = getPageFormat();
        grx.setColor(Color.white);
        grx.fillRect(0, 0, pageFormat.getPageWidth() + 1, pageFormat.getPageHeight() + 1);
    }

    protected void keyNavigate(KeyEvent evt) {
        boolean refresh = true;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_PAGE_DOWN:
                dnNavigate(evt);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_PAGE_UP:
                upNavigate(evt);
                break;
            case KeyEvent.VK_HOME:
                homeEndNavigate(0);
                break;
            case KeyEvent.VK_END:
                homeEndNavigate(jasperPrint.getPages().size() - 1);
                break;
            default:
                refresh = false;
        }

        if (refresh) {
            refreshPage();
        }
    }

    private void dnNavigate(KeyEvent evt) {
        int bottomPosition = scrollPane.getVerticalScrollBar().getValue();
        scrollPane.dispatchEvent(evt);
        if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
                scrollPane.getVerticalScrollBar().getValue() == bottomPosition) &&
                pageIndex < jasperPrint.getPages().size() - 1) {
            setPageIndex(pageIndex + 1);
            if (scrollPane.isEnabled()) {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        }
    }

    private void upNavigate(KeyEvent evt) {
        if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
                scrollPane.getVerticalScrollBar().getValue() == 0) &&
                pageIndex > 0) {
            setPageIndex(pageIndex - 1);
            if (scrollPane.isEnabled()) {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        } else {
            scrollPane.dispatchEvent(evt);
        }
    }

    private void homeEndNavigate(int pageNumber) {
        setPageIndex(pageNumber);
        if (scrollPane.isEnabled()) {
            scrollPane.getVerticalScrollBar().setValue(0);
        }
    }

    /**
     *
     */
    private void fitPage() {
        PrintPageFormat pageFormat = getPageFormat();
        float heightRatio = ((float) pnlInScroll.getVisibleRect().getHeight() - 20f) / pageFormat.getPageHeight();
        float widthRatio = ((float) pnlInScroll.getVisibleRect().getWidth() - 20f) / pageFormat.getPageWidth();
        setRealZoomRatio(heightRatio < widthRatio ? heightRatio : widthRatio);
    }

    protected class ImageMapPanel extends JPanel implements MouseListener, MouseMotionListener {
        private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

        protected final List<JRPrintImageAreaHyperlink> imageAreaHyperlinks;

        public ImageMapPanel(Rectangle renderingArea, ImageMapRenderable imageMap) {
            try {
                imageAreaHyperlinks = imageMap.getImageAreaHyperlinks(renderingArea);//FIXMECHART
            } catch (JRException e) {
                throw new JRRuntimeException(e);
            }

            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public String getToolTipText(MouseEvent event) {
            String tooltip = null;
            JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(event);
            if (imageMapArea != null) {
                tooltip = getHyperlinkTooltip(imageMapArea.getHyperlink());
            }

            if (tooltip == null) {
                tooltip = super.getToolTipText(event);
            }

            return tooltip;
        }

        public void mouseDragged(MouseEvent e) {
            pnlLinksMouseDragged(e);
        }

        public void mouseMoved(MouseEvent e) {
            JRPrintImageAreaHyperlink imageArea = getImageMapArea(e);
            if (imageArea != null
                    && imageArea.getHyperlink().getHyperlinkTypeValue() != HyperlinkTypeEnum.NONE) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        }

        protected JRPrintImageAreaHyperlink getImageMapArea(MouseEvent e) {
            return getImageMapArea((int) (e.getX() / realZoom), (int) (e.getY() / realZoom));
        }

        protected JRPrintImageAreaHyperlink getImageMapArea(int x, int y) {
            JRPrintImageAreaHyperlink image = null;
            if (imageAreaHyperlinks != null) {
                for (ListIterator<JRPrintImageAreaHyperlink> it = imageAreaHyperlinks.listIterator(imageAreaHyperlinks.size()); image == null && it.hasPrevious(); ) {
                    JRPrintImageAreaHyperlink area = it.previous();
                    if (area.getArea().containsPoint(x, y)) {
                        image = area;
                    }
                }
            }
            return image;
        }

        public void mouseClicked(MouseEvent e) {
            JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(e);
            if (imageMapArea != null) {
                hyperlinkClicked(imageMapArea.getHyperlink());
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            pnlLinksMousePressed(e);
        }

        public void mouseReleased(MouseEvent e) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
            pnlLinksMouseReleased(e);
        }
    }

    /**
     */
    class PageRenderer extends JLabel {
        private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
        JRViewer viewer = null;
        private boolean renderImage;

        public PageRenderer(JRViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void paintComponent(Graphics g) {
            if (isRenderImage()) {
                super.paintComponent(g);
            } else {
                viewer.paintPage((Graphics2D) g.create());
            }
        }

        public boolean isRenderImage() {
            return renderImage;
        }

        public void setRenderImage(boolean renderImage) {
            this.renderImage = renderImage;
        }
    }
    // End of variables declaration//GEN-END:variables

}
