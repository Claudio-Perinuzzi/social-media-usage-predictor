import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.Arrays;


//WHEN READY TO DEPLOY
    // javac --release 17 src/*.java
    // jar cfm dist/predict.jar META-INF/MANIFEST.MF -C bin .

//NEXT STEPS:
    //offer helpful suggestions
    //option for a serialized model 
    //another option for not serializing the model
    //clean all files
    //images for readme and finalize readme

public class Main {
    public static void main(String[] args)  {
        
        // Get the arg index 0 flag on whether the user wants to use the serialized model 
        boolean toSerialize = (args[0].equals("True")); 
        String location = args[1];
        String hobbies = args[2];

        // Get the rest of the arguments
        String input = String.join(",", Arrays.copyOfRange(args, 3, args.length)); 

        // Create a DataContainer object of the user's input 
        DataContainer userInput = new DataContainer(input); 

                    System.out.print("to serialize = ");
                    System.out.println(toSerialize);
                    System.out.println("my location is " + location);
                    System.out.println("my hobbies are " + hobbies);
                    System.out.println("my input is " + input);
                    System.out.print("my data container input is: ");
                    userInput.print();

                    


        // Generate a null forest that is ready to be either trained or loaded into
        // RandomForest forest = null;

        // Load the serialized model into the forest
        if (toSerialize) {
            RandomForest forest = ensureSerializedModel();     
            predict(forest, userInput);
        }
        // Or, train a new model into the null forest
        else { 
            RandomForest forest = trainNewModel(100);
            predict(forest, userInput);
        }
        
        //get the prediction using the forest and the user's input.
        // predict(forest, userInput);

        //TESTING/////////////////////////////////////////////////////////////////
        // String input = ",,2,,,,,,,,,,";
        // DataContainer userInput = new DataContainer(input);
        // // userInput.print();
        // RandomForest forest = new RandomForest(100);
        // predict(forest, userInput);
        //TESTING/////////////////////////////////////////////////////////////////
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
        
        // Calculate confidence level for each label
        double yesConfidence = (double) yes / (yes + no);
        double noConfidence = (double) no / (yes + no);
        
        // Print whether the user may be addicted to social media and offer suggestions
        if (yes > no) {
            System.out.println("You may be at risk of social media addiction!");
            System.out.println("Confidence Level: " + String.format("%.2f", yesConfidence * 100) + "%");
        } else {
            System.out.println("Your social media usage is within a healthy range!");
            System.out.println("Confidence Level: " + String.format("%.2f", noConfidence * 100) + "%");
        }
    }


    // Train the model with 100 decision trees
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
