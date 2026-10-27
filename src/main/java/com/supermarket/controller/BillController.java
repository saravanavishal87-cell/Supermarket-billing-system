package com.supermarket.controller;

import com.supermarket.dao.BillDAO;
import com.supermarket.dto.BillRequest;
import com.supermarket.model.Bill;
import com.supermarket.payment.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillDAO billDAO = new BillDAO();

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody BillRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        // Use Factory Pattern to get concrete PaymentStrategy object
        PaymentStrategy payment = PaymentFactory.createPayment(request);

        // Resolve & link customer purchase using Customer ID or Phone
        com.supermarket.dao.CustomerDAO customerDAO = new com.supermarket.dao.CustomerDAO();
        Integer customerId = request.getCustomerId();
        String customerPhone = request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()
                ? request.getCustomerPhone().trim() : null;
        String customerName = request.getCustomerName() != null && !request.getCustomerName().isBlank()
                ? request.getCustomerName().trim() : null;

        try {
            if (customerId != null && customerId > 0) {
                com.supermarket.model.Customer c = customerDAO.getById(customerId);
                if (c != null) {
                    if (customerName == null) customerName = c.getName();
                    if (customerPhone == null) customerPhone = c.getPhone();
                }
            } else if (customerPhone != null && !customerPhone.isBlank()) {
                com.supermarket.model.Customer c = customerDAO.getByPhone(customerPhone);
                if (c != null) {
                    customerId = c.getCustomerId();
                    if (customerName == null) customerName = c.getName();
                } else {
                    // Auto-register customer if new phone provided
                    com.supermarket.model.Customer newCust = new com.supermarket.model.Customer();
                    newCust.setName(customerName != null ? customerName : "Customer " + customerPhone);
                    newCust.setPhone(customerPhone);
                    if (customerDAO.addCustomer(newCust)) {
                        customerId = newCust.getCustomerId();
                        customerName = newCust.getName();
                    }
                }
            } else if (customerName != null && !customerName.isBlank() && !customerName.equalsIgnoreCase("Walk-in Customer")) {
                com.supermarket.model.Customer c = customerDAO.getByName(customerName);
                if (c != null) {
                    customerId = c.getCustomerId();
                    if (customerPhone == null) customerPhone = c.getPhone();
                }
            }
        } catch (Exception e) {
            System.err.println("[BillController] Customer linking notice: " + e.getMessage());
        }

        int cashierId = request.getCashierId() > 0 ? request.getCashierId() : 1;

        Bill bill = new Bill();
        bill.setCashierId(cashierId);
        bill.setCustomerId(customerId);
        bill.setCustomerPhone(customerPhone != null ? customerPhone : "");
        bill.setCustomerName(customerName != null && !customerName.isBlank() ? customerName : "Walk-in Customer");
        bill.setDiscount(request.getDiscount());
        bill.setItems(request.getItems());
        bill.setPaymentMode(payment.getModeName());

        if (!payment.processPayment(bill.getGrandTotal())) {
            return ResponseEntity.badRequest().body(Map.of("error", 
                String.format("Payment verification failed for amount Rs. %.2f. Please check tendered amount.", bill.getGrandTotal())));
        }

        try {
            String billNumber = billDAO.saveBill(bill);
            String qrPayload = payment.generateQrPayload(bill.getGrandTotal(), billNumber);

            Map<String, Object> resp = new java.util.LinkedHashMap<>();
            resp.put("billNumber", billNumber);
            resp.put("customerId", customerId != null ? customerId : 0);
            resp.put("customerPhone", customerPhone != null ? customerPhone : "");
            resp.put("customerName", bill.getCustomerName());
            resp.put("subtotal", bill.getSubtotal());
            resp.put("discount", bill.getDiscount());
            resp.put("taxAmount", bill.getTaxAmount());
            resp.put("grandTotal", bill.getGrandTotal());
            resp.put("paymentMode", bill.getPaymentMode());
            resp.put("transactionId", payment.getTransactionId());
            resp.put("paymentDetails", payment.getPaymentDetails());
            resp.put("qrPayload", qrPayload);

            return ResponseEntity.ok(resp);
        } catch (SQLException e) {
            System.err.println("[BillController] Bill save error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getBills(@RequestParam(value = "search", required = false) String search) {
        try {
            return ResponseEntity.ok(billDAO.getBillsByCustomer(search));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-customer")
    public ResponseEntity<?> updatePurchaseCustomer(@RequestBody Map<String, String> payload) {
        String billIdentifier = payload.get("billIdentifier");
        String customerIdOrPhone = payload.get("customerIdOrPhone");

        if (billIdentifier == null || billIdentifier.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Bill number or ID is required"));
        }
        if (customerIdOrPhone == null || customerIdOrPhone.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer ID or Mobile number is required"));
        }

        try {
            com.supermarket.dao.CustomerDAO customerDAO = new com.supermarket.dao.CustomerDAO();
            com.supermarket.model.Customer customer = customerDAO.getByIdOrPhone(customerIdOrPhone.trim());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found with ID or Mobile: " + customerIdOrPhone));
            }

            boolean updated = billDAO.updateBillCustomer(
                    billIdentifier.trim(),
                    customer.getCustomerId(),
                    customer.getPhone(),
                    customer.getName()
            );

            if (updated) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Customer purchase successfully updated to " + customer.getName(),
                        "customer", customer,
                        "billNumber", billIdentifier.trim()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Bill not found with identifier: " + billIdentifier));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
