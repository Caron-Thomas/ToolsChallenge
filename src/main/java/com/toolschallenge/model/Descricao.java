package com.toolschallenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.toolschallenge.enuns.StatusDoPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public class Descricao {

    @NotNull
    @NotEmpty
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal valor;

    @NotEmpty
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;

    @NotEmpty
    private String estabelecimento;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long nsu;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long codigoAutorizacao;

    @Enumerated(EnumType.STRING)
    private StatusDoPagamento statusDoPagamento;

    public Descricao() {
    }

    public Descricao(BigDecimal valor, LocalDateTime dataHora, String estabelecimento, Long nsu, Long codigoAutorizacao, StatusDoPagamento statusDoPagamento) {
        this.valor = valor;
        this.dataHora = dataHora;
        this.estabelecimento = estabelecimento;
        this.nsu = nsu;
        this.codigoAutorizacao = codigoAutorizacao;
        this.statusDoPagamento = statusDoPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public Long getNsu() {
        return nsu;
    }

    public void setNsu(Long nsu) {
        this.nsu = nsu;
    }

    public Long getCodigoAutorizacao() {
        return codigoAutorizacao;
    }

    public void setCodigoAutorizacao(Long codigoAutorizacao) {
        this.codigoAutorizacao = codigoAutorizacao;
    }

    public StatusDoPagamento getStatusDoPagamento() {
        return statusDoPagamento;
    }

    public void setStatusDoPagamento(StatusDoPagamento statusDoPagamento) {
        this.statusDoPagamento = statusDoPagamento;
    }
}
