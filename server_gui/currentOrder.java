import java.util.*;
import java.sql.*;

/*
* Keeps track of currentOrder
*
* totalPrice: running total of order's price
* orderList: list of all orders within currentOrder
*/
public class currentOrder {
    Double totalPrice = 0.00;
    ArrayList<order> orderList = new ArrayList<order>();
}