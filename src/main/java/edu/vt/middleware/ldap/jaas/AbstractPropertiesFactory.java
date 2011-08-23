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
package edu.vt.middleware.ldap.jaas;

import java.util.Map;
import java.util.Properties;
import edu.vt.middleware.ldap.props.PropertySource.PropertyDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation common to properties based factories.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractPropertiesFactory
{

  /** Cache ID option used on the JAAS config. */
  public static final String CACHE_ID = "cacheId";

  /** Regular expression for ldap properties to ignore. */
  private static final String IGNORE_LDAP_REGEX =
    "useFirstPass|tryFirstPass|storePass|" +
    "setLdapPrincipal|setLdapDnPrincipal|setLdapCredential|" +
    "defaultRole|principalGroupName|roleGroupName|" +
    "userRoleAttribute|roleFilter|roleAttribute|noResultsIsError|" +
    "cacheId|authenticatorFactory|roleResolverFactory|roleResolver";

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /**
   * Returns context specific properties based on the supplied JAAS options.
   *
   * @param  options  to read properties from
   *
   * @return  properties
   */
  protected static Properties createProperties(final Map<String, ?> options)
  {
    final Properties p = new Properties();
    for (Map.Entry<String, ?> entry : options.entrySet()) {
      if (!entry.getKey().matches(IGNORE_LDAP_REGEX)) {
        // if property name contains a dot, it isn't a vt-ldap property
        if (entry.getKey().indexOf(".") != -1) {
          p.setProperty(entry.getKey(), entry.getValue().toString());
        // add the domain to vt-ldap properties
        } else {
          p.setProperty(
            PropertyDomain.AUTH.value() + entry.getKey(),
            entry.getValue().toString());
        }
      }
    }
    return p;
  }
}
