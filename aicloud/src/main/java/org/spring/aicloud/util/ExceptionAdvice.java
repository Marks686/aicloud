package org.spring.aicloud.util;


import org.bouncycastle.pqc.crypto.util.PQCOtherInfoGenerator;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(BindException.class)
    public ResponseEntity handleException(BindException e) {
        return ResponseEntity.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
