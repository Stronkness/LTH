package test.tree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import tree.MyTree;
import tree.MyNode;

/**
 * This class tests MyTree
 */
public class TestMyTree extends TestCase {
  private MyTree myTree;

  public TestMyTree(String name) {
    super(name);
  }

  /**
   * This method is executed prior to each test.
   * myTree is initialized to an empty tree.
   */
  protected void setUp() {
    myTree = new MyTree();
  }

  /**
   * This method is executed after each test.
   * The reference to the created tree is cleared.
   */
  protected void tearDown() {
    myTree = null;
  }

  /**
   * Test if the newly created tree is empty.
   */
  public void testMyTree() {
    assertTrue(myTree.getRoot() == null);
  }

  /**
   * Test if nodes are inserted in the right order.
   */
  public void testInsert() {
    MyNode node5 = new MyNode(5);
    MyNode node3 = new MyNode(3);
    MyNode node7 = new MyNode(7);
    MyNode node6 = new MyNode(6);
    myTree.insert(node5);
    assertTrue("Error inserting root node", myTree.getRoot() == node5);
    myTree.insert(node3);
    assertTrue(node5.getLeftChild() == node3);
    myTree.insert(node7);
    assertTrue(node5.getRightChild() == node7);
    myTree.insert(node6);
    assertTrue(node5.getRightChild().getLeftChild() == node6);
  }

  /**
   * Test to insert an already inserted node.
   */
  public void testInsertSame() {
    MyNode node1 = new MyNode(5);
    myTree.insert(node1);
    if(myTree.insert(node1) != false) {
      fail("Managed to insert the same node twice");
    }
  }

   /**
   * Test if cardinal works by calculating tree size for a few different trees.
   */
  public void testCardinal() {
    MyNode node5 = new MyNode(5);
    MyNode node3 = new MyNode(3);
    MyNode node7 = new MyNode(7);
    MyNode node6 = new MyNode(6);
    assertTrue(myTree.cardinal() == 0);
    myTree.insert(node5);
    assertTrue(myTree.cardinal() == 1);
    myTree.insert(node3);
    assertTrue(myTree.cardinal() == 2);
    myTree.insert(node7);
    assertTrue(myTree.cardinal() == 3);
    myTree.insert(node6);
    assertTrue(myTree.cardinal() == 4);
  }

  public static void main(String[] args) {
    String[] testCaseName = { TestMyTree.class.getName() };
    junit.swingui.TestRunner.main(testCaseName);
  }
}

