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
package edu.vt.middleware.ldap.provider.jndi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.AttributeModification;
import edu.vt.middleware.ldap.AttributeModificationType;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.SortBehavior;

/**
 * Provides methods for converting between JNDI specific objects and vt-ldap
 * specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiUtil
{
  /** Whether to ignore case when creating basic attributes. */
  public static final boolean DEFAULT_IGNORE_CASE = true;

  /** Ldap result sort behavior. */
  protected SortBehavior sortBehavior;


  /** Default constructor. */
  public JndiUtil()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new bean util.
   *
   * @param  sb  sort behavior of vt-ldap beans
   */
  public JndiUtil(final SortBehavior sb)
  {
    sortBehavior = sb;
  }


  /**
   * Returns a jndi attribute that represents the values in the supplied ldap
   * attribute.
   *
   * @param  la  ldap attribute
   * @return  jndi attribute
   */
  public Attribute fromLdapAttribute(final LdapAttribute la)
  {
    final Attribute attribute = new BasicAttribute(la.getName());
    if (la.isBinary()) {
      for (byte[] value : la.getBinaryValues()) {
        attribute.add(value);
      }
    } else {
      for (String value : la.getStringValues()) {
        attribute.add(value);
      }
    }
    return attribute;
  }


  /**
   * Returns an ldap attribute using the supplied jndi attribute.
   *
   * @param  a  jndi attribute
   * @return  ldap attribute
   *
   * @throws  NamingException  if the attribute values cannot be read
   */
  public LdapAttribute toLdapAttribute(final Attribute a)
    throws NamingException
  {
    final Set<Object> values = new HashSet<Object>();
    final NamingEnumeration<?> ne = a.getAll();
    while (ne.hasMore()) {
      values.add(ne.next());
    }
    return LdapAttribute.createLdapAttribute(sortBehavior, a.getID(), values);
  }


  /**
   * Returns a jndi attributes that represents the values in the supplied ldap
   * attributes.
   *
   * @param  c  ldap attributes
   * @return  jndi attributes
   */
  public Attributes fromLdapAttributes(final Collection<LdapAttribute> c)
  {
    final Attributes attributes = new BasicAttributes(DEFAULT_IGNORE_CASE);
    for (LdapAttribute a : c) {
      attributes.put(fromLdapAttribute(a));
    }
    return attributes;
  }


  /**
   * Returns a jndi search result that represents the supplied ldap entry.
   *
   * @param  le  ldap entry
   * @return  jndi search result
   */
  public SearchResult fromLdapEntry(final LdapEntry le)
  {
    return new SearchResult(
      le.getDn(), null, fromLdapAttributes(le.getAttributes()));
  }


  /**
   * Returns an ldap entry using the supplied jndi search result.
   *
   * @param  sr  jndi search result
   * @return  ldap entry
   *
   * @throws  NamingException  if the search result cannot be read
   */
  public LdapEntry toLdapEntry(final SearchResult sr)
    throws NamingException
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(sr.getName());
    final Attributes a = sr.getAttributes();
    final NamingEnumeration<? extends Attribute> ne = a.getAll();
    while (ne.hasMore()) {
      le.addAttribute(toLdapAttribute(ne.next()));
    }
    return le;
  }


  /**
   * Returns jndi modification items using the supplied attribute modifications.
   *
   * @param  am  attribute modifications
   * @return  jndi modification items
   */
  public ModificationItem[] fromAttributeModification(
    final AttributeModification[] am)
  {
    final ModificationItem[] mods = new ModificationItem[am.length];
    for (int i = 0; i < am.length; i++) {
      mods[i] = new ModificationItem(
        getAttributeModification(am[i].getAttributeModificationType()),
        fromLdapAttribute(am[i].getAttribute()));
    }
    return mods;
  }


  /**
   * Returns the jndi modification integer constant for the supplied attribute
   * modification type.
   *
   * @param  am  attribute modification type
   * @return  integer constant
   */
  protected static int getAttributeModification(
    final AttributeModificationType am)
  {
    int op = -1;
    if (am == AttributeModificationType.ADD) {
      op = LdapContext.ADD_ATTRIBUTE;
    } else if (am == AttributeModificationType.REMOVE) {
      op = LdapContext.REMOVE_ATTRIBUTE;
    } else if (am == AttributeModificationType.REPLACE) {
      op = LdapContext.REPLACE_ATTRIBUTE;
    }
    return op;
  }
}
