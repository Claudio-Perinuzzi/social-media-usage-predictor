
import java.io.Serializable;


public class RandomForest implements Serializable {
	
	private static final long serialVersionUID = 4L;
	private DecisionTree[] trees;

	//constructor for creating an empty forest that can be trained
	public RandomForest() {
		trees = null;
	}

	//constructor for training the forest automatically
	public RandomForest(int numOfTrees) {
		train(numOfTrees);
	}

	//function to train the forest by creating n number of trees
	public void train(int numOfTrees) {
		trees = new DecisionTree[numOfTrees];
		for (int i = 0; i < numOfTrees; i++) {
        	DecisionTree newTree = new DecisionTree();
        	newTree.buildTree(4); 	//takes in the number of features for feature selection
			trees[i] = newTree;
		}
		//return trees;
	}

	//for testing purposes
	public void print() {
		for (int i = 0; i < trees.length; i++) {
			System.out.println(trees[i]);
		}
	}

	//aggregates all predictions into an array
	public int[] aggregate(DataContainer userInput) {
		int[] predictions = new int[trees.length];
		for (int i = 0; i < trees.length; i++) {
			DecisionTree tree = trees[i]; 								//get current tree to work on
			predictions[i] = getPrediction(tree.getRoot(), userInput); 	//add the tree's prediction to a prediction array
		}
		return predictions; 
	}


	//returns the prediction for the prediction array
	private int getPrediction(DecisionNode currNode, DataContainer userInput) {
		
		//if the current node is a leaf, then return the label (prediction)
		if (currNode.isLeaf()) return Integer.parseInt(currNode.getData().getLabel(0));  

		//get the current feature index of each node you visit
		int currFeatureIndex = currNode.getFeatureIndex(); 	

		//if a continous value
        if (currFeatureIndex == 0 || currFeatureIndex == 2 || currFeatureIndex == 8) { 
        	int userValue = Integer.parseInt(userInput.getValue(0, currFeatureIndex));
        	int threshold = Integer.parseInt(currNode.getThreshold());
        	if (userValue <= threshold && currNode.getLeft() != null) return getPrediction(currNode.getLeft(), userInput); 	//traverse left if user value is <= to threshold
        	else if (currNode.getRight() != null) return getPrediction(currNode.getRight(), userInput); 					//else traverse right
        }  
        //categorical values (ex: gender)              
        else { 
        	String userValue = userInput.getValue(0, currFeatureIndex);
        	String threshold = currNode.getThreshold();
        	if (userValue.equals(threshold) && currNode.getLeft() != null) return getPrediction(currNode.getLeft(), userInput);	//travere left if user value is the same threshold
        	else if (currNode.getRight() != null) return getPrediction(currNode.getRight(), userInput);							//else traverse right

        }

        /* if none of the conditions pass above, then calculate mode for current node for prediction instead. The data here is pretty small.
        This instance is pretty rare (maybe ~1/8 runs we come across 1 or 2 trees out of 100 that didnt get to a leaf node). 
        For example, the users value and threshold arent the same but the right child is null, then it is possible that we have no where else to traverse to 
		
		Here is is an example output
			the threshold is False
			the users input is true in this case
			I DID NOT PASS ANY CONDTIONS ABOVE ==============
			45 male 2 Instagram Sports United Kingdom Rural Software Engineer 13022 False False True 0 
			45 male 4 Instagram Sports United Kingdom Rural Student 14550 False False True 1 
			45 male 2 Instagram Sports United Kingdom Rural Software Engineer 13022 False False True 0 
			45 male 2 Instagram Sports United Kingdom Rural Software Engineer 13022 False False True 0 
			45 male 4 Instagram Sports United Kingdom Rural Student 14550 False False True 1 
			my label is0
			my count lable 0 is3
			my count lable 1 is2
			my right child is null
			my left child is DecisionNode@70dea4e
			my current feature is 9

		Here is the debugging code

	        // System.out.println("I DID NOT PASS ANY CONDTIONS ABOVE ==============");
	        // currNode.getData().print();
	        // System.out.println("my label is" + currNode.getData().getLabel(0));
	        // System.out.println("my count lable 0 is" + currNode.getData().getLabelCount(0));
	        // System.out.println("my count lable 1 is" + currNode.getData().getLabelCount(1));
	        // System.out.println("my right child is "+ currNode.getRight());
	        // System.out.println("my left child is "+ currNode.getLeft());
	        // System.out.println("my current feature is " + currFeatureIndex);
        */

        //calculate the mode for the prediction, traversing to a leaf node is not possible if one of the childrens is null and the threshold is not matched
        int labelCount0 = currNode.getData().getLabelCount(0);
        int labelCount1 = currNode.getData().getLabelCount(1);

        if (labelCount0 > labelCount1) return 0;
        return 1;

	}


}