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
import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.handler.AttributeHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;
import edu.vt.middleware.ldap.handler.SearchResultHandler;

/**
 * <code>Ldap</code> contains functions for basic interaction with an LDAP.
 * Methods are provided for connecting, binding, querying and updating.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Ldap extends AbstractLdap<LdapConfig> implements Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -2715321533384426365L;

  /**
   * Enum to define the type of attribute modification. See {@link
   * javax.naming.directory.DirContext}.
   */
  public enum AttributeModification {

    /** add an attribute. */
    ADD(DirContext.ADD_ATTRIBUTE),

    /** replace an attribute. */
    REPLACE(DirContext.REPLACE_ATTRIBUTE),

    /** remove an attribute. */
    REMOVE(DirContext.REMOVE_ATTRIBUTE);


    /** underlying modification operation integer. */
    private int modOp;


    /**
     * Creates a new <code>AttributeModification</code> with the supplied
     * integer.
     *
     * @param i modification operation
     */
    AttributeModification(final int i)
    {
      this.modOp = i;
    }


    /**
     * Returns the modification operation integer.
     *
     * @return  <code>int</code>
     */
    public int modOp()
    {
      return this.modOp;
    }


    /**
     * Method to convert a JNDI constant value to an enum. Returns null if the
     * supplied constant does not match a valid value.
     *
     * @param  i  modification operation
     *
     * @return  attribute modification
     */
    public static AttributeModification parseModificationOperation(final int i)
    {
      AttributeModification am = null;
      if (ADD.modOp() == i) {
        am = ADD;
      } else if (REPLACE.modOp() == i) {
        am = REPLACE;
      } else if (REMOVE.modOp() == i) {
        am = REMOVE;
      }
      return am;
    }
  }

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
   * #compare(String, SearchFilter)}.
   *
   * @param  filter  <code>SearchFilter</code> expression to use for compare
   *
   * @return  <code>boolean</code> - result of compare operation
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public boolean compare(final SearchFilter filter)
    throws NamingException
  {
    return this.compare(this.config.getBase(), filter);
  }


  /**
   * This will perform an LDAP compare operation with the supplied filter
   * and dn.
   *
   * @param  dn  <code>String</code> name to compare
   * @param  filter  <code>SearchFilter</code> expression to use for compare
   *
   * @return  <code>boolean</code> - result of compare operation
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public boolean compare(final String dn, final SearchFilter filter)
    throws NamingException
  {
    return super.compare(
      dn, filter.getFilter(), filter.getFilterArgs().toArray());
  }


  /**
   * This will query the LDAP with the supplied filter. All attributes will be
   * returned. {@link LdapConfig#getBase()} is used as the start point for
   * searching. Search controls will be created from
   * {@link LdapConfig#getSearchControls(String[])}.
   * See {@link #search(String,SearchFilter,String[])}.
   *
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(final SearchFilter filter)
    throws NamingException
  {
    return this.search(
      this.config.getBase(), filter, this.config.getSearchControls(null));
  }


  /**
   * This will query the LDAP with the supplied filter and
   * return attributes. {@link LdapConfig#getBase()} is used as the start point
   * for searching. Search controls will be created from
   * {@link LdapConfig#getSearchControls(String[])}.
   * See {@link #search(String,SearchFilter,String[])}.
   *
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final SearchFilter filter, final String[] retAttrs)
    throws NamingException
  {
    return this.search(
      this.config.getBase(), filter, this.config.getSearchControls(retAttrs));
  }


  /**
   * This will query the LDAP with the supplied filter and
   * search controls. {@link LdapConfig#getBase()} is used as the start point
   * for searching. See {@link #search(String,SearchFilter,SearchControls)}.
   *
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  searchControls  <code>SearchControls</code> to search with
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final SearchFilter filter, final SearchControls searchControls)
    throws NamingException
  {
    return this.search(this.config.getBase(), filter, searchControls);
  }


  /**
   * This will query the LDAP with the supplied dn and filter. All attributes
   * will be returned. Search controls will be created from
   * {@link LdapConfig#getSearchControls(String[])}.
   * See {@link #search(String,SearchFilter,String[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn, final SearchFilter filter)
    throws NamingException
  {
    return this.search(dn, filter, this.config.getSearchControls(null));
  }


  /**
   * This will query the LDAP with the supplied dn, filter, and
   * return attributes. Search controls will be created from
   * {@link LdapConfig#getSearchControls(String[])}. See {@link
   * #search(String,SearchFilter,SearchControls,SearchResultHandler[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final SearchFilter filter,
    final String[] retAttrs)
    throws NamingException
  {
    return
      this.search(
        dn,
        filter,
        this.config.getSearchControls(retAttrs),
        this.config.getSearchResultHandlers());
  }


  /**
   * This will query the LDAP with the supplied dn, filter, and
   * search controls. See {@link
   * #search(String,SearchFilter,SearchControls,SearchResultHandler[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  searchControls  <code>SearchControls</code> to search with
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final SearchFilter filter,
    final SearchControls searchControls)
    throws NamingException
  {
    return
      this.search(
        dn,
        filter,
        searchControls,
        this.config.getSearchResultHandlers());
  }


  /**
   * This will query the LDAP with the supplied dn, filter, return attributes,
   * and search result handler. Search controls will be created from
   * {@link LdapConfig#getSearchControls(String[])}. See {@link #search(
   * String,SearchFilter,SearchControls,SearchResultHandler...)}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  retAttrs  <code>String[]</code> attributes to return
   * @param  handler  <code>SearchResultHandler[]</code> of handlers to execute
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final SearchFilter filter,
    final String[] retAttrs,
    final SearchResultHandler... handler)
    throws NamingException
  {
    return this.search(
      dn, filter, this.config.getSearchControls(retAttrs), handler);
  }


  /**
   * This will query the LDAP with the supplied dn, filter, search controls,
   * and search result handler. If {@link LdapConfig#getPagedResultsSize()}
   * is greater than 0, the PagedResultsControl will be invoked.
   * See {@link AbstractLdap
   * #search(String,String,Object[],SearchControls,SearchResultHandler[])}.
   *
   * @param  dn  <code>String</code> name to begin search at
   * @param  filter  <code>SearchFilter</code> expression to use for the search
   * @param  searchControls  <code>SearchControls</code> to search with
   * @param  handler  <code>SearchResultHandler[]</code> of handlers to execute
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public Iterator<SearchResult> search(
    final String dn,
    final SearchFilter filter,
    final SearchControls searchControls,
    final SearchResultHandler... handler)
    throws NamingException
  {
    if (this.config.getPagedResultsSize() > 0) {
      return super.pagedSearch(
        dn,
        filter.getFilter(),
        filter.getFilterArgs().toArray(),
        searchControls,
        handler);
    } else {
      return super.search(
        dn,
        filter.getFilter(),
        filter.getFilterArgs().toArray(),
        searchControls,
        handler);
    }
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
   * return attributes. See {@link #searchAttributes( String, Attributes,
   * String[], SearchResultHandler[])}. This method converts relative DNs to
   * fully qualified DNs, no post processing is required
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
   * This will modify the supplied attributes for the supplied value given by
   * the modification operation. See {@link
   * AbstractLdap#modifyAttributes(String, int, Attributes)}.
   *
   * @param  dn  <code>String</code> named object in the LDAP
   * @param  mod  <code>AttributeModification</code> modification operation
   * @param  attrs  <code>Attributes</code> attributes to be used for the
   * operation, may be null
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public void modifyAttributes(
    final String dn, final AttributeModification mod, final Attributes attrs)
    throws NamingException
  {
    super.modifyAttributes(dn, mod.modOp(), attrs);
  }


  /** {@inheritDoc}. */
  public void modifyAttributes(final String dn, final ModificationItem[] mods)
    throws NamingException
  {
    super.modifyAttributes(dn, mods);
  }


  /** {@inheritDoc}. */
  public void create(final String dn, final Attributes attrs)
    throws NamingException
  {
    super.create(dn, attrs);
  }


  /** {@inheritDoc}. */
  public void rename(final String oldDn, final String newDn)
    throws NamingException
  {
    super.rename(oldDn, newDn);
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
