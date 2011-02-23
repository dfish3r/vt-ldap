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
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Reads properties specific to {@link SearchRequest} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SearchRequestProperties
  extends AbstractObjectProperties<SearchRequest>
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN = "edu.vt.middleware.ldap.";

  /** Invoker for search request. */
  private static final AdvancedPropertyInvoker SEARCH_REQUEST_INVOKER =
    new AdvancedPropertyInvoker(SearchRequest.class, PROPERTIES_DOMAIN);


  /**
   * Creates a new search request properties using the default properties file.
   */
  public SearchRequestProperties()
  {
    this(SearchRequestProperties.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new search request properties.
   *
   * @param  is  to read properties from
   */
  public SearchRequestProperties(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new search request properties.
   *
   * @param  props  to read properties from
   */
  public SearchRequestProperties(final Properties props)
  {
    this.object = new SearchRequest();
    this.initializeObject(
      SEARCH_REQUEST_INVOKER, this.object, getDomain(), props);
  }


  /**
   * Returns the properties domain for this invoker.
   *
   * @return  properties domain
   */
  public static String getDomain()
  {
    return PROPERTIES_DOMAIN;
  }


  /** {@inheritDoc} */
  public boolean hasProperty(final String name)
  {
    return SEARCH_REQUEST_INVOKER.hasProperty(name);
  }
}
