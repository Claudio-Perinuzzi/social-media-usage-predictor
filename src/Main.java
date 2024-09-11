
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
    //option for a serialized model
    //another option for not serializing the model
    //clean below
    //clean all files
    //images for readme, including sequence diagrams (create assets folder)

public class Main {

    public static void ensureSerializedModel(RandomForest forest) {
        String modelPath = "model/randomForestModel.ser";
        File file = new File(modelPath);

        if (file.exists()) {
            //load in
            
            // Deserialize the trained forest
            try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelPath)))) {
                forest = (RandomForest) ois.readObject();
                System.out.println("Random Forest model deserialized successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    
        }
        else {
            //train and save
            forest = new RandomForest();
            forest.train(100);

            //serialize the trained forest
            try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(modelPath)))) {
                oos.writeObject(forest);
                System.out.println("Random Forest model serialized successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }    
    }


    
    // IP ##########################################
    // Determine the outcome by running the user's input against the forest model and counting the predictions  
    public static void predict(RandomForest forest, DataContainer userInput) {
        int[] predictions = forest.aggregate(userInput); // Get predictions from the forest
        int yes = 0, no = 0; // Initialize label counts for voting
        
        // Count the yes and no labels
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == 1) yes++;
            else no++;
        }

        if (yes > no) {
            System.out.println("You are addictied");
            System.out.println("Confidence is " + yes);
        }
        else {
            System.out.println("You are not addicted");
            System.out.println("Yes " + yes);
            System.out.println("No " + no);
        }
   
    }


    // Train the model with 100 decision trees
    public static void trainNewModel(RandomForest forest) {
        forest = new RandomForest();
        forest.train(100);    
    }


    public static void main(String[] args)  {
        
        // // Get the first arg index flag on whether the user wants to use the serialized model 
        // boolean toSerialize = (args[0] == "serialized"); 

        // // Get the rest of the arguments
        // String input = String.join(",", Arrays.copyOfRange(args, 1, args.length)); 
       
        // // Create a DataContainer object of the user's input 
        // DataContainer userInput = new DataContainer(input); 

        // // Generate a null forest that is ready to be either trained or loaded into
        // RandomForest forest = null;

        // // Load the serialized model into the forest
        // if (toSerialize) {
        //     ensureSerializedModel(forest);        
        // }
        // // Or, train a new model into the null forest
        // else { 
        //     trainNewModel(forest);
        // }

        // //get the prediction using the forest and the user's input.
        // getPrediction(forest, userInput);

        //TESTING/////////////////////////////////////////////////////////////////
        String input = ",,2,,,,,,,,,,";
        DataContainer userInput = new DataContainer(input);
        userInput.print();
        RandomForest forest = new RandomForest(100);
        predict(forest, userInput);
    }
}
