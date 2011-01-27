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
package edu.vt.middleware.ldap.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import edu.vt.middleware.ldap.AbstractCli;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.dsml.Dsmlv2;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.props.LdapConfigPropertyInvoker;
import org.apache.commons.cli.CommandLine;

/**
 * Command line interface for authenticator operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorCli extends AbstractCli
{

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapauth";


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    new AuthenticatorCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions(
      new LdapConfigPropertyInvoker(
        AuthenticatorConfig.class,
        AuthenticatorConfig.PROPERTIES_DOMAIN));
  }


  /**
   * Initialize an AuthenticatorConfig with command line options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  initialized authenticator configuration
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected AuthenticatorConfig initAuthenticatorConfig(final CommandLine line)
    throws Exception
  {
    final AuthenticatorConfig config = new AuthenticatorConfig();
    this.initLdapProperties(config, line);
    if (config.getBindDn() != null && config.getBindCredential() == null) {
      // prompt the user to enter a password
      System.out.print(
        "Enter password for service user " + config.getBindDn() + ": ");

      final String pass = (new BufferedReader(new InputStreamReader(System.in)))
          .readLine();
      config.setBindCredential(new Credential(pass));
    }
    if (config.getUser() == null) {
      // prompt for a user name
      System.out.print("Enter user name: ");

      final String user = (new BufferedReader(new InputStreamReader(System.in)))
          .readLine();
      config.setUser(user);
    }
    if (config.getCredential() == null) {
      // prompt the user to enter a password
      System.out.print("Enter password for user " + config.getUser() + ": ");

      final String pass = (new BufferedReader(new InputStreamReader(System.in)))
          .readLine();
      config.setCredential(new Credential(pass));
    }
    if (line.getArgs() != null && line.getArgs().length > 0) {
      config.setReturnAttributes(line.getArgs());
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
    } else {
      authenticate(initAuthenticatorConfig(line));
    }
  }


  /**
   * Executes the authenticate operation.
   *
   * @param  config  Authenticator configuration.
   *
   * @throws  Exception  On errors.
   */
  protected void authenticate(final AuthenticatorConfig config)
    throws Exception
  {
    final Authenticator auth = new Authenticator(config);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest()).getResult();
    if (entry != null) {
      if (this.outputDsmlv1) {
        (new Dsmlv1()).outputDsml(
          new LdapResult(entry),
          new BufferedWriter(new OutputStreamWriter(System.out)));
      } else if (this.outputDsmlv2) {
        (new Dsmlv2()).outputDsml(
          new LdapResult(entry),
          new BufferedWriter(new OutputStreamWriter(System.out)));
      } else {
        (new Ldif()).outputLdif(
          new LdapResult(entry),
          new BufferedWriter(new OutputStreamWriter(System.out)));
      }
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
