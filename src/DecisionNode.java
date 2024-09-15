import java.io.Serializable;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Decision Node Class
 * Creates node objects used for the Decision Tree class. 
 * 
 * Each node will have: 
 * 		- A self contained data container object 
 * 		- A left and right child reference
 * 		- A feature index used for splitting
 * 		- A threshold value used for the split
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class DecisionNode implements Serializable{

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * FIELDS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private static final long serialVersionUID = 2L; // ID used for serializing the model
	private DataContainer data;
	private DecisionNode left;
	private DecisionNode right;
	private int featureIndex;	// The nodes best feature index that it was split on
	private String threshold;	// The nodes corresponding threshold used for the split

    
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * CONSTRUCTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	// Default empty node constructor
	public DecisionNode() {
		this.data = null;
		this.left = null;
		this.right = null;
		this.featureIndex = -1;
		this.threshold = null;
	}
	// Data contained constructor (NOTE: Internal nodes will have a featureIndex and threshold set to them)
	public DecisionNode(DataContainer data) {
		this.data = data;
		this.left = null;
		this.right = null;
		this.featureIndex = -1;
		this.threshold = null;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	public DecisionNode getRight(){
        return this.right;
    }

    public DecisionNode getLeft(){
        return this.left;
    }

    public DataContainer getData() {
    	return this.data;
    }

    // Returns the label of the given row, label is located at index 12
    public String getLabel(int row) {   
    	return this.data.getValue(row, 12);
    }

    public String getThreshold() {
    	return this.threshold;
    }

    public int getFeatureIndex() {
    	return this.featureIndex;
    }

    public void setRight(DecisionNode newRight){
        this.right = newRight;
    }

    public void setLeft(DecisionNode newLeft){
        this.left = newLeft;
    }

    public void setData(DataContainer newData) {
    	this.data = newData;
    }

    public void setFeatureIndex(int index) {
    	this.featureIndex = index;
    }

    public void setThreshold(String threshold) {
    	this.threshold = threshold;
    }

    public boolean isLeaf() {
    	return this.right == null && this.left == null && this.data.isPure();
    }

    public void print() {
    	this.data.print();
    }

}