package ro.unitbv.restlab.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.restlab.dto.*;
import ro.unitbv.restlab.service.ProductService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public List<ProductResponse> getAllProducts() {
        log.info("GET /api/products - fetching all products");
        List<ProductResponse> products = productService.findAll();
        log.info("Returning {} products", products.size());
        return products;
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public int getProductCount() {
        log.info("GET /api/products/count");
        return productService.count();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public List<ProductResponse> searchByName(@RequestParam("name") String name) {
        log.info("GET /api/products/search?name={}", name);
        return productService.findByName(name);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        log.info("GET /api/products/{}", id);
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/products - name={}, price={}, stock={}, category={}",
                request.name(), request.price(), request.stock(), request.category());
        ProductResponse created = productService.create(request);
        log.info("Product created with id={}", created.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("PUT /api/products/{}", id);
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStockRequest request) {
        log.info("PATCH /api/products/{}/stock - newStock={}", id, request.stock());
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    @PatchMapping("/{id}/price")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductResponse> updatePriceAndStock(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePriceAndStockRequest request) {
        log.info("PATCH /api/products/{}/price - price={}, stock={}", id, request.price(), request.stock());
        return ResponseEntity.ok(productService.updatePriceAndStock(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        log.info("DELETE /api/products/{}", id);
        productService.deleteById(id);
        log.info("Product with id={} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
