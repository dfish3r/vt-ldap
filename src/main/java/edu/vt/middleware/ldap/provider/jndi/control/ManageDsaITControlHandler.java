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

import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.ManageDsaITControl;
import edu.vt.middleware.ldap.provider.control.RequestControlHandler;

/**
 * ManageDsaIT control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ManageDsaITControlHandler
  implements RequestControlHandler<javax.naming.ldap.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return ManageDsaITControl.OID;
  }


  /** {@inheritDoc} */
  @Override
  public javax.naming.ldap.Control processRequest(final Control requestControl)
  {
    javax.naming.ldap.ManageReferralControl ctl = null;
    if (ManageDsaITControl.OID.equals(requestControl.getOID())) {
      final ManageDsaITControl c = (ManageDsaITControl) requestControl;
      ctl =  new javax.naming.ldap.ManageReferralControl(c.getCriticality());
    }
    return ctl;
  }
}
