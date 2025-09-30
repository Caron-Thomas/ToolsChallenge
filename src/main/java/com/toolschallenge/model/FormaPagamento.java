package com.toolschallenge.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.toolschallenge.enuns.TipoDePagamento;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record FormaPagamento(

        @Enumerated(EnumType.STRING)
        TipoDePagamento tipo,
        @JsonSerialize(using = ToStringSerializer.class)
        int parcelas
)  {}
