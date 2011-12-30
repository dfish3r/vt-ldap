/*
  $Id: CopyLdapResultHandler.java 2193 2011-12-15 22:01:04Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2193 $
  Updated: $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
*/
package edu.vt.middleware.ldap.handler;

import java.util.HashSet;
import java.util.Set;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for entry handlers which simply returns values unaltered.
 *
 * @author  Middleware Services
 * @version  $Revision: 2193 $ $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
 */
public abstract class AbstractLdapEntryHandler implements LdapEntryHandler
{

  /** Log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public HandlerResult process(
    final SearchCriteria criteria, final LdapEntry entry)
    throws LdapException
  {
    if (entry != null) {
      entry.setDn(processDn(criteria, entry));
      processAttributes(criteria, entry);
    }
    return new HandlerResult(entry);
  }


  /**
   * Process the dn of an ldap entry.
   *
   * @param  criteria  search criteria used to find the ldap entry
   * @param  entry  ldap entry to extract the dn from
   *
   * @return  processed dn
   */
  protected String processDn(
    final SearchCriteria criteria, final LdapEntry entry)
  {
    return entry.getDn();
  }


  /**
   * Process the attributes of an ldap entry.
   *
   * @param  criteria  search criteria used to find the ldap entry
   * @param  entry  ldap entry to extract the attributes from
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttributes(
    final SearchCriteria criteria, final LdapEntry entry)
    throws LdapException
  {
    for (LdapAttribute la : entry.getAttributes()) {
      processAttribute(criteria, la);
    }
  }


  /**
   * Process a single attribute.
   *
   * @param  criteria  search criteria used to find the ldap entry
   * @param  attr  to process
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttribute(
    final SearchCriteria criteria, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(processAttributeName(criteria, attr.getName()));
      if (attr.isBinary()) {
        final Set<byte[]> newValues =
          new HashSet<byte[]>(attr.size());
        for (byte[] b : attr.getBinaryValues()) {
          newValues.add(processAttributeValue(criteria, b));
        }
        attr.clear();
        attr.addBinaryValues(newValues);
      } else {
        final Set<String> newValues =
          new HashSet<String>(attr.size());
        for (String s : attr.getStringValues()) {
          newValues.add(processAttributeValue(criteria, s));
        }
        attr.clear();
        attr.addStringValues(newValues);
      }
    }
  }


  /**
   * Returns the supplied attribute name unaltered.
   *
   * @param  criteria  search criteria
   * @param  name  to process
   *
   * @return  processed name
   */
  protected String processAttributeName(
    final SearchCriteria criteria, final String name)
  {
    return name;
  }


  /**
   * Returns the supplied attribute value unaltered.
   *
   * @param  criteria  search criteria
   * @param  value  to process
   *
   * @return  processed value
   */
  protected String processAttributeValue(
    final SearchCriteria criteria, final String value)
  {
    return value;
  }


  /**
   * Returns the supplied attribute value unaltered.
   *
   * @param  criteria  search criteria
   * @param  value  to process
   *
   * @return  processed value
   */
  protected byte[] processAttributeValue(
    final SearchCriteria criteria, final byte[] value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /** {@inheritDoc} */
  @Override
  public abstract int hashCode();
}
