package test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test all classes in the tree package and all its subpackages.
 */
public class TestAll extends TestCase {
  public TestAll(String name) {
    super(name);
  }
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(test.TestMyTree.class);
    suite.addTestSuite(test.TestMyNode.class);
    return suite;
  }
  public static void main(String[] args) {
    String[] testCaseName = { TestAll.class.getName() };
    junit.swingui.TestRunner.main(testCaseName);
  }
}
