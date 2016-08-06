package lab6;

public class QuickSelection {
	static int storeIndex; 
	// a variable to share the index of the pivot after partitioning

	// return the k-th smallest element of the list[left, right] with recursions
	// note k starts from 1 not 0

	public int select(int[] list, int left, int right, int k) {
		int pivotIndex = 0;
		int pivotNewIndex = 0;
		int pivotDist = 0;

		// if the list contains only one element, return the element
		if (left == right)
			return list[left];

		// select a pivotIndex as a middle point between left and right
		pivotIndex = (left + right) / 2;

		// make partitions {less than, equal to, greater than}
		list = partition(list, left, right, pivotIndex);

		// the position of the pivot after partitioning
		pivotNewIndex = storeIndex;

		// the distance between left and pivotNewIndex
		pivotDist = pivotNewIndex - left + 1;

		/*******************************************************************************
		 * SKELETON Fill up the following recursive conditions and parameters
		 * HINT: conditions are based on the pivotDist and the parameter k
		 ******************************************************************************/
		if (k == pivotDist)
			return list[pivotNewIndex];
		else if (k < pivotDist)
			return select(list, left, pivotNewIndex - 1, k);
		else
			return select(list, pivotNewIndex + 1, right, k - pivotDist);
	}

	/*******************************************************************************
	 * SKELETON fill the body of the following method to make partitions {less
	 * than, equal to, greater than} HINT: you should use the "storeIndex"
	 * variable to update the position of the pivot after partitioning HINT: use
	 * the "swap" method to swap the values in the list
	 ******************************************************************************/
	private int[] partition(int[] list, int left, int right, int pivotIndex) {
		storeIndex = left;
		int pivotValue = list[pivotIndex];

		// CODE BEGIN: around 8 lines of code expected
		while (left <= right) {

			while (left <= right && list[left] <= pivotValue) {
				left = left + 1;
			}
			while (left <= right && list[right] >= pivotValue) {
				right = right - 1;
			}
			
			if (left < right) {
				swap(list, left, right);
			}
		}
		if (left <= pivotIndex) {
			swap(list, left, pivotIndex);
			storeIndex = left;
		} else {
			swap(list, right, pivotIndex);
			storeIndex = right;
		}

		// CODE END
		return list;
	}

	// a method to swap the values in the list
	private int[] swap(int[] list, int idx1, int idx2) {
		int temp = 0;

		temp = list[idx1];
		list[idx1] = list[idx2];
		list[idx2] = temp;

		return list;
	}
}
