/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.handler.AttributeHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;
import edu.vt.middleware.ldap.handler.SearchResultHandler;

/**
 * <code>Ldap</code> contains functions for basic interaction with a LDAP.
 * Methods are provided for connecting, binding, querying and updating.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Ldap extends AbstractLdap<LdapConfig> implements Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -3248718478821722604L;

  /** Default constructor. */
  public Ldap() {}


  /**
   * This will create a new <code>Ldap</code> with the supplied <code>
   * LdapConfig</code>.
   *
   * @param  ldapConfig  <code>LdapConfig</code>
   */
  public Ldap(final LdapConfig ldapConfig)
  {
    this.setLdapConfig(ldapConfig);
  }


  /** {@inheritDoc}. */
  public void setLdapConfig(final LdapConfig ldapConfig)
  {
    super.setLdapConfig(ldapConfig);
  }


  /**
   * This returns the <code>LdapConfig</code> of the <code>Ldap</code>.
   *
   * @return  <code>LdapConfig</code>
   */
  public LdapConfig getLdapConfig()
  {
    return this.config;
  }


  /**
   * This will set the config parameters of this <code>Ldap</code> using the
   * default properties file, which must be located in your classpath.
   */
  public void loadFromProperties()
  {
    this.setLdapConfig(LdapConfig.createFromProperties(null));
  }


  /**
   * This will set the config parameters of this <code>Ldap</code> using the
   * supplied input stream.
   *
   * @param  is  <code>InputStream</code>
   */
  public void loadFromProperties(final InputStream is)
  {
    this.setLdapConfig(LdapConfig.createFromProperties(is));
  }


  /**
   * This will perform an LDAP compare operation with the supplied filter.
   * {@link LdapConfig#getBase()} is used as the dn to compare. See {@link
   * #compare(String, String)}.
   *
   * @param  filter  <code>String</code> expression to use for compare
   *
   * @return  <code>boolean</code> - result of compare operation
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public boolean compare(final String filter)
    throws NamingException
  {
    return this.compare(this.config.getBase(), filter);
  }


  /**
   * This will perform an LDAP compare operation with the supplied filter and
   * dn.
   *
   * @param  dn  <code>String</code> name to compare
   * @param  filter  <code>String</code> expression to use for compare
   *
   * @return  <code>boolean</code> - result of compare operation
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public boolean compare(final String dn, final String filter)
    throws NamingException
  {
    return super.compare(dn, filter);
  }


  /**
   * This will query the LDAP with the supplied filter. All attributes will be
   * returned. {@link LdapConfig#getBase()} is used as the start point for
   * searching. See {@link #search(String,String,Object[],String[])}.
   *
   * @param  filter  <code>String</code> expression to use for the search
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(final String filter)
    throws NamingException
  {
    return this.search(this.config.getBase(), filter, null, null);
  }


  /**
   * This will query the LDAP with the supplied filter and return attributes.
   * {@link LdapConfig#getBase()} is used as the start point for searching. See
   * {@link #search(String,String,Object[],String[])}.
   *
   * @param  filter  <code>String</code> expression to use for the search
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String filter,
    final String[] retAttrs)
    throws NamingException
  {
    return this.search(this.config.getBase(), filter, null, retAttrs);
  }


  /**
   * This will query the LDAP with the supplied filter, filter arguments, and
   * return attributes. {@link LdapConfig#getBase()} is used as the start point
   * for searching. See {@link #search(String,String,Object[],String[])}.
   *
   * @param  filter  <code>String</code> expression to use for the search
   * @param  filterArgs  <code>Object[]</code> to substitute for variables in
   * the filter
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String filter,
    final Object[] filterArgs,
    final String[] retAttrs)
    throws NamingException
  {
    return this.search(this.config.getBase(), filter, filterArgs, retAttrs);
  }


  /**
   * This will query the LDAP with the supplied dn and filter. All attributes
   * will be returned. See {@link #search(String,String,Object[],String[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>String</code> expression to use for the search
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(final String dn, final String filter)
    throws NamingException
  {
    return this.search(dn, filter, null, null);
  }


  /**
   * This will query the LDAP with the supplied dn, filter and return
   * attributes. See {@link #search(String,String,Object[],String[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>String</code> expression to use for the search
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final String filter,
    final String[] retAttrs)
    throws NamingException
  {
    return this.search(dn, filter, null, retAttrs);
  }


  /**
   * This will query the LDAP with the supplied dn, filter, filter arguments and
   * return attributes. See {@link
   * #search(String,String,Object[],String[],SearchResultHandler[])}. This
   * method converts relative DNs to fully qualified DNs, no post processing is
   * required.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>String</code> expression to use for the search
   * @param  filterArgs  <code>Object[]</code> to substitute for variables in
   * the filter
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final String filter,
    final Object[] filterArgs,
    final String[] retAttrs)
    throws NamingException
  {
    return
      this.search(
        dn,
        filter,
        filterArgs,
        retAttrs,
        this.config.getSearchResultHandlers());
  }


  /** {@inheritDoc}. */
  public Iterator<SearchResult> search(
    final String dn,
    final String filter,
    final Object[] filterArgs,
    final String[] retAttrs,
    final SearchResultHandler... handler)
    throws NamingException
  {
    return super.search(dn, filter, filterArgs, retAttrs, handler);
  }


  /**
   * This will query the LDAP for the supplied matching attributes. All
   * attributes will be returned. {@link LdapConfig#getBase()} is used as the
   * name to search.
   * See {@link #searchAttributes(String, Attributes, String[])}.
   *
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> searchAttributes(final Attributes matchAttrs)
    throws NamingException
  {
    return this.searchAttributes(this.config.getBase(), matchAttrs, null);
  }


  /**
   * This will query the LDAP for the supplied matching attributes and return
   * attributes. {@link LdapConfig#getBase()} is used as the name to search. See
   * {@link #searchAttributes(String, Attributes, String[])}.
   *
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> searchAttributes(
    final Attributes matchAttrs,
    final String[] retAttrs)
    throws NamingException
  {
    return this.searchAttributes(this.config.getBase(), matchAttrs, retAttrs);
  }


  /**
   * This will query the LDAP for the supplied dn and matching attributes. All
   * attributes will be returned. See {@link #searchAttributes(String,
   * Attributes, String[])}.
   *
   * @param  dn  <code>String</code> name to search in
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> searchAttributes(
    final String dn,
    final Attributes matchAttrs)
    throws NamingException
  {
    return this.searchAttributes(dn, matchAttrs, null);
  }


  /**
   * This will query the LDAP for the supplied dn, matching attributes and
   * return attributes. See {@link
   * #searchAttributes( String, Attributes, String[], SearchResultHandler[])}.
   * This method converts relative DNs to fully qualified DNs, no post
   * processing is required
   *
   * @param  dn  <code>String</code> name to search in
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> searchAttributes(
    final String dn,
    final Attributes matchAttrs,
    final String[] retAttrs)
    throws NamingException
  {
    return
      this.searchAttributes(
        dn,
        matchAttrs,
        retAttrs,
        this.config.getSearchResultHandlers());
  }


  /** {@inheritDoc}. */
  public Iterator<SearchResult> searchAttributes(
    final String dn,
    final Attributes matchAttrs,
    final String[] retAttrs,
    final SearchResultHandler... handler)
    throws NamingException
  {
    return super.searchAttributes(dn, matchAttrs, retAttrs, handler);
  }


  /** {@inheritDoc}. */
  public Iterator<NameClassPair> list(final String dn)
    throws NamingException
  {
    return super.list(dn);
  }


  /** {@inheritDoc}. */
  public Iterator<Binding> listBindings(final String dn)
    throws NamingException
  {
    return super.listBindings(dn);
  }


  /**
   * This will return all the attributes associated with the supplied dn. See
   * {@link #getAttributes(String, String[])}.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   *
   * @return  <code>Attributes</code>
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Attributes getAttributes(final String dn)
    throws NamingException
  {
    return this.getAttributes(dn, null);
  }


  /**
   * This will return the matching attributes associated with the supplied dn.
   * If retAttrs is null then all attributes will be returned. If retAttrs is an
   * empty array then no attributes will be returned. See {@link
   * #getAttributes(String, String[], AttributeHandler[])}.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Attributes</code>
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Attributes getAttributes(final String dn, final String[] retAttrs)
    throws NamingException
  {
    return this.getAttributes(dn, retAttrs, new AttributeHandler[0]);
  }


  /** {@inheritDoc}. */
  public Attributes getAttributes(
    final String dn,
    final String[] retAttrs,
    final AttributeHandler... handler)
    throws NamingException
  {
    return super.getAttributes(dn, retAttrs, handler);
  }


  /** {@inheritDoc}. */
  public Iterator<SearchResult> getSchema(final String dn)
    throws NamingException
  {
    return super.getSchema(dn);
  }


  /**
   * This will add the attribute with supplied field with the supplied value for
   * the entry with the supplied dn. If attribute already exists, replaces all
   * existing values with new specified value. If the attribute does not exist,
   * creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  value  <code>Object</code> attribute value to add
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void addAttribute(
    final String dn,
    final String field,
    final Object value)
    throws NamingException
  {
    if (value == null) {
      this.addAttribute(dn, field, null);
    } else {
      this.addAttribute(dn, field, new Object[] {value});
    }
  }


  /**
   * This will add the attribute with supplied field with the supplied values
   * for the entry with the supplied dn. If attribute already exists, replaces
   * all existing values with new specified value. If the attribute does not
   * exist, creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  values  <code>Object[]</code> attribute values to add
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void addAttribute(
    final String dn,
    final String field,
    final Object[] values)
    throws NamingException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Add attribute with the following parameters:");
      this.logger.debug("  dn = " + dn);
      this.logger.debug("  field = " + field);
      this.logger.debug("  values = " +
        (values == null ? "null" : Arrays.asList(values)));
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  config = " + this.config.getEnvironment());
      }
    }

    final Attributes attrs = new BasicAttributes(this.config.isIgnoreCase());
    final Attribute attr = new BasicAttribute(field);
    if (values != null) {
      for (Object o : values) {
        attr.add(o);
      }
    }
    attrs.put(attr);
    this.addAttributes(dn, attrs);
  }


  /**
   * This will add the supplied attributes for the entry with the supplied dn.
   * If an attribute already exists, replaces all existing values with new
   * specified value. If an attribute does not exist, creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  attrs  <code>Attributes</code> attributes to add
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void addAttributes(final String dn, final Attributes attrs)
    throws NamingException
  {
    this.modifyAttributes(dn, DirContext.ADD_ATTRIBUTE, attrs);
  }


  /**
   * This will replace the attribute with supplied field with the supplied value
   * for the entry with the supplied dn. If attribute already exists, replaces
   * all existing values with new specified value. If the attribute does not
   * exist, creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  value  <code>Object</code> attribute value to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void replaceAttribute(
    final String dn,
    final String field,
    final Object value)
    throws NamingException
  {
    if (value == null) {
      this.replaceAttribute(dn, field, null);
    } else {
      this.replaceAttribute(dn, field, new Object[] {value});
    }
  }


  /**
   * This will replace the attribute with supplied field with the supplied
   * values for the entry with the supplied dn. If attribute already exists,
   * replaces all existing values with new specified value. If the attribute
   * does not exist, creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  values  <code>Object[]</code> attribute values to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void replaceAttribute(
    final String dn,
    final String field,
    final Object[] values)
    throws NamingException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Replace attribute with the following parameters:");
      this.logger.debug("  dn = " + dn);
      this.logger.debug("  field = " + field);
      this.logger.debug("  values = " +
        (values == null ? "null" : Arrays.asList(values)));
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  config = " + this.config.getEnvironment());
      }
    }

    final Attributes attrs = new BasicAttributes(this.config.isIgnoreCase());
    final Attribute attr = new BasicAttribute(field);
    if (values != null) {
      for (Object o : values) {
        attr.add(o);
      }
    }
    attrs.put(attr);
    this.replaceAttributes(dn, attrs);
  }


  /**
   * This will replace the supplied attributes for the entry with the supplied
   * dn. If an attribute already exists, replaces all existing values with new
   * specified value. If an attribute does not exist, creates it.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  attrs  <code>Attributes</code> attributes to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void replaceAttributes(final String dn, final Attributes attrs)
    throws NamingException
  {
    this.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
  }


  /**
   * This will remove the attribute with supplied field for the entry with the
   * supplied dn.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void removeAttribute(final String dn, final String field)
    throws NamingException
  {
    this.removeAttribute(dn, field, null);
  }


  /**
   * This will remove the attribute with supplied field with the supplied value
   * for the entry with the supplied dn. The resulting attribute has the set
   * difference of its prior value set and the specified value set. If no values
   * are specified, deletes the entire attribute. Removal of the last value will
   * remove the attribute if the attribute is required to have at least one
   * value.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  value  <code>Object</code> attribute value to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void removeAttribute(
    final String dn,
    final String field,
    final Object value)
    throws NamingException
  {
    if (value == null) {
      this.removeAttribute(dn, field, null);
    } else {
      this.removeAttribute(dn, field, new Object[] {value});
    }
  }


  /**
   * This will remove the attribute with supplied field with the supplied values
   * for the entry with the supplied dn. The resulting attribute has the set
   * difference of its prior value set and the specified value set. If no values
   * are specified, deletes the entire attribute. Removal of the last value will
   * remove the attribute if the attribute is required to have at least one
   * value.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  field  <code>String</code> attribute name
   * @param  values  <code>Object[]</code> attribute values to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void removeAttribute(
    final String dn,
    final String field,
    final Object[] values)
    throws NamingException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Remove attribute with the following parameters:");
      this.logger.debug("  dn = " + dn);
      this.logger.debug("  field = " + field);
      this.logger.debug("  values = " +
        (values == null ? "null" : Arrays.asList(values)));
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  config = " + this.config.getEnvironment());
      }
    }

    final Attributes attrs = new BasicAttributes(this.config.isIgnoreCase());
    final Attribute attr = new BasicAttribute(field);
    if (values != null) {
      for (Object o : values) {
        attr.add(o);
      }
    }
    attrs.put(attr);
    this.removeAttributes(dn, attrs);
  }


  /**
   * This will remove the supplied attributes for the entry with the supplied
   * dn. The resulting attribute has the set difference of its prior value set
   * and the specified value set. If no values are specified, deletes the entire
   * attribute. Removal of the last value will remove the attribute if the
   * attribute is required to have at least one value.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  attrs  <code>Attributes</code> attributes to replace with
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void removeAttributes(final String dn, final Attributes attrs)
    throws NamingException
  {
    this.modifyAttributes(dn, DirContext.REMOVE_ATTRIBUTE, attrs);
  }


  /** {@inheritDoc}. */
  public void create(final String dn, final Attributes attrs)
    throws NamingException
  {
    super.create(dn, attrs);
  }


  /** {@inheritDoc}. */
  public void delete(final String dn)
    throws NamingException
  {
    super.delete(dn);
  }


  /**
   * This will return a list of SASL mechanisms that this LDAP supports.
   *
   * @return  <code>String[]</code> - supported SASL mechanisms
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public String[] getSaslMechanisms()
    throws NamingException
  {
    final Attributes attrs = this.getAttributes(
      "",
      new String[] {LdapConstants.SUPPORTED_SASL_MECHANISMS});

    String[] results = new String[0];
    if (attrs != null) {
      final Attribute attr = attrs.get(LdapConstants.SUPPORTED_SASL_MECHANISMS);
      if (attr != null) {
        results = (String[]) COPY_RESULT_HANDLER.process(
          new SearchCriteria(""),
          attr.getAll()).toArray(results);
      }
    }

    return results;
  }


  /**
   * This will return a list of controls that this LDAP supports.
   *
   * @return  <code>String[]</code> - supported controls
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public String[] getSupportedControls()
    throws NamingException
  {
    final Attributes attrs = this.getAttributes(
      "",
      new String[] {LdapConstants.SUPPORTED_CONTROL});

    String[] results = new String[0];
    if (attrs != null) {
      final Attribute attr = attrs.get(LdapConstants.SUPPORTED_CONTROL);
      if (attr != null) {
        results = (String[]) COPY_RESULT_HANDLER.process(
          new SearchCriteria(""),
          attr.getAll()).toArray(results);
      }
    }

    return results;
  }
}
