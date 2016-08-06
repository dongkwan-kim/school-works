import junit.framework.TestCase;

public class PriorityQueueTest extends TestCase {

	PriorityQueue PQ;

	protected void setUp() throws Exception {
		super.setUp();

		PQ = new PriorityQueue();
		PQ.add(5, "e5");
		PQ.add(2, "e2");
		
		PQ.add(8, "e8");
		
		PQ.add(1, "e1");

		PQ.add(10, "e10");
		PQ.add(7, "e7");
		PQ.add(100, "e100");
		PQ.add(55, "e55");
		PQ.add(4, "e4");
		PQ.add(12, "e12");
		PQ.add(3, "e3");

		
		

	}
	
	//inorder traversal for test
	/*
	public void travel(BT root){
		
		if(root.getLeftChild() != null){
			travel(root.getLeftChild());
		}
		System.out.println(root.getPriority());
		if(root.getRightChild() != null){
			travel(root.getRightChild());
		}
		
	}
	*/

	public void testAdd() {
		
		if (PQ.root.getPriority() != 100 || !PQ.root.getValue().equals("e100"))
			fail("fail");
		
	}

	public void testSize() {
		if (PQ.size() != 11)
			fail("fail");
	}

	public void testGetHighestPriorityValue() {
		if (!PQ.getHighestPriorityValue().equals("e100"))
			fail("fail");
	}

	public void testRemove() {
		if (PQ.remove() != "e100")
			fail("fail");
		
		if (PQ.remove() != "e55")
			fail("fail");

	}

	public void testIsEmpty() {
		if (PriorityQueue.isEmpty(PQ.root))
			fail("fail");
	}

	public void testMerge() {

		PriorityQueue PQ1 = new PriorityQueue();
		PriorityQueue PQ2 = new PriorityQueue();

		PQ1.add(1, "a1");
		PQ1.add(3, "a3");
		PQ1.add(7, "a7");
		PQ1.add(11, "a11");
		PQ1.add(5, "a5");

		
		PQ2.add(4, "b4");
		PQ2.add(2, "b2");
		PQ2.add(6, "b6");
		PQ2.add(10, "b10");
		PQ2.add(8, "b8");


		BT temp = PriorityQueue.merge(PQ1.root, PQ2.root);

		if (temp.getPriority() != 11 || !temp.getValue().equals("a11"))
			fail("fail");

	}

}
