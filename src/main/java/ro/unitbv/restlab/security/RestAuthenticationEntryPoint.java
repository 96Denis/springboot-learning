package ro.unitbv.restlab.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = String.format(
                "{\"status\": %d, \"error\": \"Unauthorized\", \"message\": \"%s\", \"path\": \"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED,
                authException.getMessage().replace("\"", "\\\""),
                request.getServletPath()
        );

        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }
}
