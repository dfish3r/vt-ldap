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
package edu.vt.middleware.ldap.jaas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import edu.vt.middleware.ldap.AttributesFactory;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.Ldap.AttributeModification;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.bean.LdapEntry;
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
public class LdapLoginModuleTest
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
  @Parameters({ "createEntry7" })
  @BeforeClass(groups = {"jaastest"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToEntry(ldif);

    Ldap ldap = TestUtil.createSetupLdap();
    ldap.create(
      testLdapEntry.getDn(),
      testLdapEntry.getLdapAttributes().toAttributes());
    ldap.close();
    ldap = TestUtil.createLdap();
    while (
      !ldap.compare(
          testLdapEntry.getDn(),
          new SearchFilter(testLdapEntry.getDn().split(",")[0]))) {
      Thread.sleep(100);
    }
    ldap.close();

    System.setProperty(
      "java.security.auth.login.config",
      "src/test/resources/ldap_jaas.config");
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
  @BeforeClass(groups = {"jaastest"})
  public void createGroupEntry(
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9)
    throws Exception
  {
    groupEntries.get("6")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile6));
    groupEntries.get("7")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile7));
    groupEntries.get("8")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile8));
    groupEntries.get("9")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile9));

    Ldap ldap = TestUtil.createSetupLdap();
    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      ldap.create(
        e.getValue()[0].getDn(),
        e.getValue()[0].getLdapAttributes().toAttributes());
    }
    ldap.close();

    ldap = TestUtil.createLdap();
    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      while (
        !ldap.compare(
            e.getValue()[0].getDn(),
            new SearchFilter(e.getValue()[0].getDn().split(",")[0]))) {
        Thread.sleep(100);
      }
    }

    // setup group relationships
    ldap.modifyAttributes(
      groupEntries.get("6")[0].getDn(),
      AttributeModification.ADD,
      AttributesFactory.createAttributes(
        "member",
        new String[] {
          "uid=7,ou=test,dc=vt,dc=edu",
          "uugid=group7,ou=test,dc=vt,dc=edu",
        }));
    ldap.modifyAttributes(
      groupEntries.get("7")[0].getDn(),
      AttributeModification.ADD,
      AttributesFactory.createAttributes(
        "member",
        new String[] {
          "uugid=group8,ou=test,dc=vt,dc=edu",
          "uugid=group9,ou=test,dc=vt,dc=edu",
        }));
    ldap.modifyAttributes(
      groupEntries.get("8")[0].getDn(),
      AttributeModification.ADD,
      AttributesFactory.createAttributes(
        "member",
        "uugid=group7,ou=test,dc=vt,dc=edu"));
    ldap.close();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"jaastest"})
  public void deleteAuthEntry()
    throws Exception
  {
    System.clearProperty("java.security.auth.login.config");

    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(testLdapEntry.getDn());
    ldap.delete(groupEntries.get("6")[0].getDn());
    ldap.delete(groupEntries.get("7")[0].getDn());
    ldap.delete(groupEntries.get("8")[0].getDn());
    ldap.delete(groupEntries.get("9")[0].getDn());
    ldap.close();
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
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void contextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap", dn, user, role, credential, false);
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
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authzContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-authz", dn, user, role, credential, true);
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
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void filterContextTest(
    final String dn,
    final String user,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-filter", dn, user, "", credential, false);
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
  @Test(groups = {"jaastest"})
  public void handlerContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    final TestCallbackHandler callback = new TestCallbackHandler();
    callback.setName(user);
    callback.setPassword(credential);

    final LoginContext lc = new LoginContext("vt-ldap-handler", callback);
    try {
      lc.login();
      AssertJUnit.fail(
        "Handler succeed set to false, login should have failed");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), LoginException.class);
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
  @Parameters({ "jaasDn", "jaasUser", "jaasRoleCombined", "jaasCredential" })
  @Test(
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void rolesContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-roles", dn, user, role, credential, false);
  }


  /**
   * @param  dn  of this user
   * @param  user  to authenticate.
   * @param  role  to set for this user
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
    "jaasDn", "jaasUser", "jaasRoleCombinedRecursive", "jaasCredential" })
  @Test(groups = {"jaastest"})
  public void rolesRecursiveContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest(
      "vt-ldap-roles-recursive", dn, user, role, credential, false);
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
  @Test(groups = {"jaastest"})
  public void useFirstContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-use-first", dn, user, role, credential, false);
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
  @Test(groups = {"jaastest"})
  public void tryFirstContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-try-first", dn, user, role, credential, false);
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
  @Test(groups = {"jaastest"})
  public void oldContextTest(
    final String dn,
    final String user,
    final String role,
    final String credential)
    throws Exception
  {
    this.doContextTest("vt-ldap-deprecated", dn, user, role, credential, false);
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
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), LoginException.class);
    }

    callback.setPassword(credential);
    lc = new LoginContext(name, callback);
    try {
      lc.login();
    } catch (Exception e) {
      AssertJUnit.fail(e.getMessage());
    }

    final Set<LdapPrincipal> principals = lc.getSubject().getPrincipals(
      LdapPrincipal.class);
    AssertJUnit.assertEquals(1, principals.size());

    final LdapPrincipal p = principals.iterator().next();
    AssertJUnit.assertEquals(p.getName(), user);
    if (!role.equals("")) {
      AssertJUnit.assertTrue(p.getLdapAttributes().size() > 0);
    }

    final Set<LdapDnPrincipal> dnPrincipals = lc.getSubject().getPrincipals(
      LdapDnPrincipal.class);
    if (checkLdapDn) {
      AssertJUnit.assertEquals(1, dnPrincipals.size());
      final LdapDnPrincipal dnP = dnPrincipals.iterator().next();
      AssertJUnit.assertEquals(dnP.getName(), dn);
      if (!role.equals("")) {
        AssertJUnit.assertTrue(dnP.getLdapAttributes().size() > 0);
      }
    } else {
      AssertJUnit.assertEquals(0, dnPrincipals.size());
    }

    final Set<LdapRole> roles = lc.getSubject().getPrincipals(LdapRole.class);

    final Iterator<LdapRole> roleIter = roles.iterator();
    String[] checkRoles = role.split("\\|");
    if (checkRoles.length == 1 && checkRoles[0].equals("")) {
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
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void rolesOnlyContextTest(
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    this.doRolesContextTest("vt-ldap-roles-only", dn, user, role);
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
    groups = {"jaastest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void dnRolesOnlyContextTest(
    final String dn,
    final String user,
    final String role)
    throws Exception
  {
    this.doRolesContextTest("vt-ldap-dn-roles-only", dn, user, role);
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
