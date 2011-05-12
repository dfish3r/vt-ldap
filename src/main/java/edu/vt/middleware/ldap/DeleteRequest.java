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
 * Contains the data required to perform an ldap delete operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class DeleteRequest implements LdapRequest
{
  /** DN to delete. */
  protected String deleteDn;


  /** Default constructor. */
  public DeleteRequest() {}


  /**
   * Creates a new delete request.
   *
   * @param  dn  to delete
   */
  public DeleteRequest(final String dn)
  {
    setDn(dn);
  }


  /**
   * Returns the DN to delete.
   *
   * @return  DN
   */
  public String getDn()
  {
    return deleteDn;
  }


  /**
   * Sets the DN to delete.
   *
   * @param  dn  to delete
   */
  public void setDn(final String dn)
  {
    deleteDn = dn;
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
        "%s@%d: deleteDn=%s",
        getClass().getName(),
        hashCode(),
        deleteDn);
  }
}
