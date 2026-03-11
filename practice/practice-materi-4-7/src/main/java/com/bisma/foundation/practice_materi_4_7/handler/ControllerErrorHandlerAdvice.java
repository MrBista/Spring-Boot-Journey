package com.bisma.foundation.practice_materi_4_7.handler;

import com.bisma.foundation.practice_materi_4_7.exceptions.BadRequestException;
import com.bisma.foundation.practice_materi_4_7.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ControllerErrorHandlerAdvice {


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> badRequestHandler(BadRequestException ex, HttpServletRequest req) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("traceId", MDC.get("traceId"));
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> badRequestHandler(NotFoundException ex, HttpServletRequest req) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("traceId", MDC.get("traceId"));
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> unhandleErrorHandler(Exception e, HttpServletRequest req) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Terjadi kesalahn pada server. Silakan coba beberapa saat lagi"
        );

        return ResponseEntity
                .internalServerError()
                .body(problemDetail);
    }
}
