/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.bean;

import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import edu.vt.middleware.ldap.LdapUtil;

/**
 * <code>AbstractLdapAttribute</code> provides a base implementation of
 * <code>LdapAttribute</code> where the underlying values are backed by a
 * <code>Set</code>.
 *
 * @param  <T>  type of backing set
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapAttribute<T extends Set<Object>>
  extends AbstractLdapBean implements LdapAttribute
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 41;

  /** Name for this attribute. */
  protected String name;

  /** Values for this attribute. */
  protected Set<Object> values;


  /**
   * Creates a new <code>AbstractLdapAttribute</code> with the supplied ldap
   * bean factory.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public AbstractLdapAttribute(final LdapBeanFactory lbf)
  {
    super(lbf);
  }


  /** {@inheritDoc} */
  public String getName()
  {
    return this.name;
  }


  /** {@inheritDoc} */
  public Set<Object> getValues()
  {
    return this.values;
  }


  /** {@inheritDoc} */
  public abstract Set<String> getStringValues();


  /** {@inheritDoc} */
  public void setAttribute(final Attribute attribute)
    throws NamingException
  {
    this.setName(attribute.getID());
    final NamingEnumeration<?> ne = attribute.getAll();
    while (ne.hasMore()) {
      this.values.add(ne.next());
    }
  }


  /** {@inheritDoc} */
  public void setName(final String name)
  {
    this.name = name;
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (this.name != null) {
      hc += this.name.hashCode();
    }
    for (String s : this.getStringValues()) {
      if (s != null) {
        hc += s.hashCode();
      }
    }
    return hc;
  }


  /**
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("%s%s", this.name, this.values);
  }


  /** {@inheritDoc} */
  public Attribute toAttribute()
  {
    final Attribute attribute = new BasicAttribute(this.name);
    for (Object o : this.values) {
      attribute.add(o);
    }
    return attribute;
  }


  /**
   * Converts the underlying set of objects to a set of strings.
   * Objects of type byte[] are base64 encoded. Objects which are not of type
   * String or byte[] are converted using Object.toString().
   *
   * @param  stringValues  <code>Set</code> to populate with strings
   */
  protected void convertValuesToString(final Set<String> stringValues)
  {
    for (Object o : this.values) {
      if (o != null) {
        if (o instanceof String) {
          stringValues.add((String) o);
        } else if (o instanceof byte[]) {
          final String encodedValue = LdapUtil.base64Encode((byte[]) o);
          if (encodedValue != null) {
            stringValues.add(encodedValue);
          }
        } else {
          stringValues.add(o.toString());
        }
      }
    }
  }
}
