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

import java.util.Map;
import java.util.Properties;

/**
 * <code>PropertyConfig</code> provides an interface for objects that can be
 * configured with a <code>PropertyInvoker.</code>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PropertyConfig
{


  /**
   * This returns the properties domain for this property config.
   *
   * @return  <code>String</code> properties domain
   */
  String getPropertiesDomain();


  /**
   * This returns whether the supplied property exists.
   *
   * @param  name  <code>String</code> to check
   *
   * @return  <code>boolean</code> whether the supplied property exists
   */
  boolean hasProviderProperty(String name);


  /**
   * This adds environment properties to this object. If name or value is null,
   * then this method does nothing.
   *
   * @param  name  <code>String</code> property name
   * @param  value  <code>String</code> property value
   */
  void setProviderProperty(String name, String value);


  /**
   * See {@link #setProviderProperty(String,String)}.
   *
   * @param  properties  <code>Properties</code>
   */
  void setProviderProperties(Properties properties);


  /**
   * See {@link #setProviderProperty(String,String)}.
   *
   * @param  properties  map of provider properties
   */
  void setProviderProperties(Map<String, String> properties);
}
