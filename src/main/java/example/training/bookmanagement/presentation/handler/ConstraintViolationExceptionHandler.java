package example.training.bookmanagement.presentation.handler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ConstraintViolationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        return null;
    }
//    public ResponseEntity handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
//        return new ResponseEntity(HttpStatus.BAD_REQUEST);
//    }
}
