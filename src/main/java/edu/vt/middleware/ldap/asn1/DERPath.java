/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.asn1;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Describes paths to individual elements of an encoded DER object that may
 * be addressed during parsing to associate a parsed element with a handler to
 * process that element.  Consider the following production rule for a complex
 * type that may be DER encoded:
 *
 * <code>
 * BankAccountSet ::= SET OF {
 *   account       BankAccount
 * }
 * BankAccount ::= SEQUENCE OF {
 *   accountNumber OCTET STRING,
 *   accountName   OCTET STRING,
 *   accountType   AccountType,
 *   balance       REAL
 * }
 * AccountType ::= ENUM {
 *   checking (0),
 *   savings (0
 * }
 * </code>
 *
 * Given a BankAccountSet type with two elements, the path to the balance of the
 * second account is given by the following canonical path (using tag names from
 * {@link UniversalDERTag}):
 *
 * <code>/SET[1]/SEQ/REAL</code>
 *
 * The index of the first element in a set or sequence is optional and omitted
 * in the canonical path, which is produced by the {@link #toString()} method.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DERPath
{

  /** Separates nodes in a path specification.  */
  public static final String PATH_SEPARATOR = "/";

  /** Pattern for matching nodes. */
  public static final Pattern NODE_PATTERN =
      Pattern.compile("([A-Za-z]+)(\\[(\\d+)\\])*");

  /** Pattern group index for matching the child index. */
  private static final int CHILD_INDEX_PATTERN_GROUP = 3;

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 31;

  /** Path nodes in this DER path. */
  private final List<Node> nodeList = new ArrayList<Node>();


  /** Creates an empty path specification. */
  public DERPath()
  {
    this(PATH_SEPARATOR);
  }


  /**
   * Copy constructor.
   *
   * @param  path  to read nodes from
   */
  public DERPath(final DERPath path)
  {
    nodeList.addAll(path.nodeList);
  }


  /**
   * Creates a path specification from its string representation.
   *
   * @param  pathSpec  string representation of a path, e.g. /SEQ[1]/CHOICE.
   */
  public DERPath(final String pathSpec)
  {
    final String[] nodes = pathSpec.split(PATH_SEPARATOR);
    for (String node : nodes) {
      if ("".equals(node)) {
        continue;
      }
      final Matcher matcher = NODE_PATTERN.matcher(node);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid node name " + node);
      }
      int childIndex = 0;
      if (matcher.group(CHILD_INDEX_PATTERN_GROUP) != null) {
        childIndex = Integer.parseInt(matcher.group(CHILD_INDEX_PATTERN_GROUP));
      }
      pushChild(matcher.group(1), childIndex);
    }
  }


  /**
   * Adds a node with the supplied name at beginning of the path.
   *
   * @param  name  of the path to add
   */
  public void pushChild(final String name)
  {
    pushChild(name, 0);
  }


  /**
   * Adds a node with the supplied name at the supplied path index.
   *
   * @param  name  of the path to add
   * @param  index  location in the path
   */
  public void pushChild(final String name, final int index)
  {
    if (index < 0) {
      throw new IllegalArgumentException("Child index must be non-negative.");
    }
    nodeList.add(new Node(name, index));
  }


  /**
   * Removes the last node in the path.
   *
   * @return  last node in the path
   */
  public Node popChild()
  {
    return nodeList.remove(nodeList.size() - 1);
  }


  /**
   * Returns whether the supplied object contains the same node list. Delegates
   * to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /**
   * Returns the hash code for this object.
   *
   * @return  hash code
   */
  @Override
  public int hashCode()
  {
    return HASH_CODE_SEED + nodeList.hashCode();
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(nodeList.size() * 10);
    for (Node n : nodeList) {
      sb.append(PATH_SEPARATOR).append(n.getName());
      if (n.childIndex > 0) {
        sb.append('[').append(n.getChildIndex()).append(']');
      }
    }
    return sb.toString();
  }


  /**
   * Node which encapsulates the path name and it's location in the path.
   *
   * @author Middleware Services
   * @version: $Revision$
   */
  private class Node
  {

    /** Name of this node. */
    private final String name;

    /** Index of this node. */
    private final int childIndex;


    /**
     * Creates a new node.
     *
     * @param  n  name of this node
     * @param  i  child index location of this node in the path
     */
    public Node(final String n, final int i)
    {
      name = n;
      childIndex = i;
    }


    /**
     * Returns the name.
     *
     * @return  name
     */
    public String getName()
    {
      return name;
    }


    /**
     * Returns the child index.
     *
     * @return  child index
     */
    public int getChildIndex()
    {
      return childIndex;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o)
    {
      if (o == null) {
        return false;
      }
      return
        o == this ||
          (getClass() == o.getClass() && o.hashCode() == hashCode());
    }


    /**
     * Returns the hash code for this object.
     *
     * @return  hash code
     */
    @Override
    public int hashCode()
    {
      int hash = HASH_CODE_SEED;
      hash = HASH_CODE_SEED * hash + name.hashCode();
      hash = HASH_CODE_SEED * hash + childIndex;
      return hash;
    }


    /**
     * Provides a descriptive string representation of this instance.
     *
     * @return  string representation
     */
    @Override
    public String toString()
    {
      return String.format("%s[%s]", name, childIndex);
    }
  }
}
