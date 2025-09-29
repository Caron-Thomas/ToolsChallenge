package com.toolschallenge.dto;

import com.toolschallenge.model.Descricao;
import com.toolschallenge.model.FormaPagamento;

public record TransacaoDTO(
        Long id,
        String cartao,
        Descricao descricao,
        FormaPagamento formaPagamento
) {}
