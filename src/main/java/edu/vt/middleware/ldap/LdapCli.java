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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.dsml.Dsmlv2;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.props.LdapConfigPropertyInvoker;
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
    super.initOptions(
      new LdapConfigPropertyInvoker(
        LdapConfig.class,
        LdapConfig.PROPERTIES_DOMAIN));

    options.addOption(new Option(OPT_QUERY, true, ""));
  }


  /**
   * Initialize an LdapConfig with command line options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  <code>LdapConfig</code> that has been initialized
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected LdapConfig initLdapConfig(final CommandLine line)
    throws Exception
  {
    final LdapConfig config = new LdapConfig();
    this.initLdapProperties(config, line);
    if (config.getBindDn() != null && config.getBindCredential() == null) {
      // prompt the user to enter a password
      System.out.print(
        "Enter password for service user " + config.getBindDn() + ": ");

      final String pass = (new BufferedReader(new InputStreamReader(System.in)))
          .readLine();
      config.setBindCredential(new Credential(pass));
    }
    return config;
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
        initLdapConfig(line),
        line.getOptionValue(OPT_QUERY),
        line.getArgs());
    } else {
      printExamples();
    }
  }


  /**
   * Executes the ldap search operation.
   *
   * @param  config  Ldap configuration.
   * @param  filter  Ldap filter to search on.
   * @param  attrs  Ldap attributes to return.
   *
   * @throws  Exception  On errors.
   */
  protected void search(
    final LdapConfig config,
    final String filter,
    final String[] attrs)
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(config);
    final SearchOperation search = new SearchOperation(conn);

    try {
      LdapResult result = null;
      if (attrs == null || attrs.length == 0) {
        result = search.execute(
          new SearchRequest(new SearchFilter(filter))).getResult();
      } else {
        result = search.execute(
          new SearchRequest(new SearchFilter(filter), attrs)).getResult();
      }
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
