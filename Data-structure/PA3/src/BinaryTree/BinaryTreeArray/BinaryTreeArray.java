package BinaryTree.BinaryTreeArray;

import BinaryTree.Exceptions.*;
import BinaryTree.BinaryTree.*;

public class BinaryTreeArray<T> implements BinaryTreeADT<T> {

	protected int count;
	protected int max_index;
	protected T[] tree;
	private final int capacity = 50;

	public BinaryTreeArray() {
		max_index = -1;
		count = 0;
		tree = (T[]) new Object[capacity];
	}

	public BinaryTreeArray(T element) {
		max_index = 0;
		count = 1;
		tree = (T[]) new Object[capacity];
		tree[0] = element;

		// Implement : create a binary tree with element as root
		//
		// Implementation!!!! //Done
		//
	}

	public void addElement(T element) {

		if (tree.length < max_index * 2 + 3)
			expandCapacity();

		Comparable<T> comparableE = (Comparable<T>) element;

		if (isEmpty()) {
			tree[0] = element;
			max_index = 0;
		} else {
			boolean added = false;
			int currentIndex = 0;

			while (!added) {
				if (comparableE.compareTo((tree[currentIndex])) < 0) {

					if (tree[currentIndex * 2 + 1] == null) {
						tree[currentIndex * 2 + 1] = element;
						added = true;
						if (currentIndex * 2 + 1 > max_index)
							max_index = currentIndex * 2 + 1;
					} else
						currentIndex = currentIndex * 2 + 1;
				} else {

					if (tree[currentIndex * 2 + 2] == null) {
						tree[currentIndex * 2 + 2] = element;
						added = true;
						if (currentIndex * 2 + 2 > max_index)
							max_index = currentIndex * 2 + 2;
					} else
						currentIndex = currentIndex * 2 + 2;
				}

			}
		}
		count++;

	}

	protected void expandCapacity() {
		T[] temp = (T[]) new Object[max_index * 2 + 3];
		for (int ct = 0; ct < max_index + 1; ct++)
			temp[ct] = tree[ct];
		tree = temp;
	}

	public boolean isExternal(T Element) {

		if (isEmpty()) { // 비어 있으면 false
			return false;
		}

		int indexOfElement = -1;
		// 해당 element의 index를 찾자
		for (int index = 0; index < tree.length; index++) {
			if (Element.equals(tree[index])) {
				indexOfElement = index;
				break;
			}
		}

		if (indexOfElement == -1) { // 찾지 못하면 false
			return false;
		}

		int leftIndex = indexOfElement * 2 + 1;
		int rightIndex = indexOfElement * 2 + 2;

		if (tree[leftIndex] == null && tree[rightIndex] == null) {
			return true;
		}

		return false;

		// Implement : whether External node or not
		//
		// Implementation!!!! //Done
		//
		// Return true or false
	}

	public void deleteSubtree(int index) { // define new method(recursive)

		if (tree[index] != null) {
			tree[index] = null;
			count--;
		}

		if (tree.length >= index * 2 + 3) {
			if (tree[index * 2 + 1] != null) {
				deleteSubtree(index * 2 + 1);
			}
			if (tree[index * 2 + 2] != null) {
				deleteSubtree(index * 2 + 2);
			}
		}
	}

	public void deleteLeftSubtree() {
		

		deleteSubtree(1);

		for (int index = tree.length - 1; index >= 0; index--) {

			if (tree[index] != null) {
				max_index = index;
				break;
			}

		}

		// Implement : remove left subtree of this binary tree
		//
		// Implementation!!!! // Done
		//
		//
	}

	public void deleteRightSubtree() {

		deleteSubtree(2);

		for (int index = tree.length - 1; index >= 0; index--) {

			if (tree[index] != null) {
				max_index = index;
				break;
			}

		}

		// Implement : remove right subtree of this binary tree
		//
		// Implementation!!!! // Done
		//
		//
	}

	public void deleteAllElements() {

		deleteSubtree(0);
		max_index = -1;
		count = 0;
		// Implement : remove all elements in this binary tree
		//
		// Implementation!!!! //Done
		//
		//
	}

	public boolean isEmpty() {

		return (count == 0);

		// Implement : whether binary tree is empty or not
		//
		// Implementation!!!! //Done
		//
		// Return true or false
	}

	public int size() {

		return count;
		// Implement : size of binary tree
		//
		// Implementation!!!! //Done
		//
		// Return # of elements
	}

	public boolean contains(T targetElement) {

		Comparable<T> comparableE = (Comparable<T>) targetElement;

		int currentIndex = 0;

		while (true) {
			if (comparableE.compareTo((tree[currentIndex])) < 0) {
				currentIndex = currentIndex * 2 + 1;
			} else if (comparableE.compareTo((tree[currentIndex])) > 0) {
				currentIndex = currentIndex * 2 + 2;
			}

			if (comparableE.compareTo((tree[currentIndex])) == 0) {
				return true;
			}

			int leftIndex = currentIndex * 2 + 1;
			int rightIndex = currentIndex * 2 + 2;

			if (tree[leftIndex] == null && tree[rightIndex] == null) {
				return false;
			}
		}

		// Implement : whether contain element or not
		//
		// Implementation!!!! //Done
		//
		// Return true or false
	}

	public T find(T targetElement) throws ENotFoundException {

		Comparable<T> comparableE = (Comparable<T>) targetElement;

		int currentIndex = 0;

		while (true) {
			if (comparableE.compareTo((tree[currentIndex])) < 0) {
				currentIndex = currentIndex * 2 + 1;
			} else if (comparableE.compareTo((tree[currentIndex])) > 0) {
				currentIndex = currentIndex * 2 + 2;
			}

			if (comparableE.compareTo((tree[currentIndex])) == 0) {
				return targetElement;
			}

			int leftIndex = currentIndex * 2 + 1;
			int rightIndex = currentIndex * 2 + 2;

			if (tree[leftIndex] == null && tree[rightIndex] == null) {
				throw new ENotFoundException("There is no targetElement");
			}

		}

		// Implement : find element in binary tree
		//
		// Implementation!!!! //Done
		//
		// Return element

	}

}
