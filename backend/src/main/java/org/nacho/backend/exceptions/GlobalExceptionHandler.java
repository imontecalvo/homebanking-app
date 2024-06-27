package org.nacho.backend.exceptions;

import org.hibernate.resource.jdbc.internal.ResourceRegistryStandardImpl;
import org.nacho.backend.dtos.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UnavailableField.class)
    public ResponseEntity<ErrorDTO> UnavailableFieldException(UnavailableField e){
        ErrorDTO error = new ErrorDTO(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorDTO> ResourceNotFoundException(ResourceNotFound e){
        ErrorDTO error = new ErrorDTO(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
