package ro.unitbv.restlab.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.model.Product;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
@DisplayName("ProductMapper - teste unitare pure")
class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ProductMapper();
    }

    @Nested
    @DisplayName("metoda toResponse()")
    class ToResponseTests {
        @Test
        @DisplayName("transforma Product in ProductResponse cu toate campurile")
        void toResponse_shouldMapAllFields() {
            Category category = new Category(1, "Electronice");
            Product product = new Product("Monitor", 1299.0, 3, category);
            product.setId(7);
            product.setCreatedAt(LocalDateTime.now());

            ProductResponse response = mapper.toResponse(product);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(7);
            assertThat(response.name()).isEqualTo("Monitor");
            assertThat(response.price()).isEqualTo(1299.0);
            assertThat(response.stock()).isEqualTo(3);
            assertThat(response.category()).isEqualTo("Electronice");
        }
    }
}