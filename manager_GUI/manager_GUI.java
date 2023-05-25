import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*; 
import java.util.*;

/*
CSCE 315-901
Project 2, Group 9
Manager GUI
Compile  :   javac *.java
Run (Mac):   java -cp ".:postgresql-42.2.8.jar" manager_GUI
(Windows):   java -cp ".;postgresql-42.2.8.jar" manager_GUI

ASSUMPTIONS:
 1. each week starts with Sunday & includes Sunday
 2. the highest recorded week ID represents the current week
 3. each week includes information for all 7 days
*/

public class manager_GUI extends JFrame implements ActionListener {
  static JFrame f;
  // static JFrame login;
  // Vector<Integer> weeks;
  
  public static void main(String[] args)
  {
    /**************************************** CONNECTION CONSTRUCTION ****************************************/
    Connection conn = null;
    
    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db", "csce315901_9user", "shorts");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    JOptionPane.showMessageDialog(null,"Opened database successfully");

    String name = "";
    Vector<String> menuCols = new Vector<String>();
    Vector<String[]> menuRows = new Vector<String[]>();
    Integer menuColCnt = 0;
    
    try{
      Statement stmt = conn.createStatement();    //......... create a statement object
      String sqlStatement = "SELECT * FROM menu;";    //..... create an SQL statement
      ResultSet result = stmt.executeQuery(sqlStatement);  // send statement to DBMS
      // SOURCE: https://stackoverflow.com/questions/696782/retrieve-column-names-from-java-sql-resultset
      ResultSetMetaData rsmd = result.getMetaData();  // get column count & names
      menuColCnt = rsmd.getColumnCount();
      for (int i = 1; i <= menuColCnt; i++ ) {
        menuCols.add(rsmd.getColumnName(i));
      }
      while (result.next()) {  //....................... get table information
        name += result.getString("name")+"\n";
        
        String[] menuEntry = new String[menuColCnt];
        for (int j = 0; j < menuColCnt; ++j) {
          menuEntry[j] = result.getString(menuCols.get(j));
        }
        menuRows.add(menuEntry);
      }
    } catch (Exception e){
      JOptionPane.showMessageDialog(null,"Catch 1: Error accessing Database.");
    }

    /**************************************** GUI & FRAME INSTANTIATIONS ****************************************/

    manager_GUI man_GUI = new manager_GUI();     // manager's database GUI
    f = new JFrame("Manager GUI");               // main GUI frame

    JPanel frameBttns = new JPanel();  // panel for all final buttons
    JButton closeGUI = new JButton("Close GUI");      // closing button
    JButton invShow = new JButton("Show Inventory");  // inventory button
    JButton addRmvBttn = new JButton("Add/Remove");   // add/remove popup button

    frameBttns.add(invShow);
    frameBttns.add(addRmvBttn);
    frameBttns.add(closeGUI);

    /************************************** GENERAL FINANCES PANEL ***************************************/

    JPanel finPanel = new JPanel();  // finance panel
    JLabel finPanLabel = new JLabel("Finances");
    JButton finMoreBttn = new JButton("More Finances");
    
    finPanel.setLayout(new BorderLayout());  // set finance panel layout
    finPanel.add(BorderLayout.NORTH, finPanLabel);
    finPanel.add(BorderLayout.SOUTH, finMoreBttn);

    // -------------------------------- Getting all week IDs --------------------------------

    String getWeekIDs = "SELECT weekid FROM WeeklySales WHERE day=\'Sunday\';";  // find all the listed week numbers
    Integer currWeek = 0;
	  //start and end date
    String startDate = "2022-2-22";
    String endDate = "2022-3-6";

    try{
      Statement stmt = conn.createStatement();
      ResultSet rsltWeekIDs = stmt.executeQuery(getWeekIDs);
      Vector<Integer> weekIDs = new Vector<Integer>(); // gather all the week IDs; assume highest is current week
      while(rsltWeekIDs.next()){
        weekIDs.add(rsltWeekIDs.getInt("weekid"));  // insert latest week ID
        if (weekIDs.lastElement() > currWeek) {     // update current week if newest is larger
          currWeek = weekIDs.lastElement();
        }
      }
      rsltWeekIDs.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("General Finances: " + e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    // -------------------------------- Weekly Recap --------------------------------

    String getRecap = "SELECT day, daily_total FROM WeeklySales WHERE weekid=" + currWeek + ";";
	
    try{
      Statement stmt = conn.createStatement();
      ResultSet recapRslt = stmt.executeQuery(getRecap);
      String[][] weeklyRecap = new String[7][2];
      Integer j = 0;
      while(recapRslt.next()) {
        weeklyRecap[j][0] = recapRslt.getString("day");
        weeklyRecap[j][1] = recapRslt.getString("daily_total");
        j = j + 1;
      }
      String[] colSmpl = {"Weekday", "Total Earned ($)"};
      JTable recapTbl = new JTable(weeklyRecap, colSmpl);
      finPanel.add(new JScrollPane(recapTbl));
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("Weekly Recap: " + e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    /**************************************** DETAILED FINANCE POPUP ****************************************/

    JDialog finMore = new JDialog(f);
    JLabel finMoreLbl = new JLabel("Detailed Finances");
    JButton finLessBttn = new JButton("Less");

    // -------------------------------- Time Input --------------------------------

    JPanel timeframe = new JPanel();
    JLabel timeframeLbl = new JLabel("Dates?");
    
  	JTextField dateInput1 = new JTextField(startDate, 20);
    JTextField dateInput2 = new JTextField(endDate, 20);
	
	  JButton finSearch = new JButton("Search");

    timeframe.add(dateInput1);
    timeframe.add(dateInput2);
    timeframe.add(finSearch);

    // -------------------------------- Timeframe Search --------------------------------

    JPanel finMoreTbls = new JPanel();
    finMoreTbls.setLayout(new BorderLayout());

    Vector<String> saleCols = new Vector<String>();
    Vector<String[]> saleRows = new Vector<String[]>();
    Integer saleColCnt = 0;

    try{
      //String getWeekDetails = getDetails + "FROM WeeklySales WHERE weekid=" + a + ";";
      String getInfo = "SELECT * FROM WeeklySales WHERE dates BETWEEN \'" + startDate + "\' AND \'"+endDate+"\';";
      Statement stmt = conn.createStatement();
      ResultSet saleRslt = stmt.executeQuery(getInfo);
      // get column count & names
      ResultSetMetaData rsmd = saleRslt.getMetaData();
      saleColCnt = rsmd.getColumnCount();
      for (int i = 1; i <= saleColCnt; i++ ) {
        saleCols.add(rsmd.getColumnName(i));
      }

      Integer i = 0;
      while(saleRslt.next()) {
        String[] saleEntry = new String[saleColCnt];
        for (int j = 0; j < saleColCnt; ++j) {
          saleEntry[j] = saleRslt.getString(saleCols.get(j));
        }
        ++i;
        saleRows.add(saleEntry);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("Detailed Finances: " + e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    // -------------------------------- Item Popularity Chart --------------------------------
    int colNum = 0;
    int nonItemSaleCols = 4;  // ======================================================================= ??SET AS UNIVERSAL OR FIND ANOTHER WAY SOMEWHERE ELSE??
    String[] itemPopCols = {"Item", "Quantity Sold"};
    Integer[] itemQnts = new Integer[saleCols.size() - nonItemSaleCols];
    Integer[][] itemPopData = new Integer[saleCols.size() - nonItemSaleCols][2];
    HashMap<Integer, Integer> qntToItem = new HashMap<Integer, Integer>();

    for (int i = 0; i < saleCols.size(); i++) { // iterate through each column
      String subj = saleCols.get(i);  //.......... store column name
      if (subj.charAt(0) !='q') {  //............. if not a quantity column, continue
        // System.out.println("CONTINUING");
        continue;
      } else {  // else, gather the item & its sale count 
        int itemID = Integer.parseInt(subj.substring(subj.length()-3, subj.length()));
        itemQnts[colNum] = 0;
        for (int j = 0; j < saleRows.size(); j ++) { // iterate through each row
          int value = Integer.parseInt(saleRows.get(j)[i]);  // update item's sale count
          itemQnts[colNum] += value;
        }
        qntToItem.put(itemQnts[colNum], itemID);
        ++colNum;  // increment column number
      }
    }
    Arrays.sort(itemQnts, Collections.reverseOrder());
  
    for (int i = 0; i < colNum; i ++) {
      //System.out.println("item: " + itemPopData[i][0] + ", count: " + itemPopData[i][1]);
      itemPopData[i][0] = qntToItem.get(itemQnts[i]);
      itemPopData[i][1] = itemQnts[i];
    }

    JTable itemPopTbl = new JTable(itemPopData, itemPopCols);
    // JPanel itemPopInfo = new JPanel();
    // finMoreTbls.add(BorderLayout.NORTH, itemPopTbl.getTableHeader());
    finMoreTbls.add(BorderLayout.WEST, new JScrollPane(itemPopTbl));

    /********************************* INVENTORY USAGE CHART *********************************/

    Connection newConn = null;  // inventory usage connection

    try {  // open sql connection
      Class.forName("org.postgresql.Driver");
      newConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db", "csce315901_9user", "shorts");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    // create hashmap to store ingredients
    HashMap<String, Double> ingredientMap = new HashMap<String, Double>();

    // iterate though itemPopData
    for (Integer row = 0; row < itemPopData.length; row++) { 
      Integer itemQuantity = itemPopData[row][1]; // quantity
      Integer itemID = itemPopData[row][0]; // id

      // output debugging
      System.out.println("Processing ingredients for " + itemID);

      // create an order
      order myOrder = new order(itemID, newConn);
      
      // iterate though ingredient list
      for (Integer i = 0; i < myOrder.ingredientList.size(); i++) {
        // get ingredient name
        String key = myOrder.ingredientList.get(i).name;
        // get ingredient amount (count*quantity)*itemQuantity
        Double value = (myOrder.ingredientList.get(i).count * myOrder.ingredientList.get(i).quantity) * itemQuantity;
        // update the map accordingly
        if (ingredientMap.containsKey(key)) {
          ingredientMap.put(key, ingredientMap.get(key) + value);
        } else {
          ingredientMap.put(key, value);
        }
      }
    }

    try {  // close inventory usage connection
      newConn.close();
      } 
      catch(Exception e) {
    }

    // convert map to 2d string array 
    ArrayList<String[]> ingredientDisplay = new ArrayList<String[]>();
    for (Map.Entry<String,Double> entry : ingredientMap.entrySet()) {
      String row[] = {entry.getKey(), String.valueOf(entry.getValue())};  
      ingredientDisplay.add(row);
    }

    String[][] ingDisplayArray = new String[ingredientDisplay.size()][];
    for (int i = 0; i < ingredientDisplay.size(); i++) {
        ingDisplayArray[i] = ingredientDisplay.get(i);
    }

    // COLUMN HEADER: ingredient name, ingredient sku, ingredient count
    String[] ingHeader = {"Ingredient Name", "Quantity (Portion/Inventory Unit"};
    
    JTable usageTbl = new JTable(ingDisplayArray, ingHeader);
    finMoreTbls.add(BorderLayout.EAST, new JScrollPane(usageTbl));

    // ---------------------- Detailed Finances Panel Assembly ----------------------

    finMore.setLayout(new BorderLayout());
    finMore.setBounds(200, 200, 950, 600);
    finMore.setVisible(false);

    finMore.add(BorderLayout.PAGE_START, finMoreLbl);  // panel label
    finMore.add(BorderLayout.NORTH, timeframe);        // timeframe space
    finMore.add(BorderLayout.CENTER, finMoreTbls);     // week's details
    finMore.add(BorderLayout.PAGE_END, finLessBttn);   // escape button

    /***************************** INVENTORY PANEL *****************************/

    JDialog invPopup = new JDialog(f);
    JPanel invExtras = new JPanel();
    JLabel invPanLbl = new JLabel("Inventory");
    JButton invHide = new JButton("Hide Inventory");

    // -------------------------------- Menu --------------------------------

    JPanel menuPanel = new JPanel();
    JLabel menuLbl = new JLabel("Menu");
    JButton menuUpdt = new JButton("Update Menu");

    String[][] menuTblInfo = new String[menuRows.size()][menuColCnt];  // initialize object array to match vector of values
    
    for (Integer i = 0 ; i < menuRows.size(); ++i) {
      menuTblInfo[i] = menuRows.get(i);
      // System.out.println(menuTblInfo[i]);
    }

    JTable menuTbl = new JTable(menuTblInfo, menuCols.toArray());

    menuPanel.setLayout(new BorderLayout());
    menuPanel.add(BorderLayout.BEFORE_FIRST_LINE, menuLbl);
    menuPanel.add(new JScrollPane(menuTbl));
    menuPanel.add(BorderLayout.AFTER_LAST_LINE, menuUpdt);

    // -------------------------------- Expanded Inventory --------------------------------

    JPanel invInfo = new JPanel();
    invInfo.setLayout(new BorderLayout());

    String getInv = "SELECT * FROM inventory";
    Vector<String> invCols = new Vector<String>();
    Vector<String[]> invInfoVec = new Vector<String[]>();  // vector of all table information
    Integer invColCnt = 0;

    try{
      Statement stmt = conn.createStatement();
      ResultSet invRslt = stmt.executeQuery(getInv);
      ResultSetMetaData rsmd = invRslt.getMetaData();  // get column count & names
      invColCnt = rsmd.getColumnCount();
      for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
        invCols.add(rsmd.getColumnName(i));
      }
      while (invRslt.next()) {
        String[] infoIn = new String[invColCnt];
        for (int i = 0; i < invColCnt; ++i) {
          infoIn[i] = invRslt.getString(invCols.get(i));
        }
        invInfoVec.add(infoIn);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("Expanded Inventory: " + e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    String[][] invRows = new String[invInfoVec.size()][invColCnt];  // initialize object array to match vector of values
    for (Integer i = 0 ; i < invInfoVec.size(); ++i) {
      invRows[i] = invInfoVec.get(i);
    }
    JTable invTbl = new JTable(invRows, invCols.toArray());
    invInfo.add(BorderLayout.CENTER, new JScrollPane(invTbl));

  	// -------------------------------- Restock Report --------------------------------
	
	  JDialog RinvPanel = new JDialog(f);
	
	  JPanel RinvInfo = new JPanel();
    RinvInfo.setLayout(new BorderLayout());
	//auto
	JButton bAuto = new JButton("Auto-Order");
	Vector<String> autoOrders = new Vector<String>();
	
    try{
			//initialize
			String[] RinvTblHeaders = {"unitname", "ingredientid", "quantity", "quantityunit", "flevel", "Difference"};
			Vector<String[]> RinvInfoVec = new Vector<String[]>();
			
			int totalRestock = 0;
			//TODO: go through each inventory item
			String RgetInvLevels = "SELECT unitname, ingredientid, quantity, quantityunit, flevel FROM inventory;";
			Statement Rstmt = conn.createStatement();
			ResultSet RdetailRslt = Rstmt.executeQuery(RgetInvLevels);
			//first add headers
			String[] Rheaders = {"unitname", "ingredientid", "quantity", "quantityunit", "flevel", "Difference"};
			RinvInfoVec.add(Rheaders);
			while(RdetailRslt.next()) {
				//System.out.println("quant: "+detailRslt.getString("quantity")+", fill: "+detailRslt.getString("flevel"));
				if(RdetailRslt.getInt("flevel") > RdetailRslt.getInt("quantity")){
					totalRestock++;//incre total needed to be restocked
					//add to table of items needed to be restocked
					String[] RinfoIn = new String[6];
					for (int i = 0; i < 5; ++i) {
					  RinfoIn[i] = RdetailRslt.getString(RinvTblHeaders[i]);
					}
					RinfoIn[5] = "" + (RdetailRslt.getInt("flevel") - RdetailRslt.getInt("quantity"));
					autoOrders.add("UPDATE inventory SET quantity = "+RinfoIn[5]+" WHERE ingredientid = '"+RdetailRslt.getString("ingredientid")+"';");
					RinvInfoVec.add(RinfoIn);
				}
			}
			String[][] RinvTblInfo = new String[RinvInfoVec.size()][RinvInfoVec.get(0).length];  // initialize object array to match vector of values
			for (Integer i = 0 ; i < RinvInfoVec.size(); ++i) {
			  RinvTblInfo[i] = RinvInfoVec.get(i);
			}
			JLabel RinvInfoLbl = new JLabel("Number of items that needs to be restocked: " + totalRestock);
			JTable RinvTbl = new JTable(RinvTblInfo, RinvTblHeaders);
			RinvInfo.add(BorderLayout.EAST, bAuto);
			RinvInfo.add(BorderLayout.BEFORE_FIRST_LINE, RinvInfoLbl);
			RinvInfo.add(BorderLayout.NORTH, RinvTbl.getTableHeader());
			RinvInfo.add(BorderLayout.CENTER, RinvTbl);
  
    }
    catch (Exception e2) {
      e2.printStackTrace();
      System.err.println("Restock Report: "+e2.getClass().getName()+": "+e2.getMessage());
      System.exit(0);
    }

		RinvPanel.setLayout(new BorderLayout());
		RinvPanel.setBounds(500, 500, 1500, 500);
		RinvPanel.setVisible(false);
		RinvPanel.add(BorderLayout.CENTER, RinvInfo);
		
    // -------------------------------- Inventory Updating --------------------------------

    JButton invUpdt = new JButton("Update Inventory");
    JButton restockUpdt = new JButton("Restock");  //For the Restock
    JTextField invUpdtSubj = new JTextField("Target Ingredient ID", 15);

    // -------------------------------- Inventory Panel Assembly --------------------------------

    invExtras.add(restockUpdt);
    invExtras.add(invUpdt);
    invExtras.add(invUpdtSubj);
    invExtras.add(invHide);

    invPopup.setLayout(new BorderLayout());
    invPopup.setBounds(600, 30, 800, 1000);
    invPopup.setVisible(false);

    invPopup.add(BorderLayout.BEFORE_FIRST_LINE, invPanLbl);
    invPopup.add(BorderLayout.CENTER, invInfo);
    invPopup.add(BorderLayout.AFTER_LAST_LINE, invExtras);
  
    /***************************** ADD/REMOVE PANEL *****************************/
  
    JDialog addRmv = new JDialog(f);
    JPanel addRmvTxt = new JPanel();
    JButton addRmvExec = new JButton("Execute Add/Remove");
    JTextField menuOrInv = new JTextField("Menu or inventory", 10);
    JTextField inOrOut = new JTextField("Add or Remove", 8);
    JTextField subjID = new JTextField("item or ingredient ID", 12);
    JTextField menuVals = new JTextField("Menu Values (item, \'name\', \'price\', calories, carbs, fat, protein)", 35);
    JTextField invVals = new JTextField("Inventory Values (\'unitname\', \'ingredientid\', quantity, \'quantityunit\', priceperunit, totalprice, flevel)", 50);

    addRmv.setLayout(new BorderLayout());
    addRmv.setBounds(400, 600, 650, 200);
    addRmv.setVisible(false);

    addRmvTxt.add(menuOrInv);
    addRmvTxt.add(inOrOut);
    addRmvTxt.add(subjID);
    addRmvTxt.add(menuVals);
    addRmvTxt.add(invVals);

    addRmv.add(BorderLayout.CENTER, addRmvTxt);
    addRmv.add(BorderLayout.AFTER_LAST_LINE,addRmvExec);

    /***************************** ACTION LISTENERS *****************************/ 
    
    closeGUI.addActionListener(man_GUI);  // total close button
	  //auto order restock
	  bAuto.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
			"csce315901_9user", "shorts");
			//create connection
			for(int i = 0; i < autoOrders.size();i++){
				Statement stmt = conn.createStatement();
				int result = stmt.executeUpdate(autoOrders.get(i));
			}
			conn.close();
		}
		catch (Exception x) {
						x.printStackTrace();
						System.err.println(x.getClass().getName()+": "+x.getMessage());
						System.exit(0);
						}
      }
    });
    // Finance buttons: More, Less, & Search
    finMoreBttn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        finMore.setVisible(true);
      }
    });
    finLessBttn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        finMore.setVisible(false);
      }
    });
    finSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //Integer weekID = 2;
		    /*
        JTable newTbl = new JTable(allData.get(), saleCols);
        detailsTbl.removeAll();
        detailsTbl.add(BorderLayout.NORTH, newTbl.getTableHeader());
        detailsTbl.add(BorderLayout.CENTER, newTbl);*/
      }
    });
    invShow.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        invPopup.setVisible(true);
      }
    });
    invHide.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        invPopup.setVisible(false);
      }
    });
    menuUpdt.addActionListener(new ActionListener() {
      @ Override
      public void actionPerformed(ActionEvent e) {
        Connection updtConn = null;
        try {  // establish new connection
          Class.forName("org.postgresql.Driver");
          updtConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db", "csce315901_9user", "shorts");
        } catch (Exception connErr) {
          connErr.printStackTrace();
          System.err.println(connErr.getClass().getName()+": "+connErr.getMessage());
          System.exit(0);
        }

        for (int i = 0; i < menuRows.size(); ++i) {
          try{
            String updtMenu = "UPDATE menu SET ";
            for (int j = 1; j < menuCols.size(); ++j) {
              if (j <= 2) {
                updtMenu = updtMenu + menuCols.get(j) + "= \'" + menuTblInfo[i][j] + "\', ";
              } else {
                updtMenu = updtMenu + menuCols.get(j) + "=" + menuTblInfo[i][j] + ", ";
              }
            }
            // cut off last comma, append item stipulation
            updtMenu = updtMenu.substring(0, updtMenu.length()-2) + " WHERE item=" + menuTblInfo[i][0] + ";";
            Statement stmt = updtConn.createStatement();
            Integer menuUpdtRslt = stmt.executeUpdate(updtMenu);
            // System.out.println(updtMenu);
          }
          catch (Exception e2) {
            e2.printStackTrace();
            System.err.println("Menu Update: "+e2.getClass().getName()+": "+e2.getMessage());
            System.exit(0);
          }
        }
        try {
          updtConn.close();
          JOptionPane.showMessageDialog(null,"Menu Update Connection Closed.");
        } catch(Exception e3) {
          JOptionPane.showMessageDialog(null,"Menu Update Connection NOT Closed.");
        }
      }
    });
    invUpdt.addActionListener(new ActionListener() {
      @ Override
      public void actionPerformed(ActionEvent e) {
        Connection updtConn = null;
        try {  // establish new connection
          Class.forName("org.postgresql.Driver");
          updtConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db", "csce315901_9user", "shorts");
        } catch (Exception connErr) {
          connErr.printStackTrace();
          System.err.println(connErr.getClass().getName()+": "+connErr.getMessage());
          System.exit(0);
        }
        try{
          String updtInv = "UPDATE inventory SET ";
          String updtSubj = invUpdtSubj.getText();
          Integer itemInd = 0;
          for(int i = 0; i < invRows.length; ++i) {
            // locate the target item
            if (invRows[i][1].equals(updtSubj)) {
              itemInd = i;
              //break;
            }
          }
          for (int j = 0; j < invCols.size(); ++j) {
            String qlty = invCols.get(j);
            if (j == 1) {
              continue;
            } else if (qlty.equals("unitname") || qlty.equals("ingredientid") || qlty.equals("quantityunit") || qlty.equals("priceperunit")|| qlty.equals("totalprice")) {
              updtInv = updtInv + invCols.get(j) + "= \'" + invRows[itemInd][j] + "\', ";
            } else {
              updtInv = updtInv + invCols.get(j) + "=" + invRows[itemInd][j] + ", ";
            }
          }
          // cut off last comma, append item stipulation
          updtInv = updtInv.substring(0, updtInv.length()-2) + " WHERE ingredientid=\'" + updtSubj + "\';";
          Statement stmt = updtConn.createStatement();
          Integer invUpdtRslt = stmt.executeUpdate(updtInv);
          // System.out.println(updtInv);
        }
        catch (Exception e2) {
          e2.printStackTrace();
          System.err.println("Inventory Update: "+e2.getClass().getName()+": "+e2.getMessage());
          System.exit(0);
        }

        try {
          updtConn.close();
          JOptionPane.showMessageDialog(null,"Inventory Update Connection Closed.");
        } catch(Exception e3) {
          JOptionPane.showMessageDialog(null,"Inventory Update Connection NOT Closed.");
        }
      }
    });
    addRmvBttn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (addRmv.isVisible()) {
          addRmv.setVisible(false);
        } else {
          addRmv.setVisible(true);
        }
      }
    });
    addRmvExec.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Connection updtConn = null;
        try {  // establish new connection
          Class.forName("org.postgresql.Driver");
          updtConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db", "csce315901_9user", "shorts");
        } catch (Exception connErr) {
          connErr.printStackTrace();
          System.err.println(connErr.getClass().getName()+": "+connErr.getMessage());
          System.exit(0);
        }
        String table = menuOrInv.getText();  // should be menu or table
        String inOut = inOrOut.getText();    // should be add or remove
        String subj = subjID.getText();      // should be item or ingredient ID
        String addMenuVals = menuVals.getText();  // should be the values of the table in order
        String addInvVals = invVals.getText();
        try{
          String instr = "";  // instruction to execute
          if (inOut.equals("add") || inOut.equals("Add")) {
            // when adding to...
            if (table.equals("menu") || table.equals("Menu")) {
              // the menu
              instr = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'weeklysales';";
              Statement testStatement1 = updtConn.createStatement();
              ResultSet testResult1 = testStatement1.executeQuery(instr);
              
              testResult1.next();
              int count = Integer.parseInt(testResult1.getString("count")) - 3;
              instr = "ALTER TABLE weeklysales ADD COLUMN qnt_5" + count + " INT;"; // update weekly sales
              Statement testStatement2 = updtConn.createStatement();
              Integer testResult2 = testStatement2.executeUpdate(instr);

              instr = "INSERT INTO menu VALUES " + addMenuVals + " ;"; // update the menu


            } else if (table.equals("inventory") || table.equals("Inventory")) {
              // the inventory
              instr = "INSERT INTO inventory VALUES " + addInvVals + " ;";
            }
          }
          else if (inOut.equals("remove") || inOut.equals("Remove")) {
            // when removing from...
            if (table.equals("menu") || table.equals("Menu")) {
              // the menu
              instr = "DELETE FROM menu WHERE item = " + subj + " ;";
            } else if (table.equals("inventory") || table.equals("Inventory")) {
              // the inventory
              instr = "DELETE FROM inventory WHERE ingredientid = \'" + subj + "\';";
            }
          }
          Statement stmt = updtConn.createStatement();
          Integer addRmvRslt = stmt.executeUpdate(instr);
          System.out.println(instr);
        }
        catch (Exception e2) {
          e2.printStackTrace();
          System.err.println("Add/Remove: "+e2.getClass().getName()+": "+e2.getMessage());
          System.exit(0);
        }
        try {
          updtConn.close();
          JOptionPane.showMessageDialog(null,"Add/Remove Connection Closed.");
        } catch(Exception e3) {
          JOptionPane.showMessageDialog(null,"Add/Remove Connection NOT Closed.");
        }
      }
    });
	  //For restock
	  restockUpdt.addActionListener(new ActionListener() {
      @ Override
      public void actionPerformed(ActionEvent e) {
        RinvPanel.setVisible(true);
      }
    });

    /***************************** FRAME ASSEMBLY *****************************/
    f.setLayout(new BorderLayout());  // set frame style
    f.setSize(1000, 480);
    f.setVisible(true);

    JLabel frameLabel = new JLabel("Manager GUI");

    f.add(BorderLayout.BEFORE_FIRST_LINE, frameLabel);  // add overall GUI label
    f.add(BorderLayout.EAST, menuPanel);                // add menu panel to frame
    f.add(BorderLayout.WEST, finPanel);                 // add finances panel to frame
    f.add(BorderLayout.AFTER_LAST_LINE, frameBttns);    // add frame button set

    /***************************** CONNECTION CLOSURE *****************************/
    try {
    conn.close();
    JOptionPane.showMessageDialog(null,"Connection Closed.");
    } 
    catch(Exception e) {
    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }
  }
  /***************************** ACTION DEFINITIONS *****************************/
  public void actionPerformed(ActionEvent e) {
    // if button is pressed...
    String s = e.getActionCommand();
    if (s.equals("Close GUI")) {
      f.dispose();
    }
  }
}

/*
Source for the following: https://stackoverflow.com/questions/20194806/how-to-get-a-list-column-names-and-datatypes-of-a-table-in-postgresql
SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'menu';
*/
