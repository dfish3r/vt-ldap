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
package edu.vt.middleware.ldap.auth.ext;

import java.util.Calendar;
import edu.vt.middleware.ldap.auth.AuthenticationResponse;
import edu.vt.middleware.ldap.auth.AuthenticationResponseHandler;
import edu.vt.middleware.ldap.control.PasswordPolicyControl;
import edu.vt.middleware.ldap.control.ResponseControl;

/**
 * Attempts to parse the authentication response message and set the account
 * state using data associated with a password policy control.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class PasswordPolicyAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    if (response.getControls() != null) {
      for (ResponseControl control : response.getControls()) {
        if (control instanceof PasswordPolicyControl) {
          final PasswordPolicyControl ppc = (PasswordPolicyControl) control;
          Calendar exp = null;
          if (ppc.getTimeBeforeExpiration() > 0) {
            exp = Calendar.getInstance();
            exp.add(Calendar.SECOND, ppc.getTimeBeforeExpiration());
          }
          if (exp != null || ppc.getGraceAuthNsRemaining() > 0) {
            response.setAccountState(
              new PasswordPolicyAccountState(
                exp, ppc.getGraceAuthNsRemaining()));
          }
          if (response.getAccountState() == null && ppc.getError() != null) {
            response.setAccountState(
              new PasswordPolicyAccountState(ppc.getError()));
          }
          break;
        }
      }
    }
  }
}
