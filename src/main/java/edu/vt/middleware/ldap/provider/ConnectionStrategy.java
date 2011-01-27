/*
  $Id: ConnectionHandler.java 1616 2010-09-21 17:22:27Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1616 $
  Updated: $Date: 2010-09-21 13:22:27 -0400 (Tue, 21 Sep 2010) $
*/
package edu.vt.middleware.ldap.provider;

/**
 * Enum to define the type of connection strategy.
 *
 * @author  Middleware Services
 * @version  $Revision: 1616 $
 */
public enum ConnectionStrategy
{
  /** default strategy. */
  DEFAULT,

  /** active-passive strategy. */
  ACTIVE_PASSIVE,

  /** round robin strategy. */
  ROUND_ROBIN,

  /** random strategy. */
  RANDOM,
}
