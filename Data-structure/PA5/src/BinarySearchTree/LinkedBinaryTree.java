package BinarySearchTree;
import java.util.Iterator;

import Exception.BoundaryViolationException;
import Exception.EmptyTreeException;
import Exception.InvalidPositionException;
import Exception.NonEmptyTreeException;

public class LinkedBinaryTree<E> implements BinaryTree<E> {
  protected BTPosition<E> root;	
  protected int size;		

  public LinkedBinaryTree() { 		    
    root = null;  
    size = 0;
  }

  public int size() {
    return size; 
  } 

  public boolean isEmpty() {
    return (size == 0); 
  } 

  public boolean isInternal(Position<E> v) throws InvalidPositionException {
    checkPosition(v);	
    return (hasLeft(v) || hasRight(v));
  }

  public boolean isExternal(Position<E> v) throws InvalidPositionException {
    return !isInternal(v);
  }

  public boolean isRoot(Position<E> v) throws InvalidPositionException { 
    checkPosition(v);
    return (v == root()); 
  }

  public boolean hasLeft(Position<E> v) throws InvalidPositionException { 
    BTPosition<E> vv = checkPosition(v);
    return (vv.getLeft() != null);
  }

  public boolean hasRight(Position<E> v) throws InvalidPositionException { 
    BTPosition<E> vv = checkPosition(v);
    return (vv.getRight() != null);
  }

  public Position<E> root() throws EmptyTreeException {
    if (root == null)
      throw new EmptyTreeException("The tree is empty");
    return root;
  } 

  public Position<E> left(Position<E> v) 
    throws InvalidPositionException, BoundaryViolationException { 
    BTPosition<E> vv = checkPosition(v);
    Position<E> leftPos = vv.getLeft();
    if (leftPos == null)
      throw new BoundaryViolationException("No left child");
    return leftPos;
  }

  public Position<E> right(Position<E> v) 
    throws InvalidPositionException, BoundaryViolationException { 
    BTPosition<E> vv = checkPosition(v);
    Position<E> rightPos = vv.getRight();
    if (rightPos == null)
      throw new BoundaryViolationException("No right child");
    return rightPos;
   }

  public Position<E> parent(Position<E> v) 
    throws InvalidPositionException, BoundaryViolationException { 
    BTPosition<E> vv = checkPosition(v);
    Position<E> parentPos = vv.getParent();
    if (parentPos == null)
      throw new BoundaryViolationException("No parent");
    return parentPos; 
  }

  public Iterable<Position<E>> children(Position<E> v) 
    throws InvalidPositionException { 
   PositionList<Position<E>> children = new NodePositionList<Position<E>>();
    if (hasLeft(v))
      children.addLast(left(v));
    if (hasRight(v))
      children.addLast(right(v));
    return children;
  }

  public Iterable<Position<E>> positions() {
   PositionList<Position<E>> positions = new NodePositionList<Position<E>>();
    if(size != 0)
      preorderPositions(root(), positions);
    return positions;
  } 

  public Iterator<E> iterator() {
    Iterable<Position<E>> positions = positions();
   PositionList<E> elements = new NodePositionList<E>();
   for (Position<E> pos: positions) 
     elements.addLast(pos.element());
    return elements.iterator();
  }

  public E replace(Position<E> v, E o) 
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    E temp = v.element();
    vv.setElement(o);
    return temp;
  }

  public Position<E> sibling(Position<E> v) 
    throws InvalidPositionException, BoundaryViolationException {
      BTPosition<E> vv = checkPosition(v);
      BTPosition<E> parentPos = vv.getParent();
      if (parentPos != null) {
	BTPosition<E> sibPos;
	BTPosition<E> leftPos = parentPos.getLeft();
	if (leftPos == vv)
	  sibPos = parentPos.getRight();
	else
	  sibPos = parentPos.getLeft();
	if (sibPos != null)
	  return sibPos;
      }
      throw new BoundaryViolationException("No sibling");
  }

  public Position<E> addRoot(E e) throws NonEmptyTreeException {
    if(!isEmpty())
      throw new NonEmptyTreeException("Tree already has a root");
    size = 1;
    root = createNode(e,null,null,null);
    return root;
  }

  public Position<E>  insertLeft(Position<E> v, E e)
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    Position<E> leftPos = vv.getLeft();
    if (leftPos != null)
      throw new InvalidPositionException("Node already has a left child");
    BTPosition<E> ww = createNode(e, vv, null, null);
    vv.setLeft(ww);
    size++;
    return ww;
  }

  public Position<E>  insertRight(Position<E> v, E e)
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    Position<E> rightPos = vv.getRight();
    if (rightPos != null)
      throw new InvalidPositionException("Node already has a right child");
    BTPosition<E> w = createNode(e, vv, null, null);
    vv.setRight(w);
    size++;
    return w;
  }

  public E remove(Position<E> v)
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    BTPosition<E> leftPos = vv.getLeft();
    BTPosition<E> rightPos = vv.getRight();
    if (leftPos != null && rightPos != null)
      throw new InvalidPositionException("Cannot remove node with two children");
    BTPosition<E> ww; 	
    if (leftPos != null)
      ww = leftPos;
    else if (rightPos != null)
      ww = rightPos;
    else 		
      ww = null;
    if (vv == root) { 
      if (ww != null)
	ww.setParent(null);
      root = ww;
    }
    else { 		
      BTPosition<E> uu = vv.getParent();
      if (vv == uu.getLeft())
	uu.setLeft(ww);
      else
	uu.setRight(ww);
      if(ww != null)
	ww.setParent(uu);
    }
    size--;
    return v.element();
  }

  public void attach(Position<E> v, BinaryTree<E> T1, BinaryTree<E> T2) 
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    if (isInternal(v))
      throw new InvalidPositionException("Cannot attach from internal node");
    int newSize = size + T1.size() + T2.size();
    if (!T1.isEmpty()) {
      BTPosition<E> r1 = checkPosition(T1.root());
      vv.setLeft(r1);
      r1.setParent(vv);		
    }
    if (!T2.isEmpty()) {
      BTPosition<E> r2 = checkPosition(T2.root());
      vv.setRight(r2);
      r2.setParent(vv);		
    }
    size = newSize;
  }

  public void swapElements(Position<E> v, Position<E> w)
    throws InvalidPositionException {
    BTPosition<E> vv = checkPosition(v);
    BTPosition<E> ww = checkPosition(w);
    E temp = w.element();
    ww.setElement(v.element());
    vv.setElement(temp);	
  }

  public void expandExternal(Position<E> v, E l, E r) 
    throws InvalidPositionException {
    if (!isExternal(v)) 
      throw new InvalidPositionException("Node is not external");
    insertLeft(v, l);
    insertRight(v, r);
  }

  public void removeAboveExternal(Position<E> v) 
    throws InvalidPositionException {
    if (!isExternal(v)) 
      throw new InvalidPositionException("Node is not external");
    if (isRoot(v))
      remove(v);
    else {
      Position<E> u = parent(v);
      remove(v);
      remove(u);
    }
  }

  protected BTPosition<E> checkPosition(Position<E> v) 
    throws InvalidPositionException {
    if (v == null || !(v instanceof BTPosition))
      throw new InvalidPositionException("The position is invalid");
    return (BTPosition<E>) v;
  }

  protected BTPosition<E> createNode(E element, BTPosition<E> parent, 
				  BTPosition<E> left, BTPosition<E> right) {
    return new BTNode<E>(element,parent,left,right); }

  protected void preorderPositions(Position<E> v, PositionList<Position<E>> pos) 
    throws InvalidPositionException {
    pos.addLast(v);
    if (hasLeft(v))
      preorderPositions(left(v), pos);	
    if (hasRight(v))
      preorderPositions(right(v), pos);	
  }

  protected void inorderPositions(Position<E> v, PositionList<Position<E>> pos) 
    throws InvalidPositionException {
    if (hasLeft(v))
      inorderPositions(left(v), pos);  
    pos.addLast(v);
    if (hasRight(v))
      inorderPositions(right(v), pos); 
  }
}
