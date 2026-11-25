package com.roaddefects.api;

import com.roaddefects.dto.ErrorDto;
import com.roaddefects.service.BusinessException;
import com.roaddefects.service.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice(basePackageClasses = RestErrorHandler.class)
public class RestErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> notFound(NotFoundException e) {
        return error(HttpStatus.NOT_FOUND, List.of(e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDto> conflict(BusinessException e) {
        return error(HttpStatus.CONFLICT, List.of(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> invalidBody(MethodArgumentNotValidException e) {
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(RestErrorHandler::format)
                .toList();
        return error(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> invalidBody(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations().stream()
                .map(RestErrorHandler::format)
                .toList();
        return error(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> unreadable(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, List.of("Не удалось разобрать тело запроса: проверьте JSON/XML и типы значений."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> badParameter(MethodArgumentTypeMismatchException e) {
        return error(HttpStatus.BAD_REQUEST, List.of("Неверное значение параметра «" + e.getName() + "»."));
    }

    private static ResponseEntity<ErrorDto> error(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(new ErrorDto(status.value(), messages));
    }

    private static String format(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private static String format(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
