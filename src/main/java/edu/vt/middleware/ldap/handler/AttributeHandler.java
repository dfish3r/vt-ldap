/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.handler;

import javax.naming.directory.Attribute;

/**
 * AttributeHandler provides post search processing of an ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AttributeHandler extends ResultHandler<Attribute, Attribute> {}
