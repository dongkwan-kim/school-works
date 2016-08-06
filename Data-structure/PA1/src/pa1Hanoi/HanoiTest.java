package pa1Hanoi;

/*
 * 
 */


public class HanoiTest {

	public static void main(String[] args) {
		Lab1 HanoiObj = new Lab1();
		
		//Test
		String rst = HanoiObj.hanoi(3, 1, 3/* modify this for testing your program */);
		
		//Print out the trace of operations
		
		System.out.println(rst);
	}
}
