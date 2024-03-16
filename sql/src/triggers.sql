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
