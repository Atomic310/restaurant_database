/*
CSCE 315-901
Project 2,
Tyler Nichols, Group 9

Commands:
  javac *.java
  java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL
Assumptions & Limitations:
  1. All files of this type are include sales for all 7 weekdays
  2. All days are listed in order starting w/ Sunday & ending w/ Saturday
  3. All total prices listed include a $
  3. A quantity is listed for all 19 items each time
  4. Currently only imports from a specific hardcoded file
  4. Currently uses specific hardcoded week ID
  */
import java.sql.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class weeklySales_import {
  //MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
  public static void main(String args[]) {
    //Building the connection with your credentials
    //TODO: update teamNumber, sectionNumber, and userPassword here
    Connection conn = null;
    String teamNumber = "9";
    String sectionNumber = "901";
    String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
    String userPassword = "shorts";

    String path = "ThirdWeekSales.csv";
    int weekID = 3;
    // String path = "SecondWeekSales.csv";
    // String path = "ThirdWeekSales.csv";

    //Connecting to the database
    try {
      conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
    BufferedReader reader;
    try{
      reader = new BufferedReader(new FileReader(path));
      String line = reader.readLine();  // skip 1st line
      String[] lineData;  // declare outside to avoide constand redeclaration
      String[] splitDailyPrice;  // declare outside to avoide constand redeclaration
      String totalStr;
      for (int i = 0; i < 7; ++i) {  // iterate through each day of the week
        line = reader.readLine();    // gather day & daily total
        splitDailyPrice = line.split("\"");       //.......... isolate total price
        totalStr = splitDailyPrice[1].replaceAll(" ", "");  // remove price's surrounding spaces
        totalStr = totalStr.replaceAll(",", "");  //.......... remove the price's comma, if any
        totalStr = totalStr.substring(1);         //.......... remove the price's $
        lineData = line.split(",");
        String currDay = lineData[0];
        // creation of the instruction to execute
        String sqlStatement = "INSERT INTO WeeklySales VALUES (";
        sqlStatement = sqlStatement + "\'" + currDay + "\'";
        for (int j = 0; j < 19; ++j) {  // loop through all 19 items
          line = reader.readLine();
          lineData = line.split(",");
          // int inputItemQnt = Integer.parseInt(lineData[2]);
          // input item's value; assume item quantities are listed in numerical order
          sqlStatement = sqlStatement  + ", " + lineData[2];
        }
        
        sqlStatement = sqlStatement + ", " + totalStr + ", " + Integer.toString(weekID) + ");";  // input final daily total
        //System.out.println(sqlStatement);
        
        Statement stmt = conn.createStatement();
        int result = stmt.executeUpdate(sqlStatement);
        //OUTPUT
        //You will need to output the results differently depeninding on which function you use
        System.out.println("--------------------Query Results--------------------");
        System.out.println(result);
        /**/
        line = reader.readLine();  // skip gap between each day
      }
      reader.close();  // close file reader
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
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
