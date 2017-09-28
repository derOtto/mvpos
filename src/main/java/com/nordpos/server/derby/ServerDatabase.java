/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.server.derby;

import com.nordpos.server.ServerInterface;
import org.apache.derby.drda.NetworkServerControl;

import java.io.File;
import java.net.InetAddress;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class ServerDatabase implements ServerInterface {

    NetworkServerControl server;

    @Override
    public void start() throws Exception {
        server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
        System.setProperty("derby.system.home", new File(new File(System.getProperty("user.home")), ".derby-db").getAbsolutePath());
        java.io.PrintWriter consoleWriter = new java.io.PrintWriter(System.out, true);
        server.start(consoleWriter);
    }

    @Override
    public void stop() throws Exception {
        server.shutdown();
    }

}
