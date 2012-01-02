/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.auth.ext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.auth.AuthenticationResponse;
import edu.vt.middleware.ldap.auth.AuthenticationResponseHandler;

/**
 * Attempts to parse the authentication response and set the account state using
 * data associated with eDirectory. The {@link
 * edu.vt.middleware.ldap.auth.Authenticator} should be configured to return
 * 'passwordExpirationTime' and 'loginGraceRemaining' attributes so they can be
 * consumed by this handler.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class EDirectoryAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    final LdapEntry entry = response.getLdapEntry();
    final LdapAttribute expTime = entry.getAttribute("passwordExpirationTime");
    final LdapAttribute loginRemaining = entry.getAttribute(
      "loginGraceRemaining");
    Calendar exp = null;
    if (expTime != null) {
      exp = Calendar.getInstance();

      final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
      try {
        exp.setTime(formatter.parse(expTime.getStringValue()));
      } catch (ParseException e) {
        throw new IllegalArgumentException("Expiration time format error", e);
      }
    }
    if (exp != null || loginRemaining != null) {
      response.setAccountState(
        new EDirectoryAccountState(
          exp,
          loginRemaining != null
            ? Integer.parseInt(loginRemaining.getStringValue()) : 0));
    }

    if (response.getAccountState() == null && response.getMessage() != null) {
      final EDirectoryAccountState.Error edError =
        EDirectoryAccountState.Error.parse(response.getMessage());
      if (edError != null) {
        response.setAccountState(new EDirectoryAccountState(edError));
      }
    }
  }
}
