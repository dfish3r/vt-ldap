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

/**
 * Provides a search result handler which uses {@link BinaryAttributeHandler}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BinaryResultHandler extends CopyLdapResultHandler
{


  /** Default constructor. */
  public BinaryResultHandler()
  {
    setAttributeHandler(
      new LdapAttributeHandler[] {new BinaryAttributeHandler()});
  }
}
