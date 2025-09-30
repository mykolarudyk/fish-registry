package com.example.backend.common;

import jakarta.validation.ConstraintViolationException;
import java.util.*;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation failed");
    Map<String, List<String>> errors = new LinkedHashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      errors.computeIfAbsent(fe.getField(), k -> new ArrayList<>()).add(fe.getDefaultMessage());
    }
    pd.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Constraint violation");
    pd.setProperty("errors", ex.getConstraintViolations().stream()
        .collect(java.util.stream.Collectors.groupingBy(
            v -> v.getPropertyPath().toString(),
            java.util.stream.Collectors.mapping(v -> v.getMessage(), java.util.stream.Collectors.toList())
        )));
    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler({ NoSuchElementException.class })
  public ResponseEntity<ProblemDetail> handleNotFound(RuntimeException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setTitle("Not found");
    pd.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Invalid parameter");
    pd.setDetail("Parameter '%s' is invalid: %s".formatted(ex.getName(), String.valueOf(ex.getValue())));
    return ResponseEntity.badRequest().body(pd);
  }
}
