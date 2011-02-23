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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

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
    this.activator = la;
  }


  /**
   * Returns the ldap activator for this factory.
   *
   * @return  ldap activator
   */
  public LdapActivator<T> getLdapActivator()
  {
    return this.activator;
  }


  /**
   * Sets the ldap passivator for this factory.
   *
   * @param  lp  ldap passivator
   */
  public void setLdapPassivator(final LdapPassivator<T> lp)
  {
    this.passivator = lp;
  }


  /**
   * Returns the ldap passivator for this factory.
   *
   * @return  ldap passivator
   */
  public LdapPassivator<T> getLdapPassivator()
  {
    return this.passivator;
  }


  /**
   * Sets the ldap validator for this factory.
   *
   * @param  lv  ldap validator
   */
  public void setLdapValidator(final LdapValidator<T> lv)
  {
    this.validator = lv;
  }


  /**
   * Returns the ldap validator for this factory.
   *
   * @return  ldap validator
   */
  public LdapValidator<T> getLdapValidator()
  {
    return this.validator;
  }


  /** {@inheritDoc} */
  public boolean activate(final T t)
  {
    boolean success = false;
    if (this.activator == null) {
      success = true;
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("no activator configured");
      }
    } else {
      success = this.activator.activate(t);
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("activation for " + t + " = " + success);
      }
    }
    return success;
  }


  /** {@inheritDoc} */
  public boolean passivate(final T t)
  {
    boolean success = false;
    if (this.passivator == null) {
      success = true;
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("no passivator configured");
      }
    } else {
      success = this.passivator.passivate(t);
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("passivation for " + t + " = " + success);
      }
    }
    return success;
  }


  /** {@inheritDoc} */
  public boolean validate(final T t)
  {
    boolean success = false;
    if (this.validator == null) {
      success = true;
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("validate called, but no validator configured");
      }
    } else {
      success = this.validator.validate(t);
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("validation for " + t + " = " + success);
      }
    }
    return success;
  }
}
