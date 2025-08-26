package org.orcid.frontend.web.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(assignableTypes = WorksController.class)
public class RestExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("error", "non_public_works_selected");
        body.put("message", ex.getMessage());
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}


