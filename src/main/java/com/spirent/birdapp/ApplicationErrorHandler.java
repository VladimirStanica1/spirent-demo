package com.spirent.birdapp;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import java.sql.SQLNonTransientConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler({
            DataAccessResourceFailureException.class,
            CannotGetJdbcConnectionException.class,
            CommunicationsException.class,
            SQLNonTransientConnectionException.class
    })
    public ResponseEntity<String> handleDatabaseExceptions(Exception ex) {
        return new ResponseEntity<>("An error occurred while accessing the database: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
