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
package edu.vt.middleware.ldap.provider.jndi;

import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.PartialResultException;
import javax.naming.ReferralException;
import javax.naming.ServiceUnavailableException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Utility class that provides a bridge between JNDI naming exceptions and ldap
 * result codes.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public final class NamingExceptionUtil
{
  /** Map of naming exceptions to ldap result codes. */
  private static final Map<Class<?>, ResultCode[]> EXCEPTIONS_TO_RESULT_CODES;

  /** initialize map of exceptions to result codes. */
  static {
    EXCEPTIONS_TO_RESULT_CODES = new HashMap<Class<?>, ResultCode[]>();
    EXCEPTIONS_TO_RESULT_CODES.put(
      NamingException.class,
      new ResultCode[] {
        ResultCode.OPERATIONS_ERROR,
        ResultCode.ALIAS_PROBLEM,
        ResultCode.ALIAS_DEREFERENCING_PROBLEM,
        ResultCode.LOOP_DETECT,
        ResultCode.AFFECTS_MULTIPLE_DSAS,
        ResultCode.OTHER, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      CommunicationException.class,
      new ResultCode[] {ResultCode.PROTOCOL_ERROR, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      TimeLimitExceededException.class,
      new ResultCode[] {ResultCode.TIME_LIMIT_EXCEEDED, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      SizeLimitExceededException.class,
      new ResultCode[] {ResultCode.SIZE_LIMIT_EXCEEDED, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      AuthenticationNotSupportedException.class,
      new ResultCode[] {
        ResultCode.AUTH_METHOD_NOT_SUPPORTED,
        ResultCode.STRONG_AUTH_REQUIRED,
        ResultCode.CONFIDENTIALITY_REQUIRED,
        ResultCode.INAPPROPRIATE_AUTHENTICATION, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      PartialResultException.class,
      new ResultCode[] {ResultCode.PARTIAL_RESULTS, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      ReferralException.class,
      new ResultCode[] {ResultCode.REFERRAL, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      LimitExceededException.class,
      new ResultCode[] {
        ResultCode.REFERRAL,
        ResultCode.ADMIN_LIMIT_EXCEEDED, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      OperationNotSupportedException.class,
      new ResultCode[] {
        ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
        ResultCode.UNWILLING_TO_PERFORM, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      NoSuchAttributeException.class,
      new ResultCode[] {ResultCode.NO_SUCH_ATTRIBUTE, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      InvalidAttributeIdentifierException.class,
      new ResultCode[] {ResultCode.UNDEFINED_ATTRIBUTE_TYPE, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      InvalidSearchFilterException.class,
      new ResultCode[] {ResultCode.INAPPROPRIATE_MATCHING, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      InvalidAttributeValueException.class,
      new ResultCode[] {
        ResultCode.CONSTRAINT_VIOLATION,
        ResultCode.INVALID_ATTRIBUTE_SYNTAX, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      AttributeInUseException.class,
      new ResultCode[] {ResultCode.ATTRIBUTE_OR_VALUE_EXISTS, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      NameNotFoundException.class,
      new ResultCode[] {ResultCode.NO_SUCH_OBJECT, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      InvalidNameException.class,
      new ResultCode[] {
        ResultCode.INVALID_DN_SYNTAX,
        ResultCode.NAMING_VIOLATION, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      AuthenticationException.class,
      new ResultCode[] {ResultCode.INVALID_CREDENTIALS, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      NoPermissionException.class,
      new ResultCode[] {ResultCode.INSUFFICIENT_ACCESS_RIGHTS, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      ServiceUnavailableException.class,
      new ResultCode[] {
        ResultCode.BUSY,
        ResultCode.UNAVAILABLE, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      SchemaViolationException.class,
      new ResultCode[] {
        ResultCode.OBJECT_CLASS_VIOLATION,
        ResultCode.NOT_ALLOWED_ON_RDN,
        ResultCode.OBJECT_CLASS_MODS_PROHIBITED, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      ContextNotEmptyException.class,
      new ResultCode[] {ResultCode.NOT_ALLOWED_ON_NONLEAF, });
    EXCEPTIONS_TO_RESULT_CODES.put(
        ContextNotEmptyException.class,
        new ResultCode[] {ResultCode.NOT_ALLOWED_ON_NONLEAF, });
    EXCEPTIONS_TO_RESULT_CODES.put(
      NameAlreadyBoundException.class,
      new ResultCode[] {ResultCode.ENTRY_ALREADY_EXISTS, });
  }


  /** Default constructor. */
  private NamingExceptionUtil() {}


  /**
   * Returns the result codes that map to the supplied naming exception.
   *
   * @param  clazz  naming exception
   * @return  ldap result codes
   */
  public static ResultCode[] getResultCodes(
    final Class<? extends NamingException> clazz)
  {
    return EXCEPTIONS_TO_RESULT_CODES.get(clazz);
  }


  /**
   * Returns the result code that map to the supplied naming exception. If the
   * exception maps to multiple result codes, null is returned
   *
   * @param  clazz  naming exception
   * @return  ldap result code
   */
  public static ResultCode getResultCode(
    final Class<? extends NamingException> clazz)
  {
    final ResultCode[] codes = EXCEPTIONS_TO_RESULT_CODES.get(clazz);
    if (codes.length == 1) {
      return codes[0];
    } else {
      return null;
    }
  }


  /**
   * Returns whether the supplied naming exception maps to the supplied result
   * code.
   *
   * @param  clazz  naming exception
   * @param  code  ldap result code
   * @return  whether the naming exception matches the result code
   */
  public static boolean matches(
    final Class<? extends NamingException> clazz, final ResultCode code)
  {
    boolean match = false;
    for (ResultCode rc : getResultCodes(clazz)) {
      if (rc == code) {
        match = true;
        break;
      }
    }
    return match;
  }
}
