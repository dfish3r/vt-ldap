/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.props;

import java.lang.reflect.Array;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.handler.ConnectionHandler;
import edu.vt.middleware.ldap.handler.SearchResultHandler;
import edu.vt.middleware.ldap.ssl.CredentialConfigParser;
import edu.vt.middleware.ldap.ssl.SSLContextInitializer;

/**
 * <code>PropertyInvoker</code> stores setter methods for a class to make method
 * invocation by property easier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapConfigPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new <code>PropertyInvoker</code> for the supplied class.
   *
   * @param  c  <code>Class</code> that has setter methods
   * @param  propertiesDomain  <code>String</code> to prepend to each setter
   * name
   */
  public LdapConfigPropertyInvoker(
    final Class<?> c,
    final String propertiesDomain)
  {
    this.initialize(c, propertiesDomain);
  }


  /** {@inheritDoc} */
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (SSLSocketFactory.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          // use a credential reader to configure key/trust material
          if (CredentialConfigParser.isCredentialConfig(value)) {
            final CredentialConfigParser configParser =
              new CredentialConfigParser(value);
            newValue = instantiateType(
              SSLSocketFactory.class,
              configParser.getSslSocketFactoryClassName());

            final Object credentialConfig = configParser.initializeType();
            try {
              // set the SSL context initializer using the credential config
              invokeMethod(
                newValue.getClass().getMethod(
                  "setSSLContextInitializer",
                  SSLContextInitializer.class),
                newValue,
                invokeMethod(
                  credentialConfig.getClass().getMethod(
                    "createSSLContextInitializer",
                    new Class<?>[0]),
                  credentialConfig,
                  null));
              // initialize the TLS socket factory.
              invokeMethod(
                newValue.getClass().getMethod("initialize", new Class<?>[0]),
                newValue,
                null);
            } catch (NoSuchMethodException e) {
              throw new IllegalArgumentException(e);
            }
            // use a standard config to initialize the socket factory
          } else if (ConfigParser.isConfig(value)) {
            final ConfigParser configParser = new ConfigParser(value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SSLSocketFactory.class, value);
          }
        }
      } else if (HostnameVerifier.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          HostnameVerifier.class,
          value);
      } else if (ConnectionHandler.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          ConnectionHandler.class,
          value);
      } else if (AuthenticationHandler.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          AuthenticationHandler.class,
          value);
      } else if (DnResolver.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(DnResolver.class, value);
      } else if (SearchResultHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          SearchResultHandler.class,
          value);
      } else if (AuthenticationResultHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          AuthenticationResultHandler.class,
          value);
      } else if (AuthorizationHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          AuthorizationHandler.class,
          value);
      } else if (Class.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(Class.class, value);
      } else if (Class[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(Class.class, value);
      } else if (type.isEnum()) {
        for (Object o : type.getEnumConstants()) {
          final Enum<?> e = (Enum<?>) o;
          if (e.name().equals(value)) {
            newValue = o;
          }
        }
      } else if (String[].class == type) {
        newValue = value.split(",");
      } else if (Object[].class == type) {
        newValue = value.split(",");
      } else if (float.class == type) {
        newValue = Float.parseFloat(value);
      } else if (int.class == type) {
        newValue = Integer.parseInt(value);
      } else if (long.class == type) {
        newValue = Long.parseLong(value);
      } else if (short.class == type) {
        newValue = Short.parseShort(value);
      } else if (double.class == type) {
        newValue = Double.parseDouble(value);
      } else if (boolean.class == type) {
        newValue = Boolean.valueOf(value);
      }
    }
    return newValue;
  }


  /**
   * Returns the object which represents the supplied class given the supplied
   * string representation.
   *
   * @param  c  <code>Class</code> type to instantiate
   * @param  s  <code>String</code> to parse
   *
   * @return  <code>Object</code> of the supplied type or null
   */
  protected Object createTypeFromPropertyValue(final Class<?> c, final String s)
  {
    Object newObject = null;
    if ("null".equals(s)) {
      newObject = null;
    } else {
      if (ConfigParser.isConfig(s)) {
        final ConfigParser configParser = new ConfigParser(s);
        newObject = configParser.initializeType();
      } else {
        if (Class.class == c) {
          newObject = createClass(s);
        } else {
          newObject = instantiateType(c, s);
        }
      }
    }
    return newObject;
  }


  /**
   * Returns the object which represents an array of the supplied class given
   * the supplied string representation.
   *
   * @param  c  <code>Class</code> type to instantiate
   * @param  s  <code>String</code> to parse
   *
   * @return  <code>Object</code> that is an array or null
   */
  protected Object createArrayTypeFromPropertyValue(
    final Class<?> c,
    final String s)
  {
    Object newObject = null;
    if ("null".equals(s)) {
      newObject = null;
    } else {
      if (s.indexOf("},") != -1) {
        final String[] classes = s.split("\\},");
        newObject = Array.newInstance(c, classes.length);
        for (int i = 0; i < classes.length; i++) {
          classes[i] = classes[i] + "}";
          if (ConfigParser.isConfig(classes[i])) {
            final ConfigParser configParser = new ConfigParser(classes[i]);
            Array.set(newObject, i, configParser.initializeType());
          } else {
            throw new IllegalArgumentException(
              "Could not parse property string: " + classes[i]);
          }
        }
      } else {
        final String[] classes = s.split(",");
        newObject = Array.newInstance(c, classes.length);
        for (int i = 0; i < classes.length; i++) {
          if (ConfigParser.isConfig(classes[i])) {
            final ConfigParser configParser = new ConfigParser(classes[i]);
            Array.set(newObject, i, configParser.initializeType());
          } else {
            if (Class.class == c) {
              Array.set(newObject, i, createClass(classes[i]));
            } else {
              Array.set(newObject, i, instantiateType(c, classes[i]));
            }
          }
        }
      }
    }
    return newObject;
  }
}
