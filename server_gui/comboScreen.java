import java.sql.*;
import java.awt.event.*;

import javax.lang.model.util.AbstractAnnotationValueVisitor14;
import javax.swing.*;
import java.awt.*; 
import java.util.*;

/*

call this GUI in main
i.e.
comboScreen myComboScreen = new comboScreen(501, conn, myCurrentOrder, combo1, f);

*/

public class comboScreen extends JPanel {

    // Function to add a string element to a string array
    public static String[] addX(int n, String arr[], String x) {
        // create a new array of size n+1
        String newarr[] = new String[n + 1];
  
        // insert the elements from
        // the old array into the new array
        // insert all elements till n
        // then insert x at n+1
        for (int i = 0; i < n; i++) {
            newarr[i] = arr[i];
        }
  
        newarr[n] = x;
  
        return newarr;
    }

    public comboScreen(order myOrder, currentOrder myCurrentOrder, JFrame f, DefaultListModel currmodel) {

    // combo popup
      JDialog comboPopup = new JDialog(f);
      
      // create data to be displayed

      // copy ingredients to leftData
      String[] leftData = new String[myOrder.ingredientList.size()];
      for (int i = 0; i < myOrder.ingredientList.size(); i++) {
        leftData[i] = myOrder.ingredientList.get(i).name + " (" + myOrder.ingredientList.get(i).count + ")";
      }

      // copy individual item orders to right table
      // 505, 508, 510, 512, 513, 514, 515, 516, 517
      // populate right Data with default items
      String[] rightData = {"Gallon of Tea", "Club Sandwich", "Sandwich", "Grill Cheese", "Layne's Sauce", "Chicken Finger", "Texas Toast", "Potato Salad", "Fries"};
      int n = 9;

      // get all menu keys from the database
      // for each menuKeyID that does not exist in the mainMenyKeys[]

      // connect to database
      Connection conn = null;
      try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
            "csce315901_9user", "shorts");
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }

      try {
        // create sql statement
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT item, name FROM menu;";
        ResultSet result = stmt.executeQuery(sqlStatement);

        // get all menu item names
        // get all menu key ID's
        while (result.next()) {
          int id = Integer.parseInt(result.getString("item"));
          String name = result.getString("name");

          // if there are items apove menuKeyID 519 (new element!)
          if (id > 519) {
            // add the name to the right data array and increment (n)
            rightData = addX(n, rightData, name);
            n++;
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error accessing Database.");
      }

      // close connection
      try {
        conn.close();
        // JOptionPane.showMessageDialog(null,"Connection Closed.");
        } catch(Exception g) {
        // JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }

      // create left list model
      DefaultListModel leftListModel;
      leftListModel = new DefaultListModel();

      // add elements to model 
      for (int i = 0; i < myOrder.ingredientList.size(); i++) {
          leftListModel.addElement(myOrder.ingredientList.get(i).name + " (Count: " + myOrder.ingredientList.get(i).count + ")");
      }
      
      JList leftList = new JList(leftListModel);
      leftList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      leftList.setLayoutOrientation(JList.VERTICAL);
      leftList.setVisibleRowCount(-1);
      JScrollPane leftListScroller = new JScrollPane(leftList);
      leftListScroller.setPreferredSize(new Dimension(250, 80));



      // create right list
      JList rightList = new JList(rightData);
      rightList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      rightList.setLayoutOrientation(JList.VERTICAL);
      rightList.setVisibleRowCount(-1);
      JScrollPane rightListScroller = new JScrollPane(rightList);
      rightListScroller.setPreferredSize(new Dimension(250, 80));

      // create buttons
      JButton leftAdd = new JButton("Add");
      JButton leftRemove = new JButton("Remove");
      JButton rightAdd = new JButton("Add");
      JButton orderComplete = new JButton("Add to Current Order");

      // drink section
      JLabel drinkLabel = new JLabel();
      String myString = "Select \"drink\" in the left list and select a flavor. <br> If \"drink\" is not selected when selecting a flavor, <br> a new drink will be added to the list. Please make <br> sure all drinks have a selected flavor before exiting.";
      drinkLabel.setText("<html>"+ myString + "</html>");
      JButton flavor1 = new JButton("Dr Jones");
      JButton flavor2 = new JButton("Orange & Cream");
      JButton flavor3 = new JButton("Root Beer");
      JButton flavor4 = new JButton("Cola");
      JButton flavor5 = new JButton("Lemon Lime");
      JButton flavor6 = new JButton("Sugar Free Cola");

      // create label
      JLabel comboPopupLbl = new JLabel(myOrder.name);

      // create left list panel
      JPanel leftListPane = new JPanel();
      leftListPane.setLayout(new BoxLayout(leftListPane, BoxLayout.PAGE_AXIS));
      JLabel leftLabel = new JLabel("Combo Contents");
      leftLabel.setLabelFor(leftList);
      leftListPane.add(leftLabel);
      leftListPane.add(Box.createRigidArea(new Dimension(0,5)));
      leftListPane.add(leftListScroller);
      leftListPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

      // create right list panel
      JPanel rightListPane = new JPanel();
      rightListPane.setLayout(new BoxLayout(rightListPane, BoxLayout.PAGE_AXIS));
      JLabel rightLabel = new JLabel("Extras");
      rightLabel.setLabelFor(rightList);
      rightListPane.add(rightLabel);
      rightListPane.add(Box.createRigidArea(new Dimension(0,5)));
      rightListPane.add(rightListScroller);
      rightListPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

      // create west panel
      JPanel comboWestPanel = new JPanel();  // contains combo buttons
      comboWestPanel.setLayout(new GridLayout(3, 1));

      // add elemets to west panel
      comboWestPanel.add(leftListPane);
      comboWestPanel.add(leftAdd);
      comboWestPanel.add(leftRemove);

      // create east panel
      JPanel comboEastPanel = new JPanel();
      comboEastPanel.setLayout(new GridLayout(3, 1));

      // add elements to east panel
      comboEastPanel.add(rightListPane);
      comboEastPanel.add(rightAdd);

      // create empty central panel
      JPanel comboCentralPanel = new JPanel();
      JPanel drinkLabelPanel = new JPanel();
      JPanel drinkFlavorPanel = new JPanel();
      drinkFlavorPanel.setLayout(new GridLayout(3,3));
      comboCentralPanel.setLayout(new GridLayout(2,1));
      comboCentralPanel.add(drinkLabelPanel);
      comboCentralPanel.add(drinkFlavorPanel);

      
      // if NOT family pack combo or custom meal
      if (myOrder.menuKeyID != 506 && myOrder.menuKeyID != 0) {
        // add drink buttons and label to center
        drinkLabelPanel.add(drinkLabel);
        drinkFlavorPanel.add(flavor1);
        drinkFlavorPanel.add(flavor2);
        drinkFlavorPanel.add(flavor3);
        drinkFlavorPanel.add(flavor4);
        drinkFlavorPanel.add(flavor5);
        drinkFlavorPanel.add(flavor6);
      }

      // add elements
      comboPopup.add(comboWestPanel, BorderLayout.WEST);
      comboPopup.add(comboEastPanel, BorderLayout.EAST);
      comboPopup.add(comboCentralPanel, BorderLayout.CENTER);
      comboPopup.add(comboPopupLbl, BorderLayout.NORTH);
      comboPopup.add(orderComplete, BorderLayout.SOUTH);

      // initialize to false
      comboPopup.setBounds(500, 300, 400, 300);
      comboPopup.setVisible(true);

      // (String n, String f, order o, JList list, DefaultListModel model)
      flavor1.addActionListener(new drinkSelector("Dr Jones", "d2013", myOrder, leftList, leftListModel));
      flavor2.addActionListener(new drinkSelector("Orange & Cream", "d2014", myOrder, leftList, leftListModel));
      flavor3.addActionListener(new drinkSelector("Root Beer", "d2015", myOrder, leftList, leftListModel));
      flavor4.addActionListener(new drinkSelector("Cola", "d2016", myOrder, leftList, leftListModel));
      flavor5.addActionListener(new drinkSelector("Lemon Lime", "d2017", myOrder, leftList, leftListModel));
      flavor6.addActionListener(new drinkSelector("Sugar Free Cola", "d2018", myOrder, leftList, leftListModel));

      // add action listener to orderComplete
      orderComplete.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

          Boolean missingDrink = false;

          // make sure that all drinks have a flavor before exiting !!
          for (int i = 0; i < myOrder.ingredientList.size(); i++) {
            if (myOrder.ingredientList.get(i).ingredientID.equals("drinkID")) {
              missingDrink = true;
              break;
            }
          }

          if (missingDrink == true) {
            JOptionPane.showMessageDialog(null,"Please make sure all drinks have a flavor selected.");
          } else {
              // add order to orderList
            myCurrentOrder.orderList.add(myOrder);

            // get index of order
            int index = myCurrentOrder.orderList.size() - 1;

            // update main menu list
            currmodel.addElement(myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")");//changes display
            
            // close screen
            comboPopup.setVisible(false);
          }
        }
      });

      // add action listener to leftAdd
      leftAdd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

          // determine if element is a rightList element
          if (leftList.getSelectedValue().toString().contains("*")) {
            // get index of this element in the main model
            int index = currmodel.indexOf(leftList.getSelectedValue().toString().substring(1));

            // increase the count for its respective quantity in orderList
            myCurrentOrder.orderList.get(index).quantity ++;

            // update the leftList model
            leftListModel.setElementAt("*" + myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")", leftList.getSelectedIndex());

            // update the main model
            currmodel.setElementAt(myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")", index);

          } else {
            // see what is selected
            int index = leftList.getSelectedIndex();

            // update ingredient count
            myOrder.ingredientList.get(index).count++;

            // set element at
            leftListModel.setElementAt(myOrder.ingredientList.get(index).name + " (Count: " + myOrder.ingredientList.get(index).count + ")", index);
          }
        }
      });

      // add action listener to rightAdd
      rightAdd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

          // see what is selected
          int index = rightList.getSelectedIndex();
          int menuKeyID = 0;
          
          if (index == 0) { // gallon of tea
            menuKeyID = 505;

          } else if (index == 1) { // club sandwich
            menuKeyID = 508;

          } else if (index == 2) { // sandwich
            menuKeyID = 510;

          } else if (index == 3) { // grill cheese
            menuKeyID = 512;

          } else if (index == 4) { // Layne's Sauce
            menuKeyID = 513;

          } else if (index == 5) { // Chicken Finger
            menuKeyID = 514;

          } else if (index == 6) { // Texas Toast
            menuKeyID = 515;

          } else if (index == 7) { // potato salad
            menuKeyID = 516;

          } else if (index == 8) { // fries
            menuKeyID = 517;

            // menu key id 518 and 519 are skipped because they are implemented elsewhere

          } else {
            menuKeyID = 517 + (index - 8) + 2; // any and all aditional menu items

          }

          Connection newConn = null;
          try {
            Class.forName("org.postgresql.Driver");
            newConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
               "csce315901_9user", "shorts");
          } catch (Exception g) {
            g.printStackTrace();
            System.err.println(g.getClass().getName()+": "+g.getMessage());
            System.exit(0);
          }

          // create a new order
          order newOrder = new order(menuKeyID, newConn);

          try {
            newConn.close();
            // JOptionPane.showMessageDialog(null,"Connection Closed.");
            } catch(Exception g) {
            // JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
          }

          // add order to left list
          leftListModel.addElement("*" + newOrder.name + " (Quantity: " + newOrder.quantity + ", Unit Price: " + newOrder.price + ")");

          // add order to orderlist
          myCurrentOrder.orderList.add(newOrder);

          // add order to main order
          currmodel.addElement(newOrder.name + " (Quantity: " + newOrder.quantity + ", Unit Price: " + newOrder.price + ")");
          
        }
      });

      // add action listener to leftRemove
      leftRemove.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

          // first see if it is a rightList element
          if (leftList.getSelectedValue().toString().contains("*")) { // removing a rightList element

            // get the index of this element in the main model
            int index = currmodel.indexOf(leftList.getSelectedValue().toString().substring(1));

            // update quantity for this order in the main structure
            myCurrentOrder.orderList.get(index).quantity --;

            // if count is less than or equal to 0
            if (myCurrentOrder.orderList.get(index).quantity <= 0) {
              // remove element from main structure
              myCurrentOrder.orderList.remove(index);

              // remove element from main table
              currmodel.remove(index);

              // remove element from left table
              leftListModel.remove(leftList.getSelectedIndex());

            } else { // otherwise update its quantity on the list
              // update the leftList model
              leftListModel.setElementAt("*" + myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")", leftList.getSelectedIndex());

              // update the main model
              currmodel.setElementAt(myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")", index);

            }

          } else { // removing a leftList element
              // get index
            int index = leftList.getSelectedIndex();

            // update value
            myOrder.ingredientList.get(index).count --;

            // if count is less than or equal to 0
            if (myOrder.ingredientList.get(index).count <= 0) {
              // remove ingredient from ingredient list
              myOrder.ingredientList.remove(index);

              // remove ingredient from the leftList Model
              leftListModel.remove(index);
            } else { // otherwise update its quantity on the list
              leftListModel.setElementAt(myOrder.ingredientList.get(index).name + " (Count: " + myOrder.ingredientList.get(index).count + ")", index);

            }
          }


        }
      });
    }
}


class drinkSelector implements ActionListener {
  String flavorID = "";
  String name = "";
  order myOrder;
  JList leftList;
  DefaultListModel leftListModel;

  public drinkSelector(String n, String f, order o, JList list, DefaultListModel model) {
    flavorID = f;
    myOrder = o;
    name = n;
    leftList = list;
    leftListModel = model;
  }

  public void actionPerformed(ActionEvent e) {

    // Jones-Dr Jones d2013
    // Jones-Orange&Cream d2014
    // Jomes-Root beer d2015
    // Jones-Cola d2016
    // Jones-Lemon Lime d2017
    // Jones-Sugar Free Cola d2018

    // Bottled Root Beer d2019
    // Bottled Cream Soda d2020
    // Bottled Orange & Cream d2021
    // Bottled Berry Lemondade d2022

    // make sure drink is selected in list
    int index = leftList.getSelectedIndex();

    if(myOrder.ingredientList.get(index).name.equals("drink")) { // drink is selected
      // update the drink's flavor ID and name
      index = leftList.getSelectedIndex();
      myOrder.ingredientList.get(index).ingredientID = flavorID;
      myOrder.ingredientList.get(index).name = name;

      // update its name in the table
      leftListModel.setElementAt(myOrder.ingredientList.get(index).name + " (Count: " + myOrder.ingredientList.get(index).count + ")", index);

    } else { // not selected
      // prompt user that program is creating a new drink entry since drink wasn't selected
      // 0.00416667 is defualt portionPerInventory for a 16oz drink

      // add it to the order
      ingredient newDrink = new ingredient(flavorID, name, 1, 0.00416667);
      myOrder.ingredientList.add(newDrink);

      // add it to the list model
      leftListModel.addElement(newDrink.name + " (Count: " + newDrink.count + ")");

    }

  }
}
