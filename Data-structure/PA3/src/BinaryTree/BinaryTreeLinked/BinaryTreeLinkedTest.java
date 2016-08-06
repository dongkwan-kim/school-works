package BinaryTree.BinaryTreeLinked;

import junit.framework.TestCase;


public class BinaryTreeLinkedTest extends TestCase {

	BinaryTreeLinked<Integer> BinaryTree;
	protected void setUp() throws Exception {
		super.setUp();
		
		BinaryTree= new BinaryTreeLinked<Integer>(50);
		BinaryTree.addElement(45);
		BinaryTree.addElement(53);
		BinaryTree.addElement(20);
		BinaryTree.addElement(46);
		BinaryTree.addElement(52);
		BinaryTree.addElement(55);
		BinaryTree.addElement(51);
		BinaryTree.addElement(54);
		BinaryTree.addElement(100);
		
	}
	
	public void testIsEmpty() {
		
		if(BinaryTree.isEmpty())
			fail("fail");
	}
	
	public void testDeleteRightSubtree() {
		
		BinaryTree.deleteRightSubtree();
		if(BinaryTree.count!=4)
			fail("fail");
	}
	
	public void testDeleteLeftSubtree() {
		
		BinaryTree.deleteLeftSubtree();
		if(BinaryTree.count!=7)
			fail("fail");
	}

	public void testDeleteAllElements() {
		
		BinaryTree.deleteAllElements();
		if(BinaryTree.count!=0)
			fail("fail");
	}

	public void testSize() {
		
		if(BinaryTree.size()!=10)
			fail("fail");
	}

	public void testContains() {
		
	
		if(!BinaryTree.contains(20))
			fail("fail");
	}
	
	public void testExternal() {

		if(!BinaryTree.isExternal(100, BinaryTree.root))
			fail("fail");
	}


}
