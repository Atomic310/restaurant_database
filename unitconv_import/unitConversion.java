import java.sql.*;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
CSCE 315
9-27-2021 Lab
 */
public class unitConversion {

  //Commands to run this script
  //This will compile all java files in this directory
  //javac *.java
  //This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
  //Windows: java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL
  //Mac/Linux: java -cp ".:postgresql-42.2.8.jar" jdbcpostgreSQL

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
		File myObj = new File("unitConversion.csv");
		Scanner myReader = new Scanner(myObj);
		//skip first line
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
				String itemid = "";
				String ingredientid = "";
				String ingredientname = "";
				String _count = "";
				String portionPerInventoryUnit = "";
				int count = 1;//for determining which attribute
				//insert into database
					for(int j = 0; j < line.length(); j++){
						if(Character.compare(line.charAt(j),'\"')==0){
							if(inQ==false)
								inQ=true;
							else if(inQ==true)
								inQ=false;
						}
						if(!inQ && Character.compare(line.charAt(j),'\"')!=0){//skip quotes and not a quote
							if(j == line.length()-1){
								attribute+=line.charAt(j);
								portionPerInventoryUnit = attribute;
								//System.out.println("portion: " + attribute);
							}
							else if(Character.compare(line.charAt(j),',')!=0){
								//if does not have a '
								if(Character.compare(line.charAt(j),'\'')!=0)
									attribute+=line.charAt(j);
							}
							else{
								//System.out.println(attribute);
								if(attribute.length()>0){
									if(attribute.charAt(attribute.length()-1)==' '){
										String newAtt = attribute.substring(0,attribute.length()-1);
										attribute = newAtt;
									}
								}
								switch(count){
									case(1):
										itemid = attribute;
										//System.out.println("itemid: " + attribute);
										break;
									case(2):
										ingredientid = attribute;
										//System.out.println("ingredientID: " + attribute);
										break;
									case(3):
										ingredientname = attribute;
										//System.out.println("ingredientName: " + attribute);
										break;
									case(4):
										_count = attribute;
										//System.out.println("count: " + attribute);
										break;
									
								}
								//increment count
								count++;
								//reset
								attribute="";
							}
						}
						//System.out.println(attributes.get(i));
					}
				
					//System.out.println("item: " + item);
					//System.out.println("name: " + name);
					//System.out.println("price: " + price);
					//insert entity
					String sqlStatement = "INSERT INTO unitConversion(itemID, ingredientID, ingredientName, count, portionPerInventoryUnit)";
					String state2 = "\n"+" VALUES("+itemid+",'"+ingredientid+"','"+ ingredientname+"',"+_count+","+portionPerInventoryUnit+");";
					statements.add(sqlStatement);
					statements.add(state2);
					//reset attribute types
					itemid = "";
					ingredientname = "";
					ingredientid = "";
					_count = "";
					portionPerInventoryUnit = "";
					count = 0;
		}				
		myReader.close();
		
       //Running a query
       //TODO: update the sql command here
       //String sqlStatement = "SELECT * FROM teammembers;";

       //send statement to DBMS
       //This executeQuery command is useful for data retrieval
       //ResultSet result = stmt.executeQuery(sqlStatement);
       //OR
       //This executeUpdate command is useful for updating data
	   for(int i = 0; i<statements.size();i = i+2){
		   int result = stmt.executeUpdate(statements.get(i)+ statements.get(i+1));
	   }

       //OUTPUT
       //You will need to output the results differently depeninding on which function you use
       //System.out.println("--------------------Query Results--------------------");
       //while (result.next()) {
       //System.out.println(result.getString("student_name"));
       //}
       //OR
       //System.out.println(result);
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
