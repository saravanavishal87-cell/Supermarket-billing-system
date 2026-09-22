package com.supermarket.controller;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductDAO productDAO = new ProductDAO();

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(productDAO.getAllProducts());
        } catch (SQLException e) {
            return error(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            Product product = productDAO.findById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with ID: " + id));
            }
            return ResponseEntity.ok(product);
        } catch (SQLException e) {
            return error(e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        try {
            List<Product> results = q.isBlank()
                ? productDAO.getAllProducts()
                : productDAO.searchByName(q);
            return ResponseEntity.ok(results);
        } catch (SQLException e) {
            return error(e);
        }
    }

    @GetMapping("/barcode/{code}")
    public ResponseEntity<?> findByBarcode(@PathVariable String code) {
        try {
            Product product = productDAO.findByBarcode(code);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found"));
            }
            return ResponseEntity.ok(product);
        } catch (SQLException e) {
            return error(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Product product) {
        try {
            productDAO.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product added"));
        } catch (SQLException e) {
            return error(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Product product) {
        try {
            product.setProductId(id);
            productDAO.updateProduct(product);
            return ResponseEntity.ok(Map.of("message", "Product updated"));
        } catch (SQLException e) {
            return error(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            productDAO.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted"));
        } catch (SQLException e) {
            return error(e);
        }
    }

    private ResponseEntity<?> error(SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", e.getMessage()));
    }
}
