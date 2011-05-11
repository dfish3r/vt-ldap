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

import java.util.ArrayList;
import java.util.List;
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
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());


  /** {@inheritDoc} */
  public void process(final SearchCriteria sc, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(this.processName(sc, attr.getName()));
      final List<Object> newValues =
        new ArrayList<Object>(attr.getValues().size());
      for (Object o : attr.getValues()) {
        newValues.add(this.processValue(sc, o));
      }
      attr.getValues().clear();
      attr.getValues().addAll(newValues);
    }
  }


  /**
   * Returns the supplied named unaltered.
   *
   * @param  sc  search criteria
   * @param  name  to process
   *
   * @return  processed name
   */
  protected String processName(final SearchCriteria sc, final String name)
  {
    return name;
  }


  /**
   * Returns the supplied value unaltered.
   *
   * @param  sc  search criteria
   * @param  value  to process
   *
   * @return  processed value
   */
  protected Object processValue(final SearchCriteria sc, final Object value)
  {
    return value;
  }
}
