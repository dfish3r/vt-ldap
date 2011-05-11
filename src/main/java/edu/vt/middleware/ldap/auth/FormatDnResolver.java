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
package edu.vt.middleware.ldap.auth;

import java.io.Serializable;
import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a DN by applying a formatter. See {@link java.util.Formatter}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class FormatDnResolver implements DnResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -6508789359608064771L;

  /** log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  /** format of DN. */
  protected String format;

  /** format arguments. */
  protected Object[] formatArgs;


  /** Default constructor. */
  public FormatDnResolver() {}


  /**
   * Creates a new format DN resolver.
   *
   * @param  s  format string
   */
  public FormatDnResolver(final String s)
  {
    this.setFormat(s);
  }


  /**
   * Creates a new format DN resolver with the supplied format and arguments.
   *
   * @param  s  to set format
   * @param  o  to set format arguments
   */
  public FormatDnResolver(final String s, final Object[] o)
  {
    this.setFormat(s);
    this.setFormatArgs(o);
  }


  /**
   * Returns the format string used to return the entry DN.
   *
   * @return  user field
   */
  public String getFormat()
  {
    return this.format;
  }


  /**
   * Sets the format string used to return the entry DN.
   *
   * @param  s  format string
   */
  public void setFormat(final String s)
  {
    this.format = s;
  }


  /**
   * Gets the format arguments.
   *
   * @return  format args
   */
  public Object[] getFormatArgs()
  {
    return this.formatArgs;
  }


  /**
   * Sets the format arguments.
   *
   * @param  o  to set format arguments
   */
  public void setFormatArgs(final Object[] o)
  {
    this.formatArgs = o;
  }


  /**
   * Returns a DN for the supplied user by applying it to a format string.
   *
   * @param  user  to format dn for
   *
   * @return  user DN
   *
   * @throws  LdapException  never
   */
  public String resolve(final String user)
    throws LdapException
  {
    String dn = null;
    if (user != null && !"".equals(user)) {
      this.logger.debug("Formatting DN with {}", this.format);
      if (this.formatArgs != null && this.formatArgs.length > 0) {
        final Object[] args = new Object[this.formatArgs.length + 1];
        args[0] = user;
        System.arraycopy(this.formatArgs, 0, args, 1, this.formatArgs.length);
        dn = String.format(this.format, args);
      } else {
        dn = String.format(this.format, user);
      }
    } else {
      this.logger.debug("User input was empty or null");
    }
    return dn;
  }
}
