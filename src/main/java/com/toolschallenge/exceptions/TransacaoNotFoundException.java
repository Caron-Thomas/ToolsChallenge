package com.toolschallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TransacaoNotFoundException extends RuntimeException {

    public TransacaoNotFoundException(long e) {
        super("Transação não localizada. ID: " + e);
    }
}
