import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.File;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * MAIN
 * Collects a command line argument used to either load in or train a new random forest model
 * which is then used to print back to the service whether the user is at risk of social
 * media addiction and what the corresponding confidence score is.
 * 
 * Deployment:
 *      javac --release 17 src/*.java
 *      jar cfm dist/predict.jar META-INF/MANIFEST.MF -C bin .
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Main {
    public static void main(String[] args)  {
        
        // Get the arg index 0 flag on whether the user wants to use the serialized model 
        boolean toSerialize = (args[0].equals("True")); 

        // Get the rest of the users input arguments
        String input = String.join(",", Arrays.copyOfRange(args, 3, args.length)); 

        // Create a DataContainer object of the user's input 
        DataContainer userInput = new DataContainer(input); 

        // Load the serialized model into the forest and predict
        if (toSerialize) {
            RandomForest forest = ensureSerializedModel();     
            predict(forest, userInput);
        }
        // Or, train a new model with 100 trees and predict 
        else { 
            RandomForest forest = trainNewModel(100);
            predict(forest, userInput);
        }

    }

    // Determine the outcome by running the user's input against the forest model and counting the predictions  
    public static void predict(RandomForest forest, DataContainer userInput) {
        
        // Get predictions from the forest and initialize label counts for voting
        int[] predictions = forest.aggregate(userInput); 
        int yes = 0, no = 0; 
        
        // Count the yes and no labels
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == 1) yes++;
            else no++;
        }
        
        // Calculate probability of addiction
        double probability = (double) yes / (yes + no);
        
        // Print whether the user may be addicted to social media and offer suggestions
        if (yes > no) {
            System.out.println("You may be at risk of social media addiction!");
            System.out.println("Probability of addiction: " + String.format("%.0f", probability * 100) + "%");
        } else {
            System.out.println("Your social media usage is within a healthy range!");
            System.out.println("Probability of addiction: " + String.format("%.0f", probability * 100) + "%");
        }
    }


    // Train the model with n decision trees
    public static RandomForest trainNewModel(int numTrees) {
        RandomForest forest = new RandomForest();
        forest.train(numTrees);   
        return forest; 
    }


    // Ensure that the serialized model exists. If not, train a new model and save it
    public static RandomForest ensureSerializedModel() {
        
        // Path the model exists and a create new file object at that path
        String modelPath = "model/randomForestModel.ser";
        File file = new File(modelPath);

        
        if (file.exists()) { // Load in the model if it exists
            // Deserialize the trained forest and read it into the forest object
            try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelPath)))) {
                RandomForest forest = (RandomForest) ois.readObject();
                System.out.println("Random Forest model deserialized successfully.");
                return forest;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        else { // Train and save a new model if it does not exist in the file path
            RandomForest forest = trainNewModel(100);
            //serialize the trained forest and save it to the file path
            try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(modelPath)))) {
                oos.writeObject(forest);
                System.out.println("Random Forest model serialized successfully.");
                return forest;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }   
        
    }


}
