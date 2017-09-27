#!/bin/sh

#    Openbravo POS is a point of sales application designed for touch screens.
#    Copyright (C) 2007-2009 Openbravo, S.L.
#    http://sourceforge.net/projects/openbravopos
#
#    This file is part of Openbravo POS.
#
#    Openbravo POS is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    Openbravo POS is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

DIRNAME=`dirname $0`

CP=$DIRNAME/nordpos.jar

# LIB
CP=$CP:$DIRNAME/lib/*.jar
CP=$CP:$DIRNAME/lib-ext/*
CP=$CP:$DIRNAME/lib-ext/peripheral-device/commons-csv-1.2.jar
CP=$CP:$DIRNAME/lib-jdbc/derbyclient.jar
CP=$CP:$DIRNAME/lib-jdbc/mysql-connector-java-5.1.37-bin.jar

CP=$CP:$DIRNAME/fonts/
CP=$CP:$DIRNAME/locales/
CP=$CP:$DIRNAME/reports/
CP=$CP:$DIRNAME/transformations/
CP=$CP:$DIRNAME/services/
CP=$CP:$DIRNAME/templates/

# start NORD POS
java -cp $CP -Djava.util.logging.config.file=$DIRNAME/logging.properties -Djava.library.path=$DIRNAME$LIBRARYPATH -Ddirname.path=$DIRNAME/ -DKETTLE_PLUGIN_BASE_FOLDERS=$DIRNAME/lib-ext/data-integration/plugins com.openbravo.pos.forms.StartPOS "$@"
