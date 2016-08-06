package BinaryTree.BinaryTreeArray;

import junit.framework.TestCase;
public class BinaryTreeArrayTest extends TestCase {

	
	BinaryTreeArray<Integer> BinaryTree;
	protected void setUp() throws Exception {
		super.setUp();
		
		BinaryTree= new BinaryTreeArray<Integer>(50);
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
		if(!BinaryTree.isEmpty())
			fail("fail");
		
	}

	public void testSize() {
		
		if(BinaryTree.size()!=10)
			fail("fail");
	}

	public void testContains() {
		if(!BinaryTree.contains(100))
			fail("fail");
	}

	public void testFind() {
		
		if(BinaryTree.find(100)!=100)
			fail("fail");
	}
	
	public void testIsExternal() {
		
		if(!BinaryTree.isExternal(100))
			fail("fail");
	}


}
