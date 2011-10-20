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
package edu.vt.middleware.ldap.control;

/**
 * Request control for password policy. See
 * http://tools.ietf.org/html/draft-behera-ldap-password-policy-10.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordPolicyControl extends AbstractControl
{

  /** Enum for ppolicy errors. */
  public enum Error {

    /** password expired. */
    PASSWORD_EXPIRED(0),

    /** account locked. */
    ACCOUNT_LOCKED(1),

    /** change after reset. */
    CHANGE_AFTER_RESET(2),

    /** password modification not allowed. */
    PASSWORD_MOD_NOT_ALLOWED(3),

    /** must supply old password. */
    MUST_SUPPLY_OLD_PASSWORD(4),

    /** insufficient password quality. */
    INSUFFICIENT_PASSWORD_QUALITY(5),

    /** password too short. */
    PASSWORD_TOO_SHORT(6),

    /** password too young. */
    PASSWORD_TOO_YOUNG(7),

    /** password in history. */
    PASSWORD_IN_HISTORY(8);

    /** underlying error code. */
    private int code;


    /**
     * Creates a new error.
     *
     * @param  i  error code
     */
    Error(final int i)
    {
      code = i;
    }


    /**
     * Returns the error code value.
     *
     * @return  ppolicy error code
     */
    public int value()
    {
      return code;
    }


    /**
     * Returns the error for the supplied integer constant.
     *
     * @param  code  to find error for
     * @return  error
     */
    public static Error valueOf(final int code)
    {
      for (Error e : Error.values()) {
        if (e.value() == code) {
          return e;
        }
      }
      return null;
    }
  };

  /** OID of this control. */
  public static final String OID = "1.3.6.1.4.1.42.2.27.8.5.1";

  /** Ppolicy warning. */
  private int timeBeforeExpiration;

  /** Ppolicy warning. */
  private int graceAuthNsRemaining;

  /** Ppolicy error. */
  private Error error;


  /**
   * Default constructor.
   */
  public PasswordPolicyControl() {}


  /**
   * Creates a new password policy control.
   *
   * @param  critical  whether this control is critical
   */
  public PasswordPolicyControl(final boolean critical)
  {
    setCriticality(critical);
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /**
   * Returns the time before expiration in seconds.
   *
   * @return  time before expiration
   */
  public int getTimeBeforeExpiration()
  {
    return timeBeforeExpiration;
  }


  /**
   * Sets the time before expiration in seconds.
   *
   * @param  time  before expiration
   */
  public void setTimeBeforeExpiration(final int time)
  {
    timeBeforeExpiration = time;
  }


  /**
   * Returns the number of grace authentications remaining.
   *
   * @return  number of grace authentications remaining
   */
  public int getGraceAuthNsRemaining()
  {
    return graceAuthNsRemaining;
  }


  /**
   * Sets the number of grace authentications remaining.
   *
   * @param  count  number of grace authentications remaining
   */
  public void setGraceAuthNsRemaining(final int count)
  {
    graceAuthNsRemaining = count;
  }


  /**
   * Returns the password policy error.
   *
   * @return  password policy error
   */
  public Error getError()
  {
    return error;
  }


  /**
   * Sets the password policy error.
   *
   * @param  e  password policy error
   */
  public void setError(final Error e)
  {
    error = e;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::criticality=%s, timeBeforeExpiration=%s, " +
        "graceAuthNsRemaining=%s, error=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        timeBeforeExpiration,
        graceAuthNsRemaining,
        error);
  }
}
