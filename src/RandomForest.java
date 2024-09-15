import java.io.Serializable;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Random Forest Class
 * A array of Decision Tree objects. The user's input is ran through each Decision Tree for a
 * prediction. Each prediction is tallied up as a confidence level as the model's final prediction
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class RandomForest implements Serializable {
	
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * FIELDS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private static final long serialVersionUID = 4L; // ID used for serializing the model
	private DecisionTree[] trees;


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	// Constructor for creating an empty forest that can be trained
	public RandomForest() {
		trees = null;
	}

	// Constructor for training the forest automatically
	public RandomForest(int numOfTrees) {
		train(numOfTrees);
	}


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	// Function to train the forest by creating n number of trees
	public void train(int numOfTrees) {
		trees = new DecisionTree[numOfTrees];
		for (int i = 0; i < numOfTrees; i++) {
        	DecisionTree newTree = new DecisionTree();
        	newTree.buildTree(4);  // Takes in the number of features for feature selection
			trees[i] = newTree;
		}
	}

	// Aggregates all predictions into an array
	public int[] aggregate(DataContainer userInput) {
		int[] predictions = new int[trees.length];
		for (int i = 0; i < trees.length; i++) {
			DecisionTree tree = trees[i]; 								//get current tree to work on
			predictions[i] = getPrediction(tree.getRoot(), userInput); 	//add the tree's prediction to a prediction array
		}
		return predictions; 
	}

	// Returns the prediction for the prediction array
	private int getPrediction(DecisionNode currNode, DataContainer userInput) {
		
		// If the current node is a leaf, then return the label (prediction)
		if (currNode.isLeaf()) return Integer.parseInt(currNode.getData().getLabel(0));  

		// Get the current feature index of each node you visit
		int currFeatureIndex = currNode.getFeatureIndex(); 	

		// If a continuous value
        if (currFeatureIndex == 0 || currFeatureIndex == 2 || currFeatureIndex == 8) { 
        	int userValue = Integer.parseInt(userInput.getValue(0, currFeatureIndex));
        	int threshold = Integer.parseInt(currNode.getThreshold());
        	if (userValue <= threshold && currNode.getLeft() != null) {
				return getPrediction(currNode.getLeft(), userInput); // Traverse left if user value is <= to threshold
			} 	
        	else if (currNode.getRight() != null) {
				return getPrediction(currNode.getRight(), userInput); // Else traverse right
			}

        }  
        //categorical values (ex: gender)              
        else { 
        	String userValue = userInput.getValue(0, currFeatureIndex);
        	String threshold = currNode.getThreshold();
        	if (userValue.equals(threshold) && currNode.getLeft() != null) {
				return getPrediction(currNode.getLeft(), userInput); // Travere left if user value is the same threshold
			}
        	else if (currNode.getRight() != null) { // Else traverse right
				return getPrediction(currNode.getRight(), userInput);
			}							

        }

        /* If none of the conditions pass above, then calculate mode for current node for prediction instead. 
		The data here is pretty small. This instance is pretty rare (maybe ~1/8 runs we come across 1 or 2 trees 
		out of 100 that didnt get to a leaf node). For example, the users value and threshold aren't the same 
		but the right child is null, then it is possible that we have no where else to traverse to */

        // Calculate the mode for the prediction 
		// Traversing to a leaf node is not possible if one of the childrens is null and the threshold is not matched
        int labelCount0 = currNode.getData().getLabelCount(0);
        int labelCount1 = currNode.getData().getLabelCount(1);

        if (labelCount0 > labelCount1) return 0;
        return 1;

	}

	
}