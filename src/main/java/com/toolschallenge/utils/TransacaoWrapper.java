package com.toolschallenge.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toolschallenge.dto.TransacaoDTO;

public record TransacaoWrapper(
        @JsonProperty("transacao")
        TransacaoDTO transacao
) {}
