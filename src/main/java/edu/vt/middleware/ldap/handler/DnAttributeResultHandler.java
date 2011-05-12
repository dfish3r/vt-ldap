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

import edu.vt.middleware.ldap.LdapAttributes;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Adds the search result DN as an attribute to the result set. Provides a
 * client side implementation of RFC 5020.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DnAttributeResultHandler extends CopyLdapResultHandler
{

  /**
   * Attribute name for the entry dn. The value of this constant is {@value}.
   */
  private String dnAttributeName = "entryDN";

  /**
   * Whether to add the entry dn if an attribute of the same name exists. The
   * value of this constant is {@value}.
   */
  private boolean addIfExists;


  /**
   * Returns the DN attribute name.
   *
   * @return  DN attribute name
   */
  public String getDnAttributeName()
  {
    return dnAttributeName;
  }


  /**
   * Sets the DN attribute name.
   *
   * @param  name  of the DN attribute
   */
  public void setDnAttributeName(final String name)
  {
    dnAttributeName = name;
  }


  /**
   * Returns whether to add the entryDN if an attribute of the same name exists.
   *
   * @return  whether to add the entryDN if an attribute of the same name exists
   */
  public boolean isAddIfExists()
  {
    return addIfExists;
  }


  /**
   * Sets whether to add the entryDN if an attribute of the same name exists.
   *
   * @param  b  whether to add the entryDN if an attribute of the same name
   * exists
   */
  public void setAddIfExists(final boolean b)
  {
    addIfExists = b;
  }


  /** {@inheritDoc} */
  @Override
  protected void processAttributes(final SearchCriteria sc, final LdapEntry le)
    throws LdapException
  {
    final LdapAttributes attrs = le.getLdapAttributes();
    if (attrs.getAttribute(dnAttributeName) == null) {
      attrs.addAttribute(dnAttributeName, le.getDn());
    } else if (addIfExists) {
      attrs.getAttribute(dnAttributeName).getValues().add(le.getDn());
    }
  }
}
