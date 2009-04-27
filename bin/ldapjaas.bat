@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined VTLDAP_HOME goto no_vtldap_home

set JAVA=%JAVA_HOME%\bin\java

set LDAP_JAR=%VTLDAP_HOME%\jars\vt-ldap-3.0.jar
set LIBDIR=%VTLDAP_HOME%\lib

set JAAS_OPTS=-Djava.security.auth.login.config=properties/ldap_jaas.config

set CLASSPATH=%LIBDIR%\commons-cli-1.1.jar;%LIBDIR%\commons-codec-1.3.jar;%LIBDIR%\commons-logging-1.1.1.jar;%LIBDIR%\dom4j-1.6.1.jar;%LDAP_JAR%

call "%JAVA%" "%JAAS_OPTS%" -cp "%CLASSPATH%" edu.vt.middleware.ldap.jaas.LdapLoginModule %*
goto end

:no_vtldap_home
echo ERROR: VTLDAP_HOME environment variable must be set to VT Ldap install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end
