/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.control;

import java.nio.ByteBuffer;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.LoginException;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.asn1.DERParser;
import edu.vt.middleware.ldap.asn1.DERPath;
import edu.vt.middleware.ldap.asn1.IntegerType;
import edu.vt.middleware.ldap.asn1.ParseHandler;
import edu.vt.middleware.ldap.asn1.SimpleDERTag;
import edu.vt.middleware.ldap.auth.AccountState;

/**
 * Request/response control for password policy. See
 * http://tools.ietf.org/html/draft-behera-ldap-password-policy-10. Control is
 * defined as:
 *
 * <pre>
   PasswordPolicyResponseValue ::= SEQUENCE {
      warning [0] CHOICE {
      timeBeforeExpiration [0] INTEGER (0 .. maxInt),
      graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
      error   [1] ENUMERATED {
      passwordExpired             (0),
      accountLocked               (1),
      changeAfterReset            (2),
      passwordModNotAllowed       (3),
      mustSupplyOldPassword       (4),
      insufficientPasswordQuality (5),
      passwordTooShort            (6),
      passwordTooYoung            (7),
      passwordInHistory           (8) } OPTIONAL }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordPolicyControl extends AbstractControl
  implements RequestControl, ResponseControl
{

  /** OID of this control. */
  public static final String OID = "1.3.6.1.4.1.42.2.27.8.5.1";

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 719;

  /** Enum for ppolicy errors. */
  public enum Error implements AccountState.Error {

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


    /** {@inheritDoc} */
    @Override
    public int getCode()
    {
      return code;
    }


    /** {@inheritDoc} */
    @Override
    public String getMessage()
    {
      return name();
    }


    /** {@inheritDoc} */
    @Override
    public void throwSecurityException()
      throws LoginException
    {
      switch (this) {

      case PASSWORD_EXPIRED:
        throw new CredentialExpiredException(name());

      case ACCOUNT_LOCKED:
        throw new AccountLockedException(name());

      case CHANGE_AFTER_RESET:
        throw new CredentialExpiredException(name());

      case PASSWORD_MOD_NOT_ALLOWED:
        throw new AccountException(name());

      case MUST_SUPPLY_OLD_PASSWORD:
        throw new AccountException(name());

      case INSUFFICIENT_PASSWORD_QUALITY:
        throw new CredentialException(name());

      case PASSWORD_TOO_SHORT:
        throw new CredentialException(name());

      case PASSWORD_TOO_YOUNG:
        throw new CredentialException(name());

      case PASSWORD_IN_HISTORY:
        throw new CredentialException(name());

      default:
        throw new IllegalStateException(
          "Unknown password policy error: " + this);
      }
    }


    /**
     * Returns the error for the supplied integer constant.
     *
     * @param  code  to find error for
     *
     * @return  error
     */
    public static Error valueOf(final int code)
    {
      for (Error e : Error.values()) {
        if (e.getCode() == code) {
          return e;
        }
      }
      return null;
    }
  }

  /** Ppolicy warning. */
  private int timeBeforeExpiration;

  /** Ppolicy warning. */
  private int graceAuthNsRemaining;

  /** Ppolicy error. */
  private Error error;


  /** Default constructor. */
  public PasswordPolicyControl()
  {
    super(OID);
  }


  /**
   * Creates a new password policy control.
   *
   * @param  critical  whether this control is critical
   */
  public PasswordPolicyControl(final boolean critical)
  {
    super(OID, critical);
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


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtil.computeHashCode(
        HASH_CODE_SEED,
        getOID(),
        getCriticality(),
        timeBeforeExpiration,
        graceAuthNsRemaining,
        error);
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
        getCriticality(),
        timeBeforeExpiration,
        graceAuthNsRemaining,
        error);
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return null;
  }


  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] encoded)
  {
    final PasswordPolicyHandler handler = new PasswordPolicyHandler(this);
    final DERParser parser = new DERParser(
      new SimpleDERTag(0, "CHOICE", true),
      new SimpleDERTag(1, "ENUM", true));
    parser.registerHandler(PasswordPolicyHandler.WARNING_PATH, handler);
    parser.registerHandler(PasswordPolicyHandler.ERROR_PATH, handler);
    parser.parse(ByteBuffer.wrap(encoded));
  }


  /** Parse handler implementation for the password policy control. */
  private static class PasswordPolicyHandler implements ParseHandler
  {

    /** DER path to warnings. */
    public static final DERPath WARNING_PATH = new DERPath("/SEQ/CHOICE");

    /** DER path to errors. */
    public static final DERPath ERROR_PATH = new DERPath("/SEQ/ENUM");

    /** Choice time before expiration constant. */
    private static final byte CHOICE_TIME_BEFORE_EXPIRATION = (byte) 0x80;

    /** Choice grace auths remaining constant. */
    private static final byte CHOICE_GRACE_AUTHNS_REMAINING = (byte) 0x81;

    /** Password policy control to configure with this handler. */
    private final PasswordPolicyControl passwordPolicy;


    /**
     * Creates a new password policy handler.
     *
     * @param  control  to configure
     */
    public PasswordPolicyHandler(final PasswordPolicyControl control)
    {
      passwordPolicy = control;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final DERParser parser, final ByteBuffer encoded)
    {
      if (WARNING_PATH.equals(parser.getCurrentPath())) {
        handleWarning(parser, encoded);
      } else if (ERROR_PATH.equals(parser.getCurrentPath())) {
        handleError(parser, encoded);
      }
    }


    /**
     * Decode password policy warnings.
     *
     * @param  parser  to parse byte buffer
     * @param  encoded  containing ppolicy value
     */
    private void handleWarning(final DERParser parser, final ByteBuffer encoded)
    {
      final byte tag = encoded.get();
      encoded.limit(parser.readLength(encoded) + encoded.position());
      if (tag == CHOICE_TIME_BEFORE_EXPIRATION) {
        passwordPolicy.setTimeBeforeExpiration(
          IntegerType.decode(encoded).intValue());
      } else if (tag == CHOICE_GRACE_AUTHNS_REMAINING) {
        passwordPolicy.setGraceAuthNsRemaining(
          IntegerType.decode(encoded).intValue());
      } else {
        throw new IllegalArgumentException("Unknown warning tag " + tag);
      }
    }


    /**
     * Decode password policy errors.
     *
     * @param  parser  to parse byte buffer
     * @param  encoded  containing ppolicy value
     */
    private void handleError(final DERParser parser, final ByteBuffer encoded)
    {
      final int errValue = IntegerType.decode(encoded).intValue();
      final PasswordPolicyControl.Error e = PasswordPolicyControl.Error.valueOf(
        errValue);
      if (e == null) {
        throw new IllegalArgumentException("Unknown error code " + errValue);
      }
      passwordPolicy.setError(e);
    }
  }
}
