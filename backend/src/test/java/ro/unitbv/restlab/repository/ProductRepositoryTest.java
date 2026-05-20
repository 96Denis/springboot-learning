package ro.unitbv.restlab.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.model.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
@DataJpaTest
@DisplayName("ProductRepository - JPA tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("findByNameContainingIgnoreCase returns matching products")
    void findByNameContainingIgnoreCase_shouldReturnMatches() {
        Category category = categoryRepository.save(new Category(null, "IT"));
        productRepository.save(new Product("Monitor", 1299.0, 3, category));
        productRepository.save(new Product("Mouse", 149.0, 10, category));

        List<Product> result = productRepository.findByNameContainingIgnoreCase("mon");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Monitor");
    }

    @Test
    @DisplayName("findByPriceLessThan returns products below the threshold")
    void findByPriceLessThan_shouldReturnMatches() {
        Category category = categoryRepository.save(new Category(null, "IT"));
        productRepository.save(new Product("Budget Mouse", 49.0, 10, category));
        productRepository.save(new Product("Gaming Mouse", 149.0, 8, category));
        productRepository.save(new Product("Laptop", 4999.0, 1, category));

        List<Product> result = productRepository.findByPriceLessThan(200.0);

        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Budget Mouse", "Gaming Mouse");
    }

    @Test
    @DisplayName("save and findById persist the product with its category")
    void saveAndFindById_shouldPersistProduct() {
        Category category = categoryRepository.save(new Category(null, "Office"));
        Product saved = productRepository.save(new Product("Chair", 399.0, 7, category));

        Product loaded = productRepository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getName()).isEqualTo("Chair");
        assertThat(loaded.getCategory()).isEqualTo("Office");
        assertThat(loaded.getCategoryEntity().getId()).isEqualTo(category.getId());
    }
}
