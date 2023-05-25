import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*; 
import java.util.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
CSCE 315-901
Project 2, Group 9
Server GUI

Compile: javac *.java
Run:     java -cp ".;postgresql-42.2.8.jar" server_GUI
*/

public class server_GUI extends JFrame implements ActionListener {
    static JFrame f;
    
    public static void main(String[] args)
    {
      /******************** CONNECTION CONSTRUCTION ********************/
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
      JOptionPane.showMessageDialog(null,"Opened database successfully");

      /******************** GUI, FRAME, & PANEL INSTANTIATIONS ********************/
      // create GUI object
      server_GUI s = new server_GUI();  // server's database GUI
      currentOrder myCurrentOrder = new currentOrder(); // current order

      // create panels
      JPanel westPanel = new JPanel();  // contains combo buttons
      JPanel centerPanel = new JPanel();  // contains "new" and "drinks" button
      JPanel northPanel = new JPanel(); // contains header
      JPanel southPanel = new JPanel(); // contains close button
      JPanel eastPanel = new JPanel(); // contains current order information
	    //other panels
	    JPanel closePanel = new JPanel();
      JPanel orderPanel = new JPanel();
		
      // set grid layout on panels
      westPanel.setLayout(new GridLayout(3, 3));
      
      f = new JFrame("Server");  // main GUI frame

      /******************** COMBO PANEL INSTANTIATIONS ********************/
      JButton closeButton = new JButton("Close");  // close button
      
      // Combo Buttons
      JButton combo501 = new JButton("5 Finger Original");
      JButton combo502 = new JButton("4 Finger Original");
      JButton combo503 = new JButton("3 Finger Original");
      JButton combo504 = new JButton("Kids Meal");
      JButton combo506 = new JButton("Family Pack");
      JButton combo507 = new JButton("Club Sandwich Combo");
      JButton combo509 = new JButton("Sandwich Combo");
      JButton combo511 = new JButton("Grill Cheese Combo");

      JButton bdrinks = new JButton("Bottled Drinks");
      JButton bnew = new JButton("New");
      JButton bcomplete = new JButton("Order Complete");

      JLabel northLabel = new JLabel("Main Menu");

      /******************** COMBO POPUP ********************/
      DefaultListModel currmodel = new DefaultListModel();

      combo501.addActionListener(new comboOpener(501, currmodel, myCurrentOrder, f));
      combo502.addActionListener(new comboOpener(502, currmodel, myCurrentOrder, f));
      combo503.addActionListener(new comboOpener(503, currmodel, myCurrentOrder, f));
      combo504.addActionListener(new comboOpener(504, currmodel, myCurrentOrder, f));
      combo506.addActionListener(new comboOpener(506, currmodel, myCurrentOrder, f));
      combo507.addActionListener(new comboOpener(507, currmodel, myCurrentOrder, f));
      combo509.addActionListener(new comboOpener(509, currmodel, myCurrentOrder, f));
      combo511.addActionListener(new comboOpener(511, currmodel, myCurrentOrder, f));
      bnew.addActionListener(new comboOpener(0, currmodel, myCurrentOrder, f));

      /******************** DRINK PANEL INSTANTIATIONS ********************/
      JDialog drinkPopup = new JDialog(f);
      JLabel drinkPopLbl = new JLabel("Bottled Drinks");

      JButton flavor1 = new JButton("Bottled Root Beer");
      JButton flavor2 = new JButton("Bottled Cream Soda");
      JButton flavor3 = new JButton("Bottled Orange & Cream");
      JButton flavor4 = new JButton("Bottled Berry Lemonade");
      JButton drinkClose = new JButton("Add to Order");


      JPanel drinkTop = new JPanel();
      JPanel drinkMid = new JPanel();
      drinkMid.setLayout(new GridLayout(2,2));
      JPanel drinkBottom = new JPanel();

      drinkPopup.add(drinkTop);
      drinkPopup.add(drinkMid);
      drinkPopup.add(drinkBottom);

      drinkTop.add(drinkPopLbl);

      drinkMid.add(flavor1);
      drinkMid.add(flavor2);
      drinkMid.add(flavor3);
      drinkMid.add(flavor4);

      drinkBottom.add(drinkClose);

      drinkPopup.setBounds(500, 300, 400, 300);
      drinkPopup.setLayout(new GridLayout(3, 1));
      drinkPopup.setVisible(false);
	  
	  /******************** ACTION LISTENERS ********************/ 
      // add actionlistener to button
      closeButton.addActionListener(s);
      bdrinks.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          drinkPopup.setVisible(true);
        }
      });
	  //JButton flavor1 = new JButton("Bottled Root Beer");
      //JButton flavor2 = new JButton("Bottled Cream Soda");
      //JButton flavor3 = new JButton("Bottled Orange & Cream");
      //JButton flavor4 = new JButton("Bottled Berry Lemonade");
	  ///////////////////////////////Bottled Drinks//////////////////////////////////////////////////////////////////////
	  flavor1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
			drinkClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent y ){
					Connection conn = null;
					try{
						Class.forName("org.postgresql.Driver");
						conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
						"csce315901_9user", "shorts");
						order myorder = new order(519, conn);
						//ingredient
						myorder.ingredientList.get(0).ingredientID = "d2019";
						myorder.ingredientList.get(0).name = "Bottled Root Beer";
						myorder.name = "Bottled Root Beer";
						// add order to orderList
						myCurrentOrder.orderList.add(myorder);
						// get index of order
						int index = myCurrentOrder.orderList.size() - 1;
						currmodel.addElement("Bottled Root Beer" + " (Quantity:"+ myCurrentOrder.orderList.get(index).ingredientList.get(0).count +" , Unit Price: "+myCurrentOrder.orderList.get(index).price);
						//close DB
						conn.close();
					}
					catch (Exception x) {
						x.printStackTrace();
						System.err.println(x.getClass().getName()+": "+x.getMessage());
						System.exit(0);
						}
		
					  drinkPopup.setVisible(false);
				}
		  });
        }
      });
	  flavor2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
			drinkClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent y ){
					Connection conn = null;
					try{
						Class.forName("org.postgresql.Driver");
						conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
						"csce315901_9user", "shorts");
						order myorder = new order(519, conn);
						//ingredient
						myorder.ingredientList.get(0).ingredientID = "d2020";
						myorder.ingredientList.get(0).name = "Bottled Cream Soda";
						myorder.name = "Bottled Cream Soda";
						// add order to orderList
						myCurrentOrder.orderList.add(myorder);
						// get index of order
						int index = myCurrentOrder.orderList.size() - 1;
						currmodel.addElement("Bottled Cream Soda" + " (Quantity:"+ myCurrentOrder.orderList.get(index).ingredientList.get(0).count +" , Unit Price: "+myCurrentOrder.orderList.get(index).price);
						//close DB
						conn.close();
					}
					catch (Exception x) {
						x.printStackTrace();
						System.err.println(x.getClass().getName()+": "+x.getMessage());
						System.exit(0);
						}
		
					  drinkPopup.setVisible(false);
				}
		  });
        }
      });
	  flavor3.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
			drinkClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent y ){
					Connection conn = null;
					try{
						Class.forName("org.postgresql.Driver");
						conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
						"csce315901_9user", "shorts");
						order myorder = new order(519, conn);
						//ingredient
						myorder.ingredientList.get(0).ingredientID = "d2021";
						myorder.ingredientList.get(0).name = "Bottled Orange & Cream";
						myorder.name = "Bottled Cream Soda";
						// add order to orderList
						myCurrentOrder.orderList.add(myorder);
						// get index of order
						int index = myCurrentOrder.orderList.size() - 1;
						currmodel.addElement("Bottled Orange & Cream" + " (Quantity:"+ myCurrentOrder.orderList.get(index).ingredientList.get(0).count +" , Unit Price: "+myCurrentOrder.orderList.get(index).price);
						//close DB
						conn.close();
					}
					catch (Exception x) {
						x.printStackTrace();
						System.err.println(x.getClass().getName()+": "+x.getMessage());
						System.exit(0);
						}
		
					  drinkPopup.setVisible(false);
				}
		  });
        }
      });
	  flavor4.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
			drinkClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent y ){
					Connection conn = null;
					try{
						Class.forName("org.postgresql.Driver");
						conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
						"csce315901_9user", "shorts");
						order myorder = new order(519, conn);
						//ingredient
						myorder.ingredientList.get(0).ingredientID = "d2022";
						myorder.ingredientList.get(0).name = "Bottled Berry Lemonade";
						myorder.name = "Bottled Cream Soda";
						// add order to orderList
						myCurrentOrder.orderList.add(myorder);
						// get index of order
						int index = myCurrentOrder.orderList.size() - 1;
						currmodel.addElement("Bottled Berry Lemonade" + " (Quantity:"+ myCurrentOrder.orderList.get(index).ingredientList.get(0).count +" , Unit Price: "+myCurrentOrder.orderList.get(index).price);
						//close DB
						conn.close();
					}
					catch (Exception x) {
						x.printStackTrace();
						System.err.println(x.getClass().getName()+": "+x.getMessage());
						System.exit(0);
						}
		
					  drinkPopup.setVisible(false);
				}
		  });
        }
      });
	
      /******************** JList ********************/
	  
      //create the array
      String[] ordernames = new String[myCurrentOrder.orderList.size()];
      for(int i = 0; i < myCurrentOrder.orderList.size(); i++){
        currmodel.addElement(myCurrentOrder.orderList.get(i).name + " "+ myCurrentOrder.orderList.get(i).quantity);
      }
      //instantiate list
	    JList ordermain = new JList(currmodel);//list w/ current orders
	    ordermain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ordermain.setLayoutOrientation(JList.VERTICAL);
	    ordermain.setVisibleRowCount(-1);
      //for scrolling
      JScrollPane listScroller = new JScrollPane(ordermain);
	    listScroller.setPreferredSize(new Dimension(250, 80));
      //buttons
      JButton orderadd = new JButton("Add");
      JButton orderdelete = new JButton("Delete");
      
      // action listener for add
      orderadd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          //get index
          int index = ordermain.getSelectedIndex();
          //1. update value 2. add
          myCurrentOrder.orderList.get(index).quantity++;//increment quantity
          currmodel.setElementAt(myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: $" + myCurrentOrder.orderList.get(index).price + ")", index);//changes display
        }  
      });

      // action listener for delete
			orderdelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//get index
					int index = ordermain.getSelectedIndex();
					//1. update value 2. add
					myCurrentOrder.orderList.get(index).quantity--;//increment quantity

          // if quantity is less than or equal to 0
          if (myCurrentOrder.orderList.get(index).quantity <= 0) {
            // remove order from orderList
            myCurrentOrder.orderList.remove(index);

            // remove order from currmodel
            currmodel.remove(index);

          } else { // otherwsie update its quantity on the list
            currmodel.setElementAt(myCurrentOrder.orderList.get(index).name + " (Quantity: " + myCurrentOrder.orderList.get(index).quantity + ", Unit Price: " + myCurrentOrder.orderList.get(index).price + ")", index);//changes display
          }
				}
			});
		
		//action listener for order complete
		bcomplete.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ////////subtract current total order from inventory database///////
			//for all indexed in the JList
			for(int j = 0; j < myCurrentOrder.orderList.size(); j++){
				//System.out.println(myCurrentOrder.orderList.get(j).date);
				//for each item in ingredientList, put a statements to subtract quantity by 1 in an array
				String[] ingrSubStatements = new String[myCurrentOrder.orderList.get(j).ingredientList.size()];
				for(int i = 0; i < myCurrentOrder.orderList.get(j).ingredientList.size(); i++){
					//decrease quantity by count
					ingrSubStatements[i] = "UPDATE inventory SET quantity = quantity - "+ myCurrentOrder.orderList.get(j).ingredientList.get(i).count +" WHERE ingredientid=\'"+myCurrentOrder.orderList.get(j).ingredientList.get(i).ingredientID+"\';";
				}
				Connection conn = null;
				try{
					Class.forName("org.postgresql.Driver");
					conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
					"csce315901_9user", "shorts");
					//execute in DB
					for(int i = 0; i < myCurrentOrder.orderList.get(j).ingredientList.size(); i++){
						//System.out.println(ingrSubStatements[i]);
						Statement stmt = conn.createStatement();
						int result = stmt.executeUpdate(ingrSubStatements[i]);
					}
					/////////increase money to weeklysales depending on menuKeyID
					/*
					*/
					//update total sales
					Statement stmt2 = conn.createStatement();
					String wksales = "UPDATE weeklysales SET daily_total = daily_total + "+myCurrentOrder.orderList.get(j).price+" WHERE dates = '"+myCurrentOrder.orderList.get(j).date+"';";
					int res = stmt2.executeUpdate(wksales);
					//update number of items sold
					stmt2 = conn.createStatement();
					wksales = "UPDATE weeklysales SET qnt_"+myCurrentOrder.orderList.get(j).menuKeyID+" = qnt_"+myCurrentOrder.orderList.get(j).menuKeyID+" + 1 WHERE dates = '"+myCurrentOrder.orderList.get(j).date+"';";
					res = stmt2.executeUpdate(wksales);
					//close DB
					conn.close();
				}
				catch (Exception x) {
					x.printStackTrace();
					System.err.println(x.getClass().getName()+": "+x.getMessage());
					System.exit(0);
					}
			}
			currmodel.clear();
			myCurrentOrder.orderList.clear();
        }
      });
      
      /******************** PANEL CONSTRUCTION ********************/
      // Add objects to panels

      // southPanel
      closePanel.add(closeButton);  // close button -> frame

      // westPanel: combos
      westPanel.add(combo501);
      westPanel.add(combo502);
      westPanel.add(combo503);
      westPanel.add(combo504);
      westPanel.add(combo506);
      westPanel.add(combo507);
      westPanel.add(combo509);
      westPanel.add(combo511);
      westPanel.add(bnew);

      // centerPanel: 

      // eastPanel
      orderPanel.add(listScroller);
      eastPanel.add(orderPanel);
	    eastPanel.add(orderadd);
	    eastPanel.add(orderdelete);

	    // southPanel
      southPanel.add(bdrinks);
      southPanel.add(bcomplete);
      closePanel.add(closeButton);  // close button -> frame
	    southPanel.add(closePanel);

      // northPanel
      northPanel.add(northLabel);

      /******************** FRAME CONSTRUCTION ********************/
      f.add(westPanel, BorderLayout.WEST); // combos 
      f.add(northPanel, BorderLayout.NORTH); // header
      f.add(southPanel, BorderLayout.SOUTH); // close button
      f.add(eastPanel, BorderLayout.EAST); // current order
      f.add(centerPanel, BorderLayout.CENTER); // drinks and new 

      westPanel.setLayout(new GridLayout(3, 3, 20, 25)); 
      
      // set the size of frame
      f.setSize(600, 600);
      f.setVisible(true);

      /******************** CONNECTION CLOSURE ********************/
      try {
      conn.close();
      JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
      JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
  }
  
  
  /******************** ACTION DEFINITIONS ********************/
  public void actionPerformed(ActionEvent e)
  {  // if button is pressed...
      String s = e.getActionCommand();
      if (s.equals("Close")) {
        f.dispose();
      } 
  }
}

class comboOpener implements ActionListener {
  int number;
  DefaultListModel currmodel;
  currentOrder myCurrentOrder;
  JFrame f;

  public comboOpener(int n, DefaultListModel m, currentOrder o, JFrame frame) {
      number = n;
      currmodel = m;
      myCurrentOrder = o;
      f = frame;
  }

  public void actionPerformed(ActionEvent e) {
    // initialize connection
    Connection thisConn = null;
    try {
      Class.forName("org.postgresql.Driver");
      thisConn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315901_9db",
         "csce315901_9user", "shorts");
    } catch (Exception g) {
      g.printStackTrace();
      System.err.println(g.getClass().getName()+": "+g.getMessage());
      System.exit(0);
    }

    // create an order
    order myOrder = new order(number, thisConn);

    // create a combo screen and pass in myOrder
    new comboScreen(myOrder, myCurrentOrder, f, currmodel);
      // visability is managed within this constructor

    // close connection
    try {
      thisConn.close();
      // JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception g) {
      // JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }
  }
}

/*
SOME SOURCES:
  Swapping Between Frames:
    https://stackoverflow.com/questions/30369197/switch-between-multiple-jframes
  Making Pop-ups
    https://www.delftstack.com/howto/java/java-pop-up-window/
  JLists
    https://docs.oracle.com/javase/tutorial/uiswing/components/list.html

*/
