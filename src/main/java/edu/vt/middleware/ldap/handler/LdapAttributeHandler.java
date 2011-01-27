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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapAttribute;

/**
 * Provides post search processing of an ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface LdapAttributeHandler extends ResultHandler<LdapAttribute> {}
