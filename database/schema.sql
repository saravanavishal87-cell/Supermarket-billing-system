-- Supermarket Billing System - MySQL Database Schema
-- Run this script in MySQL Workbench or: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS supermarket_billing;
USE supermarket_billing;

-- Categories
CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE
);

-- Products
CREATE TABLE products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    barcode       VARCHAR(50) UNIQUE,
    product_name  VARCHAR(150) NOT NULL,
    category_id   INT,
    price         DECIMAL(10, 2) NOT NULL,
    stock_qty     DECIMAL(10, 3) NOT NULL DEFAULT 0,
    unit          VARCHAR(20) DEFAULT 'pcs',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Users (cashier / admin)
CREATE TABLE users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    role       ENUM('ADMIN', 'CASHIER') DEFAULT 'CASHIER'
);

-- Bills (invoices)
CREATE TABLE bills (
    bill_id        INT AUTO_INCREMENT PRIMARY KEY,
    bill_number    VARCHAR(30) NOT NULL UNIQUE,
    cashier_id     INT,
    customer_name  VARCHAR(100) DEFAULT 'Walk-in Customer',
    customer_phone VARCHAR(20),
    customer_id    INT,
    subtotal       DECIMAL(10, 2) NOT NULL,
    discount       DECIMAL(10, 2) DEFAULT 0,
    tax_amount     DECIMAL(10, 2) DEFAULT 0,
    grand_total    DECIMAL(10, 2) NOT NULL,
    payment_mode   ENUM('CASH', 'UPI', 'CARD') DEFAULT 'CASH',
    bill_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Bill line items
CREATE TABLE bill_items (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    bill_id     INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    DECIMAL(10, 3) NOT NULL,
    unit_price  DECIMAL(10, 2) NOT NULL,
    line_total  DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Customers
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20) NOT NULL UNIQUE,
    email       VARCHAR(100),
    address     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shop details
CREATE TABLE shop_details (
    id                 INT PRIMARY KEY DEFAULT 1,
    shop_name          VARCHAR(150) NOT NULL DEFAULT 'Rahul Stores Supermarket',
    address            VARCHAR(255) DEFAULT '123 Main Bazaar Road, Market City',
    phone              VARCHAR(30) DEFAULT '+91 98765 43210',
    email              VARCHAR(100) DEFAULT 'contact@rahulstores.com',
    gstin              VARCHAR(50) DEFAULT '33AAAAA0000A1Z5',
    image_path         VARCHAR(255) DEFAULT '/images/logo.png',
    auto_delete_months INT DEFAULT 3
);

INSERT INTO shop_details (id, shop_name, address, phone, email, gstin, image_path, auto_delete_months)
VALUES (1, 'Rahul Stores Supermarket', '123 Main Bazaar Road, Market City', '+91 98765 43210', 'contact@rahulstores.com', '33AAAAA0000A1Z5', '/images/logo.png', 3)
ON DUPLICATE KEY UPDATE shop_name=VALUES(shop_name);

INSERT INTO customers (name, phone, email, address) VALUES
('Ramesh Kumar', '9876543210', 'ramesh@example.com', '12 Lake View Street, City'),
('Priya Sharma', '9812345678', 'priya@example.com', '45 Market Road, City');

-- Sample data
INSERT INTO categories (category_name) VALUES
('Groceries'), ('Beverages'), ('Snacks'), ('Dairy'), ('Personal Care'), ('Vegetables'), ('Fruits');

INSERT INTO users (username, password, full_name, role) VALUES
('admin',   'admin123',   'Store Admin',  'ADMIN'),
('cashier', 'cashier123', 'John Cashier', 'CASHIER');

INSERT INTO products (barcode, product_name, category_id, price, stock_qty, unit) VALUES
('8901001100123', 'Rice 1kg',           1,  55.00, 100, 'kg'),
('8901001100124', 'Wheat Flour 1kg',    1,  42.00,  80, 'kg'),
('8901001100125', 'Sugar 1kg',          1,  48.00,  90, 'kg'),
('8901001100126', 'Cooking Oil 1L',     1, 145.00,  60, 'L'),
('8901001100127', 'Tea Powder 250g',    2, 120.00,  50, 'g'),
('8901001100128', 'Coffee 200g',        2, 280.00,  40, 'g'),
('8901001100129', 'Cola 750ml',         2,  40.00, 120, 'ml'),
('8901001100130', 'Mineral Water 1L',   2,  20.00, 200, 'L'),
('8901001100131', 'Potato Chips',       3,  30.00, 150, 'pcs'),
('8901001100132', 'Biscuits Pack',      3,  25.00, 180, 'pcs'),
('8901001100133', 'Chocolate Bar',      3,  50.00, 100, 'pcs'),
('8901001100134', 'Milk 500ml',         4,  28.00,  80, 'ml'),
('8901001100135', 'Curd 400g',          4,  35.00,  60, 'g'),
('8901001100136', 'Butter 100g',        4,  55.00,  45, 'g'),
('8901001100137', 'Soap',               5,  40.00,  70, 'pcs'),
('8901001100138', 'Shampoo 180ml',      5, 180.00,  35, 'ml'),
('8901001100139', 'Toothpaste',         5,  95.00,  55, 'pcs'),
('8901001100140', 'Detergent 1kg',      5, 110.00,  40, 'kg');
