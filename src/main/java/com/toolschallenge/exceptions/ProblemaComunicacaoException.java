package com.toolschallenge.exceptions;

import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ProblemaComunicacaoException extends RuntimeException {

    public ProblemaComunicacaoException() {
        super("Serviço indisponível no momento. Por favor, tente novamente mais tarde.");
    }
}
