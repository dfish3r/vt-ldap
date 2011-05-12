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
package edu.vt.middleware.ldap.props;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Reads properties specific to {@link SearchRequest} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SearchRequestPropertySource
  extends AbstractPropertySource<SearchRequest>
{

  /** Invoker for search request. */
  private static final AdvancedPropertyInvoker INVOKER =
    new AdvancedPropertyInvoker(SearchRequest.class);

  /**
   * Creates a new search request property source using the default properties
   * file.
   */
  public SearchRequestPropertySource()
  {
    this(
      SearchRequestPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new search request property source.
   *
   * @param  is  to read properties from
   */
  public SearchRequestPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new search request property source.
   *
   * @param  props  to read properties from
   */
  public SearchRequestPropertySource(final Properties props)
  {
    this(PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new search request property source.
   *
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public SearchRequestPropertySource(
    final PropertyDomain domain, final Properties props)
  {
    object = initializeObject(
      INVOKER, new SearchRequest(), domain.value(), props);
  }


  /**
   * Returns the property names for this property source.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return INVOKER.getProperties();
  }
}
