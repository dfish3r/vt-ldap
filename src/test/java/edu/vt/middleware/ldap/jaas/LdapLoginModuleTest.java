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
package edu.vt.middleware.ldap.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.AttributeModification;
import edu.vt.middleware.ldap.AttributeModificationType;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.ModifyOperation;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdapLoginModule}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapLoginModuleTest extends AbstractTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Entry created for auth tests. */
  private static LdapEntry testLdapEntry;

  /** Entries for group tests. */
  private static Map<String, LdapEntry[]> groupEntries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of group entries.
   */
  static {
    for (int i = 6; i <= 9; i++) {
      groupEntries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry10")
  @BeforeClass(groups = {"jaas"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);

    System.setProperty(
      "java.security.auth.login.config",
      "target/test-classes/ldap_jaas.config");
  }


  /**
   * @param  ldifFile6  to create.
   * @param  ldifFile7  to create.
   * @param  ldifFile8  to create.
   * @param  ldifFile9  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createGroup6",
      "createGroup7",
      "createGroup8",
      "createGroup9"
    }
  )
  @BeforeClass(groups = {"jaas"})
  public void createGroupEntry(
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9)
    throws Exception
  {
    // CheckStyle:Indentation OFF
    groupEntries.get("6")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile6)).getEntry();
    groupEntries.get("7")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile7)).getEntry();
    groupEntries.get("8")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile8)).getEntry();
    groupEntries.get("9")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile9)).getEntry();
    // CheckStyle:Indentation ON

    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }

    // setup group relationships
    final Connection conn = TestUtil.createSetupConnection();
    try {
      conn.open();
      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(new ModifyRequest(
        groupEntries.get("6")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{
              "uid=10,ou=test,dc=vt,dc=edu",
              "uugid=group7,ou=test,dc=vt,dc=edu", }))));
      modify.execute(new ModifyRequest(
        groupEntries.get("7")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{
              "uugid=group8,ou=test,dc=vt,dc=edu",
              "uugid=group9,ou=test,dc=vt,dc=edu", }))));
      modify.execute(new ModifyRequest(
        groupEntries.get("8")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{"uugid=group7,ou=test,dc=vt,dc=edu"}))));
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"jaas"})
  public void deleteAuthEntry()
    throws Exception
  {
    System.clearProperty("java.security.auth.login.config");

    super.deleteLdapEntry(testLdapEntry.getDn());
    super.deleteLdapEntry(groupEntries.get("6")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("7")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("8")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("9")[0].getDn());

    try {
      PropertiesAuthenticatorFactory.close();
    } catch (UnsupportedOperationException e) {
      // ignore if not supported
      AssertJUnit.assertNotNull(e);
    }
    try {
      PropertiesRoleResolverFactory.close();
    } catch (UnsupportedOperationException e) {
      // ignore if not supported
      AssertJUnit.assertNotNull(e);
    }
    try {
      SpringAuthenticatorFactory.close();
    } catch (UnsupportedOperationException e) {
      // ignore if not supported
      AssertJUnit.assertNotNull(e);
    }
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasUserRole", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void contextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasUserRole", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void contextSslTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-ssl", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasUserRole", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void randomContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-random", dn, user, role, credential, true);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void pooledDnResolverContextTest(
    final String dn,
    final String user,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-pooled-dnr", dn, user, "", credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void springPooledDnResolverContextTest(
    final String dn,
    final String user,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-pooled-dnr-spring", dn, user, "", credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined", "jaasCredential" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void rolesContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-roles", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "jaasDn", "jaasUser", "jaasRoleCombinedRecursive", "jaasCredential"
    }
  )
  @Test(groups = {"jaas"})
  public void rolesRecursiveContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest(
      "vt-ldap-roles-recursive",
      dn,
      user,
      role,
      credential,
      false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasUserRoleDefault", "jaasCredential" })
  @Test(groups = {"jaas"})
  public void useFirstContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-use-first", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined", "jaasCredential" })
  @Test(groups = {"jaas"})
  public void tryFirstContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-try-first", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasUserRole", "jaasCredential" })
  @Test(groups = {"jaas"})
  public void sufficientContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    doContextTest("vt-ldap-sufficient", dn, user, role, credential, false);
  }


  /**
   * @param  name  of the jaas configuration
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   * @param  checkLdapDn  whether to check the LdapDnPrincipal
   *
   * @throws  Exception  On test failure.
   */
  private void doContextTest(
    final String name,
    final String dn,
    final String user,
    final String role,
    final String credential,
    final boolean checkLdapDn)
    throws Exception
  {
    final TestCallbackHandler callback = new TestCallbackHandler();
    callback.setName(user);
    callback.setPassword(INVALID_PASSWD);

    LoginContext lc = new LoginContext(name, callback);
    try {
      lc.login();
      AssertJUnit.fail("Invalid password, login should have failed");
    } catch (UnsupportedOperationException e) {
      throw e;
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), LoginException.class);
    }

    callback.setPassword(credential);
    lc = new LoginContext(name, callback);
    try {
      lc.login();
    } catch (UnsupportedOperationException e) {
      throw e;
    } catch (Exception e) {
      AssertJUnit.fail(e.getMessage());
    }

    final Set<LdapPrincipal> principals = lc.getSubject().getPrincipals(
      LdapPrincipal.class);
    AssertJUnit.assertEquals(1, principals.size());

    final LdapPrincipal p = principals.iterator().next();
    AssertJUnit.assertEquals(p.getName(), user);
    if (!"".equals(role)) {
      AssertJUnit.assertTrue(p.getLdapEntry().getAttributes().size() > 0);
    }

    final Set<LdapDnPrincipal> dnPrincipals = lc.getSubject().getPrincipals(
      LdapDnPrincipal.class);
    if (checkLdapDn) {
      AssertJUnit.assertEquals(1, dnPrincipals.size());

      final LdapDnPrincipal dnP = dnPrincipals.iterator().next();
      AssertJUnit.assertEquals(dnP.getName(), dn);
      if (!"".equals(role)) {
        AssertJUnit.assertTrue(dnP.getLdapEntry().getAttributes().size() > 0);
      }
    } else {
      AssertJUnit.assertEquals(0, dnPrincipals.size());
    }

    final Set<LdapRole> roles = lc.getSubject().getPrincipals(LdapRole.class);

    final Iterator<LdapRole> roleIter = roles.iterator();
    String[] checkRoles = role.split("\\|");
    if (checkRoles.length == 1 && "".equals(checkRoles[0])) {
      checkRoles = new String[0];
    }
    AssertJUnit.assertEquals(checkRoles.length, roles.size());
    while (roleIter.hasNext()) {
      final LdapRole r = roleIter.next();
      boolean match = false;
      for (String s : checkRoles) {
        if (s.equals(r.getName())) {
          match = true;
        }
      }
      AssertJUnit.assertTrue(match);
    }

    final Set<LdapCredential> credentials = lc.getSubject()
        .getPrivateCredentials(LdapCredential.class);
    AssertJUnit.assertEquals(1, credentials.size());

    final LdapCredential c = credentials.iterator().next();
    AssertJUnit.assertEquals(
      new String((char[]) c.getCredential()),
      credential);

    try {
      lc.logout();
    } catch (Exception e) {
      AssertJUnit.fail(e.getMessage());
    }

    AssertJUnit.assertEquals(0, lc.getSubject().getPrincipals().size());
    AssertJUnit.assertEquals(0, lc.getSubject().getPrivateCredentials().size());
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void rolesOnlyContextTest(
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    doRolesContextTest("vt-ldap-roles-only", dn, user, role);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void dnRolesOnlyContextTest(
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    doRolesContextTest("vt-ldap-dn-roles-only", dn, user, role);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined" })
  @Test(
    groups = {"jaas"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void dnRolesOnlyPooledContextTest(
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    doRolesContextTest("vt-ldap-roles-only-pooled", dn, user, role);
  }


  /**
   * @param  name  of the jaas configuration
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   *
   * @throws  Exception  On test failure.
   */
  private void doRolesContextTest(
    final String name,
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    final TestCallbackHandler callback = new TestCallbackHandler();
    callback.setName(user);

    final LoginContext lc = new LoginContext(name, callback);
    try {
      lc.login();
    } catch (UnsupportedOperationException e) {
      throw e;
    } catch (Exception e) {
      AssertJUnit.fail(e.getMessage());
    }

    final Set<LdapRole> roles = lc.getSubject().getPrincipals(LdapRole.class);

    final Iterator<LdapRole> roleIter = roles.iterator();
    final String[] checkRoles = role.split("\\|");
    AssertJUnit.assertEquals(checkRoles.length, roles.size());
    while (roleIter.hasNext()) {
      final LdapRole r = roleIter.next();
      boolean match = false;
      for (String s : checkRoles) {
        if (s.equals(r.getName())) {
          match = true;
        }
      }
      AssertJUnit.assertTrue(match);
    }

    final Set<Group> roleGroups = lc.getSubject().getPrincipals(Group.class);
    AssertJUnit.assertTrue(roleGroups.size() == 2);
    for (Group g : roleGroups) {
      if ("Roles".equals(g.getName())) {
        final Enumeration<? extends Principal> members = g.members();
        int count = 0;
        while (members.hasMoreElements()) {
          final Principal p = members.nextElement();
          boolean match = false;
          for (LdapRole lr : lc.getSubject().getPrincipals(LdapRole.class)) {
            if (lr.getName().equals(p.getName())) {
              match = true;
            }
          }
          AssertJUnit.assertTrue(match);
          count++;
        }
        AssertJUnit.assertEquals(
          count,
          lc.getSubject().getPrincipals(LdapRole.class).size());
      } else if ("Principals".equals(g.getName())) {
        final Enumeration<? extends Principal> members = g.members();
        int count = 0;
        while (members.hasMoreElements()) {
          final Principal p = members.nextElement();
          boolean match = false;
          for (
            LdapPrincipal lp :
              lc.getSubject().getPrincipals(LdapPrincipal.class)) {
            if (lp.getName().equals(p.getName())) {
              match = true;
            }
          }
          AssertJUnit.assertTrue(match);
          count++;
        }
        AssertJUnit.assertEquals(
          count,
          lc.getSubject().getPrincipals(LdapPrincipal.class).size());
      } else {
        AssertJUnit.fail("Found invalid group");
      }
    }

    final Set<?> credentials = lc.getSubject().getPrivateCredentials();
    AssertJUnit.assertEquals(0, credentials.size());

    try {
      lc.logout();
    } catch (Exception e) {
      AssertJUnit.fail(e.getMessage());
    }

    AssertJUnit.assertEquals(0, lc.getSubject().getPrincipals().size());
    AssertJUnit.assertEquals(0, lc.getSubject().getPrivateCredentials().size());
  }
}
