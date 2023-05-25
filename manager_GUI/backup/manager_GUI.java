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

    // Vector<String> salesCols = new Vector<String>();
    // Integer salesColCnt = 0;

    // Vector<String> unitConvCols = new Vector<String>();

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
    Vector<String[]> menuInfo = new Vector<String[]>();
    Integer menuColCnt = 0;
    Integer menuRowCnt = 0;
    
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
      String[] menuEntry = new String[menuColCnt];
      while (result.next()) {  //....................... get table information
        name += result.getString("name")+"\n";
        for (int i = 0; i < menuColCnt; ++i) {
          menuEntry[i] = result.getString(menuCols.get(i));
        }
        menuInfo.add(menuEntry);
      }
      // SOURCE: https://stackoverflow.com/questions/8292256/get-number-of-rows-returned-by-resultset-in-java
      menuRowCnt = result.getRow();  // get row count, # of menu items
      
    } catch (Exception e){
      JOptionPane.showMessageDialog(null,"Catch 1: Error accessing Database.");
    }

    /**************************************** GUI & FRAME INSTANTIATIONS ****************************************/

    manager_GUI man_GUI = new manager_GUI();     // manager's database GUI
    f = new JFrame("Manager GUI");               // main GUI frame

    JPanel frameBttns = new JPanel(new BorderLayout());  // panel for all final buttons
    JButton closeGUI = new JButton("Close GUI");      // closing button
    JButton invShow = new JButton("Show Inventory");  // inventory button
    JButton addRmvBttn = new JButton("Add/Remove");    // add/remove button

    frameBttns.add(addRmvBttn);
    frameBttns.add(invShow);
    frameBttns.add(BorderLayout.AFTER_LAST_LINE, closeGUI);

    // JButton testPrint = new JButton("PRINT");
    // frameBttns.add(testPrint);

    /************************************** GENERAL FINANCES PANEL ***************************************/

    JPanel finPanel = new JPanel();  // finance panel
    JLabel finPanLabel = new JLabel("Finances");
    JButton finMore = new JButton("More Finances");
    
    finPanel.setLayout(new BorderLayout());  // set finance panel layout
    finPanel.add(BorderLayout.NORTH, finPanLabel);
    finPanel.add(BorderLayout.SOUTH, finMore);

    // -------------------------------- Getting all week IDs --------------------------------

    String getWeekIDs = "SELECT weekid FROM WeeklySales WHERE day=\'Sunday\';";  // find all the listed week numbers
    Integer currWeek = 0;

    try{
      Statement stmt = conn.createStatement();
      ResultSet rsltWeekIDs = stmt.executeQuery(getWeekIDs);
      // System.out.println(weekNums);
    
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
      // System.out.println(recapRslt);
      // blank 7-element array to represent weekly recap
      String[][] weeklyRecap = { {"", ""}, {"", ""},
        {"", ""}, {"", ""}, {"", ""}, 
        {"", ""}, {"", ""}};
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

    /**************************************** DETAILED FINANCE PANEL ****************************************/

    JDialog finDetailPanel = new JDialog(f);
    JLabel finDetailLbl = new JLabel("Detailed Finances");
    JButton finLess = new JButton("Less");

    // -------------------------------- Time Input --------------------------------

    JPanel timeframe = new JPanel();
    JLabel timeframeLbl = new JLabel("Week Number?");
    JTextArea timeInput = new JTextArea(Integer.toString(currWeek));
    JButton finSearch = new JButton("Search");
    // JTextArea timeStart = new JTextArea();
    // JTextArea timeEnd = new JTextArea();
    
    timeInput.setSize(100, 100);
    timeframe.add(timeframeLbl);
    timeframe.add(timeInput);
    // timeframe.add(timeStart);
    // timeframe.add(timeEnd);
    timeframe.add(finSearch);

    // -------------------------------- Detailed Finances --------------------------------

    JPanel details = new JPanel();
    JPanel detailsTbl = new JPanel();
    JLabel detailsLbl = new JLabel("Details");

    detailsTbl.setLayout(new BorderLayout());

    details.add(BorderLayout.NORTH, detailsLbl);
    details.add(BorderLayout.CENTER, detailsTbl);

    String[] colDetails = {"Weekday","501 sold","502 sold","503 sold","504 sold","505 sold",
    "506 sold","507 sold","508 sold","509 sold","510 sold","511 sold","512 sold","513 sold",
    "514 sold","515 sold","516 sold","517 sold","518 sold","519 sold","Daily Total"};

    String getDetails = "SELECT day, qnt_501, qnt_502, qnt_503, qnt_504, qnt_505, qnt_506, qnt_507, qnt_508, qnt_509, qnt_510, ";
    getDetails = getDetails + "qnt_510, qnt_511, qnt_512, qnt_513, qnt_514, qnt_515, qnt_516, qnt_517, qnt_518, qnt_519, daily_total ";

    Vector<String[][]> allData = new Vector<String[][]>();

    for(int a = 1; a <= currWeek; ++a) {
      try{
        String getWeekDetails = getDetails + "FROM WeeklySales WHERE weekid=" + a + ";";
        Statement stmt = conn.createStatement();
        ResultSet detailRslt = stmt.executeQuery(getWeekDetails);
        // System.out.println(detailRslt);
        // blank 7-element array to represent weekly recap
        String[][] weekDetails = {
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}
        };
        Integer j = 0;
        while(detailRslt.next()) {
          weekDetails[j][0] = detailRslt.getString("day");
          for (int t = 1; t <=19; ++t) {
            String qntStr = "qnt_" + Integer.toString(500+t);  // get item ID #
            weekDetails[j][t] = detailRslt.getString(qntStr);
          }
          weekDetails[j][20] = detailRslt.getString("daily_total");
          j = j + 1;
        }
        allData.add(weekDetails);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.err.println("Detailed Finances: " + e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
    }

    JTable weekTbl = new JTable(allData.get(currWeek-1), colDetails);
    detailsTbl.add(BorderLayout.NORTH, weekTbl.getTableHeader());
    detailsTbl.add(BorderLayout.CENTER, weekTbl);

    // -------------------------------- Construct Detailed Finances Panel --------------------------------

    finDetailPanel.setLayout(new BorderLayout());
    finDetailPanel.setBounds(500, 500, 900, 500);
    finDetailPanel.setVisible(false);

    finDetailPanel.add(BorderLayout.NORTH, finDetailLbl);  // panel label
    finDetailPanel.add(BorderLayout.NORTH, timeframe);      // timeframe space
    finDetailPanel.add(BorderLayout.CENTER, details);      // week's details
    finDetailPanel.add(BorderLayout.SOUTH, finLess);        // escape button

    /***************************** INVENTORY PANEL *****************************/

    JDialog invPanel = new JDialog(f);
    JLabel invPanLbl = new JLabel("Inventory");
    JButton invHide = new JButton("Hide Inventory");

    // -------------------------------- Menu --------------------------------

    JPanel menuPanel = new JPanel();
    JLabel menuLbl = new JLabel("Menu");
    JButton menuUpdt = new JButton("Update Menu");
    menuPanel.setLayout(new BorderLayout());

    String[][] menuTblInfo = new String[menuInfo.size()][menuInfo.get(0).length];  // initialize object array to match vector of values
    for (Integer i = 0 ; i < menuInfo.size(); ++i) {
      menuTblInfo[i] = menuInfo.get(i);
    }

    JTable menuTbl = new JTable(menuTblInfo, menuCols.toArray());
    menuPanel.add(BorderLayout.BEFORE_FIRST_LINE, menuLbl);
    menuPanel.add(BorderLayout.CENTER, new JScrollPane(menuTbl));
    menuPanel.add(BorderLayout.AFTER_LAST_LINE, menuUpdt);

    // -------------------------------- Expanded Inventory --------------------------------

    // Vector<String> invCols = new Vector<String>();
    // Vector<String[]> invInfo = new Vector<String[]>();
    // Integer invColCnt = 0;

    JPanel invInfo = new JPanel();
    JLabel invInfoLbl = new JLabel("Inventory Information");
    invInfo.setLayout(new BorderLayout());

    String getInv = "SELECT unitname, ingredientid, quantity, quantityunit, priceperunit, totalprice FROM inventory;";

    String[] invTblHeaders = {"unitname", "ingredientid", "quantity", "quantityunit", "priceperunit", "totalprice"};
    Vector<String[]> invInfoVec = new Vector<String[]>();  // vector of all table information
    try{
      Statement stmt = conn.createStatement();
      ResultSet invRslt = stmt.executeQuery(getInv);
      // System.out.println(itemRslt);
      // blank 7-element array to represent weekly recap
      while (invRslt.next()) {
        String[] infoIn = new String[6];
        for (int i = 0; i < 6; ++i) {
          infoIn[i] = invRslt.getString(invTblHeaders[i]);
        }
        invInfoVec.add(infoIn);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("Expanded Inventory: " + e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

    String[][] invTblInfo = new String[invInfoVec.size()][invInfoVec.get(0).length];  // initialize object array to match vector of values
    for (Integer i = 0 ; i < invInfoVec.size(); ++i) {
      invTblInfo[i] = invInfoVec.get(i);
    }

    JTable invTbl = new JTable(invTblInfo, invTblHeaders);
    invInfo.add(BorderLayout.BEFORE_FIRST_LINE, invInfoLbl);
    invInfo.add(BorderLayout.NORTH, invTbl.getTableHeader());
    invInfo.add(BorderLayout.CENTER, invTbl);

    // -------------------------------- Inventory Updating --------------------------------

    JButton invUpdt = new JButton("Update Inventory");
    JTextField invUpdtSubj = new JTextField(20);
    //invUpdtSubj.append("Item to update.");

    invInfo.add(BorderLayout.SOUTH, invUpdt);
    invInfo.add(BorderLayout.AFTER_LINE_ENDS, invUpdtSubj);

    // -------------------------------- Final Inventory Panel Construction  --------------------------------

    invPanel.setLayout(new BorderLayout());
    invPanel.setBounds(500, 500, 1500, 500);
    invPanel.setVisible(false);

    invPanel.add(BorderLayout.BEFORE_FIRST_LINE, invPanLbl);
    invPanel.add(BorderLayout.CENTER, invInfo);
    invPanel.add(BorderLayout.AFTER_LAST_LINE, invHide);
  
    /***************************** ADD/REMOVE PANEL *****************************/
  
    JDialog addRmv = new JDialog(f);
    JCheckBox inOut = new JCheckBox();
    //invUpdtSubj.append("Item to update.");

    addRmv.add(BorderLayout.WEST, inOut);
    // addRmv.add();
    
    /******************** ACTION LISTENERS ********************/ 
    
    closeGUI.addActionListener(man_GUI);  // total close button

    // Finance buttons: More, Less, & Search
    finMore.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        finDetailPanel.setVisible(true);
      }
    });
    finLess.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        finDetailPanel.setVisible(false);
      }
    });
    finSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Integer weekID = Integer.parseInt(timeInput.getText());
        JTable newTbl = new JTable(allData.get(weekID-1), colDetails);
        detailsTbl.removeAll();
        detailsTbl.add(BorderLayout.NORTH, newTbl.getTableHeader());
        detailsTbl.add(BorderLayout.CENTER, newTbl);
      }
    });
    invShow.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        invPanel.setVisible(true);
      }
    });
    invHide.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        invPanel.setVisible(false);
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

        for (int i = 0; i < 19; ++i) {
          Integer currItem = 501 + i;
          try{
            String updtMenu = "UPDATE menu SET ";
            for (int j = 1; j < menuCols.size(); ++j) {
              updtMenu = updtMenu + menuCols.get(j) + "= \'" + menuTblInfo[i][j] + "\', ";
            }
            updtMenu = updtMenu + "WHERE item=" + currItem + ";";
            Statement stmt = updtConn.createStatement();
            Integer menuUpdtRslt = stmt.executeUpdate(updtMenu);
            // System.out.println(updtMenuInstr);
          }
          catch (Exception e2) {
            e2.printStackTrace();
            System.err.println(e2.getClass().getName()+": "+e2.getMessage());
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
          String updtSubj = invUpdtSubj.getText();
          Integer itemInd = 0;
          for(int i = 0; i < invTblInfo.length; ++i) {
            // locate the target item
            //System.out.println(invTblInfo[i][1]);
            //System.out.println(invTblInfo.length);
            if (invTblInfo[i][1].equals(updtSubj)) {
              // System.out.println(updtSubj);
              // System.out.println(invTblInfo[i][1]);
              itemInd = i;
              //break;
            }
          }

          String updtInv = "UPDATE inventory SET unitname=\'" + invTblInfo[itemInd][0] + "\', ";
          updtInv = updtInv + "quantity=" + invTblInfo[itemInd][2] + ", quantityunit=\'" + invTblInfo[itemInd][3] + "\', ";
          updtInv = updtInv + "priceperunit=" + invTblInfo[itemInd][4] + ", totalprice=" + invTblInfo[itemInd][5];
          updtInv = updtInv + " WHERE ingredientid=\'" + updtSubj + "\';";
          
          Statement stmt = updtConn.createStatement();
          Integer invUpdtRslt = stmt.executeUpdate(updtInv);
          // System.out.println(updtInv);
        }
        catch (Exception e2) {
          e2.printStackTrace();
          System.err.println(e2.getClass().getName()+": "+e2.getMessage());
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

    // testPrint.addActionListener(new ActionListener() {
    //   @Override
    //   public void actionPerformed(ActionEvent e) {
    //     System.out.println(menuInfo[0][0]);
    //   }
    // });
  
    /******************** FRAME CONSTRUCTION ********************/
    // set frame style
    f.setLayout(new BorderLayout()); 
    f.setSize(700, 700);
    f.setVisible(true);

    JLabel frameLabel = new JLabel("Manager GUI");

    f.add(BorderLayout.BEFORE_FIRST_LINE, frameLabel);  // add overall GUI label
    f.add(BorderLayout.EAST, menuPanel);                // add menu panel to frame
    f.add(BorderLayout.WEST, finPanel);                 // add finances panel to frame
    f.add(BorderLayout.AFTER_LAST_LINE, frameBttns);    // add frame button set

    /******************** CONNECTION CLOSURE ********************/
    try {
    conn.close();
    JOptionPane.showMessageDialog(null,"Connection Closed.");
    } 
    catch(Exception e) {
    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }
  }
  /******************** ACTION DEFINITIONS ********************/
  public void actionPerformed(ActionEvent e) {
    // if button is pressed...
    String s = e.getActionCommand();
    if (s.equals("Close GUI")) {
      f.dispose();
    }
  }
}

/*

// -------------------------------- Getting number of Days --------------------------------
    String getDayCount = "SELECT count(*) FROM WeeklySales;";  // find all the listed week numbers
    Integer dayCount = 0;

    try{
      Statement stmt = conn.createStatement();
      ResultSet rsltDayCount = stmt.executeQuery(getWeekIDs);
      rsltDayCount.next();
      dayCount = rsltDayCount.getInt(1);
      // System.out.println("Day Count: ");
      // System.out.println(dayCount);
      rsltDayCount.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

Inventory:
- make a list of values to compare current stock amounts to
- if one is below the limit, add it to the displayed inventory
- display how much you currently have of each item you need to restock
- add the ability to change these limits later, maybe based on the day

Source for the following: https://stackoverflow.com/questions/20194806/how-to-get-a-list-column-names-and-datatypes-of-a-table-in-postgresql
SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'menu';


    Vector<String> menuCols = new Vector<String>();
    Vector<String> invCols = new Vector<String>();
    Vector<String> salesCols = new Vector<String>();
    Vector<String> unitConvCols = new Vector<String>();

    Integer menuColCnt = 0;
    Integer invColCnt = 0;
    Integer salesColCnt = 0;

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
    try{
      Statement stmt = conn.createStatement();    //......... create a statement object
      String sqlStatement = "SELECT * FROM menu;";    //..... create an SQL statement
      ResultSet result = stmt.executeQuery(sqlStatement);  // send statement to DBMS
      while (result.next()) {
        name += result.getString("name")+"\n";
      }
      // ------------------------ Characterize Menu ------------------------
      // Statement stmt2 = conn.createStatement();
      sqlStatement = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = \'menu\'";
      //send statement to DBMS
      result = stmt.executeQuery(sqlStatement);
      Integer i = 0;
      while (result.next()) {
        menuCols.add(result.getString(i));
        ++i;
      }
      menuColCnt = i;
      // ------------------------ Characterize Inventory ------------------------
      // Statement stmt3 = conn.createStatement();
      sqlStatement = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = \'inventory\'";
      //send statement to DBMS
      result = stmt.executeQuery(sqlStatement);
      i = 0;
      while (result.next()) {
        invCols.add(result.getString(i));
        ++i;
      }
      invColCnt = i;
      // // ------------------------ Characterize Sales ------------------------
      // Statement stmt4 = conn.createStatement();
      // sqlStatement = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = \'weeklysales\'";
      // //send statement to DBMS
      // result = stmt.executeQuery(sqlStatement);
      // i = 0;
      // while (result.next()) {
      //   menuCols.add(result.getString(i));
      //   ++i;
      // }
      // salesColCnt = i;
    } catch (Exception e){
      JOptionPane.showMessageDialog(null,"Error accessing Database.");
    }



*/