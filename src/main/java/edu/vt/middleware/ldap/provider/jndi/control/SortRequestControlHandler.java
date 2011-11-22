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
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.SortRequestControl;
import edu.vt.middleware.ldap.provider.control.RequestControlHandler;
import edu.vt.middleware.ldap.provider.jndi.JndiUtil;

/**
 * Sort request control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortRequestControlHandler
  implements RequestControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return SortRequestControl.OID;
  }


  /** {@inheritDoc} */
  @Override
  public javax.naming.ldap.Control processRequest(
    final RequestControl requestControl)
  {
    javax.naming.ldap.SortControl ctl = null;
    if (SortRequestControl.OID.equals(requestControl.getOID())) {
      final SortRequestControl c = (SortRequestControl) requestControl;
      try {
        ctl = new javax.naming.ldap.SortControl(
          JndiUtil.fromSortKey(c.getSortKeys()), c.getCriticality());
      } catch (IOException e) {
        throw new IllegalArgumentException("Error creating control.", e);
      }
    }
    return ctl;
  }
}
