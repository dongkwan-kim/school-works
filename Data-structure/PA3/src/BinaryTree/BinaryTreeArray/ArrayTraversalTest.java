package BinaryTree.BinaryTreeArray;

import java.util.Iterator;

import junit.framework.TestCase;

public class ArrayTraversalTest extends TestCase {

	ArrayTraversal<Integer> Traversal;
	Iterator iter;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		Traversal= new ArrayTraversal<Integer>();
		Traversal.addElement(50);
		Traversal.addElement(45);
		Traversal.addElement(53);
		Traversal.addElement(20);
		Traversal.addElement(46);
		Traversal.addElement(52);
		Traversal.addElement(55);
		Traversal.addElement(51);
		Traversal.addElement(54);
		Traversal.addElement(100);
	
		
	}
	
	public void testIteratorInOrderTraversal() {
		
		iter=Traversal.iteratorInOrderTraversal();
		//System.out.println("inorder");	
		while (iter.hasNext()){
			System.out.println(iter.next());	
		}
	}

	public void testIteratorPreOrderTraversal() {
		
		iter=Traversal.iteratorPreOrderTraversal();
		//System.out.println("preorder");
		while (iter.hasNext()){
			System.out.println(iter.next());
		}
	}

	public void testIteratorPostOrderTraversal() {
		
		iter=Traversal.iteratorPostOrderTraversal();
		//System.out.println("postorder");
		while (iter.hasNext()){
			System.out.println(iter.next());
		}
	}
}
