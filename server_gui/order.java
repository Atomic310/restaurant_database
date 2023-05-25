import java.util.*;

import javax.swing.JOptionPane;

import java.sql.*;

import java.util.Date;

import java.text.SimpleDateFormat;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import javax.swing.*;

import java.awt.BorderLayout;

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
	//static JFrame f;

    int menuKeyID = 0;
    String name = "";
    Double price = 0.00;
    int quantity = 1;
    ArrayList<ingredient> ingredientList = new ArrayList<ingredient>();
    Boolean custom = false;
    Connection conn = null;
	String date = "20";
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
		  
		  //Get current date year-month-day
		  Date currdate = new Date();
		  SimpleDateFormat sqlform = new SimpleDateFormat("yy-MM-dd");
		  date += sqlform.format(currdate);
		  //if date not in weeklysales, create a new date
		  checkDate(date, currdate);
		  
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
          JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }
  
      } else { // if menuKeyID is 0, create a new order
        name = "Custom Order";
        custom = true;
      }
    }
	
    // add operator
  
    // subtract operator
	
	public void checkDate(String date, Date currdate){
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
			"csce315901_9user", "shorts");
			//check if date is in weeklysales
				//create a statement object
			Statement stmt = conn.createStatement();
				//create an SQL statement
			String sqlStatement = "SELECT * FROM weeklysales;";
				//send statement to DBMS
			ResultSet result = stmt.executeQuery(sqlStatement);
			boolean hasDate = false;
			//System.out.println(date);
			while (result.next()) {
				//System.out.println(result.getString("Dates"));
				if(result.getString("Dates").equals(date)){
					hasDate = true;
				}
			}
			//if the date is not in the database
			if(!hasDate){
				//call createDate
				createDate(date,currdate);
			}
			//if not, create it
			
			conn.close();
		}
		catch (Exception x) {
			x.printStackTrace();
			System.err.println(x.getClass().getName()+": "+x.getMessage());
			System.exit(0);
			}
	}
	
	public void createDate(String date, Date currdate){
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
			"csce315901_9user", "shorts");
			//Get weekday
			Calendar myCal = Calendar.getInstance();
			myCal.setTime(currdate);
			int weekdayint = myCal.get(Calendar.DAY_OF_WEEK);
			String weekday = "";
			switch(weekdayint){
				case 1:
					weekday = "Sunday";
					break;
				case 2:
					weekday = "Monday";
					break;
				case 3:
					weekday = "Tuesday";
					break;
				case 4:
					weekday = "Wednesday";
					break;
				case 5:
					weekday = "Thursday";
					break;
				case 6:
					weekday = "Friday";
					break;
				case 7:
					weekday = "Saturday";
					break;
			}
			//Get weekid
			//f = new JFrame("WeekID Popup"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			boolean hasID = false;
			String id = "";
			//Prompt to manually get weekID
			/*JDialog wkidPanel = new JDialog(f);
			JPanel wkidInfo = new JPanel();
			wkidInfo.setLayout(new BorderLayout());
			wkidPanel.setLayout(new BorderLayout());
			
			JLabel promptWkid = new JLabel("Enter the weekID for today's date: ");
			wkidInfo.add(BorderLayout.NORTH, promptWkid);
			JTextField enterWkid = new JTextField(5);
			wkidInfo.add(BorderLayout.AFTER_LINE_ENDS, enterWkid);
			
			wkidPanel.setBounds(500, 500, 1500, 500);
			wkidPanel.setVisible(true);
			wkidPanel.add(BorderLayout.CENTER, wkidInfo);*/
			System.out.println("Type in weekID for this new date:");
			while(!hasID){
				id = reader.readLine();
				if(name.length() > 0){
					hasID = true;
				}
			}
			/*while(!id.equals("")){
					id = enterWkid.getText();
			}*/
				//create a statement object
			Statement stmt = conn.createStatement();
				//create an SQL statement
			String sqlStatement = "INSERT INTO weeklysales(day,qnt_501,qnt_502,qnt_503,qnt_504,qnt_505,qnt_506,qnt_507,qnt_508,qnt_509,qnt_510,qnt_511,qnt_512,qnt_513,qnt_514,qnt_515,qnt_516,qnt_517,qnt_518,qnt_519,daily_total,weekid,dates) VALUES('"+weekday+"',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.0,"+id+",'"+date+"');";
				//send statement to DBMS
			int result = stmt.executeUpdate(sqlStatement);
			conn.close();
		}
		catch (Exception x) {
			x.printStackTrace();
			System.err.println(x.getClass().getName()+": "+x.getMessage());
			System.exit(0);
			}
	}
	
  }