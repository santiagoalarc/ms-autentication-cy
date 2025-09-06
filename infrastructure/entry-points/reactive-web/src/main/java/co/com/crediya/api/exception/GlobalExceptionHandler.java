package co.com.crediya.api.exception;

import co.com.crediya.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDeniedException(AuthorizationDeniedException exception) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = exception.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    @ExceptionHandler(UserException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserException(UserException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    @ExceptionHandler(ValidateModelException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleModelException(ValidateModelException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ha ocurrido un error interno del servidor.",
                ex.getMessage()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}