package com.toolschallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TransacaoJaExisteException extends RuntimeException {

    public TransacaoJaExisteException(Long id) {
        super("Transação com ID " + id + " já foi registrada.");
    }
}
