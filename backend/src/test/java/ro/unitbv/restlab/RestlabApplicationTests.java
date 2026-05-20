package ro.unitbv.restlab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
@DisplayName("RestLab application smoke test")
class RestLabApplicationTests {

    @Test
    void contextLoads() {
        try (var context = new SpringApplicationBuilder(RestlabApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("test")
                .run()) {
            assertThat(context).isNotNull();
        }
    }
}
