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

import edu.vt.middleware.ldap.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a the base implementation of an connection factory.
 *
 * @param  <T>  type of ldap connection
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractConnectionFactory<T extends Connection>
  implements ConnectionFactory<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For activating ldap connections. */
  protected Activator<T> activator;

  /** For passivating ldap connections. */
  protected Passivator<T> passivator;

  /** For validating ldap connections. */
  protected Validator<T> validator;


  /**
   * Sets the activator for this factory.
   *
   * @param  a  activator
   */
  public void setActivator(final Activator<T> a)
  {
    activator = a;
  }


  /**
   * Returns the activator for this factory.
   *
   * @return  activator
   */
  public Activator<T> getActivator()
  {
    return activator;
  }


  /**
   * Sets the passivator for this factory.
   *
   * @param  p  passivator
   */
  public void setPassivator(final Passivator<T> p)
  {
    passivator = p;
  }


  /**
   * Returns the passivator for this factory.
   *
   * @return  passivator
   */
  public Passivator<T> getPassivator()
  {
    return passivator;
  }


  /**
   * Sets the validator for this factory.
   *
   * @param  v  validator
   */
  public void setValidator(final Validator<T> v)
  {
    validator = v;
  }


  /**
   * Returns the validator for this factory.
   *
   * @return  validator
   */
  public Validator<T> getValidator()
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
