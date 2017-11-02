package com.nordpos.device.receiptprinter.html;

import com.jcraft.jsch.*;
import com.nordpos.device.receiptprinter.DevicePrinter;
import com.nordpos.device.writter.WritterFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Properties;

public class DevicePrinterHTMLServer extends DevicePrinterHTML implements DevicePrinter {

    private static String id = LocalDateTime.now().toString().replaceAll("[:.]", "-");
    private static String fileName = id + ".html";
    private static String tempFile = System.getProperty("java.io.tmpdir") + "/" + fileName;
    private String server;
    private String user;
    private String key;
    private String workingdir;

    public DevicePrinterHTMLServer(String server, String user, String key, String workingdir) {
        super(new WritterFile(tempFile), id);
        this.server = server;
        this.user = user;
        this.key = key;
        this.workingdir = workingdir;
    }

    private void uploadFile() {
        String SFTPHOST = server;
        int SFTPPORT = 22;
        String SFTPUSER = user;
        String SFTPKEY = key;
        String SFTPWORKINGDIR = workingdir;

        Session session;
        Channel channel;
        ChannelSftp channelSftp;

        try {
            JSch jSch = new JSch();
            jSch.addIdentity(SFTPKEY);

            session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File(tempFile);
            channelSftp.put(new FileInputStream(f), f.getName());
            channelSftp.disconnect();
            channel.disconnect();

            /* nothing to execute
            //execute script
            String command = "/bin/bash /var/www/html/.makecontent.sh";

            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);

            channel.connect();

            while (true) {
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            channel.disconnect(); */
            session.disconnect();

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beginReceipt() {
        id = LocalDateTime.now().toString().replaceAll("[:.]", "-");
        fileName = id + ".html";
        tempFile = System.getProperty("java.io.tmpdir") + "/" + fileName;
        setId(id);
        setWritterFile(new WritterFile(tempFile));
        super.beginReceipt();
    }

    @Override
    public void endReceipt() {
        super.endReceipt();
        uploadFile();
    }
}
