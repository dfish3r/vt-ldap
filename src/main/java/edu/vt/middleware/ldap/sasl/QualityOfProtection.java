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
package edu.vt.middleware.ldap.sasl;

/**
 * Enum to define SASL quality of protection.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum QualityOfProtection
{
  /** Authentication only. */
  AUTH,

  /** Authentication with integrity protection. */
  AUTH_INT,

  /** Authentication with integrity and privacy protection. */
  AUTH_CONF;
}
