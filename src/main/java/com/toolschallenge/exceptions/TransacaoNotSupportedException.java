package com.toolschallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TransacaoNotSupportedException extends RuntimeException {

    public TransacaoNotSupportedException(String e) {
        super("Transação não suportada: " + e);
    }
}
