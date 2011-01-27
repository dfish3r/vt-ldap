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
 * Contains the data required to perform an ldap rename operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class RenameRequest implements LdapRequest
{
  /** DN to rename. */
  protected String oldRenameDn;

  /** New DN. */
  protected String newRenameDn;


  /** Default constructor. */
  public RenameRequest() {}


  /**
   * Creates a new rename request.
   *
   * @param  oldDn  to rename
   * @param  newDn  to rename to
   */
  public RenameRequest(final String oldDn, final String newDn)
  {
    this.setDn(oldDn);
    this.setNewDn(newDn);
  }


  /**
   * Returns the DN to rename.
   *
   * @return  DN
   */
  public String getDn()
  {
    return this.oldRenameDn;
  }


  /**
   * Sets the DN to rename.
   *
   * @param  dn  to rename
   */
  public void setDn(final String dn)
  {
    this.oldRenameDn = dn;
  }


  /**
   * Returns the new DN.
   *
   * @return  DN
   */
  public String getNewDn()
  {
    return this.newRenameDn;
  }


  /**
   * Sets the new DN.
   *
   * @param  dn  to rename to
   */
  public void setNewDn(final String dn)
  {
    this.newRenameDn = dn;
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
        "%s@%d: oldRenameDn=%s, newRenameDn=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.oldRenameDn,
        this.newRenameDn);
  }
}
