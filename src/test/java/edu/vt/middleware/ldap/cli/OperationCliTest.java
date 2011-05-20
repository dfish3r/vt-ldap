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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for ldap operation cli classes.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class OperationCliTest extends AbstractTest
{


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure
   */
  @Parameters("cliAddArgs")
  @BeforeClass(groups = {"ldapclitest"})
  public void createLdapEntry(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      AddOperationCli.main(args.split("\\|"));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure
   */
  @Parameters("cliDeleteArgs")
  @AfterClass(groups = {"ldapclitest"})
  public void deleteLdapEntry(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      DeleteOperationCli.main(args.split("\\|"));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure
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

      SearchOperationCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(ldif),
        TestUtil.convertLdifToResult(outStream.toString()));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("cliCompareArgs")
  @Test(groups = {"ldapclitest"})
  public void compare(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      CompareOperationCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        "true",
        outStream.toString().trim());
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
