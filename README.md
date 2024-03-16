# CS 166 Project Report

## Group Information

Group #65

| Name | NetID |
|---|---|
| [Adlai Morales-Bravo](https://github.com/AdlaiMB) | |
| [Denis Melnikov](https://github.com/deet5) | **dmeln003** | 

## Implementation Description

You can create a new account using `Create user`. By default every new user has the type `Customer` which can later be modified by `Admin`. When the account is created you can log in using `Log in` option. Depending on the user type you have different menu options. For example, if your user type is `Customer`, you can view stores within 30 miles, view product list, place an order or view 5 recent orders. Here is the interface flow to better understand what options are available:

> insert image here

### Queries

#### Return tuples with a given store ID.
```
SELECT *
FROM Store
WHERE storeid = <storeID>;
```
This query is to validate the store ID input.

#### Add new user.
```
INSERT INTO USERS (name, password, latitude, longitude, type) VALUES (<name>, <password>, <latitude>, <longitude>, <type>);
```
This query is for `Create User` menu option.

#### Find existing user.
```
SELECT *
FROM USERS
WHERE name = <name> AND password = <password>;
```
This query is for `Log in` menu option.

#### Return the user type given user ID.
```
SELECT type
FROM Users
WHERE userid = <userID>;
```
This query is to determine which menu to display after `Log in`.

####  Return manager ID given store ID.
```
SELECT managerid
FROM Store
WHERE storeid = <storeID>;
```
This query is used to validate that the manager has access only to their storefront.

#### Return product infromation given store ID and product name.
```
SELECT *
FROM Product
WHERE storeid = <storeID> AND productname = <productName>;
```
This query is used to check if the store carries the product.

#### Update product information in a given store.
```
UPDATE Product SET priceperunit = <PricePerUnit>, numberofunits = <NumberofUnits> WHERE storeid = <StoreID> AND productname = <ProductName>;
```
This query is to update the product information.

#### Add information about the product update to the ProductUpdates table.
```
INSERT INTO ProductUpdates (managerID, storeid, productname, updatedOn) VALUES (<ManagerID>, <StoreID>, <ProductName>, <CurrentDate>);
```
This query is to add information about the product update by the `Manager` or `Admin`.

#### Return the last 5 updates given the store ID.
```
SELECT *
FROM ProductUpdates
WHERE storeid = <StoreID>
ORDER BY updatedOn DESC
LIMIT 5;
```
This query is used to return the last 5 updates in the store.

#### For each product in a given store return the product name and the amount of that prosuct ordered. Print 5 most ordered products.
```
SELECT productname, SUM(unitsordered) AS total_units_sold
FROM Orders
WHERE storeid = <StoreID>
GROUP BY productname
ORDER BY total_units_sold DESC
LIMIT 5;
```
This query is used to print the most popular products in the store.

#### For each customer return the number of orders that customaer made. Return 5 customers with the most orders.
```
SELECT customerid, COUNT(*) AS total_orders
FROM Orders
WHERE storeid = <StoreID>
GROUP BY customerid
ORDER BY total_orders DESC
LIMIT 5;
```
This query is used to print the most popular `Customer`s.

#### Return all warehouse information given warehouse ID.
```
SELECT *
FROM Warehouse
WHERE warehouseid = <WarehouseID>;
```
This query is used to check if the given warehouse exists.

#### 
```
INSERT INTO ProductSupplyRequests (managerid, storeid, productname, unitsrequested, warehouseid) VALUES (<ManagerID>, <StoreID>, <ProductName>, <UnitsRequested>, <WarehouseID>);
```


> Include screenshots and/or code snippets for each query. In addition, explain how you implemented each query and what purpose it fulfills. (no more than 1-2 sentences per query)

> If you did any extra credit, provide screenshots and/or code snippets. Explain how you implemented the extra credit. (triggers/stored procedures, performance tuning, etc)	

## Problems/Findings

> Include problems/findings you encountered while working on the project (1-2 paragraphs max)

## Contributions

> Include descriptions of what each member worked on (1 paragraph max)
