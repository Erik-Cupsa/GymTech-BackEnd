package ca.mcgill.ecse321.gymregistration.service.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class GRSException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;

    /**
     * getStatus: Get the current http status
     * @return the httpStatus
     */
    public HttpStatus getStatus() {
        return httpStatus;
    }

    /**
     * GRSException: Gym Registration System Exception class
     * @param httpStatus : Http status of exception
     * @param response : The response message
     */
    public GRSException(HttpStatus httpStatus, String response){
        super(response);
        this.httpStatus = httpStatus;
    }
}
