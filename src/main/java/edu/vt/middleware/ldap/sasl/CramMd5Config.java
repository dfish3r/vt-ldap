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
 * Contains all the configuration data for SASL Cram-MD5 authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CramMd5Config extends SaslConfig
{


  /**
   * Default constructor.
   */
  public CramMd5Config()
  {
    mechanism = Mechanism.CRAM_MD5;
  }
}
