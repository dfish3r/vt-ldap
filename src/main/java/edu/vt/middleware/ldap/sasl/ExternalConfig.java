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
 * Contains all the configuration data for SASL EXTERNAL authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ExternalConfig extends SaslConfig
{


  /**
   * Default constructor.
   */
  public ExternalConfig()
  {
    mechanism = Mechanism.EXTERNAL;
  }
}
