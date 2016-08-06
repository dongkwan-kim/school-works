package BinarySearchTree;
import java.util.Comparator;
import java.io.Serializable;

public class DefaultComparator<E> implements Comparator<E> {

  public int compare(E a, E b) throws ClassCastException { 
    return ((Comparable<E>) a).compareTo(b);
  }
}

