package ro.unitbv.restlab.exception;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ro.unitbv.restlab.dto.CreateProductRequest;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
@DisplayName("GlobalExceptionHandler - unit tests")
class GlobalExceptionHandlerTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(meterRegistry);

    @Test
    @DisplayName("handleValidation returns field errors with 400")
    void handleValidation_shouldReturnFieldErrors() throws Exception {
        MethodArgumentNotValidException exception = validationException("name", "must not be blank");

        var response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("name", "must not be blank");
    }

    @Test
    @DisplayName("handleInvalidProduct increments the not found counter")
    void handleInvalidProduct_shouldIncrementCounter() {
        var response = handler.handleInvalidProduct(new InvalidProductException("Product not found with id: 7"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Product not found with id: 7");
        assertThat(meterRegistry.counter("products.notfound.count").count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("handleInvalidJson returns a generic JSON error")
    void handleInvalidJson_shouldReturnGenericMessage() {
        var response = handler.handleInvalidJson(new HttpMessageNotReadableException("bad json", null, null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid JSON payload");
    }

    @Test
    @DisplayName("handleGeneral returns 500 for unexpected exceptions")
    void handleGeneral_shouldReturnInternalServerError() {
        var response = handler.handleGeneral(new IllegalStateException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "An unexpected error occurred");
    }

    private MethodArgumentNotValidException validationException(String field, String message) throws Exception {
        Method method = DummyTarget.class.getDeclaredMethod("create", CreateProductRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createProductRequest");
        bindingResult.addError(new FieldError("createProductRequest", field, message));
        return new MethodArgumentNotValidException(parameter, bindingResult);
    }

    private static class DummyTarget {
        @SuppressWarnings("unused")
        void create(CreateProductRequest request) {
        }
    }
}
