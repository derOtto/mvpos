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

package com.openbravo.data.user;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author adrian
 */
public class DirtyManager implements DocumentListener, ChangeListener, ActionListener, PropertyChangeListener {

    protected Vector listeners = new Vector();
    private boolean m_bDirty;

    /**
     * Creates a new instance of DirtyManager
     */
    public DirtyManager() {
        m_bDirty = false;
    }

    public void addDirtyListener(DirtyListener l) {
        listeners.add(l);
    }

    public void removeDirtyListener(DirtyListener l) {
        listeners.remove(l);
    }

    protected void fireChangedDirty() {

        Enumeration e = listeners.elements();
        while (e.hasMoreElements()) {
            DirtyListener l = (DirtyListener) e.nextElement();
            l.changedDirty(m_bDirty);
        }
    }

    public boolean isDirty() {
        return m_bDirty;
    }

    public void setDirty(boolean bValue) {

        if (m_bDirty != bValue) {
            m_bDirty = bValue;
            fireChangedDirty();
        }
    }

    public void changedUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void insertUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void removeUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void stateChanged(ChangeEvent e) {
        setDirty(true);
    }

    public void actionPerformed(ActionEvent e) {
        setDirty(true);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //if ("image".equals(evt.getPropertyName())) {
        setDirty(true);
        //}
    }

}
