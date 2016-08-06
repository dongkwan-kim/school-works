package Lab2;

/* Maze
 * There is maze to find path. 
 * You have to find path from start to goal using data structure, stack.
 * 
 * this is an example of maze
 * 
 * 11111111111
 * 11111111111
 * 00000000011
 * 11110111111
 * 11110111111
 * 11110000000
 * 11110111111
 * 
 * 1: wall, 0: road
 * start: row=2, column=0, direction=east
 * goal: row=5, column=10
 * 
 */

public class Lab2 {

	// ////////////////////////////////////////////////////Do not modify this
	// region
	// Constant for direction.
	private final int north = 1;
	private final int east = 2;
	private final int south = 3;
	private final int west = 4;

	element stack_top = null;

	public Lab2() // constructor
	{

	}

	// ////////////////////////////////////////////////////

	public element path(int[][] maze, element start, element goal) {
		stack_top = null;

		// //////////////////////////////////////////////////// Do not modify
		// this region
		int num_rows = maze.length;
		int num_cols = maze[0].length;
		int[][] mark = new int[num_rows][num_cols];

		push(start);
		// ///////////////////////////////////////////////////

		while (stack_top != null)
		// Hint: use top variable(index of stack top) and mark variable

		{

			// if stack top equal goal
			if (stack_top.get_row() == goal.get_row()
					&& stack_top.get_col() == goal.get_col()) {
				System.out.println("path exists");
				return stack_top; // change return value
			}

			// //////////////////////////////////////////// Fill your code to
			// find path in this region

			/* FILL in HERE!!!! */

			int topRow = stack_top.get_row();
			int topCol = stack_top.get_col();
			mark[topRow][topCol] = 1; // mark

			boolean[] possibleArea = { false, false, false, false };
			// N, E, S, W

			// 1st if. Existence(IndexOutOfBoundsException)
			// 2nd if. Not wall (==0, maze)
			// 3rd if. Not marked (==0, mark)
			int newRow = topRow;
			int newCol = topCol;

			if (topRow != 0) { //N
				if (maze[topRow - 1][topCol] == 0) {
					if (mark[topRow - 1][topCol] == 0) {
						possibleArea[0] = true;
						newRow = topRow - 1;
						newCol = topCol;
					}
				}
			}
			if (topCol != num_cols - 1) { //E
				if (maze[topRow][topCol + 1] == 0) {
					if (mark[topRow][topCol + 1] == 0) {
						possibleArea[1] = true;
						newRow = topRow;
						newCol = topCol + 1;
					}
				}
			}
			if (topRow != num_rows - 1) { //S
				if (maze[topRow + 1][topCol] == 0) {
					if (mark[topRow + 1][topCol] == 0) {
						possibleArea[2] = true;
						newRow = topRow + 1;
						newCol = topCol;
					}
				}
			}

			if (topCol != 0) { //W
				if (maze[topRow][topCol - 1] == 0) {
					if (mark[topRow][topCol - 1] == 0) {
						possibleArea[3] = true;
						newRow = topRow;
						newCol = topCol - 1;
					}
				}
			}

			for (int index = 3; index >= 0; index--) {
				if (possibleArea[index]) {
					element newElement = new element();
					newElement.set(newRow, newCol, index + 1);
					push(newElement);
					break;
				}
				if (index == 0) {
					pop();
				}
			}

			// /////////////////////////////////////////////

		}

		return null; // Do not modify
	}

	public void push(element item) {

		// //////////////////////////////////////////// Fill your code to find
		// path in this region

		/* FILL in HERE!!!! */
		item.set_below(stack_top);
		stack_top = item;

		// /////////////////////////////////////////////

	}

	public void pop() {
		// //////////////////////////////////////////// Fill your code to find
		// path in this region

		/* FILL in HERE!!!! */
		if (stack_top == null) { // stack이 비어있으면 실행 안함

		} else {
			stack_top = stack_top.get_below();
		}

		// ////////////////////////////////////////////
	}

	public boolean isequal_element(element element1, element element2) {
		if ((element1.get_col() == element2.get_col())
				&& (element1.get_row() == element2.get_row())
				&& (element1.get_dir() == element2.get_dir())
				&& element1.get_below() == element2.get_below()) {
			return true;
		}
		return false;
	}
}

// ////////////////////////////////////////////////// Do not modify this region
class element {

	private int row;
	private int col;
	private int dir;
	element below;

	public element() {
		this.row = -1;
		this.col = -1;
		this.dir = -1;
		this.below = null;
	}

	public element(int row, int col, int dir, element below) {
		this.row = row;
		this.col = col;
		this.dir = dir;
		this.below = below;
	}

	public void set_row(int row) {
		this.row = row;
	}

	public void set_col(int col) {
		this.col = col;
	}

	public void set_dir(int dir) {
		this.dir = dir;
	}

	public void set(int row, int col, int dir) {
		this.row = row;
		this.col = col;
		this.dir = dir;
	}

	public void set_below(element below) {
		this.below = below;
	}

	public int get_row() {
		return this.row;
	}

	public int get_col() {
		return this.col;
	}

	public int get_dir() {
		return this.dir;
	}

	public element get_below() {
		return this.below;
	}
}
// ////////////////////////////////////////////////////////