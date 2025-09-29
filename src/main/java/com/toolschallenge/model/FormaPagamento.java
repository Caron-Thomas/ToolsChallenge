package com.toolschallenge.model;

import com.toolschallenge.enuns.TipoDePagamento;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record FormaPagamento(

        @Enumerated(EnumType.STRING)
        TipoDePagamento tipo,
        int parcelas
)  {}
