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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdapCli} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapCliTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry8" })
  @BeforeClass(groups = {"ldapclitest"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"ldapclitest"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cliSearchArgs", "cliSearchResults" })
  @Test(groups = {"ldapclitest"})
  public void search(final String args, final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      LdapCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(ldif),
        TestUtil.convertLdifToResult(outStream.toString()));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
