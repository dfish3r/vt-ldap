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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ReferralBehavior;
import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.sasl.Mechanism;

/**
 * Provides access to a provider specific connection factory.
 *
 * @param  <T>  type of provider config for this provider
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Provider<T extends ProviderConfig>
{


  /**
   * Returns whether the supplied SASL mechanism is supported by this provider.
   *
   * @param  mechanism  to check support for
   *
   * @return  whether mechanism is supported
   */
  boolean isSupported(Mechanism mechanism);


  /**
   * Returns whether the supplied control is supported by this provider.
   *
   * @param  control  to check support for
   *
   * @return  whether control is supported
   */
  boolean isSupported(Control control);


  /**
   * Returns whether the supplied referral behavior is supported by this
   * provider.
   *
   * @param  behavior  to check support for
   *
   * @return  whether behavior is supported
   */
  boolean isSupported(ReferralBehavior behavior);


  /**
   * Returns the provider configuration.
   *
   * @return  provider configuration
   */
  T getProviderConfig();


  /**
   * Sets the provider configuration.
   *
   * @param  pc  provider configuration
   */
  void setProviderConfig(T pc);


  /**
   * Returns the connection factory for this provider.
   *
   * @param  cc  connection configuration
   * @return  connection factory
   */
  ConnectionFactory<T> getConnectionFactory(ConnectionConfig cc);


  /**
   * Creates a new instance of this provider.
   *
   * @return  new instance of this provider
   */
  Provider<T> newInstance();
}
