/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.provider.jndi.control;

import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.PasswordPolicyControl;
import edu.vt.middleware.ldap.provider.control.RequestControlHandler;
import edu.vt.middleware.ldap.provider.control.ResponseControlHandler;

/**
 * Password policy control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PasswordPolicyControlHandler
  implements RequestControlHandler<javax.naming.ldap.Control>,
             ResponseControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return PasswordPolicyControl.OID;
  }


  /** {@inheritDoc} */
  @Override
  public javax.naming.ldap.Control processRequest(final Control requestControl)
  {
    javax.naming.ldap.BasicControl ctl = null;
    if (PasswordPolicyControl.OID.equals(requestControl.getOID())) {
      final PasswordPolicyControl c = (PasswordPolicyControl) requestControl;
      ctl =  new javax.naming.ldap.BasicControl(
        c.getOID(), c.getCriticality(), null);
    }
    return ctl;
  }


  /** {@inheritDoc} */
  @Override
  public Control processResponse(
    final Control requestControl,
    final javax.naming.ldap.Control responseControl)
  {
    PasswordPolicyControl ctl = null;
    if (PasswordPolicyControl.OID.equals(responseControl.getID())) {
      ctl = PasswordPolicyControl.parsePasswordPolicy(
        responseControl.isCritical(), responseControl.getEncodedValue());
    }
    return ctl;
  }
}

