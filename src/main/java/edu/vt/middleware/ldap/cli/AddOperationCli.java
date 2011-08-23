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

import java.io.FileReader;
import java.util.Map;
import edu.vt.middleware.ldap.AddOperation;
import edu.vt.middleware.ldap.AddRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.ldif.LdifReader;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link AddOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AddOperationCli extends AbstractCli
{

  /** option for LDIF file. */
  private static final String OPT_FILE = "file";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapadd";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new AddOperationCli().performAction(args);
  }


  /** {@inheritDoc} */
  @Override
  protected void initOptions()
  {
    options.addOption(new Option(OPT_FILE, true, "LDIF file"));
    final Map<String, String> desc = getArgDesc(
      ConnectionConfig.class);
    for (String s : ConnectionConfigPropertySource.getProperties()) {
      options.addOption(new Option(s, true, desc.get(s)));
    }
    super.initOptions();
  }


  /** {@inheritDoc} */
  @Override
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_HELP)) {
      printHelp();
    } else {
      add(
        initConnectionConfig(line), line.getOptionValue(OPT_FILE));
    }
  }


  /**
   * Executes the ldap add operation.
   *
   * @param  cc  connection configuration
   * @param  file  to read ldif from
   *
   * @throws  Exception  on any LDAP search error
   */
  protected void add(final ConnectionConfig cc, final String file)
    throws Exception
  {
    final Connection conn = new Connection(cc);
    conn.open();

    final LdifReader reader = new LdifReader(new FileReader(file));
    final LdapResult lr = reader.read();
    for (LdapEntry le : lr.getEntries()) {
      final AddOperation op = new AddOperation(conn);
      op.execute(new AddRequest(le.getDn(), le.getAttributes()));
      System.out.println(String.format("Added entry: %s", le));
    }
    conn.close();
  }


  /** {@inheritDoc} */
  @Override
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
