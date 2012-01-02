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
package edu.vt.middleware.ldap.pool;

/**
 * Enum to define connection pool types.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum ConnectionPoolType {

  /** blocking. */
  BLOCKING,

  /** soft limit. */
  SOFTLIMIT;
}
