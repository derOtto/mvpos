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

package com.openbravo.pos.payment;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

/**
 * @author adrianromero
 */
public class JPaymentSelectReceipt extends JPaymentSelect {

    /**
     * Creates new form JPaymentSelect
     */
    protected JPaymentSelectReceipt(Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    /**
     * Creates new form JPaymentSelect
     */
    protected JPaymentSelectReceipt(Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    public static JPaymentSelect getDialog(Component parent) {

        Window window = getWindow(parent);

        if (window instanceof Frame) {
            return new JPaymentSelectReceipt((Frame) window, true, parent.getComponentOrientation());
        } else {
            return new JPaymentSelectReceipt((Dialog) window, true, parent.getComponentOrientation());
        }
    }

    protected void addTabs() {

        addTabPayment(new JPaymentCashCreator());
        addTabPayment(new JPaymentChequeCreator());
        addTabPayment(new JPaymentPaperCreator());
        addTabPayment(new JPaymentMagcardCreator());
        addTabPayment(new JPaymentFreeCreator());
        addTabPayment(new JPaymentDebtCreator());
        setHeaderVisible(true);
    }

    protected void setStatusPanel(boolean isPositive, boolean isComplete) {

        setAddEnabled(isPositive && !isComplete);
        setOKEnabled(isComplete);
    }

    protected PaymentInfo getDefaultPayment(double total) {
        return new PaymentInfoCash(total, total);
    }
}
