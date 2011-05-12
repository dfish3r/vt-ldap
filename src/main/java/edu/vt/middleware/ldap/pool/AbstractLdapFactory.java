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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>AbstractLdapFactory</code> provides a basic implementation of an ldap
 * factory.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapFactory<T extends LdapConnection>
  implements LdapFactory<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For activating ldap objects. */
  protected LdapActivator<T> activator;

  /** For passivating ldap objects. */
  protected LdapPassivator<T> passivator;

  /** For validating ldap objects. */
  protected LdapValidator<T> validator;


  /**
   * Sets the ldap activator for this factory.
   *
   * @param  la  ldap activator
   */
  public void setLdapActivator(final LdapActivator<T> la)
  {
    activator = la;
  }


  /**
   * Returns the ldap activator for this factory.
   *
   * @return  ldap activator
   */
  public LdapActivator<T> getLdapActivator()
  {
    return activator;
  }


  /**
   * Sets the ldap passivator for this factory.
   *
   * @param  lp  ldap passivator
   */
  public void setLdapPassivator(final LdapPassivator<T> lp)
  {
    passivator = lp;
  }


  /**
   * Returns the ldap passivator for this factory.
   *
   * @return  ldap passivator
   */
  public LdapPassivator<T> getLdapPassivator()
  {
    return passivator;
  }


  /**
   * Sets the ldap validator for this factory.
   *
   * @param  lv  ldap validator
   */
  public void setLdapValidator(final LdapValidator<T> lv)
  {
    validator = lv;
  }


  /**
   * Returns the ldap validator for this factory.
   *
   * @return  ldap validator
   */
  public LdapValidator<T> getLdapValidator()
  {
    return validator;
  }


  /** {@inheritDoc} */
  @Override
  public boolean activate(final T t)
  {
    boolean success = false;
    if (activator == null) {
      success = true;
      logger.trace("no activator configured");
    } else {
      success = activator.activate(t);
      logger.trace("activation for {} = {}", t, success);
    }
    return success;
  }


  /** {@inheritDoc} */
  @Override
  public boolean passivate(final T t)
  {
    boolean success = false;
    if (passivator == null) {
      success = true;
      logger.trace("no passivator configured");
    } else {
      success = passivator.passivate(t);
      logger.trace("passivation for {} = {}", t, success);
    }
    return success;
  }


  /** {@inheritDoc} */
  @Override
  public boolean validate(final T t)
  {
    boolean success = false;
    if (validator == null) {
      success = true;
      logger.warn("validate called, but no validator configured");
    } else {
      success = validator.validate(t);
      logger.trace("validation for {} = {}", t, success);
    }
    return success;
  }
}
