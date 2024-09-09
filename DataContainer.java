import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.io.Serializable;


//NOTE: Instead of declaring the attributes, I've made a general 2D String data array to keep it concise
//this can be used for either user input data [0][12] or any number bootstrapped/feature selected data [1000][3]

//in the loadDataset method, i am assuming for now that if you spend 4 or more hours, you are at risk of social media addiction, this can be changed later
//this label gets appended to the dataset[][12] at index 12

public class DataContainer implements Serializable{

    //FIELDS -----------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    private static String[][] dataset; //static initialized
    static {
        try {
            dataset = loadDataset("dummy_data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String[][] data; //userInput data or bootstrapped data, for ex) userData = data[0][12] 
    private int rows;
    private int columns;
    private int countLabel0; //count of labels for no is not addicted
    private int countLabel1; //count of labels for yes is addicted


    //CONSTRUCTORS -----------------------------------------------------------------------

    //constructor for just the dataset if only dataset is just needed
    public DataContainer() {
        this.data = dataset;
        this.rows = 1000;
        this.columns = 13;                           //13 because last index will contain the label, either 1 or 0
        //TODO: ADD DATA IMPUTATION FOR DATA SET
        this.countLabel0 = countLabels(data, 0);     //count the 0's for this data
        this.countLabel1 = countLabels(data, 1);     //count the 1's for this data
    }

    //constructor for user input
    public DataContainer(String userInput)  {
        this.rows = 1;
        this.columns = 12;
        this.data = new String[rows][columns];
        this.countLabel0 = 0;                       //no labels for user input
        this.countLabel1 = 0;                       //no labels for user input
        this.readUserInput(userInput);
    }

    //constructor for bootstrapping (this function will bootstrap for us)
    public DataContainer(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new String[rows][columns];
        this.bootStrap(columns);
        this.countLabel0 = countLabels(data, 0);     //count the 0's for this data
        this.countLabel1 = countLabels(data, 1);     //count the 1's for this data  
    }

    //constructor for splitting data, used in the split function below
    public DataContainer(int rows, int columns, boolean split) {
        this.rows = rows;
        this.columns = columns;
        this.data = new String[rows][columns];
    }


    //METHODS ----------------------------------------------------------------------------

    public String getValue(int r, int c) {
        return this.data[r][c];
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    //returns a 1 or 0 label of that given row
    public String getLabel(int row) {
        return this.getValue(row, 12);
    }
    
    //gets the current row as a string array
    public String[] getRow(int rowIndex) {
        return data[rowIndex];
    }

    //returns the amount of 1's or 0's for the data object
    public int getLabelCount(int label) {
        if (label == 0) return this.countLabel0;
        else if (label == 1) return this.countLabel1;
        else {
            System.out.println("INVALID LABEL");
            return -1;
        }
    }
    
    private void SetCountLabel0() {
        this.countLabel0 = countLabels(this.data, 0);
    }

    private void SetCountLabel1() {
        this.countLabel1 = countLabels(this.data, 1);
    }

    //if either label is 0, then true
    public boolean isPure() {
        return (this.countLabel0 == 0 ) || (this.countLabel1 == 0);
    }

    //if the data object is empty
    public boolean isEmpty() {
        return rows == 0 && columns == 0;
    }

    //helper for counting the number of 1's and 0's in current data
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

    //helper for loading the actual dataset
    //NOTE: ASSUME TIMESPENT >= 4 IS ASSIGNED A LABEL 1 FOR YES AND 0 OTHERWISE
    //TODO: HANDLE MISSING DATA FROM DATASET (ASSUMED FOR NOW NOT MISSING ANY DATA)
    private static String[][] loadDataset(String source) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            br.readLine(); //consume header to skip so we dont load it into our data
            String line;
            int row = 0;
            String temp[][] = new String[1000][13];   //add an extra column (13) here for label. 
            while ((line = br.readLine()) != null && row < 1000) {
                String[] rowData = line.split(","); 
                for (int col = 0; col < temp[row].length && col < rowData.length; col++) {
                    temp[row][col] = rowData[col];
                }
                int timeSpent = Integer.parseInt(rowData[2]); //ASSUME FOR NOW THAT TIMESPENT >= 4 IS A 1.
                if (timeSpent >= 4) temp[row][12] = "1";
                else temp[row][12] = "0";
                row++;
            }
        return temp;
        }
    }


    //helper for reading in user input
    private void readUserInput(String userInput)  {
        String[] rowData = new String[12];

        //kind of a band-aid solution to the .split() ignoring very last ','
        if (userInput.endsWith(",")) {
            String[] temp = new String[12];
            temp = userInput.split(",");
            for (int i = 0; i < temp.length; i++) {
                rowData[i] = temp[i];
            }
        }
        else {
            rowData = userInput.split(",");
        }        
        
        for (int c = 0; c < rowData.length; c++) {
            if (rowData[c] == null || rowData[c].isEmpty()) {   //if column missing, then get mode of missing column
                String mode = dataImputation(c);                
                data[0][c] = mode;
            } 
            else {
                data[0][c] = rowData[c];
            }
        }  
    }


    //NOTE: may be better to do average for numerical features? and mode for rest? this function was applied to readuserinput already but not anywhere else yet
    //TODO: also prof said to check if col is empty
    private String dataImputation(int c) {

        HashMap<String, Integer> count = new HashMap<>();
        for (int r = 0; r < 1000; r++) {                        //iterate through values of that column
            String value = dataset[r][c];
            if (!count.containsKey(value)) count.put(value, 1);  
            else count.put(value, count.get(value) + 1);        //increment count if exists already
        }

        //FOR TESTING ONLY-----------------------
        // for (String value : count.keySet()) {
        //     int occurrences = count.get(value);
        //     System.out.println(value + ": " + occurrences);
        // }
        //---------------------------------------

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

    //randomly select rows w/ replacement
    private void bootStrap(int columns) {
        Random rand = new Random();
        int randomRow;   
        for (int r = 0; r < rows; r++) {
            randomRow = rand.nextInt(1000);
            for (int c = 0; c < columns - 1; c++) {
                data[r][c] = dataset[randomRow][c]; //load into data and the label
            }
            data[r][columns - 1] = dataset[randomRow][12]; //load label
        }
    }

    //prints data it was called on
    public void print() {
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.columns; c++) {
                System.out.print(data[r][c] + " ");
            }
            System.out.println();
        }
    }


    //splits the data based on a threshold and the index of that feature
    public DataContainer split(boolean leftSplit, int indexOfFeature, String threshold) {
        
        //use an array list, will help in creating the right size for the new data container object for the splits
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        int newRow = 0;
        
        //if the threshold is a number
        if (isNumeric(threshold)) { 

            int numthreshold = Integer.parseInt(threshold);
            
            //if leftsplit is true, then will go to the left child
            if (leftSplit) {
                for (int r = 0; r < this.rows; r++) {
                    int value = Integer.parseInt(data[r][indexOfFeature]);
                    if (value <= numthreshold) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        temp.add(newRowData);
                        newRow++;
                    }
                }
            }
            //else the split data will go to the right child
            else {
                for (int r = 0; r < this.rows; r++) {
                    int value = Integer.parseInt(data[r][indexOfFeature]);
                    if (value > numthreshold) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        temp.add(newRowData);
                        newRow++;
                    }
                }        
            }
        }
        
        //else, it is categorical and not a number
        else { 
            
            //if leftsplit is true, then will go to the left child
            if (leftSplit) {
                for (int r = 0; r < this.rows; r++) {
                    if (data[r][indexOfFeature].equals(threshold)) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        temp.add(newRowData);
                        newRow++;
                    }
                }
            }
            //else the split data will go to the right child
            else {
                for (int r = 0; r < this.rows; r++) {
                    if (!data[r][indexOfFeature].equals(threshold)) {
                        ArrayList<String> newRowData = new ArrayList<>();
                        for (int c = 0; c < this.columns; c++) {
                            newRowData.add(data[r][c]);
                        }
                        temp.add(newRowData);
                        newRow++;
                    }
                }        
            }
        }
       
        //create a new data container that has the size of the array list
        int rowSize = temp.size();
        int columnSize = temp.isEmpty() ? 0 : temp.get(0).size();
        DataContainer newData = new DataContainer(rowSize, columnSize, true);

        //fill in the new data container
        for (int r = 0; r < temp.size(); r++) {
            for (int c = 0; c < temp.get(0).size(); c++) {
                newData.addData(r, c, temp.get(r).get(c));
            }
        }

        newData.SetCountLabel0();     //count the 0's for this data
        newData.SetCountLabel1();     //count the 1's for this data

        return newData;
    }

    //helper function for adding data
    private void addData(int r, int c, String value) {
        data[r][c] = value;
    }

    //helper function for numeric values
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {     //if null or empty
            return false;
        }
        return str.matches("\\d+");             //checks if only digits
    }


}
