package recruitment_task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import reactor.core.publisher.Mono;
import recruitment_task.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(GithubUserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFoundException(GithubUserNotFoundException ex) {
        ErrorResponse responseBody = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return Mono.just(new ResponseEntity<>(responseBody, HttpStatusCode.valueOf(404)));
    }
}
