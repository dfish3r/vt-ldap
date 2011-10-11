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
package edu.vt.middleware.ldap.provider.jndi;

import javax.naming.NamingException;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortResponseControl;
import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing and converting controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiControls
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Request controls. */
  private Control[] controls;


  /**
   * Creates a new jndi controls.
   *
   * @param  ctls  to manage
   */
  public JndiControls(final Control[] ctls)
  {
    controls = ctls;
  }


  /**
   * Converts the underlying controls to jndi type controls.
   *
   * @return  jndi controls
   * @throws  NamingException  if the controls cannot be converted
   */
  public javax.naming.ldap.Control[] getJndiControls()
    throws NamingException
  {
    return JndiUtil.fromControls(controls);
  }


  /**
   * Reads the supplied response controls and makes updates to the underlying
   * control objects.
   *
   * @param  respControls  to read
   * @return  whether an update occurred to the underlying control objects
   * @throws  NamingException  if a response control indicates an error
   */
  public boolean processResponseControls(
    final javax.naming.ldap.Control[] respControls)
    throws NamingException
  {
    boolean updated = false;
    if (respControls != null) {
      for (javax.naming.ldap.Control c : respControls) {
        if (c instanceof PagedResultsResponseControl) {
          final PagedResultsResponseControl prrc =
            (PagedResultsResponseControl) c;
          // set paged result cookie if found
          if (prrc.getCookie() != null) {
            final PagedResultsControl prc = getControl(
              PagedResultsControl.class, controls);
            prc.setCookie(prrc.getCookie());
            updated = true;
          }
        } else if (c instanceof SortResponseControl) {
          final SortResponseControl src = (SortResponseControl) c;
          if (!src.isSorted()) {
            throw src.getException();
          }
        }
      }
    }
    return updated;
  }


  /**
   * Searches the supplied array for a control of the supplied type.
   *
   * @param  <T>  type of control to find
   * @param  type  of control to find
   * @param  controls  to search
   * @return  first value of the supplied type
   */
  @SuppressWarnings("unchecked")
  protected static <T extends Control> T getControl(
    final Class<T> type, final Control[] controls)
  {
    if (controls != null) {
      for (Control c : controls) {
        if (type.isInstance(c)) {
          return (T) c;
        }
      }
    }
    return null;
  }
}
