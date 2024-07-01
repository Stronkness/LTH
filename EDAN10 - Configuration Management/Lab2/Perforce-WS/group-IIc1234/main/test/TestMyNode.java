package test.tree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import tree.MyNode;

/**
 * This class tests MyNode
 */
public class TestMyNode extends TestCase {
  private MyNode myNode;

  public TestMyNode(String name) {
    super(name);
  }

  /**
   * This method is executed prior to each test.
   * myNode is set to referenca node with value 5;
   */
  protected void setUp() {
    myNode = new MyNode(5);
  }

  /**
   * This method is executed after each test.
   * All possibly created references to nodes are cleared.
   */
  protected void tearDown() {
    myNode = null;
  }

  /**
   * Test if a node is initialized to its correct value.
   */
  public void testGetValue() {
    assertTrue(myNode.getValue() == 5);
  }

  /**
   * Test if a node's value is modified correctly.
   */
  public void testSetValue() {
    myNode.setValue(3);
    assertTrue(myNode.getValue() == 3);
  }

  /**
   * Test if a node's right child is initialized to null.
   * Test if a node's right child is modified correctly.
   */
  public void testRightChild() {
    if(myNode.getRightChild() != null) {
      fail("Right child is not initialized to null");
    }
    MyNode rightChild = new MyNode(5);
    myNode.setRightChild(rightChild);
    assertTrue(myNode.getRightChild() == rightChild);
  }

  /**
   * Test if a node's left child is initialized to null.
   * Test if a node's left child is modified correctly.
   */
  public void testLeftChild() {
    if(myNode.getLeftChild() != null) {
      fail("Left child is not initialized to null");
    }
    MyNode leftChild = new MyNode(0);
    myNode.setLeftChild(leftChild);
    assertTrue(myNode.getLeftChild() == leftChild);
  }

  /**
   * Test if the size of a subtree is correclty calculated.
   * Three cases are tested: subtree of one node, subtree of root and left child,
   * subtree of root and right child, subtree of root and both left and right child.
   * This should test all possible cases because of the recursive implementation.
   */
  public void testCalcSize() {
    if(myNode.calcSize() != 1) {
      fail("Could not calculate size for single root");
    }
    MyNode leftChild = new MyNode(1);
    myNode.setLeftChild(leftChild);
    if(myNode.calcSize() != 2) {
      fail("Could not calculate size for a root with a single left child");
    }
    MyNode rightChild = new MyNode(2);
    myNode.setLeftChild(null);
    myNode.setRightChild(rightChild);
    if(myNode.calcSize() != 2) {
      fail("Could not calculate size for a root with a single right child");
    }
    myNode.setLeftChild(leftChild);
    if(myNode.calcSize() != 3) {
      fail("Could not calculate size for a root with both left and right child");
    }
  }

  public static void main(String[] args) {
    String[] testCaseName = { TestMyNode.class.getName() };
    junit.swingui.TestRunner.main(testCaseName);
  }
}

