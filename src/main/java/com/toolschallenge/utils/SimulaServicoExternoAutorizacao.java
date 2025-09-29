package com.toolschallenge.utils;

import org.hibernate.annotations.Comment;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

@Component
public class SimulaServicoExternoAutorizacao {

    public Map.Entry<Long, String> simularConsultaAprovacao() throws ConnectorStartFailedException {

        Long codigoAutorizacao = GeradorNumerosRandomicos.gerarLong();
        int resultadoConsulta = GeradorNumerosRandomicos.gerarInteger(100 + 1 );
        if(resultadoConsulta == 0 || resultadoConsulta == 50 || resultadoConsulta == 100) {
            throw new ConnectorStartFailedException(443);
        }

        String resultado = resultadoConsulta >= 96 ? "NEGADO" : "AUTORIZADO";

        return new AbstractMap.SimpleEntry<>(codigoAutorizacao,resultado);

    }
}
