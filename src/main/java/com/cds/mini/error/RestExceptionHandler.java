package com.cds.mini.error;

import com.cds.mini.model.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handle for the unexpected error for the request.
 */
@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({ ServiceException.class })
    public final ResponseEntity<Error> handleException(ServiceException ex, WebRequest request) {
        Error error = new Error()
                .code(ex.getError().getCode())
                .message(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
