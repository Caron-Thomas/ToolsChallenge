package com.toolschallenge.exceptions;

import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            PropertyReferenceException.class,
            InvalidDataAccessApiUsageException.class
    })
    public ResponseEntity<String> handleOrdenacaoNaoSuportadaException(RuntimeException ex) {
        return new ResponseEntity<>("Campo ordenação não suportado. Verifique os campos.(id, estabelecimento, dataHora, valor, cartao) ", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransacaoNotFoundException.class)
    public ResponseEntity<String> handlerTransacaoNotFoundException(TransacaoNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            TransacaoNotSupportedException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<String> handlerTransacaoNotSupportedException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectorStartFailedException.class)
    public ResponseEntity<String> handlerProblemaComunicacaoException(ProblemaComunicacaoException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

   /* @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Erro interno do servidor. Consulte os logs.", HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}
