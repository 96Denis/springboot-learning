package ro.unitbv.restlab.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ro.unitbv.restlab.dto.CreateProductRequest;
import ro.unitbv.restlab.dto.LoginRequest;
import ro.unitbv.restlab.dto.LoginResponse;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.repository.CategoryRepository;
import ro.unitbv.restlab.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@DisplayName("Product API security and validation integration tests")
class ProductTestcontainersIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        category = categoryRepository.save(new Category(null, "IT"));
    }

    @Test
    @DisplayName("user role cannot create products and admin can")
    void userCannotCreateButAdminCan() {
        String userToken = login("user", "user123").token();
        String adminToken = login("admin", "admin123").token();

        CreateProductRequest request = new CreateProductRequest("Keyboard", 299.0, 6, category.getName(), category.getId());

        ResponseEntity<String> forbidden = restTemplate.postForEntity(
                "/api/products",
                new HttpEntity<>(request, bearerHeaders(userToken)),
                String.class
        );
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<ProductResponse> created = restTemplate.postForEntity(
                "/api/products",
                new HttpEntity<>(request, bearerHeaders(adminToken)),
                ProductResponse.class
        );
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody().name()).isEqualTo("Keyboard");
    }

    @Test
    @DisplayName("invalid category data returns a validation error body")
    void invalidCategory_shouldReturnErrorBody() {
        String adminToken = login("admin", "admin123").token();
        CreateProductRequest request = new CreateProductRequest("Keyboard", 299.0, 6, "Office", category.getId());

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/products",
                new HttpEntity<>(request, bearerHeaders(adminToken)),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Category name does not match category id");
    }

    private LoginResponse login(String username, String password) {
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/auth/login",
                new LoginRequest(username, password),
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
