package ro.unitbv.restlab.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@DisplayName("Test de integrare pentru Actuator Metrics")
class ActuatorMetricsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminJwtToken;
    private Category testCategory;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Creăm o categorie de test necesară pentru validări
        testCategory = new Category();
        testCategory.setName("IT");
        testCategory = categoryRepository.save(testCategory);

        // Obținem token-ul prin endpoint-ul de login creat în DataSeeder
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        adminJwtToken = loginResponse.getBody().token();
    }

    @Test
    @DisplayName("Metrica products.created.count se incrementeaza corect dupa creare")
    void shouldIncrementCreatedProductsMetric() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminJwtToken);

        // 1. Creăm primul produs (fără .builder())
        CreateProductRequest request1 = new CreateProductRequest("Produs 1", 15.0, 5, testCategory.getName(), testCategory.getId());
        HttpEntity<CreateProductRequest> entity1 = new HttpEntity<>(request1, headers);

        restTemplate.postForEntity("/api/products", entity1, ProductResponse.class);

        ResponseEntity<String> metricResponse1 = restTemplate.exchange(
                "/actuator/metrics/products.created.count",
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(metricResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
        double count1 = extractValue(metricResponse1.getBody());
        assertThat(count1).isEqualTo(1.0);

        // 2. Creăm al doilea produs
        CreateProductRequest request2 = new CreateProductRequest("Produs 2", 25.0, 10, testCategory.getName(), testCategory.getId());
        HttpEntity<CreateProductRequest> entity2 = new HttpEntity<>(request2, headers);
        restTemplate.postForEntity("/api/products", entity2, ProductResponse.class);

        ResponseEntity<String> metricResponse2 = restTemplate.exchange(
                "/actuator/metrics/products.created.count",
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        double count2 = extractValue(metricResponse2.getBody());
        assertThat(count2).isEqualTo(2.0);
    }

    private double extractValue(String body) {
        Matcher matcher = Pattern.compile("\"value\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(body);
        assertThat(matcher.find()).isTrue();
        return Double.parseDouble(matcher.group(1));
    }
}
