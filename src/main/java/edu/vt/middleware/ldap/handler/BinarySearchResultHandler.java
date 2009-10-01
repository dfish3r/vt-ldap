/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.handler;

/**
 * <code>BinarySearchResultHandler</code> provides a search result handler
 * which uses {@link BinaryAttributeHandler}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BinarySearchResultHandler extends CopySearchResultHandler
{


  /**
   * Creates a new <code>BinarySearchResultHandler</code>.
   */
  public BinarySearchResultHandler()
  {
    this.setAttributeHandler(
      new AttributeHandler[]{new BinaryAttributeHandler()});
  }
}
