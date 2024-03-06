/*
 * Copyright (c) 2022, Robert Bosch GmbH and its subsidiaries.
 * This program and the accompanying materials are made available under
 * the terms of the Bosch Internal Open Source License v4
 * which accompanies this distribution, and is available at
 * http://bios.intranet.bosch.com/bioslv4.txt
 */

package com.bosch.eet.skill.management.exception;

import java.time.LocalDateTime;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LUK1HC
 */

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(EETResponseException.class)
    public ResponseEntity<ErrorMessage> handleResponseException(final EETResponseException ex, WebRequest webRequest) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .path(((ServletWebRequest) webRequest).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus())
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        errorMessage.setCause(null);
        return new ResponseEntity<>(
                errorMessage,
                HttpStatus.valueOf(Integer.parseInt(ex.getCode())));
    }

    // Handle Business Exceptions
    @ExceptionHandler(SkillManagementException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleSkillManagementException(final SkillManagementException ex) {

        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(ex.getCode())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(UserManagementException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUserManagementException(final UserManagementException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(ex.getCode())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleAccessDeniedException(final AccessDeniedException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(MessageCode.NOT_AUTHORIZATION.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(UserManagementBusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUserManagementBusinessException(final UserManagementBusinessException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(ex.getCode())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    //    handle exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(
                        messageSource.getMessage(
                                MessageCode.INTERNAL_SERVER_ERROR.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ))
                .timestamp(LocalDateTime.now())
                .code(MessageCode.INTERNAL_SERVER_ERROR.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler({DataAccessException.class, EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleDataAccessException(final DataAccessException ex, WebRequest webRequest) {
        final String code = MessageCode.DATA_ACCESS_ERROR.toString();
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(ex.getMessage())
                .build();
        log.error(errorMessage.getMessage(), ex);
        return errorMessage;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentException(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        FieldError fieldError = ex.getFieldError();
        if (fieldError != null) {
            sb.append(fieldError.getField());
            sb.append(" ");
            sb.append(fieldError.getDefaultMessage());
        }

        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(sb.toString())
                .timestamp(LocalDateTime.now())
                .code(MessageCode.VALIDATION_ERRORS.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleIllegalArgumentException(IllegalArgumentException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(
                        messageSource.getMessage(
                                MessageCode.INTERNAL_SERVER_ERROR.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        ))
                .timestamp(LocalDateTime.now())
                .code(MessageCode.INTERNAL_SERVER_ERROR.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(LdapException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUserManagementBusinessException(final LdapException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(
                        messageSource.getMessage(
                                MessageCode.CAN_NOT_CONNECT_LDAP_SERVER.toString(),
                                null,
                                LocaleContextHolder.getLocale()
                        )
                )
                .timestamp(LocalDateTime.now())
                .code(MessageCode.CAN_NOT_CONNECT_LDAP_SERVER.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return errorMessage;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.NOT_FOUND.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                errorMessage,
                HttpStatus.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorMessage> handleResourceExitsException(EntityExistsException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.CONFLICT.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                errorMessage,
                HttpStatus.valueOf(HttpStatus.CONFLICT.value()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.toString())
                .build();

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                errorMessage,
                HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
    
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorMessage> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex) {
		final ErrorMessage errorMessage = ErrorMessage.builder().message(ex.getMessage()).timestamp(LocalDateTime.now())
				.code(HttpStatus.BAD_REQUEST.toString()).build();

		log.error(ex.getMessage(), ex);

		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
	}

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorMessage> handleMaxFileSizeExceedException(
            MaxUploadSizeExceededException ex) {
            final ErrorMessage errorMessage = ErrorMessage.builder()
                .message(
                    messageSource.getMessage(
                            MessageCode.MAXIMUM_UPLOAD_SIZE_EXCEEDED.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ))
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.BAD_REQUEST.toString())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}
