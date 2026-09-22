# Rahul Stores - Supermarket Billing System

A **Java + Spring Boot** Object-Oriented POS Web Application for **Rahul Stores**. Open in your browser at `http://localhost:8085`.

## Tech Stack

| Layer     | Technology                              |
|-----------|-----------------------------------------|
| Language  | Java 17+ (OOP Principles)               |
| Backend   | Spring Boot (REST API)                  |
| Frontend  | HTML5 / Vanilla CSS3 / JavaScript (ES6) |
| Database  | MySQL / H2 In-Memory Fallback           |

## OOP Concepts Demonstrated

- **Classes & Objects** — `Product`, `Bill`, `BillItem`, `User`
- **Encapsulation** — Private fields with validation, getters, and setters
- **Inheritance** — Base `AbstractPayment` class extended by concrete strategies
- **Polymorphism** — `PaymentStrategy` interface (`CashPayment`, `UpiPayment`, `CardPayment`)
- **Factory Pattern** — `PaymentFactory` dynamically instantiates payment strategy
- **Abstraction** — DAO layer (`ProductDAO`, `BillDAO`) hides database queries from API controllers

## Features

1. **Multiple Input Methods**:
   - Add product by numeric **Product ID** (e.g. ID `1`, `2`, `5`)
   - Search by **Product Name** or **Barcode**
   - **Webcam Camera Barcode Scanner** modal
   - **USB Hardware Scanner Gun** auto-detection
2. **Payment Modes & Custom QR Code**:
   - **Cash Payment**: Automatic change-return calculator
   - **UPI Payment**: Displays official **Rahul Stores Google Pay QR Code**
   - **Card Payment**: Card type and last 4 digits tracking
3. **Itemized Tax Invoice Receipt**:
   - Pop-up printable thermal invoice receipt with GST calculation, transaction details, and receipt QR code.

---

## How to Run & Open the Website

### Step 1: Open Terminal / PowerShell
Open your terminal and navigate to the project directory:

```powershell
cd "c:\Users\saravana vishal\OneDrive\Desktop\supermarket billing system"
```

### Step 2: Start the Application
Run Maven Spring Boot command:

```powershell
mvn spring-boot:run
```

### Step 3: Open in Browser
Open your web browser (Chrome, Edge, Firefox) and go to:

👉 **http://localhost:8085**

### Step 4: Login Credentials

| Username | Password   | Role    |
|----------|------------|---------|
| admin    | admin123   | Admin   |
| cashier  | cashier123 | Cashier |

---

## Website Pages

| Page      | URL                    | Description                        |
|-----------|------------------------|------------------------------------|
| Home      | `/`                    | Landing page with POS launch link  |
| Login     | `/login.html`          | Authentication                     |
| Dashboard | `/dashboard.html`      | Sales stats & store overview       |
| Billing   | `/billing.html`        | POS checkout & payment receipt     |
| Products  | `/products.html`       | Admin product inventory management |
