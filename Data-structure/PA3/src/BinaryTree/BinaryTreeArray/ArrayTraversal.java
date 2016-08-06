package BinaryTree.BinaryTreeArray;

import java.util.Iterator;
import BinaryTree.BinaryTree.ArrayUnorderedList;

public class ArrayTraversal<T> extends BinaryTreeArray<T> {

	public Iterator<T> iteratorInOrderTraversal() {
		ArrayUnorderedList<T> list = new ArrayUnorderedList<T>();
		inorder(0, list);
		return list.iterator();
	}

	protected void inorder(int node, ArrayUnorderedList<T> list) {
		int leftNode = node * 2 + 1;
		int rightNode = node * 2 + 2;

		if (tree[leftNode] != null) {
			inorder(leftNode, list);
		}
		list.addToRear(tree[node]);
		if (tree[rightNode] != null) {
			inorder(rightNode, list);
		}
		// Implement a recursive inorder traversal
		//
		// Implementation!!!! // Done
		//

	}

	public Iterator<T> iteratorPreOrderTraversal() {
		ArrayUnorderedList<T> list = new ArrayUnorderedList<T>();
		preorder(0, list);
		return list.iterator();
	}

	protected void preorder(int node, ArrayUnorderedList<T> list) {
		
		int leftNode = node * 2 + 1;
		int rightNode = node * 2 + 2;
		
		list.addToRear(tree[node]);
		
		if (tree[leftNode] != null) {
			preorder(leftNode, list);
		}
		if (tree[rightNode] != null) {
			preorder(rightNode, list);
		}
		// Implement a recursive preorder traversal
		//
		// Implementation!!!! //Done
		//

	}

	public Iterator<T> iteratorPostOrderTraversal() {
		ArrayUnorderedList<T> list = new ArrayUnorderedList<T>();
		postorder(0, list);
		return list.iterator();
	}

	protected void postorder(int node, ArrayUnorderedList<T> list) {
		
		int leftNode = node * 2 + 1;
		int rightNode = node * 2 + 2;
		
		if (tree[leftNode] != null) {
			postorder(leftNode, list);
		}
		if (tree[rightNode] != null) {
			postorder(rightNode, list);
		}
		
		list.addToRear(tree[node]);
		
		// Implement a recursive postorder traversal
		//
		// Implementation!!!! // Done
		//
	}

}
