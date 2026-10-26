package com.supermarket.util;

import java.io.File;
import java.sql.*;

public final class DatabaseConnection {

    private static String url  = "jdbc:mysql://localhost:3306/supermarket_billing";
    private static String user = "root";
    private static String pass = "";

    private static boolean isH2Fallback = false;
    private static boolean h2Initialized = false;

    private DatabaseConnection() {}

    public static void configure(String jdbcUrl, String username, String password) {
        url = jdbcUrl;
        user = username;
        pass = password;
        isH2Fallback = false;
        h2Initialized = false;
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (isH2Fallback) {
            return getH2Connection();
        }

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            ensureSchemaColumns(conn);
            return conn;
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] MySQL Connection failed (" + e.getMessage() + "). Switching to persistent embedded file database...");
            isH2Fallback = true;
            return getH2Connection();
        }
    }

    private static synchronized Connection getH2Connection() throws SQLException {
        File dbDir = new File("database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        String dbPath = new File(dbDir, "supermarket_db").getAbsolutePath().replace("\\", "/");
        String h2Url = "jdbc:h2:file:" + dbPath + ";DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;MODE=MySQL";
        Connection conn = DriverManager.getConnection(h2Url, "sa", "");
        
        if (!h2Initialized) {
            initH2Schema(conn, dbPath);
            h2Initialized = true;
        }
        ensureSchemaColumns(conn);
        return conn;
    }

    private static void ensureSchemaColumns(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try {
                stmt.executeUpdate("ALTER TABLE bills ADD COLUMN customer_id INT");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE bills ADD COLUMN customer_phone VARCHAR(20)");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS expenses (
                        expense_id INT AUTO_INCREMENT PRIMARY KEY,
                        expense_date DATE NOT NULL,
                        category VARCHAR(50) NOT NULL,
                        amount DECIMAL(10, 2) NOT NULL,
                        description VARCHAR(255),
                        payment_mode VARCHAR(30) DEFAULT 'CASH',
                        recorded_by INT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE bill_items MODIFY COLUMN quantity DECIMAL(10, 3) NOT NULL");
            } catch (SQLException ignored) {
                try {
                    stmt.executeUpdate("ALTER TABLE bill_items ALTER COLUMN quantity DECIMAL(10, 3) NOT NULL");
                } catch (SQLException ignored2) {}
            }
            try {
                stmt.executeUpdate("ALTER TABLE products MODIFY COLUMN stock_qty DECIMAL(10, 3) NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {
                try {
                    stmt.executeUpdate("ALTER TABLE products ALTER COLUMN stock_qty DECIMAL(10, 3) NOT NULL DEFAULT 0");
                } catch (SQLException ignored2) {}
            }
            try {
                stmt.executeUpdate("INSERT INTO categories (category_id, category_name) VALUES (6, 'Vegetables') ON DUPLICATE KEY UPDATE category_name = 'Vegetables'");
            } catch (SQLException ignored) {
                try {
                    stmt.executeUpdate("MERGE INTO categories (category_id, category_name) KEY(category_id) VALUES (6, 'Vegetables')");
                } catch (SQLException ignored2) {}
            }
            try {
                stmt.executeUpdate("INSERT INTO categories (category_id, category_name) VALUES (7, 'Fruits') ON DUPLICATE KEY UPDATE category_name = 'Fruits'");
            } catch (SQLException ignored) {
                try {
                    stmt.executeUpdate("MERGE INTO categories (category_id, category_name) KEY(category_id) VALUES (7, 'Fruits')");
                } catch (SQLException ignored2) {}
            }
            try {
                stmt.executeUpdate("ALTER TABLE products ADD COLUMN expiry_date DATE");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE products ADD COLUMN batch_no VARCHAR(50)");
            } catch (SQLException ignored) {}
            try {
                seedProductExpiryDates(conn);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private static void seedProductExpiryDates(Connection conn) {
        java.time.LocalDate today = java.time.LocalDate.now();
        // Check if products have null expiry_date
        String checkSql = "SELECT COUNT(*) FROM products WHERE expiry_date IS NULL";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                // Populate realistic dates
                updateProductExpiry(conn, "%curd%", today.minusDays(1), "BATCH-CRD01"); // Expired yesterday!
                updateProductExpiry(conn, "%milk%", today.plusDays(3), "BATCH-MLK02");  // 3 days: Critical
                updateProductExpiry(conn, "%tomato%", today.plusDays(4), "BATCH-VEG03"); // 4 days: Critical
                updateProductExpiry(conn, "%butter%", today.plusDays(12), "BATCH-BTR04"); // 12 days: Warning
                updateProductExpiry(conn, "%potato chips%", today.plusDays(24), "BATCH-CHP05"); // 24 days: Warning
                updateProductExpiry(conn, "%biscuits%", today.plusDays(45), "BATCH-BSC06"); // 45 days: Healthy
                updateProductExpiry(conn, "%chocolate%", today.plusDays(60), "BATCH-CHO07"); // 60 days: Healthy
                updateProductExpiry(conn, "%cola%", today.plusDays(90), "BATCH-BEV08"); // 90 days: Healthy
                updateProductExpiry(conn, "%water%", today.plusDays(180), "BATCH-WTR09"); // 180 days: Healthy
                updateProductExpiry(conn, "%tea%", today.plusDays(240), "BATCH-TEA10"); // 240 days: Healthy
                updateProductExpiry(conn, "%coffee%", today.plusDays(300), "BATCH-COF11"); // 300 days: Healthy
                updateProductExpiry(conn, "%rice%", today.plusDays(365), "BATCH-RIC12"); // 1 year: Healthy
                updateProductExpiry(conn, "%sugar%", today.plusDays(400), "BATCH-SUG13"); // Healthy
                updateProductExpiry(conn, "%oil%", today.plusDays(365), "BATCH-OIL14"); // Healthy
                updateProductExpiry(conn, "%flour%", today.plusDays(180), "BATCH-FLR15"); // Healthy
                // Catch-all for any remaining products
                String fallbackSql = "UPDATE products SET expiry_date = ?, batch_no = 'BATCH-STD' WHERE expiry_date IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(fallbackSql)) {
                    ps.setDate(1, java.sql.Date.valueOf(today.plusDays(120)));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ignored) {}
    }

    private static void updateProductExpiry(Connection conn, String pattern, java.time.LocalDate date, String batch) {
        String sql = "UPDATE products SET expiry_date = ?, batch_no = ? WHERE LOWER(product_name) LIKE ? AND expiry_date IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setString(2, batch);
            ps.setString(3, pattern.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public static void disableForeignKeyChecks(Connection conn) {
        try (Statement s = conn.createStatement()) {
            try {
                s.execute("SET FOREIGN_KEY_CHECKS = 0");
            } catch (SQLException ignored) {}
            try {
                s.execute("SET REFERENTIAL_INTEGRITY FALSE");
            } catch (SQLException ignored) {}
        } catch (SQLException ignored) {}
    }

    public static void enableForeignKeyChecks(Connection conn) {
        try (Statement s = conn.createStatement()) {
            try {
                s.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (SQLException ignored) {}
            try {
                s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            } catch (SQLException ignored) {}
        } catch (SQLException ignored) {}
    }

    private static boolean isTableEmpty(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private static void initH2Schema(Connection conn, String dbPath) {
        try (Statement stmt = conn.createStatement()) {
            System.out.println("[DatabaseConnection] Initializing persistent schema at " + dbPath + "...");
            String schemaSql = """
                CREATE TABLE IF NOT EXISTS categories (
                    category_id INT AUTO_INCREMENT PRIMARY KEY,
                    category_name VARCHAR(100) NOT NULL UNIQUE
                );

                CREATE TABLE IF NOT EXISTS products (
                    product_id INT AUTO_INCREMENT PRIMARY KEY,
                    barcode VARCHAR(50) UNIQUE,
                    product_name VARCHAR(150) NOT NULL,
                    category_id INT,
                    price DECIMAL(10, 2) NOT NULL,
                    stock_qty DECIMAL(10, 3) NOT NULL DEFAULT 0,
                    unit VARCHAR(20) DEFAULT 'pcs',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(category_id)
                );

                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    role VARCHAR(20) DEFAULT 'CASHIER'
                );

                CREATE TABLE IF NOT EXISTS bills (
                    bill_id INT AUTO_INCREMENT PRIMARY KEY,
                    bill_number VARCHAR(30) NOT NULL UNIQUE,
                    cashier_id INT,
                    customer_name VARCHAR(100) DEFAULT 'Walk-in Customer',
                    customer_phone VARCHAR(20),
                    customer_id INT,
                    subtotal DECIMAL(10, 2) NOT NULL,
                    discount DECIMAL(10, 2) DEFAULT 0,
                    tax_amount DECIMAL(10, 2) DEFAULT 0,
                    grand_total DECIMAL(10, 2) NOT NULL,
                    payment_mode VARCHAR(20) DEFAULT 'CASH',
                    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS bill_items (
                    item_id INT AUTO_INCREMENT PRIMARY KEY,
                    bill_id INT NOT NULL,
                    product_id INT NOT NULL,
                    quantity DECIMAL(10, 3) NOT NULL,
                    unit_price DECIMAL(10, 2) NOT NULL,
                    line_total DECIMAL(10, 2) NOT NULL
                );

                CREATE TABLE IF NOT EXISTS customers (
                    customer_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL UNIQUE,
                    email VARCHAR(100),
                    address TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS shop_details (
                    id INT PRIMARY KEY,
                    shop_name VARCHAR(150) NOT NULL,
                    address VARCHAR(255),
                    phone VARCHAR(30),
                    email VARCHAR(100),
                    gstin VARCHAR(50),
                    image_path VARCHAR(255),
                    auto_delete_months INT DEFAULT 3
                );

                CREATE TABLE IF NOT EXISTS expenses (
                    expense_id INT AUTO_INCREMENT PRIMARY KEY,
                    expense_date DATE NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    description VARCHAR(255),
                    payment_mode VARCHAR(30) DEFAULT 'CASH',
                    recorded_by INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """;

            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }

            // Only insert default seed data if the tables are empty
            if (isTableEmpty(conn, "categories")) {
                stmt.execute("""
                    INSERT INTO categories (category_id, category_name) VALUES
                    (1, 'Groceries'), (2, 'Beverages'), (3, 'Snacks'), (4, 'Dairy'), (5, 'Personal Care')
                """);
            }

            if (isTableEmpty(conn, "users")) {
                stmt.execute("""
                    INSERT INTO users (user_id, username, password, full_name, role) VALUES
                    (1, 'admin', 'admin123', 'Store Admin', 'ADMIN'),
                    (2, 'cashier', 'cashier123', 'John Cashier', 'CASHIER')
                """);
            }

            if (isTableEmpty(conn, "shop_details")) {
                stmt.execute("""
                    INSERT INTO shop_details (id, shop_name, address, phone, email, gstin, image_path, auto_delete_months) VALUES
                    (1, 'RAHUL STORES', '123 Main Bazaar Road, Market City', '+91 98765 43210', 'contact@rahulstores.com', '33AAAAA0000A1Z5', '/images/logo.png', 3)
                """);
            }

            if (isTableEmpty(conn, "customers")) {
                stmt.execute("""
                    INSERT INTO customers (customer_id, name, phone, email, address) VALUES
                    (1, 'Ramesh Kumar', '9876543210', 'ramesh@example.com', '12 Lake View Street, City'),
                    (2, 'Priya Sharma', '9812345678', 'priya@example.com', '45 Market Road, City'),
                    (3, 'Anita Patel', '9765432109', 'anita@example.com', '88 Green Park, City')
                """);
            }

            if (isTableEmpty(conn, "products")) {
                stmt.execute("""
                    INSERT INTO products (product_id, barcode, product_name, category_id, price, stock_qty, unit) VALUES
                    (1, '8901001100123', 'Rice 1kg', 1, 55.00, 100, 'kg'),
                    (2, '8901001100124', 'Wheat Flour 1kg', 1, 42.00, 80, 'kg'),
                    (3, '8901001100125', 'Sugar 1kg', 1, 48.00, 90, 'kg'),
                    (4, '8901001100126', 'Cooking Oil 1L', 1, 145.00, 60, 'L'),
                    (5, '8901001100127', 'Tea Powder 250g', 2, 120.00, 50, 'g'),
                    (6, '8901001100128', 'Coffee 200g', 2, 280.00, 40, 'g'),
                    (7, '8901001100129', 'Cola 750ml', 2, 40.00, 120, 'ml'),
                    (8, '8901001100130', 'Mineral Water 1L', 2, 20.00, 200, 'L'),
                    (9, '8901001100131', 'Potato Chips', 3, 30.00, 150, 'pcs'),
                    (10, '8901001100132', 'Biscuits Pack', 3, 25.00, 180, 'pcs'),
                    (11, '8901001100133', 'Chocolate Bar', 3, 50.00, 100, 'pcs'),
                    (12, '8901001100134', 'Milk 500ml', 4, 28.00, 80, 'ml'),
                    (13, '8901001100135', 'Curd 400g', 4, 35.00, 60, 'g'),
                    (14, '8901001100136', 'Butter 100g', 4, 55.00, 45, 'g'),
                    (15, '8901001100137', 'Soap', 5, 40.00, 70, 'pcs'),
                    (16, '8901001100138', 'Shampoo 180ml', 5, 180.00, 35, 'ml'),
                    (17, '8901001100139', 'Toothpaste', 5, 95.00, 55, 'pcs'),
                    (18, '8901001100140', 'Detergent 1kg', 5, 110.00, 40, 'kg')
                """);
            }
            System.out.println("[DatabaseConnection] Persistent H2 Database ready at " + dbPath + ".mv.db");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error initializing H2 schema: " + e.getMessage());
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
