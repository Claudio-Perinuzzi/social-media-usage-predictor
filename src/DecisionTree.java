import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
import java.lang.Math;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Decision Tree Class
 * Creates Tree objects used for the Random Forest Class. Each tree object will predict
 * whether a new user's profile is at risk of social media addiction.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class DecisionTree implements Serializable{
	
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * FIELDS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final long serialVersionUID = 3L; // ID used for serializing the model
	private DecisionNode root;


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Default constructor for an empty tree
	public DecisionTree() {
        this.root = null;
	}


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Gets the roots left child
    public DecisionNode getLeft() {
        return this.root.getLeft();
    }

    // Gets the roots right child 
    public DecisionNode getRight() {
        return this.root.getRight();
    }

    public DecisionNode getRoot() {
        return this.root;
    }


	// Build the tree starting from the root
    public void buildTree(int numOfFeatures) {
        DataContainer bootstrappedData = new DataContainer(1000, 13); // Bootstrap first
        this.root = new DecisionNode(bootstrappedData);
        buildTreeRecursively(this.root, bootstrappedData, numOfFeatures);
    }

    // Helper function to recursively build the tree from the root
    private DecisionNode buildTreeRecursively(DecisionNode parentNode, DataContainer data, int numOfFeatures) {
    	
        if (data.isPure()) return new DecisionNode(data);   // Base case: return a leaf node with data, since this data is pure
        int[] randomColumns = featureSelect(numOfFeatures); // Collect random features into an array, about 3
        
        // Find the best feature to split on (calculate the max info gained)
	    double maxInfoGain = Double.NEGATIVE_INFINITY; 
	    int bestFeatureIndexToSplit = -1; 
        String bestThreshold = null;
        for (int featureIndex : randomColumns) {
            double infoGain;
            String threshold;

            // Numeric values (ex: age)
            if (featureIndex == 0 || featureIndex == 2 || featureIndex == 8) { 
                int average = averageThreshold(data, featureIndex);
                infoGain = calculateInfoGainFromNumericData(data, featureIndex, average);
                threshold = String.valueOf(average);
            }  
            // Categorical values (ex: gender)              
            else { 
                String mode = modeThreshold(data, featureIndex);
                infoGain = calculateInfoGainFromCategoricalData(data, featureIndex, mode);
                threshold = mode;
            }

            if (infoGain > maxInfoGain) {
	            maxInfoGain = infoGain;
	            bestFeatureIndexToSplit = featureIndex;
                bestThreshold = threshold;
	        }
	    }


        // Set the parents nodes feature index that we will split on and the corresponding threshold
        parentNode.setFeatureIndex(bestFeatureIndexToSplit);
        parentNode.setThreshold(bestThreshold);

        // Split data first
        DataContainer leftData = data.split(true, bestFeatureIndexToSplit, bestThreshold);
        DataContainer rightData = data.split(false, bestFeatureIndexToSplit, bestThreshold);

        // Deal with splits that are potentially uneven where data on either side can be empty
        if (!leftData.isEmpty()) {
            DecisionNode leftChild = new DecisionNode(leftData);
            parentNode.setLeft(leftChild);
            buildTreeRecursively(leftChild, leftData, numOfFeatures);
        }

        if (!rightData.isEmpty()) {
            DecisionNode rightChild = new DecisionNode(rightData);
            parentNode.setRight(rightChild);
            buildTreeRecursively(rightChild, rightData, numOfFeatures);
        }

        return parentNode; 
    }

    // Calculate the average from numeric data for the threshold value
    private int averageThreshold(DataContainer data, int featureIndex) {
        int sum = 0;
        for (int r = 0; r < data.getRows(); r++) {
            sum += Integer.parseInt(data.getValue(r, featureIndex));
        }
        return sum / data.getRows();
    }

    
    // Calculate the mode from categorical data for the threshold value
    private String modeThreshold(DataContainer data, int featureIndex) {
        HashMap<String, Integer> count = new HashMap<>();
        for (int r = 0; r < data.getRows(); r++) {
            String value = data.getValue(r, featureIndex);
            if (!count.containsKey(value)) count.put(value, 1);   // Put in if value doesn't exist yet
            else count.put(value, count.get(value) + 1);                // Increment count if value exists already
        }
        
        // Iterate through and count to find mode
        String modeThreshold = null;
        int max = 0;
        for (String value : count.keySet()) {
            if (count.get(value) > max) {
                max = count.get(value);
                modeThreshold = value; 
            } 
        }

        return modeThreshold;
    }


    // Uses average of that column to determine the information gain of this potential split
    private Double calculateInfoGainFromNumericData(DataContainer data, int featureIndex, int averageThreshold) {
        
        int leftCountLabel0 = 0, leftCountLabel1 = 0;
        int rightCountLabel0 = 0, rightCountLabel1 = 0;
       
        // Use average Threshold to count the labels for a potential split
        for (int r = 0; r < data.getRows(); r++) {
            if (Integer.parseInt(data.getValue(r, featureIndex)) <= averageThreshold) {
                if (data.getLabel(r).equals("0")) leftCountLabel0++;
                else if (data.getLabel(r).equals("1")) leftCountLabel1++;
            }
            else {
                if (data.getLabel(r).equals("0")) rightCountLabel0++;
                else if (data.getLabel(r).equals("1")) rightCountLabel1++;
            }
        }

        // Calculate giniImpurities
        double giniImpurityParent = calculateGiniImpurity(data.getLabelCount(0), data.getLabelCount(1));
        double giniImpurityLeft, giniImpurityRight;

        // If the left side has no labels, there was a full split and the gini impurity is 0
        if (leftCountLabel0 == 0 || leftCountLabel1 == 0) giniImpurityLeft = 0.0;
        else giniImpurityLeft = calculateGiniImpurity(leftCountLabel0, leftCountLabel1);
        
        // If the right side has no labels, there was a full split and the gini impurity is 0
        if (rightCountLabel0 == 0 || rightCountLabel1 == 0) giniImpurityRight = 0.0;
        else giniImpurityRight = calculateGiniImpurity(rightCountLabel0, rightCountLabel1);


        // Calculate IG based on giniImpurities
        int totalSamples = leftCountLabel0 + leftCountLabel1 + rightCountLabel0 + rightCountLabel1;
        double informationGain = giniImpurityParent - ((double) (leftCountLabel0 + leftCountLabel1) / totalSamples * giniImpurityLeft 
                                 + (double) (rightCountLabel0 + rightCountLabel1) / totalSamples * giniImpurityRight);

        return informationGain;
    }

    // Uses the mode of that column to determine the information gain of this potential split
    private Double calculateInfoGainFromCategoricalData(DataContainer data, int featureIndex, String modeThreshold) {
        
        int leftCountLabel0 = 0, leftCountLabel1 = 0;
        int rightCountLabel0 = 0, rightCountLabel1 = 0;

        // Use mode to count the labels for a potential split
        for (int r = 0; r < data.getRows(); r++) {
            if (data.getValue(r, featureIndex).equals(modeThreshold)) {
                if (data.getLabel(r).equals("0")) leftCountLabel0++;
                else if (data.getLabel(r).equals("1")) leftCountLabel1++;
            }
            else {
                if (data.getLabel(r).equals("0")) rightCountLabel0++;
                else if (data.getLabel(r).equals("1")) rightCountLabel1++;
            }
        }

        // Calculate giniImpurities
        double giniImpurityParent = calculateGiniImpurity(data.getLabelCount(0), data.getLabelCount(1));
        double giniImpurityLeft, giniImpurityRight;

        // If the left side has no labels, there was a full split and the gini impurity is 0
        if (leftCountLabel0 == 0 || leftCountLabel1 == 0) giniImpurityLeft = 0.0;
        else giniImpurityLeft = calculateGiniImpurity(leftCountLabel0, leftCountLabel1);
        
        // If the right side has no labels, there was a full split and the gini impurity is 0
        if (rightCountLabel0 == 0 || rightCountLabel1 == 0) giniImpurityRight = 0.0;
        else giniImpurityRight = calculateGiniImpurity(rightCountLabel0, rightCountLabel1);
    
        // Calculate IG based on giniImpurities
        int totalSamples = leftCountLabel0 + leftCountLabel1 + rightCountLabel0 + rightCountLabel1;
        double informationGain = giniImpurityParent - ((double) (leftCountLabel0 + leftCountLabel1) / totalSamples * giniImpurityLeft 
                                 + (double) (rightCountLabel0 + rightCountLabel1) / totalSamples * giniImpurityRight);

        return informationGain;
    }

    // Helper function used to calculate information gain
    private double calculateGiniImpurity(int countLabel0, int countLabel1) {
        // Total number of labels
        int totalSamples = countLabel0 + countLabel1; 

        // Probabilities of each label
        double probability0 = (double) countLabel0 / totalSamples;
        double probability1 = (double) countLabel1 / totalSamples;

        // GiniImpurity formula
        double giniImpurity = 1.0 - (Math.pow(probability0, 2) + Math.pow(probability1, 2));

        return giniImpurity;
    }

    // Randomly select different columns of the bootstrapped data
    private int[] featureSelect(int numOfFeatures) {
    	Random rand = new Random();
    	int[] randomColumns = new int[numOfFeatures];
    	for (int i = 0; i < numOfFeatures; i++) {
    		int randCol;
    		boolean isUnique;
    		do {
                randCol = rand.nextInt(12);
                isUnique = true;
                for (int j = 0; j < numOfFeatures; j++) {
                    if (randomColumns[j] == randCol) {
                        isUnique = false;
                        break;
                    }
                }
            } while (!isUnique);
            randomColumns[i] = randCol;
    	}
    	return randomColumns;
    }

    // Prints the roots data
    public void print() {
        this.root.print();
    }

}