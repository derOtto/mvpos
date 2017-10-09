@echo off

REM    Openbravo POS is a point of sales application designed for touch screens.
REM    Copyright (C) 2007-2009 Openbravo, S.L.
REM    http://sourceforge.net/projects/openbravopos
REM
REM    This file is part of Openbravo POS.
REM
REM    Openbravo POS is free software: you can redistribute it and/or modify
REM    it under the terms of the GNU General Public License as published by
REM    the Free Software Foundation, either version 3 of the License, or
REM    (at your option) any later version.
REM
REM    Openbravo POS is distributed in the hope that it will be useful,
REM    but WITHOUT ANY WARRANTY; without even the implied warranty of
REM    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM    GNU General Public License for more details.
REM
REM    You should have received a copy of the GNU General Public License
REM    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

set DIRNAME=%~dp0

set CP="%DIRNAME%nordpos.jar"

REM LIB
set CP=%CP%;"%DIRNAME%lib/*.jar"
set CP=%CP%;"%DIRNAME%lib-ext/*"
set CP=%CP%;"%DIRNAME%lib-ext/peripheral-device/commons-csv-1.2.jar"
set CP=%CP%;"%DIRNAME%lib-jdbc/derbyclient.jar"
set CP=%CP%;"%DIRNAME%lib-jdbc/mysql-connector-java-5.1.37-bin.jar"

set CP=%CP%;"%DIRNAME%fonts/"
set CP=%CP%;"%DIRNAME%locales/"
set CP=%CP%;"%DIRNAME%reports/"
set CP=%CP%;"%DIRNAME%transformations/"
set CP=%CP%;"%DIRNAME%services/"
set CP=%CP%;"%DIRNAME%templates/"

REM start NORD POS
start /B javaw -cp %CP% -Djava.util.logging.config.file="%DIRNAME%logging.properties" -Djava.library.path="%DIRNAME%lib/Windows/i368-mingw32" -Ddirname.path="%DIRNAME%./" -DKETTLE_PLUGIN_BASE_FOLDERS="%DIRNAME%lib-ext/data-integration/plugins" com.openbravo.pos.forms.StartPOS %1
