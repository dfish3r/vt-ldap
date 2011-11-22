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
package edu.vt.middleware.ldap.provider.jndi.control;

import java.io.IOException;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;
import edu.vt.middleware.ldap.provider.control.RequestControlHandler;
import edu.vt.middleware.ldap.provider.control.ResponseControlHandler;

/**
 * Paged results control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PagedResultsControlHandler
  implements RequestControlHandler<javax.naming.ldap.Control>,
             ResponseControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return PagedResultsControl.OID;
  }


  /** {@inheritDoc} */
  @Override
  public javax.naming.ldap.Control processRequest(
    final RequestControl requestControl)
  {
    javax.naming.ldap.PagedResultsControl ctl = null;
    if (PagedResultsControl.OID.equals(requestControl.getOID())) {
      final PagedResultsControl c = (PagedResultsControl) requestControl;
      try {
        ctl = new javax.naming.ldap.PagedResultsControl(
          c.getSize(), c.getCookie(), c.getCriticality());
      } catch (IOException e) {
        throw new IllegalArgumentException("Error creating control.", e);
      }
    }
    return ctl;
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl processResponse(
    final RequestControl requestControl,
    final javax.naming.ldap.Control responseControl)
  {
    PagedResultsControl ctl = null;
    if (PagedResultsControl.OID.equals(responseControl.getID())) {
      final javax.naming.ldap.PagedResultsResponseControl c =
        (javax.naming.ldap.PagedResultsResponseControl) responseControl;
      ctl = (PagedResultsControl) requestControl;
      ctl.setCookie(c.getCookie());
    }
    return ctl;
  }
}
