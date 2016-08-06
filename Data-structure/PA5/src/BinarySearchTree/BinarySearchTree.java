package BinarySearchTree;

import java.util.Comparator;

import Exception.InvalidEntryException;
import Exception.InvalidKeyException;

public class BinarySearchTree<K, V> extends LinkedBinaryTree<Entry<K, V>>
		implements Dictionary<K, V> {

	protected Comparator<K> C;
	protected Position<Entry<K, V>> actionPos;
	protected int numEntries = 0;

	public BinarySearchTree() {
		C = new DefaultComparator<K>();
		addRoot(null);
	}

	public BinarySearchTree(Comparator<K> c) {
		C = c;
		addRoot(null);
	}

	public static class BSTEntry<K, V> implements Entry<K, V> {
		protected K key;
		protected V value;
		protected Position<Entry<K, V>> pos;

		BSTEntry() {
		}

		BSTEntry(K k, V v, Position<Entry<K, V>> p) {
			key = k;
			value = v;
			pos = p;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public Position<Entry<K, V>> position() {
			return pos;
		}

		// JH
		public void setPosition(Position<Entry<K, V>> _pos) {
			pos = _pos;
		}
	}

	protected K key(Position<Entry<K, V>> position) {
		return position.element().getKey();
	}

	protected V value(Position<Entry<K, V>> position) {
		return position.element().getValue();
	}

	protected Entry<K, V> entry(Position<Entry<K, V>> position) {
		return position.element();
	}

	protected void replaceEntry(Position<Entry<K, V>> pos, Entry<K, V> ent) {
		((BSTEntry<K, V>) ent).pos = pos;
		replace(pos, ent);
	}

	protected void checkKey(K key) throws InvalidKeyException {
		if (key == null)
			throw new InvalidKeyException("null key");
	}

	protected void checkEntry(Entry<K, V> ent) throws InvalidEntryException {
		if (ent == null || !(ent instanceof BSTEntry))
			throw new InvalidEntryException("invalid entry");
	}

	protected Entry<K, V> insertAtExternal(Position<Entry<K, V>> v,
			Entry<K, V> e) {
		expandExternal(v, null, null);
		replace(v, e);
		numEntries++;
		return e;
	}

	protected void removeExternal(Position<Entry<K, V>> v) {
		removeAboveExternal(v);
		numEntries--;
	}

	protected Position<Entry<K, V>> treeSearch(K key, Position<Entry<K, V>> pos) {
		if (isExternal(pos))
			return pos;
		else {
			K curKey = key(pos);
			int comp = C.compare(key, curKey);
			if (comp < 0)
				return treeSearch(key, left(pos));
			else if (comp > 0)
				return treeSearch(key, right(pos));
			return pos;
		}
	}

	protected void addAll(PositionList<Entry<K, V>> L, Position<Entry<K, V>> v,
			K k) {
		if (isExternal(v))
			return;
		Position<Entry<K, V>> pos = treeSearch(k, v);
		if (!isExternal(pos)) {
			addAll(L, left(pos), k);
			L.addLast(pos.element());
			addAll(L, right(pos), k);
		}
	}

	public int size() {
		return numEntries;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Entry<K, V> find(K key) throws InvalidKeyException {
		checkKey(key);
		Position<Entry<K, V>> curPos = treeSearch(key, root());
		actionPos = curPos;
		if (isInternal(curPos))
			return entry(curPos);
		return null;
	}

	public Iterable<Entry<K, V>> findAll(K key) throws InvalidKeyException {
		checkKey(key);
		PositionList<Entry<K, V>> L = new NodePositionList<Entry<K, V>>();
		addAll(L, root(), key);
		return L;
	}

	public Entry<K, V> insert(K k, V x) throws InvalidKeyException {
		checkKey(k);
		Position<Entry<K, V>> insPos = treeSearch(k, root());
		while (!isExternal(insPos))
			insPos = treeSearch(k, left(insPos));
		actionPos = insPos;
		return insertAtExternal(insPos, new BSTEntry<K, V>(k, x, insPos));
	}

	public Entry<K, V> remove(Entry<K, V> ent) throws InvalidEntryException {
		checkEntry(ent);
		Position<Entry<K, V>> remPos = ((BSTEntry<K, V>) ent).position();
		Entry<K, V> toReturn = entry(remPos);
		if (isExternal(left(remPos)))
			remPos = left(remPos);
		else if (isExternal(right(remPos)))
			remPos = right(remPos);
		else {
			Position<Entry<K, V>> swapPos = remPos;
			remPos = right(swapPos);
			do
				remPos = left(remPos);
			while (isInternal(remPos));
			replaceEntry(swapPos, (Entry<K, V>) parent(remPos).element());
		}
		actionPos = sibling(remPos);
		removeExternal(remPos);
		return toReturn;
	}

	public Iterable<Entry<K, V>> entries() {
		PositionList<Entry<K, V>> entries = new NodePositionList<Entry<K, V>>();
		Iterable<Position<Entry<K, V>>> positer = positions();
		for (Position<Entry<K, V>> cur : positer)
			if (isInternal(cur))
				entries.addLast(cur.element());
		return entries;
	}

}
