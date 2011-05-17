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
import java.util.Collection;
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

  /** String values for this attribute. */
  protected Set<String> stringValues;

  /** byte[] values for this attribute. */
  protected Set<byte[]> binaryValues;


  /** Default constructor. */
  public LdapAttribute()
  {
    this(SortBehavior.getDefaultSortBehavior(), false);
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  binary  whether this attribute contains binary values
   */
  public LdapAttribute(final boolean binary)
  {
    this(SortBehavior.getDefaultSortBehavior(), binary);
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  sb  sort behavior of this attribute
   * @param  binary  whether this attribute contains binary values
   */
  public LdapAttribute(final SortBehavior sb, final boolean binary)
  {
    super(sb);
    if (binary) {
      if (SortBehavior.UNORDERED == sortBehavior) {
        binaryValues = new HashSet<byte[]>();
      } else if (SortBehavior.ORDERED == sortBehavior) {
        binaryValues = new LinkedHashSet<byte[]>();
      } else if (SortBehavior.SORTED == sortBehavior) {
        binaryValues = new TreeSet<byte[]>();
      }
    } else {
      if (SortBehavior.UNORDERED == sortBehavior) {
        stringValues = new HashSet<String>();
      } else if (SortBehavior.ORDERED == sortBehavior) {
        stringValues = new LinkedHashSet<String>();
      } else if (SortBehavior.SORTED == sortBehavior) {
        stringValues = new TreeSet<String>();
      }
    }
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  s  name of this attribute
   */
  public LdapAttribute(final String s)
  {
    this();
    setName(s);
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  s  name of this attribute
   * @param  values  of this attribute
   */
  public LdapAttribute(final String s, final String ... values)
  {
    this(false);
    setName(s);
    for (String value : values) {
      addStringValue(value);
    }
  }


  /**
   * Creates a new ldap attribute.
   *
   * @param  s  name of this attribute
   * @param  values  of this attribute
   */
  public LdapAttribute(final String s, final byte[] ... values)
  {
    this(true);
    setName(s);
    for (byte[] value : values) {
      addBinaryValue(value);
    }
  }


  /**
   * Returns the name of this attribute.
   *
   * @return  attribute name
   */
  public String getName()
  {
    return name;
  }


  /**
   * Sets the name of this attribute.
   *
   * @param  s  name to set
   */
  public void setName(final String s)
  {
    name = s;
  }


  /**
   * Returns the values of this attribute as strings. Binary data is base64
   * encoded. See {@link #convertValuesToString()}. The return collection cannot
   * be modified.
   *
   * @return  set of string attribute values
   */
  public Set<String> getStringValues()
  {
    return Collections.unmodifiableSet(convertValuesToString());
  }


  /**
   * Returns a single string value of this attribute. See
   * {@link #getStringValues()}.
   *
   * @return  single string attribute value
   */
  public String getStringValue()
  {
    final Set<String> s = getStringValues();
    if (s.size() == 0) {
      return null;
    }
    return s.iterator().next();
  }


  /**
   * Returns the values of this attribute as byte arrays. See
   * {@link #convertValuesToByteArray()}. The return collection cannot be
   * modified.
   *
   * @return  set of byte array attribute values
   */
  public Set<byte[]> getBinaryValues()
  {
    return Collections.unmodifiableSet(convertValuesToByteArray());
  }


  /**
   * Returns a single byte array value of this attribute. See
   * {@link #getBinaryValues()}.
   *
   * @return  single byte array attribute value
   */
  public byte[] getBinaryValue()
  {
    final Set<byte[]> s = getBinaryValues();
    if (s.size() == 0) {
      return null;
    }
    return s.iterator().next();
  }


  /**
   * Returns whether this ldap attribute contains a value of type byte[].
   *
   * @return  whether this ldap attribute contains a value of type byte[]
   */
  public boolean isBinary()
  {
    return binaryValues != null;
  }


  /**
   * Adds the supplied string as a value for this attribute.
   *
   * @param  value  to add
   * @throws  NullPointerException if value is null
   */
  public void addStringValue(final String ... value)
  {
    for (String s : value) {
      if (s == null) {
        throw new NullPointerException("Value cannot be null");
      }
      stringValues.add(s);
    }
  }


  /**
   * Adds all the strings in the supplied collection as values for this
   * attribute. See {@link #addStringValue(String...)}.
   *
   * @param  values  to add
   */
  public void addStringValues(final Collection<String> values)
  {
    for (String value : values) {
      addStringValue(value);
    }
  }


  /**
   * Adds the supplied byte array as a value for this attribute.
   *
   * @param  value  to add
   * @throws  NullPointerException if value is null
   */
  public void addBinaryValue(final byte[] ... value)
  {
    for (byte[] b : value) {
      if (b == null) {
        throw new NullPointerException("Value cannot be null");
      }
      binaryValues.add(b);
    }
  }


  /**
   * Adds all the byte arrays in the supplied collection as values for this
   * attribute. See {@link #addBinaryValue(byte[][])}.
   *
   * @param  values  to add
   */
  public void addBinaryValues(final Collection<byte[]> values)
  {
    for (byte[] value : values) {
      addBinaryValue(value);
    }
  }


  /**
   * Removes the supplied value from the attribute values if it exists.
   *
   * @param  value  to remove
   */
  public void removeStringValue(final String ... value)
  {
    for (String s : value) {
      stringValues.remove(s);
    }
  }


  /**
   * Removes the supplied values from the attribute values if they exists. See
   * {@link #removeStringValue(String...)}.
   *
   * @param  values  to remove
   */
  public void removeStringValues(final Collection<String> values)
  {
    for (String value : values) {
      removeStringValue(value);
    }
  }


  /**
   * Removes the supplied value from the attribute values if it exists.
   *
   * @param  value  to remove
   */
  public void removeBinaryValue(final byte[] ... value)
  {
    for (byte[] b : value) {
      binaryValues.remove(b);
    }
  }


  /**
   * Removes the supplied values from the attribute values if they exists. See
   * {@link #removeBinaryValue(byte[][])}.
   *
   * @param  values  to remove
   */
  public void removeBinaryValues(final Collection<byte[]> values)
  {
    for (byte[] value : values) {
      removeBinaryValue(value);
    }
  }


  /**
   * Returns the number of values in this ldap attribute.
   *
   * @return  number of values in this ldap attribute
   */
  public int size()
  {
    if (binaryValues != null) {
      return binaryValues.size();
    } else {
      return stringValues.size();
    }
  }


  /**
   * Removes all the values in this ldap attribute.
   */
  public void clear()
  {
    if (binaryValues != null) {
      binaryValues.clear();
    } else {
      stringValues.clear();
    }
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    hc += name != null ? name.toLowerCase().hashCode() : 0;
    if (binaryValues != null) {
      for (byte[] b : binaryValues) {
        hc += b != null ? Arrays.hashCode(b) : 0;
      }
    } else {
      for (String s : stringValues) {
        hc += s != null ? s.hashCode() : 0;
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
    if (binaryValues != null) {
      return String.format("%s%s", name, binaryValues);
    } else {
      return String.format("%s%s", name, stringValues);
    }
  }


  /**
   * Creates a new ldap attribute. The collection of values is inspected for
   * either String or byte[] and the appropriate attribute is created.
   *
   * @param  sb  sort behavior
   * @param  name  of this attribute
   * @param  values  of this attribute
   *
   * @throws  IllegalArgumentException  if values contains something other than
   * String or byte[]
   *
   * @return  ldap attribute
   */
  public static LdapAttribute createLdapAttribute(
    final SortBehavior sb, final String name, final Collection<Object> values)
  {
    final Set<String> stringValues = new HashSet<String>();
    final Set<byte[]> binaryValues = new HashSet<byte[]>();
    for (Object value : values) {
      if (value instanceof byte[]) {
        binaryValues.add((byte[]) value);
      } else if (value instanceof String) {
        stringValues.add((String) value);
      } else {
        throw new IllegalArgumentException(
          "Values must contain either String or byte[]");
      }
    }
    LdapAttribute la = null;
    if (!binaryValues.isEmpty()) {
      la = new LdapAttribute(sb, true);
      la.setName(name);
      la.addBinaryValues(binaryValues);
    } else {
      la = new LdapAttribute(sb, false);
      la.setName(name);
      la.addStringValues(stringValues);
    }
    return la;
  }


  /**
   * Converts the underlying set of values to a set of strings. Objects of type
   * byte[] are base64 encoded.
   *
   * @return  set of string values
   */
  protected Set<String> convertValuesToString()
  {
    if (stringValues != null) {
      return stringValues;
    }
    Set<String> s = null;
    if (SortBehavior.UNORDERED == sortBehavior) {
      s = new HashSet<String>();
    } else if (SortBehavior.ORDERED == sortBehavior) {
      s = new LinkedHashSet<String>();
    } else if (SortBehavior.SORTED == sortBehavior) {
      s = new TreeSet<String>();
    }
    for (byte[] value : binaryValues) {
      s.add(LdapUtil.base64Encode(value));
    }
    return s;
  }


  /**
   * Converts the underlying set of values to a set of byte[]. Objects of type
   * String are UTF-8 encoded.
   *
   * @return  set of byte array values
   */
  protected Set<byte[]> convertValuesToByteArray()
  {
    if (binaryValues != null) {
      return binaryValues;
    }
    Set<byte[]> s = null;
    if (SortBehavior.UNORDERED == sortBehavior) {
      s = new HashSet<byte[]>();
    } else if (SortBehavior.ORDERED == sortBehavior) {
      s = new LinkedHashSet<byte[]>();
    } else if (SortBehavior.SORTED == sortBehavior) {
      s = new TreeSet<byte[]>();
    }
    for (String value : stringValues) {
      s.add(LdapUtil.utf8Encode(value));
    }
    return s;
  }
}
