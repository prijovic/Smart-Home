package com.bsep.smart.home.exception;

import com.bsep.smart.home.model.Person;
import com.bsep.smart.home.model.events.ErrorEvent;
import com.bsep.smart.home.services.auth.GetLoggedInUser;
import com.bsep.smart.home.translations.Translator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    private final KieSession kieSession;
    private final GetLoggedInUser getLoggedInUser;

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            PasswordSameException.class,
            PasswordMismatchException.class,
            InvalidSortUserFieldException.class})
    protected ResponseEntity<?> handleBadRequestExceptions(CustomRuntimeException ex) {
        return buildResponseEntity(new ApiException(Translator.toLocale(ex.getKey()), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({
            AuthTokenExpiredException.class,
            AuthTokenInvalidException.class,
            UnauthorizedException.class,
            FingerprintInvalidException.class,
            SigningAlgorithmInvalidException.class,
            InvalidPinException.class,
            LockedAccountException.class
    })
    protected ResponseEntity<?> handleUnauthorizedExceptions(CustomRuntimeException ex) {
        return buildResponseEntity(new ApiException(Translator.toLocale(ex.getKey()), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler({AccessDeniedException.class, EmailNotVerifiedException.class, ForbiddenException.class})
    protected ResponseEntity<?> handleAccessDeniedException() {
        return buildResponseEntity(new ApiException(Translator.toLocale(ExceptionKeys.INSUFFICIENT_PERMISSIONS), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler({
            RoleNotFoundException.class,
            UserNotFoundException.class,
            PropertyNotFoundException.class
    })
    protected ResponseEntity<?> handleNotFoundExceptions(CustomRuntimeException ex) {
        return buildResponseEntity(new ApiException(Translator.toLocale(ex.getKey()), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<?> handleBadCredentialsException() {
        return buildResponseEntity(new ApiException(Translator.toLocale(ExceptionKeys.BAD_LOGIN_CREDENTIALS), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<?> handleInsufficientAuthenticationException() {
        return buildResponseEntity(new ApiException(Translator.toLocale(ExceptionKeys.MISSING_AUTHENTICATION), HttpStatus.UNAUTHORIZED));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>(ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage
        ).toList());
        errors.addAll(ex.getBindingResult().getGlobalErrors().stream().map((err) ->
                err.getObjectName() + " " + err.getDefaultMessage()
        ).toList());

        errors.forEach(error -> {
            logger.error("Validation error: {}", error);
        });

        String errorMessage = String.join(", ", errors);

        return (ResponseEntity<Object>) buildResponseEntity(new ApiException(errorMessage, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<?> defaultExceptionHandler(Throwable t) throws UnauthorizedException {
        ErrorEvent errorEvent = ErrorEvent.builder().message(t.getMessage()).build();
        Person loggedInUser = getLoggedInUser.execute();
        errorEvent.setUserEmail(loggedInUser.getEmail());
        kieSession.insert(errorEvent);
        kieSession.fireAllRules();
        logger.error("Unhandled exception: " + Strings.join(Arrays.asList(t.getStackTrace()), '\n'));
        return buildResponseEntity(new ApiException("Unhandled exception", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<?> buildResponseEntity(final ApiException err) {
        return new ResponseEntity<>(err, err.getStatus());
    }

}
