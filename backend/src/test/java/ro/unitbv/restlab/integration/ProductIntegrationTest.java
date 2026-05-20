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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ro.unitbv.restlab.dto.CreateProductRequest;
import ro.unitbv.restlab.dto.LoginRequest;
import ro.unitbv.restlab.dto.LoginResponse;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.dto.UpdateProductRequest;
import ro.unitbv.restlab.model.Category;
import ro.unitbv.restlab.repository.CategoryRepository;
import ro.unitbv.restlab.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@DisplayName("Product API integration tests")
class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private String adminToken;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        category = categoryRepository.save(new Category(null, "IT"));
        adminToken = login("admin", "admin123").token();
    }

    @Test
    @DisplayName("full CRUD flow works over HTTP")
    void fullCrudFlow_shouldWork() {
        HttpHeaders headers = bearerHeaders(adminToken);

        CreateProductRequest createRequest = new CreateProductRequest("Monitor", 1299.0, 3, category.getName(), category.getId());
        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                "/api/products",
                new HttpEntity<>(createRequest, headers),
                ProductResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductResponse created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.category()).isEqualTo("IT");

        ResponseEntity<ProductResponse> getResponse = restTemplate.exchange(
                "/api/products/" + created.id(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name()).isEqualTo("Monitor");

        ResponseEntity<ProductResponse[]> searchResponse = restTemplate.exchange(
                "/api/products/search?name=mon",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse[].class
        );
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).hasSize(1);

        UpdateProductRequest updateRequest = new UpdateProductRequest("Monitor Pro", 1499.0, 5, category.getName(), category.getId());
        ResponseEntity<ProductResponse> updateResponse = restTemplate.exchange(
                "/api/products/" + created.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, headers),
                ProductResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().name()).isEqualTo("Monitor Pro");
        assertThat(updateResponse.getBody().stock()).isEqualTo(5);

        ResponseEntity<Integer> countResponse = restTemplate.exchange(
                "/api/products/count",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Integer.class
        );
        assertThat(countResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(countResponse.getBody()).isEqualTo(1);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/products/" + created.id(),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> afterDelete = restTemplate.exchange(
                "/api/products/" + created.id(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(afterDelete.getBody()).contains("Product not found with id: " + created.id());
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
