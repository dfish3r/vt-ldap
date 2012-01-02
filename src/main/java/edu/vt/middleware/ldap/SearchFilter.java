/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
 * Simple bean for an ldap search filter and it's arguments.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchFilter
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 311;

  /** filter. */
  private String searchFilter;

  /** filter arguments. */
  private List<Object> searchFilterArgs = new ArrayList<Object>();


  /** Default constructor. */
  public SearchFilter() {}


  /**
   * Creates a new search filter with the supplied filter.
   *
   * @param  filter  to set
   */
  public SearchFilter(final String filter)
  {
    searchFilter = filter;
  }


  /**
   * Creates a new string search filter with the supplied filter and arguments.
   *
   * @param  filter  to set
   * @param  args  to set
   */
  public SearchFilter(final String filter, final List<Object> args)
  {
    setFilter(filter);
    setFilterArgs(args);
  }


  /**
   * Creates a new search filter with the supplied filter and arguments.
   *
   * @param  filter  to set
   * @param  args  to set
   */
  public SearchFilter(final String filter, final Object[] args)
  {
    setFilter(filter);
    setFilterArgs(args);
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return searchFilter;
  }


  /**
   * Sets the filter.
   *
   * @param  filter  to set
   */
  public void setFilter(final String filter)
  {
    searchFilter = filter;
  }


  /**
   * Gets the filter arguments.
   *
   * @return  filter args
   */
  public List<Object> getFilterArgs()
  {
    return searchFilterArgs;
  }


  /**
   * Sets the filter arguments.
   *
   * @param  args  to set
   */
  public void setFilterArgs(final List<Object> args)
  {
    if (args != null) {
      searchFilterArgs = args;
    }
  }


  /**
   * Sets the filter arguments.
   *
   * @param  args  to set
   */
  public void setFilterArgs(final Object[] args)
  {
    if (args != null) {
      searchFilterArgs = Arrays.asList(args);
    }
  }


  /**
   * Returns an ldap filter with it's arguments encoded and replaced. See {@link
   * #encode(Object)}.
   *
   * @param  filter  to format
   *
   * @return  formated and encoded filter
   */
  public static String format(final SearchFilter filter)
  {
    String s = filter.getFilter();

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
   *
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
   *
   * @return  escaped string
   */
  private static String escape(final String s)
  {
    final int len = s.length();
    final StringBuffer sb = new StringBuffer(len);
    char ch;
    for (int i = 0; i < len; i++) {
      ch = s.charAt(i);
      switch (ch) {

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
   * Returns a search filter initialized with the supplied filter.
   *
   * @param  filter  search filter to read properties from
   *
   * @return  search filter
   */
  public static SearchFilter newSearchFilter(final SearchFilter filter)
  {
    final SearchFilter sf = new SearchFilter();
    sf.setFilter(filter.getFilter());
    sf.setFilterArgs(new ArrayList<Object>(filter.getFilterArgs()));
    return sf;
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
      o == this || (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtil.computeHashCode(HASH_CODE_SEED, searchFilter, searchFilterArgs);
  }


  /**
   * This returns a string representation of this search filter.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::filter=%s, filterArgs=%s]",
        getClass().getName(),
        hashCode(),
        searchFilter,
        searchFilterArgs);
  }
}
