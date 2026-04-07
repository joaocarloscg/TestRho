package pt.rho.exchangerate.web;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.dto.ErrorResponse;

@Component
@RequiredArgsConstructor
public class ErrorResponseFactory {

    public ErrorResponse create(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);
    }

    public ResponseEntity<ErrorResponse> createResponse(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(create(status, message, path));
    }
}