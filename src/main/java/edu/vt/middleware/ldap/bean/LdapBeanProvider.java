/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.bean;

import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.vt.middleware.ldap.servlets.AttributeServlet;

/**
 * <code>LdapBeanProvider</code> provides a single source for ldap bean
 * types and configuration.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LdapBeanProvider
{

  /** bean factory class name. */
  public static final String BEAN_FACTORY =
    "edu.vt.middleware.ldap.beanFactory";

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(LdapBeanProvider.class);

  /** single instance of the ldap bean provider. */
  private static final LdapBeanProvider INSTANCE = new LdapBeanProvider();

  /** factory used to create ldap beans. */
  private static LdapBeanFactory beanFactory;


  /** Default constructor. */
  private LdapBeanProvider()
  {
    final String beanFactoryClass = System.getProperty(BEAN_FACTORY);
    if (beanFactoryClass != null) {
      try {
        beanFactory = (LdapBeanFactory) Class.forName(
          beanFactoryClass).newInstance();
        if (LOG.isInfoEnabled()) {
          LOG.info("Set provider bean factory to " + beanFactoryClass);
        }
      } catch (ClassNotFoundException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error instantiating " + beanFactoryClass, e);
        }
      } catch (InstantiationException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error instantiating " + beanFactoryClass, e);
        }
      } catch (IllegalAccessException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error instantiating " + beanFactoryClass, e);
        }
      }
    } else {
      // set default ldap bean factory to unordered
      beanFactory = new UnorderedLdapBeanFactory();
    }
  }


  /**
   * Returns the instance of this <code>LdapBeanProvider</code>.
   *
   * @return  <code>LdapBeanProvider</code>
   */
  public static LdapBeanProvider getInstance()
  {
    return INSTANCE;
  }


  /**
   * Returns the factory for creating ldap beans.
   *
   * @return  <code>LdapBeanFactory</code>
   */
  public static LdapBeanFactory getLdapBeanFactory()
  {
    return beanFactory;
  }


  /**
   * Sets the factory for creating ldap beans.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public static void setLdapBeanFactory(final LdapBeanFactory lbf)
  {
    if (lbf != null) {
      beanFactory = lbf;
    }
  }
}
