package com.supermarket.controller;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final com.supermarket.dao.BillDAO billDAO = new com.supermarket.dao.BillDAO();

    @GetMapping("/lookup")
    public ResponseEntity<?> lookupCustomer(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false) String type) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search query is required"));
        }
        String trimmed = query.trim();
        try {
            Customer customer = null;
            if ("id".equalsIgnoreCase(type) || "customerId".equalsIgnoreCase(type)) {
                try {
                    int id = Integer.parseInt(trimmed);
                    customer = customerDAO.getById(id);
                } catch (NumberFormatException ignored) {}
            } else if ("phone".equalsIgnoreCase(type) || "mobile".equalsIgnoreCase(type)) {
                customer = customerDAO.getByPhone(trimmed);
            } else if ("name".equalsIgnoreCase(type)) {
                customer = customerDAO.getByName(trimmed);
            } else {
                // Auto fallback across ID, Phone, or Name
                customer = customerDAO.getByIdOrPhone(trimmed);
                if (customer == null) {
                    customer = customerDAO.getByName(trimmed);
                }
            }

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No customer found for: " + trimmed));
            }
            List<com.supermarket.model.Bill> purchases = billDAO.getBillsByCustomerRef(customer.getCustomerId(), customer.getPhone());
            return ResponseEntity.ok(Map.of(
                    "customer", customer,
                    "purchases", purchases,
                    "totalPurchases", purchases.size()
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/purchases")
    public ResponseEntity<?> getCustomerPurchases(@PathVariable("id") int id) {
        try {
            Customer customer = customerDAO.getById(id);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }
            List<com.supermarket.model.Bill> purchases = billDAO.getBillsByCustomerRef(customer.getCustomerId(), customer.getPhone());
            return ResponseEntity.ok(purchases);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-phone/{phone}/purchases")
    public ResponseEntity<?> getPurchasesByPhone(@PathVariable("phone") String phone) {
        try {
            Customer customer = customerDAO.getByPhone(phone.trim());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found with phone: " + phone));
            }
            List<com.supermarket.model.Bill> purchases = billDAO.getBillsByCustomerRef(customer.getCustomerId(), customer.getPhone());
            return ResponseEntity.ok(purchases);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers(@RequestParam(value = "search", required = false) String search) {
        try {
            List<Customer> customers = customerDAO.getAllCustomers(search);
            return ResponseEntity.ok(customers);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable("id") int id) {
        try {
            Customer customer = customerDAO.getById(id);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }
            return ResponseEntity.ok(customer);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        if (customer.getName() == null || customer.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer name is required"));
        }
        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer phone is required"));
        }
        try {
            if (customer.getCustomerId() > 0) {
                Customer existingId = customerDAO.getById(customer.getCustomerId());
                if (existingId != null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Customer ID " + customer.getCustomerId() + " is already in use by " + existingId.getName()));
                }
            }
            Customer existing = customerDAO.getByPhone(customer.getPhone().trim());
            if (existing != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Customer with this phone number already exists"));
            }

            boolean success = customerDAO.addCustomer(customer);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body(customer);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to add customer"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable("id") int id, @RequestBody Customer customer) {
        if (customer.getName() == null || customer.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer name is required"));
        }
        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer phone is required"));
        }
        try {
            Customer existing = customerDAO.getById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found with ID: " + id));
            }

            int newId = customer.getCustomerId() > 0 ? customer.getCustomerId() : id;
            if (newId != id) {
                Customer conflict = customerDAO.getById(newId);
                if (conflict != null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Customer ID " + newId + " is already in use by " + conflict.getName()));
                }
            }

            Customer phoneOwner = customerDAO.getByPhone(customer.getPhone().trim());
            if (phoneOwner != null && phoneOwner.getCustomerId() != id) {
                return ResponseEntity.badRequest().body(Map.of("error", "Customer with phone " + customer.getPhone() + " already exists (" + phoneOwner.getName() + ")"));
            }

            customer.setCustomerId(newId);
            boolean success = customerDAO.updateCustomer(id, customer);
            if (success) {
                return ResponseEntity.ok(customer);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to update customer"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") int id) {
        try {
            boolean success = customerDAO.deleteCustomer(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Customer deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
