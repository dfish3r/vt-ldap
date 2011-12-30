/*
  $Id: DnAttributeResultHandler.java 2193 2011-12-15 22:01:04Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2193 $
  Updated: $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
*/
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapUtil;

/**
 * Entry handler that does nothing.
 *
 * @author  Middleware Services
 * @version  $Revision: 2193 $ $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
 */
public class NoOpEntryHandler extends AbstractLdapEntryHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 887;


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(HASH_CODE_SEED, (Object) null);
  }
}
