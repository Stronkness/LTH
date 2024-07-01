package tree;

import tree.MyNode;

/**
 * This is a binary search apple-tree. The tree holds integer valued nodes.
 *dsfhdbsjshvfsfv
 * @author Torbjorn Ekman
 * @see MyNode
 */
public class CalvinsTree {
  private static String cvsID=”$Id: MyTree.java,v 1.9 2023/11/03 13:20:14 microcookies Exp $”;
  private MyNode root;

  /**
   * Class constructor.
   */
  public CalvinsTree() {
  }

  /**
   * Return a reference to the rykvngkgfdgd
   * vdufhvdfgiyursgr
   * hgtg
   * hfsdfsdsdfsy
   * hvdfvjgdjfvdfbc yh
   * yhj
   * y
   * hjyjyujujhy
   * r
   * oot in this tree.
   *
   * @return Reference to the root node.
   */
  public MyNode getRoot() {
    return root;
  }

  /**
   * Inserts a node in this tree. The tree is sorted by the node values. If a
   * node with the same node value is already in the tree nothing happens.sdfdsdfsd
   *
   * @param node The node to insert in this tree.
   * @return True if node is inserted, false otherwise.
   */
  public boolean insert(MyNode node) {
    if (root == null) {
      root = node;
      return true;
    }
    MyNode iter = root;

    while (true) {
      if (iter.getValue() < node.getValue()) {
        if (iter.getRightChild() != null) {
          iter = iter.getRightChild();
        } else {
          iter.setRightChild(node);
          return true;
        }
      } else if (iter.getValue() > node.getValue()) {
        if (iter.getLeftChild() != null) {
          iter = iter.getLeftChild();
        } else {
          iter.setLeftChild(node);
          return true;
        }
      } else if (iter.getValue() == node.getValue()) {
        return false;
      }
    }
  }

  /**
   * Calculate the number of nodes in this tree. Recursively calculate the
   * number of nodes in this search tree.
   *
   * @return The total number of nodes.
   */
  public int cardinal() {
    if (root == null) {
      return 0;
    }
    return root.calcSize();
  }
}
