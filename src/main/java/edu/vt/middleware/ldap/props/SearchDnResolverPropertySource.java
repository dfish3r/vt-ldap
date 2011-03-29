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
import edu.vt.middleware.ldap.auth.SearchDnResolver;

/**
 * Reads properties specific to {@link SearchDnResolver} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SearchDnResolverPropertySource
  extends AbstractPropertySource<SearchDnResolver>
{

  /** Invoker for search dn resolver. */
  private static final AdvancedPropertyInvoker INVOKER =
    new AdvancedPropertyInvoker(SearchDnResolver.class);


  /**
   * Creates a new search dn resolver property source using the default
   * properties file.
   */
  public SearchDnResolverPropertySource()
  {
    this(
      SearchDnResolverPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new search dn resolver property source.
   *
   * @param  is  to read properties from
   */
  public SearchDnResolverPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new search dn resolver property source.
   *
   * @param  props  to read properties from
   */
  public SearchDnResolverPropertySource(final Properties props)
  {
    this(PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new search dn resolver property source.
   *
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public SearchDnResolverPropertySource(
    final PropertyDomain domain, final Properties props)
  {
    this.object = initializeObject(
      INVOKER, new SearchDnResolver(), domain.value(), props);
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
