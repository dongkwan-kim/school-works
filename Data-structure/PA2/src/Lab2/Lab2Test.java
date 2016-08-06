package Lab2;

/*
 * This is for checking your code.
 * If you want, you can change maze and solution or this file
 * to test your code. 
 * 
 * 
 * Evaluation of your code will be done by using different mazes.
 * 
 *     
 */

import junit.framework.TestCase;

public class Lab2Test extends TestCase {

	private final int north = 1;
	private final int east = 2;
	private final int south = 3;
	private final int west = 4;

	public void testPath() {

		element start = new element();
		element goal = new element();

		/*
		 * 0110011000 0010110101 1010110001 0000000110 1101111010 0100000000
		 * 0101110110 0101111111 0100000000 0001111111
		 */

		// test1
		// ////////////////////////////////////////////////////////////////////
		// find path
		int[][] maze1 = {

				// 0 1 2 3 4 5 6 7 8 9
				{ 0, 1, 1, 0, 0, 1, 1, 0, 0, 0 }, // 0
				{ 0, 0, 1, 0, 1, 1, 0, 1, 0, 1 }, // 1
				{ 1, 0, 1, 0, 1, 1, 0, 0, 0, 1 }, // 2
				{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 }, // 3
				{ 1, 1, 0, 1, 1, 1, 1, 0, 1, 0 }, // 4
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, // 5
				{ 0, 1, 0, 1, 1, 1, 0, 1, 1, 0 }, // 6
				{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, // 7
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, // 8
				{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 } // 9
		};

		//
		start.set_row(0);
		start.set_col(0);
		start.set_dir(east);

		// goal
		goal.set_row(8);
		goal.set_col(9);

		// student
		Lab2 student = new Lab2();
		element student_solution = student.path(maze1, start, goal);

		int num_rows = maze1.length;
		int num_cols = maze1[0].length;
		int max_stack_size = num_rows * num_cols;

		element[] solution1 = new element[max_stack_size];

		// ///////////////////////////////////// if you change maze, this part
		// will be changed

		for (int i = 0; i < 18; i++)
			solution1[i] = new element();

		solution1[0].set(0, 0, east);
		solution1[1].set(1, 0, south);
		solution1[2].set(1, 1, east);
		solution1[3].set(2, 1, south);
		solution1[4].set(3, 1, south);
		solution1[5].set(3, 2, east);
		solution1[6].set(4, 2, south);
		solution1[7].set(5, 2, south);
		solution1[8].set(6, 2, south);
		solution1[9].set(7, 2, south);
		solution1[10].set(8, 2, south);
		solution1[11].set(8, 3, east);
		solution1[12].set(8, 4, east);
		solution1[13].set(8, 5, east);
		solution1[14].set(8, 6, east);
		solution1[15].set(8, 7, east);
		solution1[16].set(8, 8, east);
		solution1[17].set(8, 9, east);

		//consol test
/*
		for (int i = 17; i >= 0; i--) {
	
			System.out.println(i + "," + student_solution.get_row() + ","
					+ student_solution.get_col() + ","
					+ student_solution.get_dir());
			student_solution = student_solution.get_below();

		}
*/

		
		for (int i = 17; i >= 0; i--) {
			assertEquals(student_solution.get_row(), solution1[i].get_row());
			assertEquals(student_solution.get_col(), solution1[i].get_col());
			assertEquals(student_solution.get_dir(), solution1[i].get_dir());
			student_solution = student_solution.get_below();
		}
		

		// ///////////////////////////////////////////////////////////////////////////

	}

	public void testNonPath() {

		// test2
		// ////////////////////////////////////////////////////////////////////////////
		// no path

		element start = new element();
		element goal = new element();
		Lab2 student_solution = new Lab2();

		int[][] maze2 = {

				// 0 1 2 3 4 5 6 7 8 9
				{ 0, 1, 1, 0, 0, 1, 1, 0, 0, 0 }, // 0
				{ 0, 0, 1, 0, 1, 1, 0, 1, 0, 1 }, // 1
				{ 1, 0, 1, 0, 1, 1, 0, 0, 0, 1 }, // 2
				{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 }, // 3
				{ 1, 1, 0, 1, 1, 1, 1, 0, 1, 0 }, // 4
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, // 5
				{ 0, 1, 0, 1, 1, 1, 0, 1, 1, 0 }, // 6
				{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, // 7
				{ 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 }, // 8
				{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 } // 9
		};

		// start
		start.set_row(0);
		start.set_col(0);
		start.set_dir(east);

		// goal
		goal.set_row(8);
		goal.set_col(9);

		element solution2 = null;
		assertEquals(student_solution.path(maze2, start, goal), solution2);

		// ////////////////////////////////////////////////////////////
	}

	public void testStackPush() {

		// test3
		// ////////////////////////////////////////////////////////////////////////////
		// stack push

		int size = 100;

		Lab2 student_solution = new Lab2();

		for (int i = 0; i < size; ++i) {
			student_solution.push(new element(size - i, i, i * i, null));
			element ptr = student_solution.stack_top;
			for (int j = i; j >= 0; --j) {
				assertTrue(student_solution.isequal_element(ptr, new element(
						size - j, j, j * j, (j == 0) ? null : ptr.below)));
				ptr = ptr.below;
			}
		}

		// ////////////////////////////////////////////////////////////
	}

	public void testStackPop() {

		// test4
		// ////////////////////////////////////////////////////////////////////////////
		// stack pop

		int size = 100;

		Lab2 student_solution = new Lab2();

		element[] elements = new element[size];
		for (int i = 0; i < size; ++i) {
			elements[i] = new element(size - i, i, i * i, (i == 0) ? null
					: elements[i - 1]);
		}
		student_solution.stack_top = elements[size - 1];

		for (int i = size - 1; i >= 0; --i) {
			element ptr = student_solution.stack_top;
			for (int j = i; j >= 0; --j) {
				assertTrue(student_solution.isequal_element(ptr, new element(
						size - j, j, j * j, (j == 0) ? null : ptr.below)));
				ptr = ptr.below;
			}
			student_solution.pop();
		}
		assertEquals(student_solution.stack_top, null);
		// ////////////////////////////////////////////////////////////
	}

}
