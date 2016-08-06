package BinaryTree.Exceptions;

public class ENotFoundException extends RuntimeException
{
   public ENotFoundException (String collection)
   {
      super ("The target element is not in this " + collection);
   }
}

