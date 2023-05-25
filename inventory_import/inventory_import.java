import java.sql.*;
import java.io.File; // import file class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class inventory_import {

//Commands to run this script
//This will compile all java files in this directory
//javac *.java
//This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
//Windows: java -cp ".;postgresql-42.2.8.jar" iventory_import
//Mac/Linux: java -cp ".:postgresql-42.2.8.jar" inventory_import

//MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
public static void main(String args[]) {

    //Building the connection with your credentials
    Connection conn = null;
    String teamNumber = "9";
    String sectionNumber = "901";
    String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
    String userPassword = "shorts";

    //Connecting to the database
    try {
        conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }

     System.out.println("Opened database successfully");

    try{

        File myObj = new File("First day order.csv");
        Scanner myReader = new Scanner(myObj);

        // keep track of lines to ommit header lines
        int line = 0;
            
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine(); // entry
            line ++;

            // disect the entry
            // regEx to make sure that embended commas in entries are handled
            /* used https://www.javacodeexamples.com/java-split-string-by-comma-example/740 to learn about this */
            String[] attributes = data.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // initalization of attribute varibales
            /* first index is always null */
            /* ternary oprators "?" fill in empty attributes with NULL */
            String unitName = (attributes[1] == "") ? "NULL" : attributes[1];
            String ingredientID = (attributes[2] == "") ? "NULL" : attributes[2]; 
            String quantityAmt = (attributes[3] == "") ? "0" : attributes[3];
            // String deliveredAmt = (attributes[4] == "") ? "0" : attributes[4]; // removed
            String quantityUnit = (attributes[5] == "") ? "NULL" : attributes[5]; // "quantityType" column
            // String deliveredType = (attributes[6] == "") ? "NULL" : attributes[6]; // removed
            String quantityMultiplier = (attributes[7] == "") ? "0" : attributes[7];
            Double pricePerUnit = (attributes[8] == "" || line <= 2) ? 0.00 : Double.parseDouble(attributes[8].replace("$", "").replaceAll("\"", "").replace(",", "")); // "price" column ... convert to double
            Double totalPrice = (attributes[9] == "" || line <= 2) ? 0.00 : Double.parseDouble(attributes[9].replace("$", "").replaceAll("\"", "").replace(",", "")); // "extended" column .. convert to double

            // String catagory = (attributes[10] == "") ? "NULL" : attributes[10]; // removed
            // String invoiceNum = (attributes[11] == "") ? "99999" : attributes[11]; // removed
            // String detailedDescription = (attributes[12] == "") ? "NULL" : attributes[12].replaceAll("'", ""); // take out any and all single quotes // removed
            /* random single quotes mess with SQL insert function */

            /* calcualte total quantity */
            double amt = 0.00;
            double multiplier = 0.00;
            double quantity = 0.00;

            if (line > 2) { // do data conversion only if line is greater than 2
                amt = Double.parseDouble(quantityAmt);  
                multiplier = Double.parseDouble(quantityMultiplier);  
                quantity =  amt * multiplier;
            }

            // create sql statement
            Statement stmt = conn.createStatement();
            String sqlStatement = "INSERT INTO inventory (unitName, ingredientID, quantity, quantityUnit, pricePerUnit, totalPrice)";
            sqlStatement = sqlStatement + " VALUES ('" + unitName + "', '" + ingredientID + "', " + quantity + ", '" + quantityUnit + "', " +  pricePerUnit + ", " + totalPrice + ");";
            
            Boolean empty = true;

            // determine if statement is an empty entry
            for (int i = 1; i < attributes.length; i++) {
                if (attributes[i] != "") {
                    empty = false;
                }
            }

            // if statement is not a header and not an empty entry, send
            int result = 0;
            if (empty == false && line > 2) { // omitt headerlines and empty entries
                result = 1;
                System.out.println(sqlStatement);
                result = stmt.executeUpdate(sqlStatement);
            }

            System.out.println(result);

        }
        myReader.close();

        //ResultSet result = stmt.executeQuery(sqlStatement); // data retreval
        //int result = stmt.executeUpdate(sqlStatement); // send data

    } catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }

    //closing the connection
    try {
        conn.close();
        System.out.println("Connection Closed.");
    } catch(Exception e) {
        System.out.println("Connection NOT Closed.");
    }//end try catch
  }//end main
}//end Class
