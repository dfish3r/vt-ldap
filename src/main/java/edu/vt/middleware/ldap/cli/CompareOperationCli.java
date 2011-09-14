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
import edu.vt.middleware.ldap.CompareOperation;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link CompareOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareOperationCli extends AbstractCli
{

  /** option for LDAP DN. */
  private static final String OPT_DN = "dn";

  /** option for LDAP attribute name/value pair. */
  private static final String OPT_ATTR = "attribute";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapcompare";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new CompareOperationCli().performAction(args);
  }


  /** {@inheritDoc} */
  @Override
  protected void initOptions()
  {
    options.addOption(new Option(OPT_DN, true, "entry DN"));
    options.addOption(
      new Option(
        OPT_ATTR,
        true,
        "colon delimited name value pair (attr:value|attr::b64value)"));
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
      LdapAttribute la = null;
      final String[] attr = line.getOptionValue(OPT_ATTR).split(":", 2);
      if (attr[1].startsWith(":")) {
        la = new LdapAttribute(
          attr[0], LdapUtil.base64Decode(attr[1].substring(1)));
      } else {
        la = new LdapAttribute(attr[0], attr[1]);
      }
      compare(
        initConnectionFactory(line), line.getOptionValue(OPT_DN), la);
    }
  }


  /**
   * Executes the ldap compare operation.
   *
   * @param  cf  connection factory
   * @param  dn  to compare attribute on
   * @param  attr  attribute to compare
   *
   * @throws  Exception  on any LDAP search error
   */
  protected void compare(
    final ConnectionFactory cf, final String dn, final LdapAttribute attr)
    throws Exception
  {
    final Connection conn = cf.getConnection();
    conn.open();

    final CompareOperation op = new CompareOperation(conn);
    System.out.println(op.execute(new CompareRequest(dn, attr)).getResult());
    conn.close();
  }


  /** {@inheritDoc} */
  @Override
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
