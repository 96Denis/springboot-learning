package ro.unitbv.restlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ro.unitbv.restlab.dto.CreateProductRequest;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.exception.GlobalExceptionHandler;
import ro.unitbv.restlab.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController - standalone tests")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler(meterRegistry))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/products returns 400 for invalid payloads")
    void createProduct_whenInvalid_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", 0.0, -1, "IT", 1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.stock").exists());
    }

    @Test
    @DisplayName("POST /api/products returns 201 for a valid payload")
    void createProduct_whenValid_shouldReturn201() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Monitor", 1299.0, 3, "IT", 1);
        ProductResponse response = new ProductResponse(7, "Monitor", 1299.0, 3, "IT", null);
        when(productService.create(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.price").value(1299.0))
                .andExpect(jsonPath("$.stock").value(3))
                .andExpect(jsonPath("$.category").value("IT"));

        verify(productService).create(any(CreateProductRequest.class));
    }
}
