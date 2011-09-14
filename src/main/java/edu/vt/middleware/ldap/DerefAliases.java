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
 * Enum to define how aliases are dereferenced.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public enum DerefAliases
{

  /** never dereference aliases. */
  NEVER,

  /** dereference when searching the entries beneath the starting point but
      not when searching for the starting entry. */
  SEARCHING,

  /** dereference when searching for the starting entry but not when searching
      the entries beneath the starting point. */
  FINDING,

  /** dereference when searching for the starting entry and when searching the
      entries beneath the starting point. */
  ALWAYS;
}
