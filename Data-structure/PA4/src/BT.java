// Do not modify this code.

public class BT {

	private BT leftChild;
	private BT rightChild;
	private int priority;
	private String value;
	
	public BT(){
		this.leftChild = null;
		this.rightChild = null;
		this.priority = 0;
		this.value = null;
	}
	
	public BT(int priority, String name){
		this();
		this.priority = priority;
		this.value = name;
	}
	
	public BT getLeftChild(){
		return leftChild;
	}
	
	public BT getRightChild(){
		return rightChild;
	}

    public void setLeftChild(BT child){
        this.leftChild = child;
    }
    public void setRightChild(BT child){
        this.rightChild = child;
    }
    
    public int getPriority() {
    	return this.priority;
    }
    public void setPriority(int priority) {
    	this.priority = priority;
    }
    
    public String getValue() {
    	return this.value;
    }
    public void setValue(String value) {
    	this.value = value;
    }
}