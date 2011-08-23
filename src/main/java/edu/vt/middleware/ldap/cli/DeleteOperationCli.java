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

import java.util.Map;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.DeleteOperation;
import edu.vt.middleware.ldap.DeleteRequest;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link DeleteOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DeleteOperationCli extends AbstractCli
{

  /** option for LDAP DN. */
  private static final String OPT_DN = "dn";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapdelete";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new DeleteOperationCli().performAction(args);
  }


  /** {@inheritDoc} */
  @Override
  protected void initOptions()
  {
    options.addOption(new Option(OPT_DN, true, "entry DN"));
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
      delete(
        initConnectionConfig(line), line.getOptionValues(OPT_DN));
    }
  }


  /**
   * Executes the ldap delete operation.
   *
   * @param  cc  connection configuration.
   * @param  entryDns  to delete
   *
   * @throws  Exception  on any LDAP search error
   */
  protected void delete(final ConnectionConfig cc, final String[] entryDns)
    throws Exception
  {
    final Connection conn = new Connection(cc);
    conn.open();

    for (String dn : entryDns) {
      final DeleteOperation op = new DeleteOperation(conn);
      op.execute(new DeleteRequest(dn));
      System.out.println(String.format("Deleted entry: %s", dn));
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
