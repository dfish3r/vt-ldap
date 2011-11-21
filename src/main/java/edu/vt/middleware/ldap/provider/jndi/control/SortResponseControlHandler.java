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

import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.SortResponseControl;
import edu.vt.middleware.ldap.provider.control.ResponseControlHandler;

/**
 * Sort response control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortResponseControlHandler
  implements ResponseControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return SortResponseControl.OID;
  }


  /** {@inheritDoc} */
  @Override
  public Control processResponse(
    final Control requestControl,
    final javax.naming.ldap.Control responseControl)
  {
    SortResponseControl ctl = null;
    if (SortResponseControl.OID.equals(responseControl.getID())) {
      ctl = (SortResponseControl) requestControl;
      final javax.naming.ldap.SortResponseControl c =
        (javax.naming.ldap.SortResponseControl) responseControl;
      ctl = new SortResponseControl(
        ResultCode.valueOf(c.getResultCode()),
        c.getAttributeID(),
        c.isCritical());
    }
    return ctl;
  }
}
