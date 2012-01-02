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
package edu.vt.middleware.ldap.provider;

/**
 * Enum to define the type of connection strategy.
 *
 * @author  Middleware Services
 * @version  $Revision: 1616 $
 */
public enum ConnectionStrategy {

  /** default strategy. */
  DEFAULT,

  /** active-passive strategy. */
  ACTIVE_PASSIVE,

  /** round robin strategy. */
  ROUND_ROBIN,

  /** random strategy. */
  RANDOM,
}
