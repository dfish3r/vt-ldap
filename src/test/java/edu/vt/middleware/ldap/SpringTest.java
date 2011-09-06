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

import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for Spring integration.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SpringTest
{


  /**
   * Attempts to load all Spring application context XML files to
   * verify proper wiring.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"springtest"})
  public void testSpringWiring()
    throws Exception
  {
    final ClassPathXmlApplicationContext context =
      new ClassPathXmlApplicationContext(new String[] {
        "/spring-context.xml",
      });
    AssertJUnit.assertTrue(context.getBeanDefinitionCount() > 0);
    final ConnectionFactory cf =
      (ConnectionFactory) context.getBean("connectionFactory");
    final Connection conn = cf.getConnection();
    conn.open();
    conn.close();

    final ClassPathXmlApplicationContext poolContext =
      new ClassPathXmlApplicationContext(new String[] {
        "/spring-pool-context.xml",
      });
    AssertJUnit.assertTrue(poolContext.getBeanDefinitionCount() > 0);
    final BlockingConnectionPool pool =
      (BlockingConnectionPool) poolContext.getBean("pool");
    pool.close();
  }
}
