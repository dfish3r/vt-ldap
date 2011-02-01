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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Simple bean for ldap attribute. Contains a name and a set of values.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapAttribute extends AbstractLdapBean
{
  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 41;

  /** Name for this attribute. */
  protected String name;

  /** Values for this attribute. */
  protected Set<Object> values;


  /** Default constructor. */
  public LdapAttribute()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  sb  sort behavior of this attribute
   */
  public LdapAttribute(final SortBehavior sb)
  {
    super(sb);
    if (SortBehavior.UNORDERED == this.sortBehavior) {
      this.values = new HashSet<Object>();
    } else if (SortBehavior.ORDERED == this.sortBehavior) {
      this.values = new LinkedHashSet<Object>();
    } else if (SortBehavior.SORTED == this.sortBehavior) {
      this.values = new TreeSet<Object>();
    }
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  name  of this attribute
   */
  public LdapAttribute(final String name)
  {
    this();
    this.setName(name);
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  name  of this attribute
   * @param  value  of this attribute
   */
  public LdapAttribute(final String name, final Object value)
  {
    this();
    this.setName(name);
    this.getValues().add(value);
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  name  of this attribute
   * @param  values  of this attribute
   */
  public LdapAttribute(final String name, final Object[] values)
  {
    this();
    this.setName(name);
    for (Object o : values) {
      this.getValues().add(o);
    }
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  name  of this attribute
   * @param  values  of this attribute
   */
  public LdapAttribute(final String name, final Set<Object> values)
  {
    this();
    this.setName(name);
    this.getValues().addAll(values);
  }


  /**
   * Returns the name of this attribute.
   *
   * @return  attribute name
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * Sets the name of this attribute.
   *
   * @param  name  to set
   */
  public void setName(final String name)
  {
    this.name = name;
  }


  /**
   * Returns the values of this attribute.
   *
   * @return  set of attribute values
   */
  public Set<Object> getValues()
  {
    return this.values;
  }


  /**
   * Returns a single value of this attribute. If multiple values exist the
   * first value returned by the underlying iterator is used. If no values
   * exist null is returned.
   *
   * @return  single attribute value
   */
  public Object getValue()
  {
    if (this.values.size() == 0) {
      return null;
    }
    return this.values.iterator().next();
  }


  /**
   * Returns the values of this attribute as strings. Byte arrays are base64
   * encoded. See {@link #convertValuesToString()}.
   *
   * @return  set of string attribute values
   */
  public Set<String> getStringValues()
  {
    return Collections.unmodifiableSet(this.convertValuesToString());
  }


  /**
   * Returns a single string value of this attribute. See {@link #getValue()}
   * and {@link #getStringValues()}.
   *
   * @return  single string attribute value
   */
  public String getStringValue()
  {
    final Set<String> s = this.getStringValues();
    if (s.size() == 0) {
      return null;
    }
    return s.iterator().next();
  }


  /**
   * Returns the values of this attribute as byte arrays. See
   * {@link #convertValuesToByteArray()}.
   *
   * @return  set of byte array attribute values
   */
  public Set<byte[]> getBinaryValues()
  {
    return Collections.unmodifiableSet(this.convertValuesToByteArray());
  }


  /**
   * Returns a single byte array value of this attribute. See
   * {@link #getValue()} and {@link #getBinaryValues()}.
   *
   * @return  single byte array attribute value
   */
  public byte[] getBinaryValue()
  {
    final Set<byte[]> s = this.getBinaryValues();
    if (s.size() == 0) {
      return null;
    }
    return s.iterator().next();
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (this.name != null) {
      hc += this.name.hashCode();
    }
    for (Object o : this.getValues()) {
      if (o != null) {
        if (o instanceof String) {
          hc += o.hashCode();
        } else if (o instanceof byte[]) {
          hc += Arrays.hashCode((byte[]) o);
        } else {
          throw new IllegalStateException(
            "Unsupported attribute value type " + o.getClass());
        }
      }
    }
    return hc;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format("%s%s", this.name, this.values);
  }


  /**
   * Converts the underlying set of objects to a set of strings. Objects of type
   * byte[] are base64 encoded. Objects which are not of type String or byte[]
   * are converted using Object.toString().
   *
   * @return  set of string values
   */
  protected Set<String> convertValuesToString()
  {
    Set<String> s = null;
    if (SortBehavior.UNORDERED == this.sortBehavior) {
      s = new HashSet<String>();
    } else if (SortBehavior.ORDERED == this.sortBehavior) {
      s = new LinkedHashSet<String>();
    } else if (SortBehavior.SORTED == this.sortBehavior) {
      s = new TreeSet<String>();
    }
    for (Object o : this.values) {
      if (o != null) {
        if (o instanceof String) {
          s.add((String) o);
        } else if (o instanceof byte[]) {
          final String encodedValue = LdapUtil.base64Encode((byte[]) o);
          if (encodedValue != null) {
            s.add(encodedValue);
          }
        } else {
          throw new IllegalStateException(
            "Unsupported attribute value type " + o.getClass());
        }
      }
    }
    return s;
  }


  /**
   * Converts the underlying set of objects to a set of byte[]. Objects of type
   * String are UTF-8 encoded. Objects which are not of type String or byte[]
   * are serialized.
   *
   * @return  set of byte array values
   */
  protected Set<byte[]> convertValuesToByteArray()
  {
    Set<byte[]> s = null;
    if (SortBehavior.UNORDERED == this.sortBehavior) {
      s = new HashSet<byte[]>();
    } else if (SortBehavior.ORDERED == this.sortBehavior) {
      s = new LinkedHashSet<byte[]>();
    } else if (SortBehavior.SORTED == this.sortBehavior) {
      s = new TreeSet<byte[]>();
    }
    for (Object o : this.values) {
      if (o != null) {
        if (o instanceof String) {
          final byte[] encodedValue = LdapUtil.utf8Encode((String) o);
          if (encodedValue != null) {
            s.add(encodedValue);
          }
        } else if (o instanceof byte[]) {
          s.add((byte[]) o);
        } else {
          throw new IllegalStateException(
            "Unsupported attribute value type " + o.getClass());
        }
      }
    }
    return s;
  }
}