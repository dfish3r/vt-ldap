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
package edu.vt.middleware.ldap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.vt.middleware.ldap.props.LdapConfigPropertyInvoker;
import edu.vt.middleware.ldap.props.LdapProperties;
import edu.vt.middleware.ldap.props.PropertyConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract base class for all CLI handlers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractCli
{

  /** Option to print usage. */
  protected static final String OPT_HELP = "help";

  /** Option for ldap trace. */
  protected static final String OPT_TRACE = "trace";

  /** Option for loading ldap configuration from properties. */
  protected static final String OPT_USE_PROPERTIES = "useProperties";

  /** Option for dsmlv1 output. */
  protected static final String OPT_DSMLV1 = "dsmlv1";

  /** Option for dsmlv2 output. */
  protected static final String OPT_DSMLV2 = "dsmlv2";

  /** List of command options. */
  protected List<String> opts = new ArrayList<String>();

  /** Log. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Command line options. */
  protected Options options = new Options();

  /** Whether to output dsml version 1, the default is ldif. */
  protected boolean outputDsmlv1;

  /** Whether to output dsml version 2, the default is ldif. */
  protected boolean outputDsmlv2;


  /** Default constructor. */
  public AbstractCli()
  {
    this.opts.add(OPT_HELP);
    this.opts.add(OPT_TRACE);
    this.opts.add(OPT_USE_PROPERTIES);
    this.opts.add(OPT_DSMLV1);
    this.opts.add(OPT_DSMLV2);
  }


  /**
   * Parses command line options and invokes the proper handler to perform the
   * requested action, or the default action if no action is specified.
   *
   * @param  args  Command line arguments.
   */
  public final void performAction(final String[] args)
  {
    initOptions();
    try {
      if (args.length > 0) {
        final CommandLineParser parser = new GnuParser();
        final CommandLine line = parser.parse(options, args);
        dispatch(line);
      } else {
        printExamples();
      }
    } catch (ParseException pex) {
      System.err.println(
        "Failed parsing command arguments: " + pex.getMessage());
    } catch (IllegalArgumentException iaex) {
      String msg = "Operation failed: " + iaex.getMessage();
      if (iaex.getCause() != null) {
        msg += " Underlying reason: " + iaex.getCause().getMessage();
      }
      System.err.println(msg);
    } catch (Exception ex) {
      System.err.println("Operation failed:");
      ex.printStackTrace(System.err);
    }
  }


  /** Initialize CLI options. */
  protected abstract void initOptions();


  /**
   * Initialize CLI options with the supplied invoker.
   *
   * @param  invoker  <code>PropertyInvoker</code>
   */
  protected void initOptions(final LdapConfigPropertyInvoker invoker)
  {
    final Map<String, String> args = this.getArgs();
    for (String s : invoker.getProperties()) {
      final String arg = s.substring(s.lastIndexOf(".") + 1, s.length());
      if (args.containsKey(arg)) {
        options.addOption(new Option(arg, true, args.get(arg)));
      }
    }
    options.addOption(new Option(OPT_HELP, false, "display all options"));
    options.addOption(
      new Option(OPT_TRACE, false, "print ASN.1 BER packets to System.out"));
    options.addOption(
      new Option(
        OPT_USE_PROPERTIES,
        false,
        "load options from the default properties file"));
    options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
    options.addOption(
      new Option(OPT_DSMLV2, false, "output results in DSML v2"));
  }


  /**
   * Gets the name of the command for which this class provides a CLI interface.
   *
   * @return  Name of CLI command.
   */
  protected abstract String getCommandName();


  /**
   * Dispatch command line data to the handler that can perform the operation
   * requested on the command line.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected abstract void dispatch(final CommandLine line)
    throws Exception;


  /** Prints CLI help text. */
  protected void printHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(getCommandName(), options);
  }


  /** Prints CLI usage examples. */
  protected void printExamples()
  {
    final String fullName = getClass().getName();
    final String name = fullName.substring(fullName.lastIndexOf('.') + 1);
    final InputStream in = getClass().getResourceAsStream(name + ".examples");
    if (in != null) {
      final BufferedReader reader = new BufferedReader(
        new InputStreamReader(in));
      try {
        System.out.println();

        String line = null;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      } catch (IOException e) {
        System.err.println("Error reading examples from resource stream.");
      } finally {
        try {
          reader.close();
        } catch (IOException ex) {
          System.err.println("Error closing example resource stream.");
        }
        System.out.println();
      }
    } else {
      System.out.println("No usage examples available for " + getCommandName());
    }
  }


  /**
   * Returns the command line arguments for this cli.
   *
   * @return  map of arg name to description
   */
  protected Map<String, String> getArgs()
  {
    final Map<String, String> args = new HashMap<String, String>();
    final String fullName = getClass().getName();
    final String name = fullName.substring(fullName.lastIndexOf('.') + 1);
    final InputStream in = getClass().getResourceAsStream(name + ".args");
    if (in != null) {
      final BufferedReader reader = new BufferedReader(
        new InputStreamReader(in));
      try {
        System.out.println();

        String line = null;
        while ((line = reader.readLine()) != null) {
          final String[] s = line.split(":");
          if (s.length > 1) {
            args.put(s[0], s[1]);
          }
        }
      } catch (IOException e) {
        System.err.println("Error reading arguments from resource stream.");
      } finally {
        try {
          reader.close();
        } catch (IOException ex) {
          System.err.println("Error closing arguments resource stream.");
        }
        System.out.println();
      }
    } else {
      System.out.println("No arguments available for " + getCommandName());
    }
    return args;
  }


  /**
   * Initialize the supplied config with command line options.
   *
   * @param  config  property config to configure
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On errors thrown by handler.
   */
  protected void initLdapProperties(
    final PropertyConfig config,
    final CommandLine line)
    throws Exception
  {
    final LdapProperties ldapProperties = new LdapProperties(config);
    for (Option o : line.getOptions()) {
      if (o.getOpt().equals(OPT_USE_PROPERTIES)) {
        ldapProperties.useDefaultPropertiesFile();
      } else if (!this.opts.contains(o.getOpt())) {
        ldapProperties.setProperty(o.getOpt(), o.getValue());
      }
    }
    ldapProperties.configure();
  }
}
