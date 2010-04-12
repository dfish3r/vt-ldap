/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.props;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractPropertyInvoker</code> provides methods common to property
 * invokers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractPropertyInvoker
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Class to invoke methods on. */
  protected Class<?> clazz;

  /** Map of all properties to their getter and setter methods. */
  protected final Map<String, Method[]> properties =
    new HashMap<String, Method[]>();


  /**
   * Initializes the properties map with the supplied class.
   *
   * @param  c  to read methods from
   * @param  domain  optional domain that properties are in
   */
  protected void initialize(final Class<?> c, final String domain)
  {
    for (Method setterMethod : c.getMethods()) {
      if (
        setterMethod.getName().startsWith("set") &&
          setterMethod.getParameterTypes().length == 1) {
        final String mName = setterMethod.getName().substring("set".length());
        try {
          final Method getterMethod = c.getMethod("get" + mName, new Class[0]);
          final StringBuffer pName = new StringBuffer(domain);
          pName.append(mName.substring(0, 1).toLowerCase());
          pName.append(mName.substring(1, mName.length()));
          this.properties.put(
            pName.toString(),
            new Method[] {getterMethod, setterMethod});
        } catch (NoSuchMethodException e) {
          // no matching getter method
        }
      }
    }
    this.clazz = c;
  }


  /**
   * This invokes the setter method for the supplied property name with the
   * supplied value. If name or value is null, then this method does nothing.
   *
   * @param  object  <code>Object</code> to invoke method on
   * @param  name  <code>String</code> property name
   * @param  value  <code>String</code> property value
   *
   * @throws  IllegalArgumentException  if an invocation exception occurs
   */
  public void setProperty(
    final Object object,
    final String name,
    final String value)
  {
    if (!this.clazz.isInstance(object)) {
      throw new IllegalArgumentException(
        "Illegal attempt to set property for class " + this.clazz.getName() +
        " on object of type " + object.getClass().getName());
    }

    final Method getter =
      this.properties.get(name) != null ? this.properties.get(name)[0] : null;
    if (getter == null) {
      throw new IllegalArgumentException(
        "No getter method found for " + name + " on object " +
        this.clazz.getName());
    }

    final Method setter =
      this.properties.get(name) != null ? this.properties.get(name)[1] : null;
    if (setter == null) {
      throw new IllegalArgumentException(
        "No setter method found for " + name + " on object " +
        this.clazz.getName());
    }

    invokeMethod(
      setter, object, this.convertValue(getter.getReturnType(), value));
  }


  /**
   * This converts the supplied string value into an Object of the appropriate
   * supplied type. If value cannot be converted it is returned as is.
   *
   * @param  type  of object to convert value into
   * @param  value  to parse
   * @return  object of the supplied type
   */
  protected abstract Object convertValue(
    final Class<?> type, final String value);


  /**
   * This returns whether the supplied property exists.
   *
   * @param  name  <code>String</code> to check
   *
   * @return  <code>boolean</code> whether the supplied property exists
   */
  public boolean hasProperty(final String name)
  {
    return this.properties.containsKey(name);
  }


  /**
   * This returns the property keys.
   *
   * @return  <code>String[]</code> of property names
   */
  public String[] getProperties()
  {
    return this.properties.keySet().toArray(new String[0]);
  }


  /**
   * Creates an instance of the supplied type.
   *
   * @param  <T>  type of class returned
   * @param  type  of class to create
   * @param  className  to create
   *
   * @return  class of type T
   *
   * @throws  IllegalArgumentException  if the supplied class name cannot create
   * a new instance of T
   */
  @SuppressWarnings("unchecked")
  public static <T> T instantiateType(final T type, final String className)
  {
    try {
      return (T) createClass(className).newInstance();
    } catch (InstantiationException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }


  /**
   * Creates the class with the supplied name.
   *
   * @param  className  to create
   *
   * @return  class
   *
   * @throws  IllegalArgumentException  if the supplied class name cannot be
   * created
   */
  public static Class<?> createClass(final String className)
  {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
        "Could not find class '" + className + "'", e);
    }
  }


  /**
   * Invokes the supplied method on the supplied object with the supplied
   * argument.
   *
   * @param  method  <code>Method</code> to invoke
   * @param  object  <code>Object</code> to invoke method on
   * @param  arg  <code>Object</code>  to invoke method with
   * @return  <code>Object</code> produced by the invocation
   * @throws  IllegalArgumentException  if an error occurs invoking the method
   */
  public static Object invokeMethod(
    final Method method, final Object object, final Object arg)
  {
    try {
      Object[] params = new Object[] {arg};
      if (arg == null && method.getParameterTypes().length == 0) {
        params = (Object[]) null;
      }
      return method.invoke(object, params);
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
