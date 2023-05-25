import java.sql.*;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class menu_import {

  //Commands to run this script
  //This will compile all java files in this directory
  //javac *.java
  //This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
  //Windows: java -cp ".;postgresql-42.2.8.jar" menu_import
  //Mac/Linux: java -cp ".:postgresql-42.2.8.jar" menu_import

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
    	//create a statement object
    	Statement stmt = conn.createStatement();

		//read from csv
		//iterate through csv
		//String sqlStatement = "INSERT INTO menu(item, name, price) VALUES();";
		File myObj = new File("MenuKey.csv");
		Scanner myReader = new Scanner(myObj);

		//skip first 2 lines
		myReader.nextLine();
		myReader.nextLine();

		//attributes
		List<String> statements = new ArrayList<>();

		//iterate
		while(myReader.hasNextLine()){
			String line = myReader.nextLine();
			//entities.add(line);
			//detect quotes
			boolean inQ = false;
			//item to insert
			String attribute = "";
			//types of attributes
			String item = "";
			String name = "";
			String price = "";
			int count = 0;//for determining which attribute
			//insert into database
			for(int j = 0; j < line.length(); j++) {
				if(Character.compare(line.charAt(j),'\"')==0) {
					if (inQ==false) {
						inQ=true;
					} else if (inQ==true) {
						inQ=false;
					}
				}

				if (!inQ && Character.compare(line.charAt(j),'\"')!=0) { //skip quotes and not a quote
					if (Character.compare(line.charAt(j),',')!=0) {
						//if does not have a '
						if (Character.compare(line.charAt(j),'\'')!=0) {
								attribute+=line.charAt(j);
						}
					} else {
						//System.out.println(attribute);
						if(attribute.length()>0){
							if(attribute.charAt(attribute.length()-1)==' '){
								String newAtt = attribute.substring(0,attribute.length()-1);
								attribute = newAtt;
							}
						}

						switch(count){
							case(1):
								item = attribute;
								//System.out.println("item: " + item);
								break;
							case(2):
								name = attribute;
								//System.out.println("name: " + name);
								break;
							case(4):
								price = attribute;
								//System.out.println("price: " + price);
								break;
						}

						//increment count
						count++;
						//reset
						attribute="";
					}
				}
			}

			//insert entity
			String sqlStatement = "INSERT INTO menu(item, name, price)";
			String state2 = "\n"+" VALUES("+item+",'"+name+"','"+price+"');";
			statements.add(sqlStatement);
			statements.add(state2);

			//reset attribute types
			item = "";
			name = "";
			price = "";
			count = 0;
		}		
		
		myReader.close();

		for(int i = 0; i < statements.size(); i = i + 2){
			int result = stmt.executeUpdate(statements.get(i) + statements.get(i+1));
		}

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
    } //end try catch
  } //end main
}//end Class
