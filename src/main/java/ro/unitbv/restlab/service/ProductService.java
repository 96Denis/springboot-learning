package ro.unitbv.restlab.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.unitbv.restlab.dto.CreateProductRequest;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.dto.UpdatePriceAndStockRequest;
import ro.unitbv.restlab.dto.UpdateProductRequest;
import ro.unitbv.restlab.dto.UpdateStockRequest;
import ro.unitbv.restlab.exception.InvalidProductException;
import ro.unitbv.restlab.mapper.ProductMapper;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.model.Product;
import ro.unitbv.restlab.repository.CategoryRepository;
import ro.unitbv.restlab.repository.ProductRepository;

import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;

    private Counter createdCounter;
    private Timer findByIdTimer;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper,
            MeterRegistry meterRegistry
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        createdCounter = meterRegistry.counter("products.created.count");
        findByIdTimer = meterRegistry.timer("products.findById.time");

        meterRegistry.gauge("products.total.count", productRepository, r -> (double) r.count());
    }

    public ProductResponse create(CreateProductRequest request) {
        log.debug("Creating product: name={}, price={}, stock={}", request.name(), request.price(), request.stock());
        Category category = getValidatedCategory(request.categoryId(), request.category());
        Product product = new Product(request.name(), request.price(), request.stock(), category);
        product.setCategory(request.category());
        product.setCategoryEntity(category);
        ProductResponse response = productMapper.toResponse(productRepository.save(product));
        createdCounter.increment();
        log.info("Product created successfully with id={}", response.id());
        return response;
    }

    public List<ProductResponse> findAll() {
        log.debug("Fetching all products from database");
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
        log.info("Found {} products", products.size());
        return products;
    }

    public ProductResponse findById(Integer id) {
        log.debug("Looking up product with id={}", id);
        return findByIdTimer.record(() -> {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Product not found with id={}", id);
                        return new InvalidProductException("Product not found with id: " + id);
                    });
            log.info("Product found: id={}, name={}", product.getId(), product.getName());
            return productMapper.toResponse(product);
        });
    }

    public ProductResponse update(Integer id, UpdateProductRequest request) {
        log.debug("Updating product with id={}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for update, id={}", id);
                    return new InvalidProductException("Product not found with id: " + id);
                });
        Category category = getValidatedCategory(request.categoryId(), request.category());
        existing.setName(request.name());
        existing.setPrice(request.price());
        existing.setStock(request.stock());
        existing.setCategory(request.category());
        existing.setCategoryEntity(category);
        ProductResponse response = productMapper.toResponse(productRepository.save(existing));
        log.info("Product updated successfully: id={}", id);
        return response;
    }

    private Category getValidatedCategory(Integer categoryId, String categoryName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new InvalidProductException("Category not found with id: " + categoryId));
        if (!category.getName().equalsIgnoreCase(categoryName)) {
            throw new InvalidProductException("Category name does not match category id: " + categoryId);
        }
        return category;
    }

    public void deleteById(Integer id) {
        log.debug("Deleting product with id={}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent product with id={}", id);
            throw new InvalidProductException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully: id={}", id);
    }

    public int count() {
        return (int) productRepository.count();
    }

    public List<ProductResponse> findByName(String name) {
        log.debug("Searching products by name containing '{}'", name);
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse updateStock(Integer id, UpdateStockRequest request) {
        log.debug("Updating stock for product id={}, newStock={}", id, request.stock());
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new InvalidProductException("Product not found with id: " + id));
        existing.setStock(request.stock());
        ProductResponse response = productMapper.toResponse(productRepository.save(existing));
        log.info("Stock updated for product id={}, newStock={}", id, request.stock());
        return response;
    }

    public ProductResponse updatePriceAndStock(Integer id, UpdatePriceAndStockRequest request) {
        log.debug("Updating price and stock for product id={}, price={}, stock={}", id, request.price(), request.stock());
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new InvalidProductException("Product not found with id: " + id));
        if (request.price() != null) {
            existing.setPrice(request.price());
        }
        if (request.stock() != null) {
            existing.setStock(request.stock());
        }
        ProductResponse response = productMapper.toResponse(productRepository.save(existing));
        log.info("Price and stock updated for product id={}", id);
        return response;
    }
}