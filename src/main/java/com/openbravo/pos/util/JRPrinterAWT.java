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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrinterAWT implements Printable {
    public static final String EXCEPTION_MESSAGE_KEY_INVALID_PAGE_RANGE = "print.invalid.page.range";
    public static final String EXCEPTION_MESSAGE_KEY_ERROR_PRINTING_REPORT = "print.error.printing.report";
    private static final Log log = LogFactory.getLog(JRPrinterAWT.class);
    /**
     *
     */
    private JasperReportsContext jasperReportsContext;
    private JasperPrint jasperPrint;
    private int pageOffset;


    /**
     *
     */
    protected JRPrinterAWT(JasperPrint jrPrint) throws JRException {
        this(DefaultJasperReportsContext.getInstance(), jrPrint);
    }


    /**
     *
     */
    public JRPrinterAWT(JasperReportsContext jasperReportsContext, JasperPrint jasperPrint) throws JRException {
        JRGraphEnvInitializer.initializeGraphEnv();

        this.jasperReportsContext = jasperReportsContext;
        this.jasperPrint = jasperPrint;
    }


    /**
     * @see #printPages(int, int, boolean)
     */
    public static boolean printPages(
            JasperPrint jrPrint,
            int firstPageIndex,
            int lastPageIndex,
            PrintService service
    ) throws JRException {
        JRPrinterAWT printer = new JRPrinterAWT(jrPrint);
        return printer.printPages(
                firstPageIndex,
                lastPageIndex,
                service
        );
    }


    /**
     * @see #printPageToImage(int, float)
     */
    public static Image printPageToImage(
            JasperPrint jrPrint,
            int pageIndex,
            float zoom
    ) throws JRException {
        JRPrinterAWT printer = new JRPrinterAWT(jrPrint);
        return printer.printPageToImage(pageIndex, zoom);
    }

    /**
     * Fix for bug ID 6255588 from Sun bug database
     *
     * @param job print job that the fix applies to
     */
    public static void initPrinterJobFields(PrinterJob job) {
        try {
            job.setPrintService(job.getPrintService());
        } catch (PrinterException e) {
        }
    }

    /**
     * @deprecated To be removed.
     */
    public static long getImageSize(JasperPrint jasperPrint, float zoom) {
        int width = (int) (jasperPrint.getPageWidth() * zoom) + 1;
        int height = (int) (jasperPrint.getPageHeight() * zoom) + 1;
        return width * height;
    }

    /**
     *
     */
    public boolean printPages(
            int firstPageIndex,
            int lastPageIndex,
            PrintService service
    ) throws JRException {
        boolean isOK = true;

        if (
                firstPageIndex < 0 ||
                        firstPageIndex > lastPageIndex ||
                        lastPageIndex >= jasperPrint.getPages().size()
                ) {
            throw
                    new JRException(
                            EXCEPTION_MESSAGE_KEY_INVALID_PAGE_RANGE,
                            new Object[]{firstPageIndex, lastPageIndex, jasperPrint.getPages().size()}
                    );
        }

        pageOffset = firstPageIndex;

        PrinterJob printJob = PrinterJob.getPrinterJob();

        // fix for bug ID 6255588 from Sun bug database
        initPrinterJobFields(printJob);

        PageFormat pageFormat = printJob.defaultPage();
        Paper paper = pageFormat.getPaper();

        printJob.setJobName("JasperReports - " + jasperPrint.getName());

        switch (jasperPrint.getOrientationValue()) {
            case LANDSCAPE: {
                pageFormat.setOrientation(PageFormat.LANDSCAPE);
                paper.setSize(jasperPrint.getPageHeight(), jasperPrint.getPageWidth());
                paper.setImageableArea(
                        0,
                        0,
                        jasperPrint.getPageHeight(),
                        jasperPrint.getPageWidth()
                );
                break;
            }
            case PORTRAIT:
            default: {
                pageFormat.setOrientation(PageFormat.PORTRAIT);
                paper.setSize(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
                paper.setImageableArea(
                        0,
                        0,
                        jasperPrint.getPageWidth(),
                        jasperPrint.getPageHeight()
                );
            }
        }

        pageFormat.setPaper(paper);

        Book book = new Book();
        book.append(this, pageFormat, lastPageIndex - firstPageIndex + 1);
        printJob.setPageable(book);
        try {
            if (service == null) {
                if (printJob.printDialog()) {
                    printJob.print();
                } else {
                    isOK = false;
                }
            } else {
                printJob.setPrintService(service);
                printJob.print();
            }
        } catch (Exception ex) {
            throw
                    new JRException(
                            EXCEPTION_MESSAGE_KEY_ERROR_PRINTING_REPORT,
                            null,
                            ex);
        }

        return isOK;
    }

    /**
     *
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (Thread.interrupted()) {
            throw new PrinterException("Current thread interrupted.");
        }

        pageIndex += pageOffset;

        if (pageIndex < 0 || pageIndex >= jasperPrint.getPages().size()) {
            return Printable.NO_SUCH_PAGE;
        }

        try {
            JRGraphics2DExporter exporter = new JRGraphics2DExporter(jasperReportsContext);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
            output.setGraphics2D((Graphics2D) graphics);
            exporter.setExporterOutput(output);
            SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
            configuration.setPageIndex(pageIndex);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (JRException e) {
            if (log.isDebugEnabled()) {
                log.debug("Print failed.", e);
            }

            throw new PrinterException(e.getMessage()); //NOPMD
        }

        return Printable.PAGE_EXISTS;
    }

    /**
     *
     */
    public Image printPageToImage(int pageIndex, float zoom) throws JRException {
        PrintPageFormat pageFormat = jasperPrint.getPageFormat(pageIndex);

        Image pageImage = new BufferedImage(
                (int) (pageFormat.getPageWidth() * zoom) + 1,
                (int) (pageFormat.getPageHeight() * zoom) + 1,
                BufferedImage.TYPE_INT_RGB
        );

        JRGraphics2DExporter exporter = new JRGraphics2DExporter(jasperReportsContext);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
        output.setGraphics2D((Graphics2D) pageImage.getGraphics());
        exporter.setExporterOutput(output);
        SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
        configuration.setPageIndex(pageIndex);
        configuration.setZoomRatio(zoom);
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return pageImage;
    }
}
