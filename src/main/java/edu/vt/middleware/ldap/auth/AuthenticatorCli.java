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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import edu.vt.middleware.ldap.AbstractCli;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.dsml.Dsmlv2;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.props.AuthenticatorConfigPropertySource;
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
    final AuthenticatorConfigPropertySource acSource =
      new AuthenticatorConfigPropertySource(
        this.getPropertiesFromOptions(line));
    final AuthenticatorConfig config = acSource.get();
    if (config.getBindDn() != null && config.getBindCredential() == null) {
      // prompt the user to enter a password
      final char[] pass = System.console().readPassword(
          "[%s]", "Enter password for bind DN " + config.getBindDn() + ": ");
      config.setBindCredential(new Credential(pass));
    }
    return config;
  }


  protected AuthenticationRequest initAuthenticationRequest(
    final CommandLine line)
    throws Exception
  {
    final AuthenticationRequest request = new AuthenticationRequest();
    // prompt for a user name
    final String user = System.console().readLine("[%s]", "Enter user name: ");
    request.setUser(user);

    // prompt the user to enter a password
    final char[] pass = System.console().readPassword(
      "[%s]", "Enter password for user " + user + ": ");
    request.setCredential(new Credential(pass));

    if (line.getArgs() != null && line.getArgs().length > 0) {
      request.setReturnAttributes(line.getArgs());
    }
    return request;
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
      authenticate(
        this.initAuthenticatorConfig(line),
        this.initAuthenticationRequest(line));
    }
  }


  /**
   * Executes the authenticate operation.
   *
   * @param  config  Authenticator configuration.
   *
   * @throws  Exception  On errors.
   */
  protected void authenticate(
    final AuthenticatorConfig config, final AuthenticationRequest request)
    throws Exception
  {
    final Authenticator auth = new Authenticator(config);
    final LdapEntry entry = auth.authenticate(request).getResult();
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
