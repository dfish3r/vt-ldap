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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for invoking provider specific control processors.
 *
 * @param  <T>  type of provider specific control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ControlProcessor<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Control handler. */
  private ControlHandler<T> controlHandler;


  /**
   * Creates a new control processor.
   *
   * @param  handler  to process controls with
   */
  public ControlProcessor(final ControlHandler<T> handler)
  {
    controlHandler = handler;
  }


  /**
   * Converts the supplied request controls to a provider specific request
   * controls.
   *
   * @param  requestControls  to convert
   * @return  provider specific controls
   */
  @SuppressWarnings("unchecked")
  public T[] processRequestControls(final RequestControl[] requestControls)
  {
    if (requestControls == null) {
      return null;
    }
    logger.debug("processing request controls: {}", requestControls);
    final List<T> providerCtls = new ArrayList<T>(requestControls.length);
    for (RequestControl c : requestControls) {
      final T providerCtl = processRequest(c);
      if (providerCtl != null) {
        providerCtls.add(providerCtl);
      }
    }
    logger.debug("produced provider request controls: {}", providerCtls);
    return !providerCtls.isEmpty() ?
      providerCtls.toArray(
        (T[]) Array.newInstance(
          providerCtls.iterator().next().getClass(), providerCtls.size())) :
      null;
  }


  /**
   * Converts the supplied control to a provider control.
   *
   * @param  ctl  to convert
   * @return  provider control
   */
  protected T processRequest(final RequestControl ctl)
  {
    if (ctl == null) {
      return null;
    }
    final T providerCtl = controlHandler.processRequest(ctl);
    if (providerCtl == null) {
      throw new UnsupportedOperationException(
        "Request control not supported: " + ctl);
    }
    return providerCtl;
  }


  /**
   * Converts the supplied provider controls to a response controls. The
   * supplied request controls were used to produce the response.
   *
   * @param  requestControls  that produced the response
   * @param  responseControls  to convert
   * @return  controls
   */
  public ResponseControl[] processResponseControls(
    final RequestControl[] requestControls, final T[] responseControls)
  {
    if (responseControls == null) {
      return null;
    }
    logger.debug("processing provider response controls: {}", responseControls);
    final List<ResponseControl> ctls = new ArrayList<ResponseControl>(
      responseControls.length);
    for (T c : responseControls) {
      final ResponseControl ctl = processResponse(requestControls, c);
      if (ctl != null) {
        ctls.add(ctl);
      }
    }
    logger.debug("produced response controls: {}", ctls);
    return ctls.toArray(new ResponseControl[ctls.size()]);
  }


  /**
   * Converts the supplied provider control to a control.
   *
   * @param  requestControls  that produced the response controls
   * @param  providerCtl  to convert
   * @return  control
   */
  protected ResponseControl processResponse(
    final RequestControl[] requestControls, final T providerCtl)
  {
    if (providerCtl == null) {
      return null;
    }
    final ResponseControl ctl = controlHandler.processResponse(
      findControl(requestControls, controlHandler.getOID(providerCtl)),
      providerCtl);
    if (ctl == null) {
      throw new UnsupportedOperationException(
        "Response control not supported: " + providerCtl);
    }
    return ctl;
  }


  /**
   * Examines the supplied response controls and determines whether another
   * search should be executed.
   *
   * @param  responseControls  to inspect
   * @return  whether another search should be executed
   */
  public static boolean searchAgain(
    final ResponseControl[] responseControls)
  {
    boolean b = false;
    final PagedResultsControl ctl = (PagedResultsControl) findControl(
      responseControls, PagedResultsControl.OID);
    if (ctl != null) {
      if (ctl.getCookie() != null && ctl.getCookie().length > 0) {
        b = true;
      }
    }
    return b;
  }


  /**
   * Searches the supplied array for a control that matches the supplied OID.
   *
   * @param  <T>  type of control
   * @param  controls  to search
   * @param  oid  to search for
   * @return  control that matches the oid
   */
  private static <T extends Control> T findControl(
    final T[] controls, final String oid)
  {
    if (controls == null || controls.length == 0) {
      return null;
    }
    T match = null;
    for (T c : controls) {
      if (c.getOID().equals(oid)) {
        match = c;
        break;
      }
    }
    return match;
  }
}
