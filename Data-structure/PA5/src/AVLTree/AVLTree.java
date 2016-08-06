/* PA5 - AVL tree 
 * Due : 12/5/2014 23:59
 */

package AVLTree;

import java.util.Comparator;

import BinarySearchTree.BTNode;
import BinarySearchTree.BTPosition;
import BinarySearchTree.BinarySearchTree;
import BinarySearchTree.Dictionary;
import BinarySearchTree.Entry;
import BinarySearchTree.Position;
import Exception.InvalidEntryException;
import Exception.InvalidKeyException;

public class AVLTree<K, V> extends BinarySearchTree<K, V> implements
		Dictionary<K, V> {

	public AVLTree(Comparator<K> c) {
		super(c);
	}

	public AVLTree() {
		super();
	}

	/* Nested class for the nodes of an AVL tree. */
	protected static class AVLNode<K, V> extends BTNode<Entry<K, V>> {

		protected int height;

		// constructor1
		AVLNode() {
		}

		// consturctor2
		AVLNode(Entry<K, V> element, BTPosition<Entry<K, V>> parent,
				BTPosition<Entry<K, V>> left, BTPosition<Entry<K, V>> right) {

			super(element, parent, left, right);

			height = 0;

			if (left != null)
				height = Math.max(height,
						1 + ((AVLNode<K, V>) left).getHeight());

			if (right != null)
				height = Math.max(height,
						1 + ((AVLNode<K, V>) right).getHeight());
		}

		public void setHeight(int h) {
			height = h;
		}

		public int getHeight() {
			return height;
		}
	}

	/* Create a new binary search tree node */
	protected BTPosition<Entry<K, V>> createNode(Entry<K, V> element,
			BTPosition<Entry<K, V>> parent, BTPosition<Entry<K, V>> left,
			BTPosition<Entry<K, V>> right) {

		return new AVLNode<K, V>(element, parent, left, right);
	}

	/* Returns the height of a node */
	protected int height(Position<Entry<K, V>> p) {
		return ((AVLNode<K, V>) p).getHeight();
	}

	protected void setHeight(Position<Entry<K, V>> p) {
		((AVLNode<K, V>) p).setHeight(1 + Math.max(height(left(p)),
				height(right(p))));
	}

	/* Returns whether tree is balanced */
	protected boolean isBalanced(Position<Entry<K, V>> p) {
		int bf = height(left(p)) - height(right(p));
		return Math.abs(bf) <= 1;
	}

	/*
	 * Insert(k, v) insert new entry
	 * 
	 * @see BinarySearchTree.BinarySearchTree#insert , and then call 'rebalance'
	 * method for updating height and restructuring
	 */
	public Entry<K, V> insert(K k, V v) throws InvalidKeyException {
		Entry<K, V> newEntry = super.insert(k, v);
		// actionPos becomes inserted node
		//System.out.println("\nadd-----------" + actionPos.element().getKey());
		rebalance(actionPos);
		return newEntry;
	}

	/*
	 * remove(ent) remove entry that corresponds to 'ent'
	 * 
	 * @see BinarySearchTree.BinarySearchTree#remove , and then call 'rebalance'
	 * method for updating height and restructuring
	 */
	public Entry<K, V> remove(Entry<K, V> ent) throws InvalidEntryException {
		Entry<K, V> rmEntry = super.remove(ent);
		// actionPos becomes the removed node's parent
		if (rmEntry != null) {
			//System.out.println("\nremove-----------"+ ent.getKey());
			rebalance(actionPos);
		}
		return rmEntry;
	}

	/*
	 * getTallerChild(p) Returns a child of p with height no smaller than that
	 * of other child
	 */
	protected Position<Entry<K, V>> getTallerChild(Position<Entry<K, V>> p) {
		if (height(left(p)) > height(right(p)))
			return left(p);
		else if (height(left(p)) < height(right(p)))
			return right(p);
		// equal height child - break tie using parent's type
		if (isRoot(p))
			return left(p);
		if (p == left(parent(p)))
			return left(p);
		else
			return right(p);

	}

	/*
	 * Skeleton - rebalance(zPos) Traverse the path from zPos to the root. For
	 * each node encountered, recompute its height Perform a trinode
	 * reconstructing if it's unbalanced --> Call restructure method
	 * 
	 * Refer to methods in 'BinarySearchTree' package to access nodes in binary
	 * search tree.
	 */
	protected void rebalance(Position<Entry<K, V>> zPos) {

		//remove했을 때 null이면 그 위의 parent를 조사한다
		if(zPos.element() == null){
			zPos = parent(zPos);
			setHeight(zPos);
		}

		while (true) {

			setHeight(zPos);
			
			/*
			System.out
					.println(zPos.element().getKey() + "/bal:" + isBalanced(zPos)+"/root:"+root.element().getKey()+isRoot(zPos));
			System.out.println("P" + height(zPos));
			System.out.println("L" + height(left(zPos)));
			System.out.println("R" + height(right(zPos)));
			if(height(right(zPos)) == 3){
				System.out.println(right(zPos).element().getKey());
				System.out.println(left(right(zPos)).element().getKey());
				System.out.println(right(right(zPos)).element().getKey());
			}
			System.out.println();
			 */
			
			if (!isBalanced(zPos)) {
				
				zPos = restructure(getTallerChild(getTallerChild(zPos)));
				/*
				System.out.println("restructured"+zPos.element().getKey());
				System.out.println("root"+root.element().getKey());
				*/
				setHeight(left(zPos));
				setHeight(right(zPos));
				setHeight(zPos);

			} else {
				if (isRoot(zPos)) {
					break;
				} else {
					zPos = parent(zPos);
				}
			}

		}
		/* END CODE */
	}

	/*
	 * Skeleton - restructure(x) Input : A node x of a binary search tree that
	 * has both a parent y and grandparent z conducts a trinode restructuring
	 * involving nodes x, y, and z
	 */
	protected Position<Entry<K, V>> restructure(Position<Entry<K, V>> x) {
		Position<Entry<K, V>> y = parent(x);
		Position<Entry<K, V>> z = parent(y);

		/* For more information, refer to lecture node p. 16~20 */
		BTPosition<Entry<K, V>> a, b, c;
		// Variable for renaming x, y, z as a, b, c in an inorder traversal
		BTPosition<Entry<K, V>> t0, t1, t2, t3;
		// Variable for roots of subtrees

		/*
		 * Type casting for invoking methods accessing its child nodes or parent
		 * node
		 */
		BTPosition<Entry<K, V>> xx = (BTPosition<Entry<K, V>>) x, yy = (BTPosition<Entry<K, V>>) y, zz = (BTPosition<Entry<K, V>>) z;

		/* BEGIN CODE */

		// a, b, c를 inorder 순으로 재설정한다
		// t0, t1, t2, t3를 설정한다.
		if (zz.getLeft().equals(yy)) {
			c = zz;
			if (yy.getLeft().equals(xx)) {
				b = yy;
				a = xx;
				t0 = xx.getLeft();
				t1 = xx.getRight();
				t2 = yy.getRight();
				t3 = zz.getRight();
			} else {
				b = xx;
				a = yy;
				t0 = yy.getLeft();
				t1 = xx.getLeft();
				t2 = xx.getRight();
				t3 = zz.getRight();
			}
		} else {
			a = zz;
			if (yy.getLeft().equals(xx)) {
				b = xx;
				c = yy;
				t0 = zz.getLeft();
				t1 = xx.getLeft();
				t2 = xx.getRight();
				t3 = yy.getRight();
			} else {
				b = yy;
				c = xx;
				t0 = zz.getLeft();
				t1 = yy.getLeft();
				t2 = xx.getLeft();
				t3 = xx.getRight();
			}
		}

		// Restruction

		// R1. parent node의 설정
		if (isRoot(zz)) {
			b.setParent(null);
			root = b;
		} else {
			BTPosition<Entry<K, V>> ww = zz.getParent();
			b.setParent(ww);
			if (ww.getLeft().equals(zz)) {
				ww.setLeft(b);
			} else {
				ww.setRight(b);
			}
		}

		// R2. left, right child의 설정
		b.setLeft(a);
		a.setParent(b);
		b.setRight(c);
		c.setParent(b);

		// R3. ti의 설정
		a.setLeft(t0);
		a.setRight(t1);
		c.setLeft(t2);
		c.setRight(t3);

		t0.setParent(a);
		t1.setParent(a);
		t2.setParent(c);
		t3.setParent(c);

		/* END CODE */

		return b;
	}
}
