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

import java.util.HashSet;
import java.util.Set;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for attribute handlers which simply returns values unaltered.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopyLdapAttributeHandler implements LdapAttributeHandler
{

  /** Log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria criteria, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(processName(criteria, attr.getName()));
      if (attr.isBinary()) {
        final Set<byte[]> newValues =
          new HashSet<byte[]>(attr.size());
        for (byte[] b : attr.getBinaryValues()) {
          newValues.add(processValue(criteria, b));
        }
        attr.clear();
        attr.addBinaryValues(newValues);
      } else {
        final Set<String> newValues =
          new HashSet<String>(attr.size());
        for (String s : attr.getStringValues()) {
          newValues.add(processValue(criteria, s));
        }
        attr.clear();
        attr.addStringValues(newValues);
      }
    }
  }


  /**
   * Returns the supplied named unaltered.
   *
   * @param  criteria  search criteria
   * @param  name  to process
   *
   * @return  processed name
   */
  protected String processName(final SearchCriteria criteria, final String name)
  {
    return name;
  }


  /**
   * Returns the supplied value unaltered.
   *
   * @param  criteria  search criteria
   * @param  value  to process
   *
   * @return  processed value
   */
  protected String processValue(
    final SearchCriteria criteria, final String value)
  {
    return value;
  }


  /**
   * Returns the supplied value unaltered.
   *
   * @param  criteria  search criteria
   * @param  value  to process
   *
   * @return  processed value
   */
  protected byte[] processValue(
    final SearchCriteria criteria, final byte[] value)
  {
    return value;
  }
}
