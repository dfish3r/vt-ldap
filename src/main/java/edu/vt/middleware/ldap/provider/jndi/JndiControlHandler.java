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

import edu.vt.middleware.ldap.control.ControlFactory;
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;
import edu.vt.middleware.ldap.provider.ControlHandler;

/**
 * JNDI request control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiControlHandler
  implements ControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID(final javax.naming.ldap.Control control)
  {
    return control.getID();
  }


  /** {@inheritDoc} */
  @Override
  public javax.naming.ldap.Control processRequest(
    final RequestControl requestControl)
  {
    return new javax.naming.ldap.BasicControl(
      requestControl.getOID(),
      requestControl.getCriticality(),
      requestControl.encode());
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl processResponse(
    final javax.naming.ldap.Control responseControl)
  {
    return ControlFactory.createResponseControl(
      responseControl.getID(),
      responseControl.isCritical(),
      responseControl.getEncodedValue());
  }
}
