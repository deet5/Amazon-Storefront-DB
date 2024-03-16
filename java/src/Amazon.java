/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Amazon {

   // reference to physical database connection.
   private Connection _connection = null;

   // Keep track of the current user id
   private String currentUser = null;
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));

   /**
    * Creates a new instance of Amazon store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   }// end Amazon

   // Check if the input string is an integer
   public static boolean isInteger(String s) {
      try {
         Integer.parseInt(s);
      } catch (NumberFormatException e) {
         return false;
      } catch (NullPointerException e) {
         return false;
      }
      return true;
   }

   // Check if the input string is a double
   public static boolean isDouble(String s) {
      try {
         Double.parseDouble(s);
      } catch (NumberFormatException e) {
         return false;
      } catch (NullPointerException e) {
         return false;
      }
      return true;
   }

   // Get store ID from the user
   public static String getStoreID(Amazon esql) {
      String store_id = null;
      while (true) {
         System.out.print("\tEnter store id: ");
         try {
            store_id = in.readLine();
            if (!isInteger(store_id)) {
               System.out.println("Invalid input. Try again!");
               continue;
            }
            String query = String.format("SELECT * FROM Store WHERE storeid = %s", store_id);
            if (esql.executeQuery(query) == 0) {
               System.out.println("Store does not exist. Try again!");
               continue;
            }
            break;
         } catch (Exception e) {
            System.err.println(e.getMessage());
            continue;
         }
      }

      return store_id;
   }

   // Method to calculate euclidean distance between two latitude, longitude pairs.
   public double calculateDistance(double lat1, double long1, double lat2, double long2) {
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2);
   }

   /**
    * Method to execute an update SQL statement. Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the update instruction
      stmt.executeUpdate(sql);

      // close the instruction
      stmt.close();
   }// end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
         if (outputHeader) {
            for (int i = 1; i <= numCol; i++) {
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i = 1; i <= numCol; ++i)
            System.out.print(rs.getString(i) + "\t");
         System.out.println();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   }// end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
         for (int i = 1; i <= numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
      } // end while
      stmt.close();
      return result;
   }// end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      int rowCount = 0;

      // iterates through the result set and count nuber of results.
      while (rs.next()) {
         rowCount++;
      } // end while
      stmt.close();
      return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement();

      ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close();
         } // end if
      } catch (SQLException e) {
         // ignored.
      } // end try
   }// end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login
    *             file>
    */
   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println(
               "Usage: " +
                     "java [-classpath <classpath>] " +
                     Amazon.class.getName() +
                     " <dbname> <port> <user>");
         return;
      } // end if

      Greeting();
      Amazon esql = null;
      try {
         // use postgres JDBC driver.
         Class.forName("org.postgresql.Driver").newInstance();
         // instantiate the Amazon object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Amazon(dbname, dbport, user, "");

         boolean keepon = true;
         while (keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()) {
               case 1:
                  CreateUser(esql);
                  break;
               case 2:
                  authorisedUser = LogIn(esql);
                  break;
               case 9:
                  keepon = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }// end switch

            if (authorisedUser.trim().equalsIgnoreCase("Customer")) {
               boolean usermenu = true;
               while (usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Stores within 30 miles");
                  System.out.println("2. View Product List");
                  System.out.println("3. Place an Order");
                  System.out.println("4. View 5 recent orders");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        viewStores(esql);
                        break;
                     case 2:
                        viewProducts(esql);
                        break;
                     case 3:
                        placeOrder(esql);
                        break;
                     case 4:
                        viewRecentOrders(esql);
                        break;
                     case 9:
                        usermenu = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            } else if (authorisedUser.trim().equalsIgnoreCase("Manager")) {
               boolean usermenu = true;
               while (usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. Update Product");
                  System.out.println("2. View 5 recent Product Updates Info");
                  System.out.println("3. View 5 Popular Items");
                  System.out.println("4. View 5 Popular Customers");
                  System.out.println("5. Place Product Supply Request to Warehouse");

                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        updateProduct(esql);
                        break;
                     case 2:
                        viewRecentUpdates(esql);
                        break;
                     case 3:
                        viewPopularProducts(esql);
                        break;
                     case 4:
                        viewPopularCustomers(esql);
                        break;
                     case 5:
                        placeProductSupplyRequests(esql);
                        break;

                     case 9:
                        usermenu = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            } else if (authorisedUser.trim().equalsIgnoreCase("Admin")) {
               boolean usermenu = true;
               while (usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. Search User by User Name");
                  System.out.println("2. Update User");
                  System.out.println("3. Update Product");

                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        searchUserByName(esql);
                        break;
                     case 2:
                        updateUser(esql);
                        break;
                     case 3:
                        updateProduct(esql);
                        break;
                     case 9:
                        usermenu = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            }
         } // end while
      } catch (Exception e) {
         System.err.println(e.getMessage());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try {
            if (esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   }// end main

   public static void Greeting() {
      System.out.println(
            "\n\n*******************************************************\n" +
                  "              User Interface      	               \n" +
                  "*******************************************************\n");
   }// end Greeting

   /*
    * Reads the users choice given from the keyboard
    * 
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         } // end try
      } while (true);
      return input;
   }// end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Amazon esql) {
      try {
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");
         String latitude = in.readLine(); // enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: "); // enter long value between [0.0, 100.0]
         String longitude = in.readLine();

         String type = "Customer";

         String query = String.format(
               "INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name,
               password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println("User successfully created!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end CreateUser

   /*
    * Check log in credentials for an existing user
    * 
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Amazon esql) {
      try {
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         // Get the user type
         String userType = result.get(0).get(5);
         // Save the user id
         esql.currentUser = result.get(0).get(0);
         if (userNum > 0)
            return userType;
         return null;
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }// end

   // Rest of the functions definition go in here

   public static void viewStores(Amazon esql) {
   }

   public static void viewProducts(Amazon esql) {
   }

   public static void placeOrder(Amazon esql) {
   }

   public static void viewRecentOrders(Amazon esql) {
   }

   public static void updateProduct(Amazon esql) {
      try {
         // Get Store ID from the user
         String store_id = getStoreID(esql);

         String ifManager = String.format("SELECT type FROM Users WHERE userid = %s", esql.currentUser);
         List<List<String>> user_result = esql.executeQueryAndReturnResult(ifManager);
         if (user_result.get(0).get(0).trim().equals("Manager")) {

            // Check if the user is the manager of the store
            String storeID_query = String.format("SELECT managerid FROM Store WHERE storeid = %s", store_id);
            List<List<String>> result = esql.executeQueryAndReturnResult(storeID_query);
            if (!result.get(0).get(0).equals(esql.currentUser)) {
               System.out.println("You are not the manager of this store. Try again!");
               return;
            }
         }
         // Get product name from the user
         System.out.print("\tEnter product name: ");
         String product_name = in.readLine();

         // Check if the product exists in the store
         String product_query = String.format("SELECT * FROM Product WHERE storeid = %s AND productname = '%s'",
               store_id,
               product_name);
         if (esql.executeQuery(product_query) == 0) {
            System.out.println("Product does not exist in the store.");
            return;
         }

         // Get new price
         System.out.print("\tEnter new price: ");
         String new_price = in.readLine();
         if (!isDouble(new_price)) {
            System.out.println("Invalid input. Try again!");
            return;
         }

         // Get new quantity
         System.out.print("\tEnter new quantity: ");
         String new_quantity = in.readLine();
         if (!isInteger(new_quantity)) {
            System.out.println("Invalid input. Try again!");
            return;
         }

         // Update the product
         String update_query = String.format(
               "UPDATE Product SET priceperunit = %s, numberofunits = %s WHERE storeid = %s AND productname = '%s'",
               new_price,
               new_quantity, store_id, product_name);
         esql.executeUpdate(update_query);
         System.out.println("Product successfully updated!");

         // Insert into ProductUpdates
         String insert_query = String.format(
               "INSERT INTO ProductUpdates (managerID, storeid, productname, updatedOn) VALUES (%s, %s, '%s', CURRENT_DATE)",
               esql.currentUser, store_id, product_name);
         esql.executeUpdate(insert_query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewRecentUpdates(Amazon esql) {
      try {
         // Get Store ID from the user
         String store_id = getStoreID(esql);

         String valid_manager = String.format("SELECT managerid FROM Store WHERE storeid = %s", store_id);
         List<List<String>> result = esql.executeQueryAndReturnResult(valid_manager);
         if (!result.get(0).get(0).equals(esql.currentUser)) {
            System.out.println("You are not the manager of this store. Try again!");
            return;
         }

         String query = String.format(
               "SELECT * FROM ProductUpdates WHERE storeid = %s ORDER BY updatedOn DESC LIMIT 5",
               store_id);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewPopularProducts(Amazon esql) {
      try {
         // Get Store ID from the user
         String store_id = getStoreID(esql);

         String valid_manager = String.format("SELECT managerid FROM Store WHERE storeid = %s", store_id);
         List<List<String>> result = esql.executeQueryAndReturnResult(valid_manager);
         if (!result.get(0).get(0).equals(esql.currentUser)) {
            System.out.println("You are not the manager of this store. Try again!");
            return;
         }

         String query = String.format(
               "SELECT productname, SUM(unitsordered) AS total_units_sold FROM Orders WHERE storeid = %s GROUP BY productname ORDER BY total_units_sold DESC LIMIT 5",
               store_id);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewPopularCustomers(Amazon esql) {
      try {
         // Get Store ID from the user
         String store_id = getStoreID(esql);

         String valid_manager = String.format("SELECT managerid FROM Store WHERE storeid = %s", store_id);
         List<List<String>> result = esql.executeQueryAndReturnResult(valid_manager);
         if (!result.get(0).get(0).equals(esql.currentUser)) {
            System.out.println("You are not the manager of this store. Try again!");
            return;
         }

         String query = String.format(
               "SELECT customerid, COUNT(*) AS total_orders FROM Orders WHERE storeid = %s GROUP BY customerid ORDER BY total_orders DESC LIMIT 5",
               store_id);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void placeProductSupplyRequests(Amazon esql) {
      try {
         // Get Store ID from the user
         String store_id = getStoreID(esql);

         String valid_manager = String.format("SELECT managerid FROM Store WHERE storeid = %s", store_id);
         List<List<String>> result = esql.executeQueryAndReturnResult(valid_manager);
         if (!result.get(0).get(0).equals(esql.currentUser)) {
            System.out.println("You are not the manager of this store. Try again!");
            return;
         }

         // Get product name from the user
         System.out.print("\tEnter product name: ");
         String product_name = in.readLine();

         // Check if the product exists in the store
         String product_query = String.format("SELECT * FROM Product WHERE storeid = %s AND productname = '%s'",
               store_id,
               product_name);
         if (esql.executeQuery(product_query) == 0) {
            System.out.println("Product does not exist in the store.");
            return;
         }

         // Get quantity
         System.out.print("\tEnter quantity: ");
         String quantity = in.readLine();
         if (!isInteger(quantity)) {
            System.out.println("Invalid input. Try again!");
            return;
         }

         // Get the warehouse id
         System.out.print("\tEnter warehouse id: ");
         String warehouse_id = in.readLine();
         if (!isInteger(warehouse_id)) {
            System.out.println("Invalid input. Try again!");
            return;
         }

         // Check if the warehouse exists
         String warehouse_query = String.format("SELECT * FROM Warehouse WHERE warehouseid = %s", warehouse_id);
         if (esql.executeQuery(warehouse_query) == 0) {
            System.out.println("Warehouse does not exist. Try again!");
            return;
         }

         // Insert into ProductSupplyRequest
         String insert_query = String.format(
               "INSERT INTO ProductSupplyRequests (managerid, storeid, productname, unitsrequested, warehouseid) VALUES (%s, %s, '%s', %s, %s)",
               esql.currentUser, store_id, product_name, quantity, warehouse_id);
         esql.executeUpdate(insert_query);
         System.out.println("Product supply request successfully placed!");

         // Update the product
         String update_query = String.format(
               "UPDATE Product SET numberofunits = numberofunits + %s WHERE storeid = %s AND productname = '%s'",
               quantity, store_id, product_name);
         esql.executeUpdate(update_query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void searchUserByName(Amazon esql) {
      try {
         System.out.print("\tEnter user name: ");
         String user_name = in.readLine();

         String query = String.format("SELECT * FROM Users WHERE name = '%s'", user_name);
         if (esql.executeQuery(query) == 0) {
            System.out.println("User does not exist.");
            return;
         }
         esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void updateUser(Amazon esql) {
      try {
         System.out.print("\tEnter user id: ");
         String user_id = in.readLine();
         if (!isInteger(user_id)) {
            System.out.println("Invalid input. Try again!");
            return;
         }

         String query = String.format("SELECT * FROM Users WHERE userid = %s", user_id);
         if (esql.executeQuery(query) == 0) {
            System.out.println("User does not exist.");
            return;
         }

         System.out.print("\tEnter new name: ");
         String new_name = in.readLine();
         System.out.print("\tEnter new password: ");
         String new_password = in.readLine();
         System.out.print("\tEnter new latitude: ");
         String new_latitude = in.readLine();
         System.out.print("\tEnter new longitude: ");
         String new_longitude = in.readLine();

         String new_type = null;
         System.out.println("Choose user type:");
         System.out.println("1. Customer");
         System.out.println("2. Manager");
         System.out.println("3. Admin");
         switch (readChoice()) {
            case 1:
               new_type = "Customer";
               break;
            case 2:
               new_type = "Manager";
               break;
            case 3:
               new_type = "Admin";
               break;
            default:
               System.out.println("Unrecognized choice!");
               return;
         }

         String update_query = String.format(
               "UPDATE Users SET name = '%s', password = '%s', latitude = %s, longitude = %s, type = '%s' WHERE userid = %s",
               new_name, new_password, new_latitude, new_longitude, new_type, user_id);
         esql.executeUpdate(update_query);
         System.out.println("User successfully updated!");

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
}// end Amazon
