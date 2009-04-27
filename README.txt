LDAP 2.8.2 README

    This is the 2.8.2 release of the VT LDAP Java libraries.
    It is dual licensed under both the LGPL and Apache 2.
    If you have questions or comments about this library contact
    Daniel Fisher (dfisher@vt.edu)

DESCRIPTION
    LDAP is a set of Java libraries which provide easy access to a
    LDAP directory.  Methods are provided for searching, updating,
    adding, and deleting against version 2 and version 3 LDAPs.
    Support is also available for ldaps and StartTLS.
    SASL support is available for EXTERNAL, DIGEST-MD5, and CRAM-MD5.
    In addition, connection pooling is available using the LdapPool class.
    This library was tested against OpenLdap.
    See the JavaDocs for method specifics.
    This library has a command-line interface:
    execute java edu.vt.middleware.ldap.Ldap or 
      java edu.vt.middleware.ldap.Authenticator
      for a usage statement.

    LDAP now includes a login and logout servlet for adding ldap
     authentication to your web applications.
    To configure these servlets add the following your web.xml file:

    <!-- Servlet which can be used to perform ldap authentication -->
    <servlet>
      <servlet-name>Login</servlet-name>
      <servlet-class>edu.vt.middleware.ldap.servlets.LoginServlet</servlet-class>
      <!-- properties file to configure Authenticator with
           default is '/ldap.properties' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.propertiesFile</param-name>
        <param-value>/my.ldap.properties</param-value>
      </init-param>
      <!-- session attribute to set which will contain the user identifier
           default is 'user' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionId</param-name>
        <param-value>application.user</param-value>
      </init-param>
      <!-- URL of the page that collects user credentials
           default is '/' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.loginUrl</param-name>
        <param-value>login.jsp</param-value>
      </init-param>
      <!-- Error message to display if authentication fails
           default is 'Could not authenticate or authorize user' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.errorMsg</param-name>
        <param-value>Invalid credentials</param-value>
      </init-param>
      <!-- Class which extends
           edu.vt.middleware.ldap.servlets.session.SessionManager
           This param is optional, and only needed if additional
           session initialization is required
           By default the only session initialization that occurs is
           to set the sessionId to the user name -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionManager</param-name>
        <param-value>path.to.your.package.customSessionManager</param-value>
      </init-param>
    </servlet>
    <servlet-mapping>
      <servlet-name>Login</servlet-name>
      <url-pattern>/Login</url-pattern>
    </servlet-mapping>

    <!-- Servlet used to remove the session attribute set by Login -->
    <servlet>
      <servlet-name>Logout</servlet-name>
      <servlet-class>edu.vt.middleware.ldap.servlets.LogoutServlet</servlet-class>
      <!-- session attribute to remove which contains the user identifier
           if set it must match the sessionId param set in the Login servlet
           default is 'user' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionId</param-name>
        <param-value>application.user</param-value>
      </init-param>
      <!-- Class which extends
           edu.vt.middleware.ldap.servlets.session.SessionManager
           This param is optional, and only needed if additional
           session cleanup is required
           By default the only session cleanup that occurs is
           to remove the sessionId -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionManager</param-name>
        <param-value>path.to.your.package.customSessionManager</param-value>
      </init-param>
    </servlet>
    <servlet-mapping>
      <servlet-name>Logout</servlet-name>
      <url-pattern>/Logout</url-pattern>
    </servlet-mapping>

    The HTML form you use should look like this:
    <form method="post" action="Login">
      <input type="text" name="user"/>
      <input type="password" name="credential"/>
      <input type="hidden" name="url" value="<url to redirect to after login>"/>
      <input type="submit" name="login" value="Login"/>
    </form>

    Included in this package are classes for creating DSML.
    DSML can be created on the fly or converted directly from JNDI result sets.
    This library has a command-line interface:
    execute java edu.vt.middleware.ldap.dsml.util.Dsmlv2 or
      java edu.vt.middleware.ldap.dsml.util.Dsmlv1
      for a usage statement.
    In addition a servlet is provided which can act as a gateway to a LDAP.

    Included in this package are classes for creating LDIFs.
    LDIFs can be created on the fly or converted directly from JNDI result sets.
    This library has a command-line interface:
    execute java edu.vt.middleware.ldap.ldif.util.Ldif
      for a usage statement.

REQUIRED SOFTWARE
    JDK 1.2, JDK 1.3, or JDK 1.4
    If using JDK 1.3 or JDK 1.2 you must install the JNDI and JSSE libs

BUILDING
    A build.xml file is provided for building with ant.
    Edit the build.properties file to set the build options.
    To build the distribution run:
      build tar OR build zip
    This will create a release tarball in the dist directory, which is
      set in the build.properties file.
    Copy the tarball to the location you which to install from
      and then extract it.
    This project requires JDK 1.4.x to build properly, however the
    classes will run under JDK 1.2 or JDK 1.3.
