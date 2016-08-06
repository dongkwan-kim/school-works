package lab6;



import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

public class QuickSelectionTest extends TestCase{
	
	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void testSelection() {
		System.out.println("***************** Selection operation *****************");
		
		int[] list = {10, 2, 4, 8, 3, 6, 5, 7, 9, 1};
		 
		System.out.println("Sort the list in an ascending order of the value with Quick Selection method");
		System.out.println("Given list: 10, 2, 4, 8, 3, 6, 5, 7, 9, 1");
		System.out.println("Correct answer: 1 2 3 4 5 6 7 8 9 10");
		System.out.print("Result of yours: ");
		
		for(int i=1; i < (list.length+1); i++) {
			QuickSelection qs = new QuickSelection();
			System.out.print(qs.select(list, 0, 9, i) + " ");
		}
		System.out.println(); 
		
	}
}