package ro.unitbv.restlab.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
@DisplayName("ProductNotFoundException - compatibility test")
class ProductNotFoundExceptionTest {

    @Test
    @DisplayName("InvalidProductException exposes the message that the API returns")
    void shouldExposeMessage() {
        InvalidProductException exception = new InvalidProductException("Product not found with id: 7");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found with id: 7");
    }
}
