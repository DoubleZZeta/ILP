package uk.ac.ed.acp.cw2.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception Handler class that deals with returning message for different status codes.
 * By making the return value void for status code 400,
 * the application return nothing but the error code for any bad request.
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
<<<<<<< HEAD
    // Use this part for submission
    @ExceptionHandler(Exception.class)
=======
//    // Use this part for submission
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public void handleValidationException(Exception ex)
//    {
//        //No return value
//    }

    //Use this part of code for debug
    @ExceptionHandler(MethodArgumentNotValidException.class)
>>>>>>> 5365566ebb5c426dc62c9641b83acc4aacc3bfa1
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleValidationException(MethodArgumentNotValidException ignoredEx)
    {

    }

<<<<<<< HEAD
    //Use this part of code for debug
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public void handleValidationException(MethodArgumentNotValidException ignoredEx)
//    {
//
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public void handleAllOtherExceptions(Exception ex)
//    {
//        System.err.println("Unexpected error: " + ex.getMessage());
//    }
=======
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleAllOtherExceptions(Exception ex)
    {
        System.err.println("Unexpected error: " + ex.getMessage());
    }
>>>>>>> 5365566ebb5c426dc62c9641b83acc4aacc3bfa1
}
