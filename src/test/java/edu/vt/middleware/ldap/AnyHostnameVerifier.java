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
package edu.vt.middleware.ldap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * <code>AnyHostnameVerifier</code> returns true for any host.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AnyHostnameVerifier implements HostnameVerifier
{


  /** {@inheritDoc} */
  public boolean verify(final String hostname, final SSLSession seession)
  {
    return true;
  }


  /**
   * Dummy getter method.
   *
   * @return  'foo'
   */
  public String getFoo()
  {
    return "foo";
  }


  /**
   * Dummy setter method. Noop.
   *
   * @param  s  <code>String</code>
   */
  public void setFoo(final String s) {}


  /**
   * Dummy getter method.
   *
   * @return  true
   */
  public boolean getBar()
  {
    return true;
  }


  /**
   * Dummy setter method. Noop.
   *
   * @param  b  <code>boolean</code>
   */
  public void setBar(final boolean b) {}
}
