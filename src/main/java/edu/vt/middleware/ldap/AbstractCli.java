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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

  /** Option for dsmlv1 output. */
  protected static final String OPT_DSMLV1 = "dsmlv1";

  /** List of command options. */
  protected List<String> opts = new ArrayList<String>();

  /** Log. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Command line options. */
  protected Options options = new Options();

  /** Whether to output dsml version 1, the default is ldif. */
  protected boolean outputDsmlv1;


  /** Default constructor. */
  public AbstractCli()
  {
    this.opts.add(OPT_HELP);
    this.opts.add(OPT_DSMLV1);
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
  protected void initOptions()
  {
    final Map<String, String> args = this.getArgs();
    for (Map.Entry<String, String> entry : args.entrySet()) {
      options.addOption(new Option(entry.getKey(), true, entry.getValue()));
    }
    options.addOption(new Option(OPT_HELP, false, "display all options"));
    options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
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
   */
  protected Properties getPropertiesFromOptions(final CommandLine line)
    throws Exception
  {
    final Properties props = new Properties();
    for (Option o : line.getOptions()) {
      props.setProperty(o.getOpt(), o.getValue());
    }
    return props;
  }
}
