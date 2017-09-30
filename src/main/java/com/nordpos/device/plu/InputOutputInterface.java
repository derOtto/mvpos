/**
 * NORD POS is a fork of Openbravo POS.
 * <p>
 * Copyright (C) 2009-2016 Nord Trading Ltd. <http://www.nordpos.com>
 * <p>
 * This file is part of NORD POS.
 * <p>
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.device.plu;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public interface InputOutputInterface {

    public DeviceInputOutput getDeviceIO(String sProperty) throws Exception;
}
