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
public class ControlHandler<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Control processors. */
  private List<ControlProcessor<T>> controlProcessors =
    new ArrayList<ControlProcessor<T>>();


  /** Default constructor. */
  public ControlHandler() {}


  /**
   * Returns the OIDs of controls that are supported by this control handler.
   *
   * @return  list of control OIDs
   */
  public String[] getSupportedControls()
  {
    final String[] controls = new String[controlProcessors.size()];
    for (int i = 0; i < controlProcessors.size(); i++) {
      controls[i] = controlProcessors.get(i).getOID();
    }
    return controls;
  }


  /**
   * Adds a control processor to this control handler.
   *
   * @param  processor  to add
   */
  public void addControlProcessor(final ControlProcessor<T> processor)
  {
    controlProcessors.add(processor);
  }


  /**
   * Converts the supplied request controls to a provider specific request
   * controls.
   *
   * @param  requestControls  to convert
   * @return  provider specific controls
   */
  @SuppressWarnings("unchecked")
  public T[] processRequestControls(final Control[] requestControls)
  {
    if (requestControls == null) {
      return null;
    }
    final List<T> providerCtls = new ArrayList<T>(requestControls.length);
    for (Control c : requestControls) {
      final T providerCtl = processRequestControl(c);
      if (providerCtl != null) {
        providerCtls.add(providerCtl);
      }
    }
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
  protected T processRequestControl(final Control ctl)
  {
    if (ctl == null || controlProcessors == null) {
      return null;
    }
    T providerCtl = null;
    for (ControlProcessor<T> ch : controlProcessors) {
      providerCtl = ch.processRequestControl(ctl);
      if (providerCtl != null) {
        break;
      }
    }
    if (providerCtl == null) {
      throw new UnsupportedOperationException(
        "Request Control not supported: " + ctl);
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
  public Control[] processResponseControls(
    final Control[] requestControls, final T[] responseControls)
  {
    if (responseControls == null) {
      return null;
    }
    final List<Control> ctls = new ArrayList<Control>(responseControls.length);
    for (T c : responseControls) {
      final Control ctl = processResponseControl(requestControls, c);
      if (ctl != null) {
        ctls.add(ctl);
      }
    }
    return ctls.toArray(new Control[ctls.size()]);
  }


  /**
   * Converts the supplied provider control to a control.
   *
   * @param  requestControls  that produced the response controls
   * @param  providerCtl  to convert
   * @return  control
   */
  protected Control processResponseControl(
    final Control[] requestControls, final T providerCtl)
  {
    if (providerCtl == null || controlProcessors == null) {
      return null;
    }
    Control ctl = null;
    for (ControlProcessor<T> cp : controlProcessors) {
      ctl = cp.processResponseControl(
        findControl(requestControls, cp.getOID()), providerCtl);
      if (ctl != null) {
        break;
      }
    }
    return ctl;
  }


  /**
   * Searches the supplied array for a control that matches the supplied OID.
   *
   * @param  controls  to search
   * @param  oid  to search for
   * @return  control that matches the oid
   */
  public static Control findControl(
    final Control[] controls, final String oid)
  {
    if (controls == null) {
      return null;
    }
    Control match = null;
    for (Control c : controls) {
      if (c.getOID().equals(oid)) {
        match = c;
        break;
      }
    }
    return match;
  }


  /**
   * Handles provider specific request and response controls.
   *
   * @param  <T>  type of provider specific control
   *
   * @author  Middleware Services
   * @version  $Revision$ $Date$
   */
  public interface ControlProcessor<T>
  {


    /**
     * Returns the OID of the control processed by this instance.
     *
     * @return  control oid
     */
    String getOID();


    /**
     * Converts the supplied control to a provider specific request control.
     *
     * @param  requestControl  to convert
     * @return  provider specific controls
     */
    T processRequestControl(Control requestControl);


    /**
     * Converts the supplied provider control to a response control. The request
     * control is provided if there is an associated request control for the
     * response control. Otherwise it is null.
     *
     * @param  requestControl  that produced the response
     * @param  responseControl  to convert
     * @return  control
     */
    Control processResponseControl(Control requestControl, T responseControl);
  }
}
