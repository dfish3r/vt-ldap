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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.provider.LdapProvider;
import edu.vt.middleware.ldap.ssl.CredentialConfigParser;
import edu.vt.middleware.ldap.ssl.SSLContextInitializer;

/**
 * Handles complex properties specific to the vt-ldap package.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AdvancedPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new advanced property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public AdvancedPropertyInvoker(final Class<?> c)
  {
    this.initialize(c);
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
          } else if (PropertyValueParser.isConfig(value)) {
            final PropertyValueParser configParser =
              new PropertyValueParser(value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SSLSocketFactory.class, value);
          }
        }
      } else if (LdapProvider.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          LdapProvider.class,
          value);
      } else if (HostnameVerifier.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          HostnameVerifier.class,
          value);
      } else if (AuthenticationHandler.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(
          AuthenticationHandler.class,
          value);
      } else if (DnResolver.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(DnResolver.class, value);
      } else if (LdapResultHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          LdapResultHandler.class,
          value);
      } else if (AuthenticationResultHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          AuthenticationResultHandler.class,
          value);
      } else if (AuthorizationHandler[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(
          AuthorizationHandler.class,
          value);
      } else if (ResultCode[].class.isAssignableFrom(type)) {
        newValue = this.createArrayEnumFromPropertyValue(
          ResultCode.class, value);
      } else if (Class.class.isAssignableFrom(type)) {
        newValue = this.createTypeFromPropertyValue(Class.class, value);
      } else if (Class[].class.isAssignableFrom(type)) {
        newValue = this.createArrayTypeFromPropertyValue(Class.class, value);
      } else if (type.isEnum()) {
        newValue = getEnum(type, value);
      } else if (SearchFilter.class.isAssignableFrom(type)) {
        newValue = new SearchFilter(value);
      } else if (Credential.class.isAssignableFrom(type)) {
        newValue = new Credential(value);
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
}
