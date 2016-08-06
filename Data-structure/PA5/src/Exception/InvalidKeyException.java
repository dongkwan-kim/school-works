package Exception;

public class InvalidKeyException  extends RuntimeException {
  public InvalidKeyException (String message) {
    super (message);
  }
  public static final long serialVersionUID = 424242L;
}
