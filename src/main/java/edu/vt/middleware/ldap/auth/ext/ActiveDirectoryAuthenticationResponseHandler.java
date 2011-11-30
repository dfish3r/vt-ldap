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

import edu.vt.middleware.ldap.auth.AuthenticationResponse;
import edu.vt.middleware.ldap.auth.AuthenticationResponseHandler;

/**
 * Attempts to parse the authentication response message and set the account
 * state using data associated with active directory.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class ActiveDirectoryAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    if (response.getMessage() != null) {
      final ActiveDirectoryAccountState.Error adError =
        ActiveDirectoryAccountState.Error.parse(response.getMessage());
      if (adError != null) {
        response.setAccountState(new ActiveDirectoryAccountState(adError));
      }
    }
  }
}
