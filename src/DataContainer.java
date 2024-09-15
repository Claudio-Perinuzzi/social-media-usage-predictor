import java.io.BufferedReader;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Random;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Data Container Class 
 * Reads in and loads the dataset into a 2D String called data. The dataset is static 
 * initialized to only load in once. The rest of the class contains the  methods for 
 * manipulating and handling the data in the 2D String object.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


public class DataContainer implements Serializable{

    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * FIELDS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final long serialVersionUID = 1L; // ID used for serializing the model
    private static String[][] dataset;               // Static initialized, shared by all instances of the class
    static {
        try {
            dataset = loadDataset("/Users/claudioperinuzzi/Desktop/Projects/Java/social-media-usage-predictor/data/user_social_media_profiles.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String[][] data; //userInput data or bootstrapped data, for ex) userData = data[0][12] 
    private int rows;
    private int columns;
    private int countLabel0; //count of labels for no is not addicted
    private int countLabel1; //count of labels for yes is addicted


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * CONSTRUCTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Dataset constructor
    public DataContainer() {
        this.data = dataset;
        this.rows = 1000;
        this.columns = 13;                                 // 13, the last column will contain the label, either 1 or 0
        this.countLabel0 = countLabels(data, 0);     // Count the 0's for this data
        this.countLabel1 = countLabels(data, 1);     // Count the 1's for this data
    }

    // User input constructor 
    public DataContainer(String userInput)  {
        this.rows = 1;
        this.columns = 12;
        this.data = new String[rows][columns];
        this.countLabel0 = 0;                   // No labels for user input
        this.countLabel1 = 0;                   // No labels for user input
        this.readUserInput(userInput);          // Read the user's input
    }

    // Bootstrapping constructor 
    public DataContainer(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new String[rows][columns];
        this.bootStrap(columns);
        this.countLabel0 = countLabels(data, 0);     // Count the 0's for this data
        this.countLabel1 = countLabels(data, 1);     // Count the 1's for this data  
    }

    // Splitting Constructor (used for splitting the data in the  function) 
    public DataContainer(int rows, int columns, boolean split) {
        this.rows = rows;
        this.columns = columns;
        this.data = new String[rows][columns];
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    * METHODS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    public String getValue(int r, int c) {
        return this.data[r][c];
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    // Returns a 1 or 0 label of that given row
    public String getLabel(int row) {
        return this.getValue(row, 12);
    }
    
    // Gets the current row as a string array
    public String[] getRow(int rowIndex) {
        return data[rowIndex];
    }

    // Returns true if either label count is 0 meaning the data is pure
    public boolean isPure() {
        return (this.countLabel0 == 0 ) || (this.countLabel1 == 0);
    }

    // Checks if the data is empty
    public boolean isEmpty() {
        return rows == 0 && columns == 0;
    }

    // Helper for setting the number of 0 labels
    private void SetCountLabel0() {
        this.countLabel0 = countLabels(this.data, 0);
    }

    // Helper for setting the number of 1 labels
    private void SetCountLabel1() {
        this.countLabel1 = countLabels(this.data, 1);
    }

    // Returns the Count of 1's or 0's for the data object
    public int getLabelCount(int label) {
        if (label == 0) return this.countLabel0;
        else if (label == 1) return this.countLabel1;
        else {
            System.out.println("INVALID LABEL");
            return -1;
        }
    }
    
    // Helper for counting the number of 1 or 0 labels in the passed in data
    private int countLabels(String[][] data, int label) {
        int count0 = 0;
        int count1 = 0;
        int labelIndex = columns - 1;
        
        for (int r = 0; r < rows; r++) {
            if (data[r][labelIndex].equals("0")) count0++;
            else if (data[r][labelIndex].equals("1")) count1++;
        }

        if (label == 0) return count0;
        else if (label == 1) return count1;
        else return -1;
    }

    // Helper for loading the actual dataset. Also assigns a 1 or 0 label for every user profile in the dataset
    private static String[][] loadDataset(String source) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            br.readLine(); // Consume header to skip so we don't load it into our data
            String line;
            int row = 0;
            String dataset[][] = new String[1000][13];   // Add an extra column (13) here for label placement
            while ((line = br.readLine()) != null && row < 1000) {
                String[] rowData = line.split(","); 
                for (int col = 0; col < dataset[row].length && col < rowData.length; col++) {
                    dataset[row][col] = rowData[col];
                }
                int timeSpent = Integer.parseInt(rowData[2]);   // Get time spent
                if (timeSpent >= 4) dataset[row][12] = "1";     // If a profile in the dataset spends >= 4 hours, assign a true label
                else dataset[row][12] = "0";
                row++;
            }
        return dataset;
        }
    }

    // Helper for reading in user input
    private void readUserInput(String userInput)  {
        
        // row Data is where the user's input is stored
        String[] rowData = new String[12];

        // Deal with .split() ignoring the very last ','
        if (userInput.endsWith(",")) {
            String[] temp = new String[12];
            temp = userInput.split(",");
            for (int i = 0; i < temp.length; i++) {
                rowData[i] = temp[i]; // Place temp into the user's row data
            }
        }
        else { // else split normally
            rowData = userInput.split(",");
        }        
        
        // Finally parse the newly created row data and check if any attributes are missing
        for (int c = 0; c < rowData.length; c++) {
            if (rowData[c] == null || rowData[c].isEmpty()) {   // if a column is missing, then get mode of missing column
                String mode = dataImputation(c);                
                data[0][c] = mode;
            } 
            else {
                data[0][c] = rowData[c];
            }
        }  
    }


    // Helper function for determining the mode of a given column in the data set. Used for data imputation on a user's input
    private String dataImputation(int c) {

        // Count the unique values of a given column
        HashMap<String, Integer> count = new HashMap<>();       // Hash map used to keep track of count
        for (int r = 0; r < 1000; r++) {                        // Iterate through values of that column
            String value = dataset[r][c];
            if (!count.containsKey(value)) count.put(value, 1);  
            else count.put(value, count.get(value) + 1);        // Increment count if that value exists already
        }

        // Get the max count of the hashmap
        String mode = null;
        int max = 0;
        for (String value : count.keySet()) {
            if (count.get(value) > max) {
                max = count.get(value);
                mode = value; 
            } 
        }
        return mode;
    }

    
    // Bootstraps the data (randomly select rows with replacement)
    private void bootStrap(int columns) {
        Random rand = new Random();
        int randomRow;   
        for (int r = 0; r < rows; r++) {
            randomRow = rand.nextInt(1000);
            for (int c = 0; c < columns - 1; c++) {
                data[r][c] = dataset[randomRow][c];        // Load into data and the label
            }
            data[r][columns - 1] = dataset[randomRow][12]; // Load label
        }
    }

    // Prints data it was called on
    public void print() {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                System.out.print(data[r][c] + " ");
            }
            System.out.println();
        }
    }


    // Splits the data based on a threshold and the index of that feature
    public DataContainer split(boolean leftSplit, int indexOfFeature, String threshold) {
        
        // Array list will help in creating the right size for the new data container object for the splits
        ArrayList<ArrayList<String>> correctSize = new ArrayList<>();
        int newRow = 0;
        
        // If the threshold is a number
        if (isNumeric(threshold)) { 

            int numthreshold = Integer.parseInt(threshold);
            
            // If left split is true, then the split data will go to the left child
            if (leftSplit) {
                for (int r = 0; r < this.rows; r++) {
                    int value = Integer.parseInt(data[r][indexOfFeature]);
                    if (value <= numthreshold) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        correctSize.add(newRowData);
                        newRow++;
                    }
                }
            }
            else {  // Else the split data will go to the right child
                for (int r = 0; r < this.rows; r++) {
                    int value = Integer.parseInt(data[r][indexOfFeature]);
                    if (value > numthreshold) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        correctSize.add(newRowData);
                        newRow++;
                    }
                }        
            }
        }
        
        //else, it is categorical and not a number
        else { 
            
            // If left split is true, then the split data will go to the left child
            if (leftSplit) {
                for (int r = 0; r < this.rows; r++) {
                    if (data[r][indexOfFeature].equals(threshold)) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        correctSize.add(newRowData);
                        newRow++;
                    }
                }
            }
            else { // Else the split data will go to the right child
                for (int r = 0; r < this.rows; r++) {
                    if (!data[r][indexOfFeature].equals(threshold)) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        correctSize.add(newRowData);
                        newRow++;
                    }
                }        
            }
        }
       
        // Create a new data container that has the size of the array list called correctSize
        int rowSize = correctSize.size();
        int columnSize = correctSize.isEmpty() ? 0 : correctSize.get(0).size();
        DataContainer newData = new DataContainer(rowSize, columnSize, true);

        // Fill in the new data container
        for (int r = 0; r < correctSize.size(); r++) {
            for (int c = 0; c < correctSize.get(0).size(); c++) {
                newData.addData(r, c, correctSize.get(r).get(c));
            }
        }

        newData.SetCountLabel0();     //count the 0's for this data
        newData.SetCountLabel1();     //count the 1's for this data

        return newData;
    }

    // Helper function for adding data
    private void addData(int r, int c, String value) {
        data[r][c] = value;
    }

    // Helper function for checking for numeric values
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {  // If null or empty
            return false;
        }
        return str.matches("\\d+");    // Checks if only digits
    }
}