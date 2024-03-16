DROP INDEX IF EXISTS storeID_on_products;
DROP INDEX IF EXISTS storeID_on_orders;

CREATE INDEX storeID_on_products ON product USING btree (storeid);
CREATE INDEX storeID_on_orders ON orders USING btree (storeid);
