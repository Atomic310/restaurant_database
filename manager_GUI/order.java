import java.util.*;

import javax.swing.JOptionPane;

import java.sql.*;

import java.util.Date;

import java.text.SimpleDateFormat;

import java.io.BufferedReader;

import java.io.InputStreamReader;

/*
* Holds details for specific order
*
* menuKeyID: used for constructing order based on menuKey in db
*     - if menuKeyID is 0 in the constructor, a blank order is created
* name: used to describe order
* price: price of individual order
* quantity: quantity of this specific order
* ingredientsList: list of ingredients in the order
* custom: boolean to indicate if order is custom or not
* addition operator: used to add two orders together, adds ingredients and price
* subtraction operator: used to subtract RHS from LHS, subtracts ingredients and price
*/

public class order {
    int menuKeyID = 0;
    String name = "";
    Double price = 0.00;
    int quantity = 1;
    ArrayList<ingredient> ingredientList = new ArrayList<ingredient>();
    Boolean custom = false;
    Connection conn = null;
    // constructor (menuKeyID parameter)
    public order(int m, Connection c) {
      // set menuKeyID
      menuKeyID = m;
      conn = c;
  
      if (menuKeyID != 0) { // if menuKeyID is valid
        /* contact menu table, get name and price */
        // create a statement object

        try {
          Statement stmt = conn.createStatement();
          // create an SQL statement
          String sqlStatement = "SELECT name, price FROM menu WHERE item = " + menuKeyID + ";";
          // send statement to DBMS
          ResultSet result = stmt.executeQuery(sqlStatement);
          result.next();
          // store result
          name += result.getString("name"); 

          // store price
          price = result.getDouble("price");
    
          /* contact unitConversion table, get list of ingredients and thier portions */
          sqlStatement = "SELECT ingredientID, ingredientName, count, portionPerInventoryUnit FROM unitconversion WHERE itemID = " + menuKeyID + ";";
          result = stmt.executeQuery(sqlStatement);
		  
          while (result.next()) {
            // get ingredient attributes
            String ingredientID = result.getString("ingredientID");
            String name = result.getString("ingredientname");
            int count = Integer.parseInt(result.getString("count"));
            Double portionPerInventoryUnit = Double.parseDouble(result.getString("portionPerInventoryUnit"));
    
            // construct the ingredient 
            ingredient newIngredient = new ingredient(ingredientID, name, count, portionPerInventoryUnit);
    
            // add ingredient to the list
            ingredientList.add(newIngredient);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Error accessing Database in order creation.");
        }
  
      } else { // if menuKeyID is 0, create a new order
        name = "Custom Order";
        custom = true;
      }
    }
	
}
