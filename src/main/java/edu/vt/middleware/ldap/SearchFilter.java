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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.binary.Hex;

/**
 * Simple bean for a search filter and it's arguments.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchFilter
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 89;

  /** filter. */
  private String filter;

  /** filter arguments. */
  private List<Object> filterArgs = new ArrayList<Object>();


  /** Default constructor. */
  public SearchFilter() {}


  /**
   * Creates a new search filter with the supplied filter.
   *
   * @param  s  to set filter
   */
  public SearchFilter(final String s)
  {
    filter = s;
  }


  /**
   * Creates a new string search filter with the supplied filter and arguments.
   *
   * @param  s  to set filter
   * @param  o  to set filter arguments
   */
  public SearchFilter(final String s, final List<Object> o)
  {
    setFilter(s);
    setFilterArgs(o);
  }


  /**
   * Creates a new search filter with the supplied filter and arguments.
   *
   * @param  s  to set filter
   * @param  o  to set filter arguments
   */
  public SearchFilter(final String s, final Object[] o)
  {
    setFilter(s);
    setFilterArgs(o);
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return filter;
  }


  /**
   * Sets the filter.
   *
   * @param  s  to set filter
   */
  public void setFilter(final String s)
  {
    filter = s;
  }


  /**
   * Gets the filter arguments.
   *
   * @return  filter args
   */
  public List<Object> getFilterArgs()
  {
    return filterArgs;
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter arguments
   */
  public void setFilterArgs(final List<Object> o)
  {
    if (o != null) {
      filterArgs = o;
    }
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter arguments
   */
  public void setFilterArgs(final Object[] o)
  {
    if (o != null) {
      filterArgs = Arrays.asList(o);
    }
  }


  /**
   * Returns an ldap filter with it's arguments encoded and replaced. See
   * {@link #encode(Object)}.
   *
   * @param  filter  to format
   * @return  formated and encoded filter
   */
  public static String format(final SearchFilter filter)
  {
    String s = filter.getFilter();

    // put '(' and ')' around filter if missing
    if (!s.startsWith("(")) {
      s = String.format("(%s)", s);
    }

    final List<Object> args = filter.getFilterArgs();
    if (args.size() > 0) {
      int i = 0;
      for (Object o : args) {
        s = s.replaceAll("\\{" + i++ + "\\}", encode(o));
      }
    }
    return s;
  }


  /**
   * Hex encodes the supplied object if it is of type byte[], otherwise the
   * string format of the object is escaped. See {@link #escape(String)}.
   *
   * @param  obj  to encode
   * @return  encoded object
   */
  private static String encode(final Object obj)
  {
    if (obj == null) {
      return null;
    }
    String str;
    if (obj instanceof byte[]) {
      final String s = Hex.encodeHexString((byte[]) obj);
      final StringBuffer sb = new StringBuffer(s.length() * 2);
      for (int i = 0; i < s.length(); i++) {
        sb.append('\\');
        sb.append(s.charAt(i));
      }
      str = sb.toString();
    } else {
      String s = null;
      if (obj instanceof String) {
        s = (String) obj;
      } else {
        s = obj.toString();
      }
      str = escape(s);
    }
    return str;
  }


  /**
   * Escapes the supplied string per RFC 2254.
   *
   * @param  s  to escape
   * @return  escaped string
   */
  private static String escape(final String s)
  {
    final int len = s.length();
    final StringBuffer sb = new StringBuffer(len);
    char ch;
    for (int i = 0; i < len; i++) {
      switch (ch = s.charAt(i)) {
      case '*':
        sb.append("\\2a");
        break;
      case '(':
        sb.append("\\28");
        break;
      case ')':
        sb.append("\\29");
        break;
      case '\\':
        sb.append("\\5c");
        break;
      case 0:
        sb.append("\\00");
        break;
      default:
        sb.append(ch);
      }
    }
    return sb.toString();
  }


  /**
   * Returns whether the supplied object contains the same data as this filter.
   * Delegates to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
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
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    hc += filter != null ? filter.hashCode() : 0;
    hc += filterArgs != null && !filterArgs.isEmpty() ?
      filterArgs.hashCode() : 0;
    return hc;
  }


  /**
   * This returns a string representation of this search filter.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return
      String.format("filter=%s, filterArgs=%s", filter, filterArgs);
  }
}
