package gr.leonzch.sudoku.utils.errors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleError(Exception e, HttpServletRequest request) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "path", request.getRequestURI(),
                "message", e.getMessage()
        );
    }
}
