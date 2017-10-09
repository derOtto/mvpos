package com.openbravo.pos.forms;

import gnu.io.*;

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
    //- hardcoded port!!!
    private String portName = "COM1"; //TODO: change
    private byte[] data = new byte[400];
    private CommPortIdentifier serialPortId;
    private Enumeration enumComm;
    private SerialPort serialPort;
    private InputStream inputStream;
    private Boolean serialPortOpen = false;
    private int baudrate = 9600;
    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;

    // workaround
    public static void putAppView(JRootApp appview) {
        m_appview = appview;
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new IButtonWatch());
        t1.start();
        while (true) {
            System.out.println(getIButton_Key());
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
            // System.out.println("Serialport not found: " + portName);
            return false;
        }
        try {
            serialPort = serialPortId.open("PORTID", 2000);
            System.out.println("hi");
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

    private class serialPortEventListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            // System.out.println("serialPortEventlistener");
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
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