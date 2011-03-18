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
package edu.vt.middleware.ldap.cli;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.dsml.Dsmlv1Writer;
import edu.vt.middleware.ldap.ldif.LdifWriter;
import edu.vt.middleware.ldap.props.AuthenticationRequestPropertySource;
import edu.vt.middleware.ldap.props.AuthenticatorConfigPropertySource;
import edu.vt.middleware.ldap.props.LdapConnectionConfigPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorCli extends AbstractCli
{

  /** option for dsmlv1 output. */
  private static final String OPT_DSMLV1 = "dsmlv1";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapauth";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new AuthenticatorCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    this.options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
    final Map<String, String> desc = this.getArgDesc(
      LdapConnectionConfig.class,
      AuthenticatorConfig.class,
      AuthenticationRequest.class);
    for (String s : LdapConnectionConfigPropertySource.getProperties()) {
      final String opt = s.substring(s.lastIndexOf(".") + 1);
      this.options.addOption(new Option(opt, true, desc.get(opt)));
    }
    for (String s : AuthenticatorConfigPropertySource.getProperties()) {
      final String opt = s.substring(s.lastIndexOf(".") + 1);
      this.options.addOption(new Option(opt, true, desc.get(opt)));
    }
    for (String s : AuthenticationRequestPropertySource.getProperties()) {
      final String opt = s.substring(s.lastIndexOf(".") + 1);
      this.options.addOption(new Option(opt, true, desc.get(opt)));
    }
    super.initOptions();
  }


  /**
   * Initialize an AuthenticatorConfig with command line options.
   *
   * @param  line  parsed command line arguments
   *
   * @return  authenticator configuration that has been initialized
   *
   * @throws  Exception  if an authenticator config cannot be created
   */
  protected AuthenticatorConfig initAuthenticatorConfig(final CommandLine line)
    throws Exception
  {
    final AuthenticatorConfigPropertySource acSource =
      new AuthenticatorConfigPropertySource(
        this.getPropertiesFromOptions(
          AuthenticatorConfigPropertySource.getDomain(), line));
    final AuthenticatorConfig config = acSource.get();
    if (config.getBindDn() != null && config.getBindCredential() == null) {
      // prompt the user to enter a password
      final char[] pass = System.console().readPassword(
        "[Enter password for %s]: ", config.getBindDn());
      config.setBindCredential(new Credential(pass));
    }
    return config;
  }


  /**
   * Initialize an authentication request with command line options.
   *
   * @param  line  parsed command line arguments
   *
   * @return  authentication request that has been initialized
   *
   * @throws  Exception  if an authentication request cannot be created
   */
  protected AuthenticationRequest initAuthenticationRequest(
    final CommandLine line)
    throws Exception
  {
    final AuthenticationRequestPropertySource arSource =
      new AuthenticationRequestPropertySource(
        this.getPropertiesFromOptions(
          AuthenticationRequestPropertySource.getDomain(), line));
    final AuthenticationRequest request = arSource.get();
    if (request.getUser() == null) {
      // prompt for a user name
      final String user = System.console().readLine("[Enter user name]: ");
      request.setUser(user);
    }

    if (request.getCredential() == null) {
      // prompt the user to enter a password
      final char[] pass = System.console().readPassword(
        "[Enter password for %s]: ", request.getUser());
      request.setCredential(new Credential(pass));
    }

    return request;
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_DSMLV1)) {
      this.outputDsmlv1 = true;
    }
    if (line.hasOption(OPT_HELP)) {
      this.printHelp();
    } else {
      this.authenticate(
        this.initAuthenticatorConfig(line),
        this.initAuthenticationRequest(line));
    }
  }


  /**
   * Executes the authentication operation.
   *
   * @param  config  authenticator configuration
   * @param  request  authentication request
   *
   * @throws  Exception  on any LDAP error
   */
  protected void authenticate(
    final AuthenticatorConfig config, final AuthenticationRequest request)
    throws Exception
  {
    final Authenticator auth = new Authenticator(config);
    // by default return all attributes
    if (request.getReturnAttributes() != null &&
        request.getReturnAttributes().length == 0) {
      request.setReturnAttributes(null);
    }
    final LdapEntry entry = auth.authenticate(request).getResult();
    if (entry != null) {
      if (this.outputDsmlv1) {
        final Dsmlv1Writer writer = new Dsmlv1Writer(
          new BufferedWriter(new OutputStreamWriter(System.out)));
        writer.write(new LdapResult(entry));
      } else {
        final LdifWriter writer = new LdifWriter(
          new BufferedWriter(new OutputStreamWriter(System.out)));
        writer.write(new LdapResult(entry));
      }
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
