package com.openbravo.pos.forms;

import gnu.io.*;
import org.mozilla.javascript.tools.shell.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * Monitor serial iButton-Reader
 *
 * @author Michael Geringer
 * @version 1.00, 24 October 2008
 */
public class IButtonWatch implements Runnable {
    private static JRootApp m_appview = null;
    private static String iButton_Key = null;
    private static String iButton_Code = "";
    private AppConfig config;
    private String portName;
    private byte[] data = new byte[400];
    private CommPortIdentifier serialPortId;
    private Enumeration enumComm;
    private SerialPort serialPort;
    private InputStream inputStream;
    private Boolean serialPortOpen = false;
    private int baudrate;
    private int dataBits;
    private int stopBits;
    private int parity;

    public IButtonWatch() {
        config = new AppConfig(new String[0]); //default Configfile in homedirectory
        config.load();
        portName = getIButtonPort();
        baudrate = getBaudrate();
        dataBits = getDataBits();
        stopBits = getStopBits();
        parity = getParity();
    }

    // workaround
    public static void putAppView(JRootApp appview) {
        m_appview = appview;
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new IButtonWatch());
        t1.start();
        while (true) {
            try {
                t1.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // call this to get code of button in reader
    public static String getIButton_Key() {
        return iButton_Key;
    }

    public void run() {
        if (!openSerialPort(portName)) {
            closeSerialPort();
            if (!openSerialPort(portName)) {
                return;
            }
        }
        try {
            serialPort.addEventListener(new serialPortEventListener());
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private boolean openSerialPort(String portName) {
        Boolean foundPort = false;
        if (serialPortOpen) {
            return false;
        }
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (portName.contentEquals(serialPortId.getName())) {
                foundPort = true;
                break;
            }
        }
        if (!foundPort) {
            System.out.println("Serialport not found: " + portName);
            return false;
        }
        try {
            serialPort = (SerialPort) serialPortId.open("PORTID", 2000);
        } catch (PortInUseException e) {
            System.out.println("Port in use");
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("no access to InputStream");
        }
        try {
            serialPort.addEventListener(new serialPortEventListener());
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException for Serialport");
        }
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("could not set parameter");
        }
        serialPortOpen = true;
        return true;
    }

    private void closeSerialPort() {
        if (serialPortOpen) {
            serialPort.close();
            serialPortOpen = false;
        }
    }

    @SuppressWarnings("StringConcatenationInLoop")
    private void serialPortDataAvail() {
        try {
            int num, start;
            String x;
            //--- iButton String ---
            // insert iButton:   ||||||EA00001235D65901'
            // remove iButton: ||||||'
            while (inputStream.available() > 0) {
                num = inputStream.read(data, 0, data.length);
                x = new String(data, 0, num);
                if (x.contains("'")) {
                    //-- Data complete --
                    iButton_Code += x;
                    if (iButton_Code.contains("||||||'")) {
                        //-- iButton removed --
                        m_appview.removeButton(iButton_Key);
                        iButton_Key = null;
                        iButton_Code = "";
                    } else {
                        start = iButton_Code.lastIndexOf("|");
                        if (start > 0) {
                            //-- iButton inserted --
                            iButton_Key = iButton_Code.substring(start + 1, start + 17);
                            iButton_Code = "";
                            m_appview.insertButton(iButton_Key);
                        }
                    }
                    iButton_Code = "";
                } else {
                    iButton_Code += x;
                }
            }
        } catch (IOException e) {
            System.err.println("error reading data: " + e.getMessage());
        }
    }

    private String getIButtonPort() {
        return config.getProperty("iButton.port");
    }

    private int getBaudrate() {
        try {
            baudrate = Integer.parseInt(config.getProperty("iButton.baudrate"));
        } catch (NumberFormatException e) {
            baudrate = 9600;
        }
        if (baudrate <= 0) {
            baudrate = 9600;
        }
        return baudrate;
    }

    private int getDataBits() {
        int readDataBits;
        try {
            readDataBits = Integer.parseInt(config.getProperty("iButton.databits"));
        } catch (NumberFormatException e) {
            readDataBits = SerialPort.DATABITS_8;
        }
        switch (readDataBits) {
            case 5: return SerialPort.DATABITS_5;
            case 6: return SerialPort.DATABITS_6;
            case 7: return SerialPort.DATABITS_7;
            default: return SerialPort.DATABITS_8;
        }
    }

    private int getStopBits() {
        int readStopBits;
        try {
            readStopBits = Integer.parseInt(config.getProperty("iButton.stopbits"));
        } catch (NumberFormatException e) {
            readStopBits = SerialPort.STOPBITS_1;
        }
        switch (readStopBits) {
            case 15: return SerialPort.STOPBITS_1_5;
            case 2: return SerialPort.STOPBITS_2;
            default: return SerialPort.STOPBITS_1;
        }
    }

    private int getParity() {
        switch (config.getProperty("iButton.parity")) {
            case "odd": return SerialPort.PARITY_ODD;
            case "even": return SerialPort.PARITY_EVEN;
            case "mark": return SerialPort.PARITY_MARK;
            case "space": return SerialPort.PARITY_SPACE;
            default: return SerialPort.PARITY_NONE;
        }
    }

    private class serialPortEventListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    try {
                        serialPortDataAvail();
                    } catch (org.pushingpixels.substance.api.UiThreadingViolationException e) {
                        //do nothing fix if time
                    }
                    serialPortDataAvail();
                    break;
                case SerialPortEvent.BI:
                case SerialPortEvent.CD:
                case SerialPortEvent.CTS:
                case SerialPortEvent.DSR:
                case SerialPortEvent.FE:
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                case SerialPortEvent.PE:
                case SerialPortEvent.RI:
                default:
            }
        }
    }
}