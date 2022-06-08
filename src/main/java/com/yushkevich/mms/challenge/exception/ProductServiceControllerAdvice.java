package com.yushkevich.mms.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProductServiceControllerAdvice {

  @ResponseBody
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String notFoundHandler(NotFoundException ex) {
    return ex.getMessage();
  }

  @ResponseBody
  @ExceptionHandler(CustomInterruptedException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  String interruptedHandler(CustomInterruptedException ex) {
    return ex.getMessage();
  }
}
