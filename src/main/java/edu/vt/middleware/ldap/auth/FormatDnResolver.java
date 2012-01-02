/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a DN by applying a formatter. See {@link java.util.Formatter}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class FormatDnResolver implements DnResolver
{

  /** log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** format of DN. */
  private String formatString;

  /** format arguments. */
  private Object[] formatArgs;


  /** Default constructor. */
  public FormatDnResolver() {}


  /**
   * Creates a new format DN resolver.
   *
   * @param  format  formatter string
   */
  public FormatDnResolver(final String format)
  {
    setFormat(format);
  }


  /**
   * Creates a new format DN resolver with the supplied format and arguments.
   *
   * @param  format  to set formatter string
   * @param  args  to set formatter arguments
   */
  public FormatDnResolver(final String format, final Object[] args)
  {
    setFormat(format);
    setFormatArgs(args);
  }


  /**
   * Returns the formatter string used to return the entry DN.
   *
   * @return  user field
   */
  public String getFormat()
  {
    return formatString;
  }


  /**
   * Sets the formatter string used to return the entry DN.
   *
   * @param  format  formatter string
   */
  public void setFormat(final String format)
  {
    formatString = format;
  }


  /**
   * Gets the format arguments.
   *
   * @return  format args
   */
  public Object[] getFormatArgs()
  {
    return formatArgs;
  }


  /**
   * Sets the format arguments.
   *
   * @param  args  to set format arguments
   */
  public void setFormatArgs(final Object[] args)
  {
    formatArgs = args;
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
      logger.debug("Formatting DN with {}", formatString);
      if (formatArgs != null && formatArgs.length > 0) {
        final Object[] args = new Object[formatArgs.length + 1];
        args[0] = user;
        System.arraycopy(formatArgs, 0, args, 1, formatArgs.length);
        dn = String.format(formatString, args);
      } else {
        dn = String.format(formatString, user);
      }
    } else {
      logger.debug("User input was empty or null");
    }
    return dn;
  }
}
