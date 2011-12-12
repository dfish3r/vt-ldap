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

import java.util.Arrays;
import java.util.Collection;

/**
 * Contains the data required to perform an ldap add operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AddRequest extends AbstractRequest
{

  /** DN to create. */
  private String createDn;

  /** Attributes to add to the newly created entry. */
  private Collection<LdapAttribute> attributes;


  /** Default constructor. */
  public AddRequest() {}


  /**
   * Creates a new add request.
   *
   * @param  dn  to create
   * @param  attrs  attributes to add
   */
  public AddRequest(final String dn, final Collection<LdapAttribute> attrs)
  {
    setDn(dn);
    setLdapAttributes(attrs);
  }


  /**
   * Returns the DN to create.
   *
   * @return  DN
   */
  public String getDn()
  {
    return createDn;
  }


  /**
   * Sets the DN to create.
   *
   * @param  dn  to create
   */
  public void setDn(final String dn)
  {
    createDn = dn;
  }


  /**
   * Returns the attributes to add.
   *
   * @return  attributes
   */
  public Collection<LdapAttribute> getLdapAttributes()
  {
    return attributes;
  }


  /**
   * Sets the attributes to add.
   *
   * @param  attrs  to add
   */
  public void setLdapAttributes(final Collection<LdapAttribute> attrs)
  {
    attributes = attrs;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::createDn=%s, attributes=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        createDn,
        attributes,
        Arrays.toString(getControls()));
  }
}
