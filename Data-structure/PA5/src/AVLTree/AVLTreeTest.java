
package AVLTree;

import java.util.Iterator;
import AVLTree.AVLTree.AVLNode;
import BinarySearchTree.DefaultComparator;
import BinarySearchTree.Entry;
import junit.framework.TestCase;

public class AVLTreeTest extends TestCase{
	AVLTree<Integer, String> avlTree;
	
	protected void setUp() throws Exception{
		super.setUp();
		avlTree = new AVLTree<Integer, String>(new DefaultComparator<Integer>());
	}
	

	public void testInsert() {

		System.out.println("***************** Insert operation *****************\n");
		
		avlTree.insert(1, "1");
		avlTree.insert(7, "7");
		avlTree.insert(5, "5");
		avlTree.insert(11, "11");
		avlTree.insert(9, "9");
		avlTree.insert(3, "3");
		avlTree.insert(2, "2");
		avlTree.insert(10, "10");
		avlTree.insert(6, "6");
		avlTree.insert(8, "8");
		avlTree.insert(12, "12");
		avlTree.insert(4, "4");

		System.out.println("Result of preorder traversal after performing insert operation");
		System.out.println("Correct output: 5 2 1 3 4 9 7 6 8 11 10 12 ");
		System.out.print("Result of yours: ");
			
		String result = "";
		Iterator itr = avlTree.positions().iterator();
		while(itr.hasNext()){
			AVLNode nextNode = (AVLNode)itr.next();
			if(nextNode.element() != null) {
				System.out.print(((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
				result = result + (((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
			}
		}
		System.out.println();
		System.out.println();
		assertEquals("5 2 1 3 4 9 7 6 8 11 10 12 ", result);

	}
	
	public void testRemoveEntryOfKV() {

		System.out.println("***************** Delete operation *****************\n");
		avlTree.insert(33, "33");
		avlTree.insert(24, "24");
		avlTree.insert(27, "27");
		avlTree.insert(32, "32");
		avlTree.insert(35, "35");
		avlTree.insert(29, "29");
		avlTree.insert(23, "23");
		avlTree.insert(21, "21");
		avlTree.insert(30, "30");
		avlTree.insert(25, "25");
		avlTree.insert(28, "28");
		avlTree.insert(22, "22");
		avlTree.insert(31, "31");
		avlTree.insert(26, "26");
		avlTree.insert(34, "34");


		avlTree.remove(avlTree.find(21));
		avlTree.remove(avlTree.find(22));
		//test 1) restructuring after removal of node
		System.out.println("Test 1) Result of preorder traversal after performing delete operation");
		System.out.println("Correct output: 27 25 23 24 26 32 29 28 30 31 34 33 35 ");
		System.out.print("Result of yours: ");
		
		String result = "";
		Iterator itr = avlTree.positions().iterator();
		while(itr.hasNext()){
			AVLNode nextNode = (AVLNode)itr.next();
			if(nextNode.element() != null) {
				System.out.print(((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
				result = result + (((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
			}
		}
		System.out.println();
		System.out.println();
		
		avlTree.remove(avlTree.find(26));
	
		//test 2) when imbalance propagate upward and additional restructuring is needed
		System.out.println("Test 2) Result of preorder traversal after performing delete operation - Propagated");
		System.out.println("Correct output: 29 27 24 23 25 28 32 30 31 34 33 35 ");
		System.out.print("Result of yours: ");
		
		result = "";
		itr = avlTree.positions().iterator();
		while(itr.hasNext()){
			AVLNode nextNode = (AVLNode)itr.next();
			if(nextNode.element() != null) {
				System.out.print(((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
				result = result + (((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
			}
		}
		System.out.println();
		System.out.println();
		
		avlTree.remove(avlTree.find(34));
		avlTree.remove(avlTree.find(35));
		avlTree.remove(avlTree.find(33));
		
		//Test 3) Final result of tree after deletion of several nodes
		System.out.println("Test 3) Final result of preorder traversal traversal after deletion of several nodes");
		System.out.println("Correct output: 29 27 24 23 25 28 31 30 32 ");
		System.out.print("Result of yours: ");
		
		result = "";
		itr = avlTree.positions().iterator();
		while(itr.hasNext()){
			AVLNode nextNode = (AVLNode)itr.next();
			if(nextNode.element() != null) {
				System.out.print(((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
				result = result + (((Entry<Integer,String>)(nextNode.element())).getValue()+" ");
			}
		}
		System.out.println();
		assertEquals("29 27 24 23 25 28 31 30 32 ", result);

	}
}



