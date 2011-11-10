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

import java.io.IOException;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.ManageDsaITControl;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.control.SortRequestControl;
import edu.vt.middleware.ldap.control.SortResponseControl;
import edu.vt.middleware.ldap.provider.ControlHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JNDI specific control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiControlHandler
  extends ControlHandler<javax.naming.ldap.Control>
{


  /** Default constructor. */
  public JndiControlHandler()
  {
    addControlProcessor(new ManageDsaITControlProcessor());
    addControlProcessor(new SortRequestControlProcessor());
    addControlProcessor(new SortResponseControlProcessor());
    addControlProcessor(new PagedResultsControlProcessor());
  }


  /**
   * ManageDsaIT control processor.
   */
  public static class ManageDsaITControlProcessor
    implements ControlProcessor<javax.naming.ldap.Control>
  {


    /** {@inheritDoc} */
    @Override
    public String getOID()
    {
      return ManageDsaITControl.OID;
    }


    /** {@inheritDoc} */
    @Override
    public javax.naming.ldap.Control processRequestControl(
      final Control requestControl)
    {
      javax.naming.ldap.ManageReferralControl ctl = null;
      if (ManageDsaITControl.OID.equals(requestControl.getOID())) {
        final ManageDsaITControl c = (ManageDsaITControl) requestControl;
        ctl =  new javax.naming.ldap.ManageReferralControl(c.getCriticality());
      }
      return ctl;
    }


    /** {@inheritDoc} */
    @Override
    public Control processResponseControl(
      final Control requestControl,
      final javax.naming.ldap.Control responseControl)
    {
      return null;
    }
  }


  /**
   * Sort request control processor.
   */
  public static class SortRequestControlProcessor
    implements ControlProcessor<javax.naming.ldap.Control>
  {

    /** Logger for this class. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /** {@inheritDoc} */
    @Override
    public String getOID()
    {
      return SortRequestControl.OID;
    }


    /** {@inheritDoc} */
    @Override
    public javax.naming.ldap.Control processRequestControl(
      final Control requestControl)
    {
      javax.naming.ldap.SortControl ctl = null;
      if (SortRequestControl.OID.equals(requestControl.getOID())) {
        final SortRequestControl c = (SortRequestControl) requestControl;
        try {
          ctl = new javax.naming.ldap.SortControl(
            JndiUtil.fromSortKey(c.getSortKeys()), c.getCriticality());
        } catch (IOException e) {
          logger.warn("Error creating control.", e);
        }
      }
      return ctl;
    }


    /** {@inheritDoc} */
    @Override
    public Control processResponseControl(
      final Control requestControl,
      final javax.naming.ldap.Control responseControl)
    {
      return null;
    }
  }


  /**
   * Sort response control processor.
   */
  public static class SortResponseControlProcessor
    implements ControlProcessor<javax.naming.ldap.Control>
  {


    /** {@inheritDoc} */
    @Override
    public String getOID()
    {
      return SortResponseControl.OID;
    }


    /** {@inheritDoc} */
    @Override
    public javax.naming.ldap.Control processRequestControl(
      final Control requestControl)
    {
      return null;
    }


    /** {@inheritDoc} */
    @Override
    public Control processResponseControl(
      final Control requestControl,
      final javax.naming.ldap.Control responseControl)
    {
      SortResponseControl ctl = null;
      if (SortResponseControl.OID.equals(responseControl.getID())) {
        ctl = (SortResponseControl) requestControl;
        final javax.naming.ldap.SortResponseControl c =
          (javax.naming.ldap.SortResponseControl) responseControl;
        ctl = new SortResponseControl(
          ResultCode.valueOf(c.getResultCode()),
          c.getAttributeID(),
          c.isCritical());
      }
      return ctl;
    }
  }


  /**
   * Paged results control processor.
   */
  public static class PagedResultsControlProcessor
    implements ControlProcessor<javax.naming.ldap.Control>
  {

    /** Logger for this class. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /** {@inheritDoc} */
    @Override
    public String getOID()
    {
      return PagedResultsControl.OID;
    }


    /** {@inheritDoc} */
    @Override
    public javax.naming.ldap.Control processRequestControl(
      final Control requestControl)
    {
      javax.naming.ldap.PagedResultsControl ctl = null;
      if (PagedResultsControl.OID.equals(requestControl.getOID())) {
        final PagedResultsControl c = (PagedResultsControl) requestControl;
        try {
          ctl = new javax.naming.ldap.PagedResultsControl(
            c.getSize(), c.getCookie(), c.getCriticality());
        } catch (IOException e) {
          logger.warn("Error creating control.", e);
        }
      }
      return ctl;
    }


    /** {@inheritDoc} */
    @Override
    public Control processResponseControl(
      final Control requestControl,
      final javax.naming.ldap.Control responseControl)
    {
      PagedResultsControl ctl = null;
      if (PagedResultsControl.OID.equals(responseControl.getID())) {
        final javax.naming.ldap.PagedResultsResponseControl c =
          (javax.naming.ldap.PagedResultsResponseControl) responseControl;
        // set paged result cookie if found
        if (c.getCookie() != null && c.getCookie().length > 0) {
          ctl = (PagedResultsControl) requestControl;
          if (ctl != null) {
            ctl.setCookie(c.getCookie());
          } else {
            ctl = new PagedResultsControl(
              c.getResultSize(), c.getCookie(), c.isCritical());
          }
        }
      }
      return ctl;
    }
  }
}
