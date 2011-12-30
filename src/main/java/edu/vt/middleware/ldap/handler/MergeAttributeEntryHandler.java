/*
  $Id: MergeAttributeResultHandler.java 2193 2011-12-15 22:01:04Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2193 $
  Updated: $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
*/
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapUtil;

/**
 * Merges the values of one or more attributes into a single attribute. The
 * merged attribute may or may not already exist on the entry. If it does exist
 * it's existing values will remain intact.
 *
 * @author  Middleware Services
 * @version  $Revision: 2193 $ $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
 */
public class MergeAttributeEntryHandler extends AbstractLdapEntryHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 827;

  /** Attribute name to add merge values into. */
  private String mergeAttributeName;

  /** Attribute names to read values from. */
  private String[] attributeNames;


  /**
   * Returns the merge attribute name.
   *
   * @return  merge attribute name
   */
  public String getMergeAttributeName()
  {
    return mergeAttributeName;
  }


  /**
   * Sets the merge attribute name.
   *
   * @param  name  of the merge attribute
   */
  public void setMergeAttributeName(final String name)
  {
    mergeAttributeName = name;
  }


  /**
   * Returns the attribute names.
   *
   * @return  attribute names
   */
  public String[] getAttributeNames()
  {
    return attributeNames;
  }


  /**
   * Sets the attribute names.
   *
   * @param  names  of the attributes
   */
  public void setAttributeNames(final String[] names)
  {
    attributeNames = names;
  }


  /** {@inheritDoc} */
  @Override
  protected void processAttributes(
    final SearchCriteria criteria, final LdapEntry entry)
    throws LdapException
  {
    boolean newAttribute = false;
    LdapAttribute mergedAttribute = entry.getAttribute(mergeAttributeName);
    if (mergedAttribute == null) {
      mergedAttribute = new LdapAttribute(mergeAttributeName);
      newAttribute = true;
    }
    for (String s : attributeNames) {
      final LdapAttribute la = entry.getAttribute(s);
      if (la != null) {
        if (la.isBinary()) {
          mergedAttribute.addBinaryValues(la.getBinaryValues());
        } else {
          mergedAttribute.addStringValues(la.getStringValues());
        }
      }
    }

    if (mergedAttribute.size() > 0 && newAttribute) {
      entry.addAttribute(mergedAttribute);
    }
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED, attributeNames, mergeAttributeName);
  }
}
