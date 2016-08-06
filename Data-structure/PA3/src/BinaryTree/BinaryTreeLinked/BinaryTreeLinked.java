package BinaryTree.BinaryTreeLinked;

import BinaryTree.BinaryTree.*;
import BinaryTree.Exceptions.*;

public class BinaryTreeLinked<T> implements BinaryTreeADT<T> {

	protected int count;
	protected BinaryTreeNode<T> root;

	public BinaryTreeLinked() {
		count = 0;
		root = null;
	}

	public BinaryTreeLinked(T element) {
		count = 1;
		root = new BinaryTreeNode<T>(element);
		// Implement : create a binary tree with element as root
		//
		// Implementation!!!! // Done
		//
	}

	public void addElement(T element) {

		BinaryTreeNode<T> temp = new BinaryTreeNode<T>(element);
		Comparable<T> comparableE = (Comparable<T>) element;

		if (isEmpty())
			root = temp;
		else {
			BinaryTreeNode<T> current = root;
			boolean added = false;

			while (!added) {
				if (comparableE.compareTo(current.element) < 0)

					if (current.left == null) {
						current.left = temp;
						added = true;
					} else
						current = current.left;
				else if (current.right == null) {
					current.right = temp;
					added = true;
				} else
					current = current.right;
			}
		}

		count++;

	}

	public boolean isExternal(T Element, BinaryTreeNode<T> next) {

		if (isEmpty()) { // 비어 있으면 false
			return false;
		}

		Comparable<T> comparableE = (Comparable<T>) Element;

		while (true) {

			if (comparableE.compareTo(next.element) < 0) {
				next = next.left;
			} else if (comparableE.compareTo(next.element) > 0) {
				next = next.right;
			}

			if (comparableE.compareTo(next.element) == 0) {
				if (next.left == null && next.right == null) {
					return true;
				} else {
					return false;
				}
			}

			if (next.left == null && next.right == null) { // 찾지 못하면 false
				return false;
			}

		}

		// Implement : whether External node or not
		//
		// Implementation!!!! // Done
		//
		// Return true or false
	}

	public void deleteSubtree(BinaryTreeNode<T> node) {
		// new method(recursive)

		if (node.left != null) {
			deleteSubtree(node.left);
		}
		if (node.right != null) {
			deleteSubtree(node.right);
		}

		node = new BinaryTreeNode<T>(null);
		count--;

	}

	public void deleteLeftSubtree() {

		deleteSubtree(root.left);

		// Implement : remove left subtree of this binary tree
		//
		// Implementation!!!! //Done
		//
		//
	}

	public void deleteRightSubtree() {

		deleteSubtree(root.right);
		// Implement : remove right subtree of this binary tree
		//
		// Implementation!!!! //Done
		//
		//
	}

	public void deleteAllElements() {

		deleteSubtree(root);
		// Implement : remove all elements in this binary tree
		//
		// Implementation!!!!//Done
		//
		//
	}

	public boolean isEmpty() {

		return (count == 0);
		// Implement : whether binary tree is empty or not
		//
		// Implementation!!!!//Done
		//
		// Return true or false
	}

	public int size() {

		return count;
		// Implement : size of binary tree
		//
		// Implementation!!!!//Done
		//
		// Return # of elements
	}

	public boolean contains(T targetElement) {

		T temp;
		boolean found = false;

		try {
			temp = find(targetElement);
			found = true;
		}// try

		catch (Exception ENotFoundException) {
			found = false;
		}

		return found;

	}

	public T find(T targetElement) throws ENotFoundException {
		BinaryTreeNode<T> current = findrepeat(targetElement, root);
		if (current == null)
			throw new ENotFoundException("binarytree");
		return (current.element);
	}

	private BinaryTreeNode<T> findrepeat(T targetElement, BinaryTreeNode<T> next) {

		Comparable<T> comparableE = (Comparable<T>) targetElement;
		BinaryTreeNode<T> temp = null;

		if (comparableE.compareTo(next.element) < 0) {
			temp = findrepeat(targetElement, next.left);
		} else if (comparableE.compareTo(next.element) > 0) {
			temp = findrepeat(targetElement, next.right);
		}

		if (comparableE.compareTo(next.element) == 0) {
			temp = next;
		}

		return temp;

		// return (new BinaryTreeNode(234));

		// Implement : find element by using recursive method
		//
		// Implementation!!!!//Done
		//
		// Return element
	}

}
