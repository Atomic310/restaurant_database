import java.util.*;
import java.sql.*;

/*
* Holds details for a specific ingredient within an order
*
* ingredientID: holds the ingredientID number for reference in the inventory db
* name: used to describe ingredient
* quantity: portionPerInventoryUnit for this ingredient
*   quantity is constructed based on user input or db initialization
*/
public class ingredient {
    String ingredientID = "";
    String name = "";
    int count = 0;
    Double quantity = 0.00;
  
    public ingredient (String i, String n, int c, Double q) {
      ingredientID = i;
      name = n;
      count = c;
      quantity = q;
    }
  }