package BinarySearchTree;
import java.util.Iterator;

import Exception.BoundaryViolationException;
import Exception.EmptyListException;
import Exception.InvalidPositionException;

public class NodePositionList<E> implements PositionList<E> {

  protected int numElts;           
  protected DNode<E> header, trailer;	

  public NodePositionList() {
    numElts = 0;
    header = new DNode<E>(null, null, null);	
    trailer = new DNode<E>(header, null, null);
    header.setNext(trailer);	
  }

  protected DNode<E> checkPosition(Position<E> p)
    throws InvalidPositionException {
    if (p == null)
      throw new InvalidPositionException
	("Null position passed to NodeList");
    if (p == header)
	throw new InvalidPositionException
	  ("The header node is not a valid position");
    if (p == trailer)
	throw new InvalidPositionException
	  ("The trailer node is not a valid position");
    try {
      DNode<E> temp = (DNode<E>) p;
      if ((temp.getPrev() == null) || (temp.getNext() == null))
	throw new InvalidPositionException
	  ("Position does not belong to a valid NodeList");
      return temp;
    } catch (ClassCastException e) {
      throw new InvalidPositionException
	("Position is of wrong type for this list");
    }
  }

  public int size() { return numElts; }

  public boolean isEmpty() { return (numElts == 0); }

  public Position<E> first()
      throws EmptyListException {
    if (isEmpty())
      throw new EmptyListException("List is empty");
    return header.getNext();
  }

  public Position<E> last()
      throws EmptyListException {
    if (isEmpty())
      throw new EmptyListException("List is empty");
    return trailer.getPrev();
  }

  public Position<E> prev(Position<E> p)
      throws InvalidPositionException, BoundaryViolationException {
    DNode<E> v = checkPosition(p);
    DNode<E> prev = v.getPrev();
    if (prev == header)
      throw new BoundaryViolationException
	("Cannot advance past the beginning of the list");
    return prev;
  }

  public Position<E> next(Position<E> p)
      throws InvalidPositionException, BoundaryViolationException {
    DNode<E> v = checkPosition(p);
    DNode<E> next = v.getNext();
    if (next == trailer)
      throw new BoundaryViolationException
	("Cannot advance past the end of the list");
    return next;
  }

  public void addBefore(Position<E> p, E element) 
      throws InvalidPositionException {
    DNode<E> v = checkPosition(p);
    numElts++;
    DNode<E> newNode = new DNode<E>(v.getPrev(), v, element);
    v.getPrev().setNext(newNode);
    v.setPrev(newNode);
  }

  public void addAfter(Position<E> p, E element) 
      throws InvalidPositionException {
    DNode<E> v = checkPosition(p);
    numElts++;
    DNode<E> newNode = new DNode<E>(v, v.getNext(), element);
    v.getNext().setPrev(newNode);
    v.setNext(newNode);
  }

  public void addFirst(E element) {
    numElts++;
    DNode<E> newNode = new DNode<E>(header, header.getNext(), element);
    header.getNext().setPrev(newNode);
    header.setNext(newNode);
  }

  public void addLast(E element) {
    numElts++;
    DNode<E> oldLast = trailer.getPrev();
    DNode<E> newNode = new DNode<E>(oldLast, trailer, element);
    oldLast.setNext(newNode);
    trailer.setPrev(newNode);
  }

  public E remove(Position<E> p)
      throws InvalidPositionException {
    DNode<E> v = checkPosition(p);
    numElts--;
    DNode<E> vPrev = v.getPrev();
    DNode<E> vNext = v.getNext();
    vPrev.setNext(vNext);
    vNext.setPrev(vPrev);
    E vElem = v.element();

    v.setNext(null);
    v.setPrev(null);
    return vElem;
  }

  public E set(Position<E> p, E element)
      throws InvalidPositionException {
    DNode<E> v = checkPosition(p);
    E oldElt = v.element();
    v.setElement(element);
    return oldElt;
  }

  public Iterator<E> iterator() { return new ElementIterator<E>(this); }

  public Iterable<Position<E>> positions() { 
    PositionList<Position<E>> P = new NodePositionList<Position<E>>();
    if (!isEmpty()) {
      Position<E> p = first();
      while (true) {
	P.addLast(p); 
	if (p == last())
	  break;
	p = next(p);
      }
    }
    return P;
  }

  public boolean isFirst(Position<E> p)
    throws InvalidPositionException {  
    DNode<E> v = checkPosition(p);
    return v.getPrev() == header;
  }

  public boolean isLast(Position<E> p)
      throws InvalidPositionException {  
    DNode<E> v = checkPosition(p);
    return v.getNext() == trailer;
  }

  public void swapElements(Position<E> a, Position<E> b) 
      throws InvalidPositionException {
    DNode<E> pA = checkPosition(a);
    DNode<E> pB = checkPosition(b);
    E temp = pA.element();
    pA.setElement(pB.element());
    pB.setElement(temp);
  }

  public static <E> String forEachToString(PositionList<E> L) {
    String s = "[";
    int i = L.size();
    for (E elem: L) {
      s += elem; 
      i--;
      if (i > 0)
	s += ", "; 
    }
    s += "]";
    return s;
  }

  public static <E> String toString(PositionList<E> l) {
    Iterator<E> it = l.iterator();
    String s = "[";
    while (it.hasNext()) {
      s += it.next();
      if (it.hasNext())
	s += ", ";
      }
    s += "]";
    return s;
  }

  public String toString() {
    return toString(this);
  }
}
