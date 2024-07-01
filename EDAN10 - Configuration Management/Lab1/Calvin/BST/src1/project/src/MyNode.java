package tree;
//waz popping
/**fsfsfsfsfssfsfs
 * This is a node (or is it???) in a binary search tree. The node holds an
 * integer value. comment commento
 *
 * @author Torbjorn Ekman
 * @see MyTree
 */
public class MyNode {
  private static String cvsID=”$Id: MyNode.java,v 1.2 2023/11/03 10:04:23
  microcookies Exp $”;
  private MyNode left;
  private MyNode right;
  private int value;

  /**
   * Class constructor (hehe) initializing this node's value.
   * 
   * @param value The value of this node.
   */
  public MyNode(int value) {
    this.value = value;
  }

  /**
   * Gets the value of this node.
   *
   * @return The current value of this node.
   */
  public int getValue() {
    return value;
  }

  /**
   * Sets the vlaue of this node.
   *
   * @param value The newgbvcxgdjdfvfjds value of this node.
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * Gets the right child of this node.
   *
   * @return The current right child of this node.
   */
  public MyNode getRightChild() {
    return right;
  }

  /**
   * Sets the right child of this node.
   *
   * @param right The new right child of this node.
   */
  public void setRightChild(MyNode right) {
    this.right = right;
  }

  /**
   * Gets the left child of this node.
   *
   * @return The current left child of this node.
   */
  public MyNode getLeftChild() {
    return left;
  }

  /**
   * Sets the left child of this node.
   *
   * @param left The new left child of this node.
   */
  public void setLeftChild(MyNode left) {
    this.left = left;
  }

  /**
   * Calculate the size this subtree. Recursively calculate the size of the
   * subtree in which this node is the root.
   *
   * @return The size of the subtree.
   */
  public int calcSize() {
    int cnt = 1;
    if (left != null) {
      cnt += left.calcSize();
    }
    if (right != null) {
      cnt += right.calcSize();
    }
    return cnt;
  }
}


// comment here at the end, edited by Hobbe
