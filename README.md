# CS 166 Project Report

## Group Information

Group #65

| Name | NetID |
|---|---|
| [Adlai Morales-Bravo](https://github.com/AdlaiMB) | |
| [Denis Melnikov](https://github.com/deet5) | **dmeln003** | 

## Implementation Description

You can create a new account using `Create user`. By default every new user has the type `Customer` which can later be modified by `Admin`. When the account is created you can log in using `Log in` option. Depending on the user type you have different menu options. For example, if your user type is `Customer`, you can view stores within 30 miles, view product list, place an order or view 5 recent orders. Here is the interface flow to better understand what options are available:

![](images/interface-flow.png)

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

#### Add a new record to the product supply request.
```
INSERT INTO ProductSupplyRequests (managerid, storeid, productname, unitsrequested, warehouseid) VALUES (<ManagerID>, <StoreID>, <ProductName>, <UnitsRequested>, <WarehouseID>);
```
This query is sused to add the product supply request from the `Manager`.

#### Change user information. 
```
UPDATE Users SET name = <UserName>, password = <Password>, latitude = <Latitude>, longitude = <Longitude>, type = <UserType> WHERE userid = <UserID>;
```
This query is used by `Admin` to update user information.

### Extra credit

#### Triggers
```
DROP TRIGGER IF EXISTS trg_product_update ON Product;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION product_update()
RETURNS "trigger" AS 
$BODY$
BEGIN
  INSERT INTO ProductUpdates (storeid, productname, updatedon, managerid)
  VALUES (NEW.storeid, NEW.productname, now(), (SELECT managerid FROM Store WHERE storeid = NEW.storeid));
  RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER trg_product_update
AFTER UPDATE ON Product
FOR EACH ROW EXECUTE PROCEDURE product_update();
```
This trigger adds information to the `ProductUpdates` after a `Manager` updates the product. 

#### Indices
```
CREATE INDEX storeID_on_products ON product USING btree (storeid);
```
This index improves the query on `Product`.

![](images/product-before.png)

![](images/product-after.png)

```
CREATE INDEX storeID_on_orders ON orders USING btree (storeid);
```
This index improves the query on `Orders`.

![](images/orders-before.png)

![](images/orders-after.png)

## Problems/Findings

- Some user types in the USER schema have extra whispaces. So when we compare type `Manager` with the string "Manager" it returns `false`. We solved it by trimming the return type from the schema.
- There was no type checking for user input. So we implemented `isInteger()` and `isDouble()` functions to check if the user inputs correct values.
- The trigger to add information product update assumes that only `Manager`s are responsible for product updates. So if `Admin` makes any changes to the product, their ID will not be in the `managerid` column.

## Contributions

**Denis Melnikov**
- Added Update Product for Managers and Admins
- Added ViewRecentUpdates for Managers
- Added viewPopularProducts for Managers
- Added viewPopularCustomers for Managers
- Added placeProductSupplyRequests for Managers
- Added searchUserbyName for Admins
- Added updateUser for Admins
- Added indices for Products and Orders
- Added trigger for product updates

**Adlai Morales-Bravo**

