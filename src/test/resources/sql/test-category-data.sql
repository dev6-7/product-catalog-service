--categories
INSERT INTO categories (name, version) VALUES ('Electronics', 0);
INSERT INTO categories (name, version) VALUES ('Home & Kitchen', 0);
INSERT INTO categories (name, version) VALUES ('Clothing', 0);
INSERT INTO categories (name, version) VALUES ('Accessories', 0);
INSERT INTO categories (name, version) VALUES ('Sports', 0);
INSERT INTO categories (name, version) VALUES ('Musical Instr.', 0);
INSERT INTO categories (name, version) VALUES ('Footwear', 0);
INSERT INTO categories (name, version) VALUES ('Home Appliances', 0);
INSERT INTO categories (name, version) VALUES ('Stationery', 0);
INSERT INTO categories (name, version) VALUES ('Toys & Games', 0);

--products
INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0001', 19.99, 'Wireless Mouse with ergonomic design',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0002', 499.00, '4K Ultra HD Smart TV, 55 inches',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0003', 29.50, 'Stainless Steel Water Bottle, 1L',
        (SELECT id FROM categories WHERE name='Home & Kitchen'), 'Home & Kitchen');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0004', 15.00, 'Cotton T-Shirt, Unisex, Size M',
        (SELECT id FROM categories WHERE name='Clothing'), 'Clothing');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0005', 120.00, 'Noise-Cancelling Over-Ear Headphones',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0006', 9.99, 'USB-C to USB Adapter',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0007', 75.00, 'Leather Wallet with RFID Protection',
        (SELECT id FROM categories WHERE name='Accessories'), 'Accessories');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0008', 35.00, 'Yoga Mat with Non-Slip Surface',
        (SELECT id FROM categories WHERE name='Sports'), 'Sports');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0009', 220.00, 'Smartwatch with Heart Rate Monitor',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0010', 12.50, 'Ceramic Coffee Mug, 350ml',
        (SELECT id FROM categories WHERE name='Home & Kitchen'), 'Home & Kitchen');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0011', 60.00, 'Bluetooth Portable Speaker',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0012', 85.00, 'Backpack with Laptop Compartment',
        (SELECT id FROM categories WHERE name='Accessories'), 'Accessories');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0013', 18.00, 'Stainless Steel Cutlery Set, 24 Pieces',
        (SELECT id FROM categories WHERE name='Home & Kitchen'), 'Home & Kitchen');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0014', 250.00, 'Electric Guitar Starter Pack',
        (SELECT id FROM categories WHERE name='Musical Instr.'), 'Musical Instr.');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0015', 42.00, 'Running Shoes, Men''s Size 42',
        (SELECT id FROM categories WHERE name='Footwear'), 'Footwear');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0016', 27.99, 'Digital Bathroom Scale with Body Fat Analyzer',
        (SELECT id FROM categories WHERE name='Home Appliances'), 'Home Appliances');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0017', 14.99, 'Set of 6 Organic Cotton Socks',
        (SELECT id FROM categories WHERE name='Clothing'), 'Clothing');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0018', 300.00, 'DSLR Camera with 18-55mm Lens',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0019', 8.99, 'Hardcover Notebook, A5, 200 Pages',
        (SELECT id FROM categories WHERE name='Stationery'), 'Stationery');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0020', 65.00, 'Microwave Oven, 20L Capacity',
        (SELECT id FROM categories WHERE name='Home Appliances'), 'Home Appliances');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0021', 23.50, 'LED Desk Lamp with Adjustable Brightness',
        (SELECT id FROM categories WHERE name='Home & Kitchen'), 'Home & Kitchen');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0022', 19.00, 'Wireless Charger Pad for Smartphones',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0023', 55.00, 'Men''s Quartz Analog Watch with Leather Strap',
        (SELECT id FROM categories WHERE name='Accessories'), 'Accessories');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0024', 30.00, 'Wooden Chess Set with Folding Board',
        (SELECT id FROM categories WHERE name='Toys & Games'), 'Toys & Games');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0025', 99.00, 'Home Security Camera with Night Vision',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0026', 16.50, 'Aromatherapy Essential Oil Diffuser',
        (SELECT id FROM categories WHERE name='Home & Kitchen'), 'Home & Kitchen');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0027', 40.00, 'Professional Blender with 2L Jar',
        (SELECT id FROM categories WHERE name='Home Appliances'), 'Home Appliances');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0028', 22.00, 'Kids'' Educational Tablet Toy',
        (SELECT id FROM categories WHERE name='Toys & Games'), 'Toys & Games');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0029', 110.00, 'Mechanical Gaming Keyboard with RGB Lighting',
        (SELECT id FROM categories WHERE name='Electronics'), 'Electronics');

INSERT INTO products (sku, price, description, category_id, category_name)
VALUES ('SKU0030', 7.50, 'Pack of 10 Ballpoint Pens, Blue Ink',
        (SELECT id FROM categories WHERE name='Stationery'), 'Stationery');

-- CATEGORY: Electronics → 15%
INSERT INTO discount_rules (scope, category_id, percent)
VALUES ('CATEGORY', (SELECT id FROM categories WHERE name = 'Electronics'), 15);
-- CATEGORY: Home & Kitchen → 25%
INSERT INTO discount_rules (scope, category_id, percent)
VALUES ('CATEGORY', (SELECT id FROM categories WHERE name = 'Home & Kitchen'), 25);
-- SKU_SUFFIX: ends with '5' → 30%
INSERT INTO discount_rules (scope, sku_suffix, percent)
VALUES ('SKU_SUFFIX', '5', 30);

WITH pct AS (
SELECT p.id,
       GREATEST(
         COALESCE((
           SELECT MAX(percent)::numeric
           FROM discount_rules
           WHERE scope='CATEGORY'
             AND category_id = p.category_id
         ), 0),
         COALESCE((
           SELECT MAX(percent)::numeric
           FROM discount_rules
           WHERE scope='SKU_SUFFIX'
             AND p.sku LIKE '%' || sku_suffix
         ), 0)
       ) AS percent
FROM products p
)
UPDATE products p
SET discount       = pct.percent::bigint,
  discount_price = ROUND(p.price * (1 - pct.percent/100.0), 2),
  updated_at     = NOW()
FROM pct
WHERE p.id = pct.id;