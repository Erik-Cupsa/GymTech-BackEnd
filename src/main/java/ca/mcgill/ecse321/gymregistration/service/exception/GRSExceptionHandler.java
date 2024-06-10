package ca.mcgill.ecse321.gymregistration.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GRSExceptionHandler {
    /**
     * handleGRSException: handling the grs exception
     * @param e : The grs exception
     * @return response entity of error message and status
     */
    @ExceptionHandler(GRSException.class)
    public ResponseEntity<String> handleGRSException(GRSException e) {
        // constructing the error response based on the GRSException
        String errorMessage = e.getMessage();
        HttpStatus status = e.getStatus();
        return new ResponseEntity<>(errorMessage, status);
    }

    /**
     * handleValidationException: handling method argument not valid exception
     * @param e: exception
     * @return Response entity of response message and status code
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e){
        String response = "";
        for (FieldError fieldError: e.getBindingResult().getFieldErrors()){
            response += fieldError.getDefaultMessage() + "\n";
        }
        return new ResponseEntity<>(response, e.getStatusCode());
    }
}
