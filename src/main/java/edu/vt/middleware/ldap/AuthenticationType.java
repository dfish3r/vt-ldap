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
package edu.vt.middleware.ldap;

/**
 * Enum to define ldap authentication types.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum AuthenticationType
{
  /** Anonymous authentication type. */
  ANONYMOUS,

  /** Simple authentication type. */
  SIMPLE,

  /** External authentication type. */
  EXTERNAL,

  /** Digest MD5 authentication type. */
  DIGEST_MD5,

  /** Cram MD5 authentication type. */
  CRAM_MD5,

  /** Kerberos authentication type. */
  GSSAPI;
}
