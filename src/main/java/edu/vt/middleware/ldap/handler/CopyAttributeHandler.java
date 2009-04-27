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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

/**
 * <code>CopyAttributeHandler</code> converts a NamingEnumeration of attribute
 * into a List of attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopyAttributeHandler extends CopyResultHandler<Attribute>
  implements AttributeHandler
{


  /**
   * This will return a deep copy of the supplied <code>Attribute</code>.
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  attr  <code>Attribute</code> to copy
   *
   * @return  <code>Attribute</code>
   *
   * @throws  NamingException  if the attribute values cannot be read
   */
  protected Attribute processResult(
    final SearchCriteria sc,
    final Attribute attr)
    throws NamingException
  {
    Attribute newAttr = null;
    if (attr != null) {
      newAttr = new BasicAttribute(attr.getID(), attr.isOrdered());

      final NamingEnumeration<?> en = attr.getAll();
      while (en.hasMore()) {
        newAttr.add(this.processValue(sc, en.next()));
      }
    }
    return newAttr;
  }


  /**
   * This returns the supplied value unaltered.
   *
   * @param  sc  <code>LdapSearchCritieria</code> used to find enumeration
   * @param  value  <code>Object</code> to process
   *
   * @return  <code>Object</code>
   */
  protected Object processValue(final SearchCriteria sc, final Object value)
  {
    return value;
  }
}
