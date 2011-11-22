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

import java.io.IOException;

import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.control.ManageDsaITControl;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.control.PasswordPolicyControl;
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;
import edu.vt.middleware.ldap.control.SortRequestControl;
import edu.vt.middleware.ldap.control.SortResponseControl;
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
    javax.naming.ldap.Control ctl = null;
    if (ManageDsaITControl.OID.equals(requestControl.getOID())) {
      ctl =  new javax.naming.ldap.ManageReferralControl(
        requestControl.getCriticality());
    } else if (SortRequestControl.OID.equals(requestControl.getOID())) {
      final SortRequestControl c = (SortRequestControl) requestControl;
      try {
        ctl = new javax.naming.ldap.SortControl(
          JndiUtil.fromSortKey(c.getSortKeys()), c.getCriticality());
      } catch (IOException e) {
        throw new IllegalArgumentException("Error creating control.", e);
      }
    } else if (PagedResultsControl.OID.equals(requestControl.getOID())) {
      final PagedResultsControl c = (PagedResultsControl) requestControl;
      try {
        ctl = new javax.naming.ldap.PagedResultsControl(
          c.getSize(), c.getCookie(), c.getCriticality());
      } catch (IOException e) {
        throw new IllegalArgumentException("Error creating control.", e);
      }
    } else if (PasswordPolicyControl.OID.equals(requestControl.getOID())) {
      final PasswordPolicyControl c = (PasswordPolicyControl) requestControl;
      ctl =  new javax.naming.ldap.BasicControl(
        c.getOID(), c.getCriticality(), null);
    }
    return ctl;
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl processResponse(
    final RequestControl requestControl,
    final javax.naming.ldap.Control responseControl)
  {
    ResponseControl ctl = null;
    if (SortResponseControl.OID.equals(responseControl.getID())) {
      final javax.naming.ldap.SortResponseControl c =
        (javax.naming.ldap.SortResponseControl) responseControl;
      ctl = new SortResponseControl(
        ResultCode.valueOf(c.getResultCode()),
        c.getAttributeID(),
        c.isCritical());
    } else if (PagedResultsControl.OID.equals(responseControl.getID())) {
      final javax.naming.ldap.PagedResultsResponseControl c =
        (javax.naming.ldap.PagedResultsResponseControl) responseControl;
      ctl = (PagedResultsControl) requestControl;
      ((PagedResultsControl) ctl).setCookie(c.getCookie());
    } else if (PasswordPolicyControl.OID.equals(responseControl.getID())) {
      ctl = PasswordPolicyControl.parsePasswordPolicy(
        responseControl.isCritical(), responseControl.getEncodedValue());
    }
    return ctl;
  }
}
