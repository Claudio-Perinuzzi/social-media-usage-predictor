import java.io.Serializable;


public class DecisionNode implements Serializable{

	private static final long serialVersionUID = 2L;
	private DataContainer data;
	private DecisionNode left;
	private DecisionNode right;
	private int featureIndex;	//the nodes feature index that it was split on
	private String threshold;	//the nodes corresponding threshold used for the split


	//default empty node constructor
	public DecisionNode() {
		this.data = null;
		this.left = null;
		this.right = null;
		this.featureIndex = -1;
		this.threshold = null;
	}

	//if internal or leaf node; internal nodes will have a featureIndex and threshold set to them
	public DecisionNode(DataContainer data) {
		this.data = data;
		this.left = null;
		this.right = null;
		this.featureIndex = -1;
		this.threshold = null;
	}


	public DecisionNode getRight(){
        return this.right;
    }

    public DecisionNode getLeft(){
        return this.left;
    }

    public DataContainer getData() {
    	return this.data;
    }

    //returns the label of the given row, label is located at index 12
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