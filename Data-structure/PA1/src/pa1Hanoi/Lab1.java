package pa1Hanoi;

/*******************************************************************************************************************************************************************************
 * Lab1: Tower of Hanoi There are three rods which are indexed as "1", "2", "3",
 * and there are n disks on the rod "1". The objective of your program is to
 * move the entire stack to another rod, "3", obeying the following rules: -
 * Only one disk may be moved at a time. - Each move consists of taking the
 * upper disk from one of the rods and sliding it onto another rod, on top of
 * the other disks that may already be present on that rod. - No disk may be
 * placed on top of a smaller disk.
 ********************************************************************************************************************************************************************************/

public class Lab1 {
	private static int n; // A variable for the number of disks.

	// Constructor for the default case. Do not modify this.
	public Lab1() {
		n = 3;
	}

	// Constructor with a parameter for the number of disks. Do not modify this.
	public Lab1(int n) {
		this.n = n;
	}

	public String hanoi(int nDisks, int from, int to) {
		// parameters:
		// nDisks - the number of disks to be moved,
		// from - the index of the rod where the disk is,
		// to - the index of the rod where the disk is moving to.

		String sol1, sol2, currentStep, solution; // String variables to contain
													// moves
		int theOther = 6 - from - to; // The other rod, not "from" nor "to"
		// Because the sum of indexes of rods is 6, i.e. 1+2+3=6.

		// String containing the solution. Do not modify the next line.
		currentStep = "Move a disc from " + from + " to " + to + "\n";

		/******************/
		/* Fill in this part */

		if (this.n < nDisks) {
			return "You choose too many disk to move";
		}

		/******************/
		if (nDisks == 1) {
			// Return the atomic solution. You don't need to modify this.
			return currentStep;
		} else {
			// Call the hanoi method recursively to make an intermediate move.
			sol1 = hanoi(nDisks - 1, from, theOther);
			// Call the hanoi method recursively to make a secondary move.
			sol2 = hanoi(nDisks - 1, theOther, to);

			// Construct a local solution. You don't need to modify this.
			solution = sol1 + currentStep + sol2;

			// Return the local solution. You don't need to modify this.
			return solution;
		}
	}
}