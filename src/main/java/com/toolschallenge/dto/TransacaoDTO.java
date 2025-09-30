package com.toolschallenge.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.toolschallenge.model.Descricao;
import com.toolschallenge.model.FormaPagamento;

public record TransacaoDTO(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String cartao,
        Descricao descricao,
        FormaPagamento formaPagamento
) {}
