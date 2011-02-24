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
package edu.vt.middleware.ldap;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.dsml.Dsmlv2;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.props.LdapConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapCli extends AbstractCli
{

  /** Option for ldap query. */
  protected static final String OPT_QUERY = "query";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapsearch";


  /** Default constructor. */
  public LdapCli()
  {
    super();
    this.opts.add(OPT_QUERY);
  }


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    new LdapCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();
    options.addOption(new Option(OPT_QUERY, true, ""));
  }


  /**
   * Initialize an LdapConnectionConfig with command line options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  ldap connection config that has been initialized
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected LdapConnectionConfig initLdapConnectionConfig(
    final CommandLine line)
    throws Exception
  {
    final LdapConnectionConfigPropertySource lccSource =
      new LdapConnectionConfigPropertySource(
        this.getPropertiesFromOptions(line));
    final LdapConnectionConfig config = lccSource.get();
    if (config.getBindDn() != null && config.getBindCredential() == null) {
      // prompt the user to enter a password
      final char[] pass = System.console().readPassword(
        "[%s]", "Enter password for bind DN " + config.getBindDn() + ": ");
      config.setBindCredential(new Credential(pass));
    }
    return config;
  }


  /**
   * Initialize a search request with command line options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  search config that has been initialized
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected SearchRequest initSearchRequest(final CommandLine line)
    throws Exception
  {
    final SearchRequestPropertySource reader = new SearchRequestPropertySource(
      this.getPropertiesFromOptions(line));
    return reader.get();
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_DSMLV1)) {
      this.outputDsmlv1 = true;
    } else if (line.hasOption(OPT_DSMLV2)) {
      this.outputDsmlv2 = true;
    }
    if (line.hasOption(OPT_HELP)) {
      printHelp();
    } else if (line.hasOption(OPT_QUERY)) {
      search(
        initLdapConnectionConfig(line),
        initSearchRequest(line),
        line.getOptionValue(OPT_QUERY),
        line.getArgs());
    } else {
      printExamples();
    }
  }


  /**
   * Executes the ldap search operation.
   *
   * @param  lcc  ldap connection configuration.
   * @param  sr  search reqeust
   * @param  filter  ldap filter to search on.
   * @param  attrs  ldap attributes to return.
   *
   * @throws  Exception  On errors.
   */
  protected void search(
    final LdapConnectionConfig lcc,
    final SearchRequest sr,
    final String filter,
    final String[] attrs)
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(lcc);
    final SearchOperation search = new SearchOperation(conn);
    sr.setSearchFilter(new SearchFilter(filter));

    if (attrs != null && attrs.length > 0) {
      sr.setReturnAttributes(attrs);
    }
    try {
      final LdapResult result = search.execute(sr).getResult();
      if (this.outputDsmlv1) {
        (new Dsmlv1()).outputDsml(
          result,
          new BufferedWriter(new OutputStreamWriter(System.out)));
      } else if (this.outputDsmlv2) {
        (new Dsmlv2()).outputDsml(
          result,
          new BufferedWriter(new OutputStreamWriter(System.out)));
      } else {
        (new Ldif()).outputLdif(
          result,
          new BufferedWriter(new OutputStreamWriter(System.out)));
      }
    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
