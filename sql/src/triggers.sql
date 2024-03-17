DROP TRIGGER IF EXISTS trg_product_update ON Product;
DROP TRIGGER IF EXISTS trg_new_order ON Orders;

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

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION product_units_update()
RETURNS "trigger" AS 
$BODY$
DECLARE 
  oldUnits  integer;
BEGIN
  oldUnits := (SELECT numberOfUnits FROM Product WHERE storeID = NEW.storeID AND productName = NEW.productName);
  UPDATE Product SET numberOfUnits = oldUnits - NEW.unitsOrdered WHERE storeID = NEW.storeID AND productname = NEW.productName;
  RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER trg_product_update
AFTER UPDATE ON Product
FOR EACH ROW EXECUTE PROCEDURE product_update();

CREATE TRIGGER trg_new_order
AFTER INSERT ON Orders
FOR EACH ROW EXECUTE PROCEDURE product_units_update();