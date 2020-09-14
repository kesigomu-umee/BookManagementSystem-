package example.training.bookmanagement.presentation.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AllExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Object> handleAllException(Exception e, WebRequest request) {
        return super.handleExceptionInternal(e, null, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
