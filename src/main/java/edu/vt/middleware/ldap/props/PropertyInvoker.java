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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.handler.SearchResultHandler;

/**
 * <code>PropertyInvoker</code> stores setter methods for a class to make method
 * invocation by property easier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropertyInvoker
{

  /** Class to invoke methods on. */
  private final Class<?> clazz;

  /** Map of all properties to their getter and setter methods. */
  private final Map<String, Method[]> properties =
    new HashMap<String, Method[]>();


  /**
   * Creates a new <code>PropertyInvoker</code> for the supplied class.
   *
   * @param  c  <code>Class</code> that has setter methods
   * @param  propertiesDomain  <code>String</code> to prepend to each setter
   * name
   */
  public PropertyInvoker(final Class<?> c, final String propertiesDomain)
  {
    this.clazz = c;
    for (Method setterMethod : c.getMethods()) {
      if (
        setterMethod.getName().startsWith("set") &&
          setterMethod.getParameterTypes().length == 1) {
        final String mName = setterMethod.getName().substring("set".length());
        try {
          final Method getterMethod = c.getMethod("get" + mName, new Class[0]);
          final StringBuffer pName = new StringBuffer(propertiesDomain);
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

    final Method getter = this.properties.get(name)[0];
    if (getter == null) {
      throw new IllegalArgumentException(
        "No getter method found for " + name + "on object " +
        this.clazz.getName());
    }

    final Method setter = this.properties.get(name)[1];
    if (setter == null) {
      throw new IllegalArgumentException(
        "No setter method found for " + name + "on object " +
        this.clazz.getName());
    }

    Object newValue = value;
    if (getter.getReturnType() != String.class) {
      if (SSLSocketFactory.class.isAssignableFrom(getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          newValue = instantiateType(SSLSocketFactory.class, value);
        }
      } else if (
        HostnameVerifier.class.isAssignableFrom(getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          newValue = instantiateType(HostnameVerifier.class, value);
        }
      } else if (
        SearchResultHandler[].class.isAssignableFrom(getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          final String[] classes = value.split(",");
          newValue = Array.newInstance(
            SearchResultHandler.class, classes.length);
          for (int i = 0; i < classes.length; i++) {
            Array.set(
              newValue,
              i,
              instantiateType(SearchResultHandler.class, classes[i]));
          }
        }
      } else if (
        AuthenticationResultHandler[].class.isAssignableFrom(
            getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          final String[] classes = value.split(",");
          newValue = Array.newInstance(
            AuthenticationResultHandler.class,
            classes.length);
          for (int i = 0; i < classes.length; i++) {
            Array.set(
              newValue,
              i,
              instantiateType(AuthenticationResultHandler.class, classes[i]));
          }
        }
      } else if (
        AuthorizationHandler[].class.isAssignableFrom(getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          final String[] classes = value.split(",");
          newValue = Array.newInstance(
            AuthorizationHandler.class,
            classes.length);
          for (int i = 0; i < classes.length; i++) {
            Array.set(
              newValue,
              i,
              instantiateType(AuthorizationHandler.class, classes[i]));
          }
        }
      } else if (Class[].class.isAssignableFrom(getter.getReturnType())) {
        if (value.equals("null")) {
          newValue = null;
        } else {
          final String[] classes = value.split(",");
          newValue = Array.newInstance(Class.class, classes.length);
          for (int i = 0; i < classes.length; i++) {
            Array.set(newValue, i, createClass(classes[i]));
          }
        }
      } else if (getter.getReturnType().isEnum()) {
        if (LdapConfig.SearchScope.class == getter.getReturnType()) {
          newValue = Enum.valueOf(LdapConfig.SearchScope.class, value);
        }
      } else if (String[].class == getter.getReturnType()) {
        newValue = value.split(",");
      } else if (Object[].class == getter.getReturnType()) {
        newValue = value.split(",");
      } else if (float.class == getter.getReturnType()) {
        newValue = Float.parseFloat(value);
      } else if (int.class == getter.getReturnType()) {
        newValue = Integer.parseInt(value);
      } else if (long.class == getter.getReturnType()) {
        newValue = Long.parseLong(value);
      } else if (short.class == getter.getReturnType()) {
        newValue = Short.parseShort(value);
      } else if (boolean.class == getter.getReturnType()) {
        newValue = Boolean.valueOf(value);
      }
    }
    try {
      setter.invoke(object, new Object[] {newValue});
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }


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
  private static <T> T instantiateType(final T type, final String className)
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
  private static Class<?> createClass(final String className)
  {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
