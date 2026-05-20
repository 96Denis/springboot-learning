package ro.unitbv.restlab.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - unit tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private SimpleMeterRegistry meterRegistry;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        productService = new ProductService(productRepository, categoryRepository, new ProductMapper(), meterRegistry);
        productService.initMetrics();
    }

    @Test
    @DisplayName("create persists a product and increments the metric")
    void create_shouldPersistProductAndIncrementMetric() {
        Category category = new Category(1, "IT");
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(10);
            product.setCreatedAt(LocalDateTime.of(2026, 5, 20, 10, 0));
            return product;
        });

        ProductResponse response = productService.create(new CreateProductRequest("Monitor", 1299.0, 3, "IT", 1));

        assertThat(response.id()).isEqualTo(10);
        assertThat(response.name()).isEqualTo("Monitor");
        assertThat(response.category()).isEqualTo("IT");
        assertThat(meterRegistry.counter("products.created.count").count()).isEqualTo(1.0);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("findById returns mapped product and records timing")
    void findById_shouldReturnMappedProduct() {
        Product product = new Product("Monitor", 1299.0, 3, new Category(1, "IT"));
        product.setId(7);
        product.setCreatedAt(LocalDateTime.of(2026, 5, 20, 11, 0));
        when(productRepository.findById(7)).thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(7);

        assertThat(response.id()).isEqualTo(7);
        assertThat(response.name()).isEqualTo("Monitor");
        assertThat(meterRegistry.find("products.findById.time").timer()).isNotNull();
        assertThat(meterRegistry.find("products.findById.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("findById throws when product does not exist")
    void findById_shouldThrowWhenMissing() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product not found with id: 99");
    }

    @Test
    @DisplayName("update replaces all mutable fields")
    void update_shouldReplaceFields() {
        Product existing = new Product("Old", 10.0, 1, new Category(1, "IT"));
        existing.setId(5);
        when(productRepository.findById(5)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(new Category(2, "Office")));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.update(5, new UpdateProductRequest("New", 20.0, 4, "Office", 2));

        assertThat(response.name()).isEqualTo("New");
        assertThat(response.price()).isEqualTo(20.0);
        assertThat(response.stock()).isEqualTo(4);
        assertThat(response.category()).isEqualTo("Office");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("updateStock changes stock only")
    void updateStock_shouldChangeStock() {
        Product existing = new Product("Monitor", 1299.0, 3, new Category(1, "IT"));
        existing.setId(7);
        when(productRepository.findById(7)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateStock(7, new UpdateStockRequest(9));

        assertThat(response.stock()).isEqualTo(9);
        assertThat(response.price()).isEqualTo(1299.0);
    }

    @Test
    @DisplayName("updatePriceAndStock changes the requested fields")
    void updatePriceAndStock_shouldChangeFields() {
        Product existing = new Product("Monitor", 1299.0, 3, new Category(1, "IT"));
        existing.setId(7);
        when(productRepository.findById(7)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updatePriceAndStock(7, new UpdatePriceAndStockRequest(999.0, 12));

        assertThat(response.price()).isEqualTo(999.0);
        assertThat(response.stock()).isEqualTo(12);
    }

    @Test
    @DisplayName("findAll maps every product")
    void findAll_shouldMapProducts() {
        Product first = new Product("Monitor", 1299.0, 3, new Category(1, "IT"));
        first.setId(1);
        first.setCreatedAt(LocalDateTime.of(2026, 5, 20, 9, 0));
        Product second = new Product("Mouse", 149.0, 10, new Category(1, "IT"));
        second.setId(2);
        second.setCreatedAt(LocalDateTime.of(2026, 5, 20, 9, 5));
        when(productRepository.findAll()).thenReturn(List.of(first, second));

        List<ProductResponse> response = productService.findAll();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).name()).isEqualTo("Monitor");
        assertThat(response.get(1).name()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("findByName searches case-insensitively")
    void findByName_shouldSearchCaseInsensitively() {
        Product product = new Product("Mechanical Keyboard", 499.0, 5, new Category(1, "IT"));
        product.setId(3);
        when(productRepository.findByNameContainingIgnoreCase("keyboard")).thenReturn(List.of(product));

        List<ProductResponse> response = productService.findByName("keyboard");

        assertThat(response).hasSize(1);
        assertThat(response.get(0).name()).isEqualTo("Mechanical Keyboard");
    }

    @Test
    @DisplayName("count returns repository count")
    void count_shouldReturnRepositoryCount() {
        when(productRepository.count()).thenReturn(17L);

        assertThat(productService.count()).isEqualTo(17);
    }

    @Test
    @DisplayName("deleteById removes an existing product")
    void deleteById_shouldDeleteExistingProduct() {
        when(productRepository.existsById(7)).thenReturn(true);

        productService.deleteById(7);

        verify(productRepository).deleteById(7);
    }

    @Test
    @DisplayName("deleteById throws when the product is missing")
    void deleteById_shouldThrowWhenMissing() {
        when(productRepository.existsById(anyInt())).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteById(7))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product not found with id: 7");
    }
}
