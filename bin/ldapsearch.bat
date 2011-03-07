@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined VTLDAP_HOME goto no_vtldap_home

set JAVA=%JAVA_HOME%\bin\java

set LDAP_JAR=%VTLDAP_HOME%\jars\vt-ldap-${project.version}.jar
set LIBDIR=%VTLDAP_HOME%\lib

set CLASSPATH=%LIBDIR%\commons-cli-1.2.jar;%LIBDIR%\commons-codec-1.4.jar;%LIBDIR%\commons-logging-1.1.1.jar;%LDAP_JAR%

call "%JAVA%" -cp "%CLASSPATH%" edu.vt.middleware.ldap.LdapCli %*
goto end

:no_vtldap_home
echo ERROR: VTLDAP_HOME environment variable must be set to VT Ldap install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end
