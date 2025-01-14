CREATE KEYSPACE IF NOT EXISTS ecommerce WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE ecommerce.low_stock (
                                     sku_code text,
                                     design_no text,
                                     stock int,
                                     category text,
                                     size text,
                                     color text,
                                     PRIMARY KEY (sku_code)
);
CREATE TABLE ecommerce.inventory_data (
                                          sku_code text,
                                          design_no text,
                                          stock int,
                                          category text,
                                          size text,
                                          color text,
                                          PRIMARY KEY (sku_code)
);
CREATE TABLE ecommerce.category_stock (
                                          category text,
                                          total_stock int,
                                          PRIMARY KEY (category)
);
