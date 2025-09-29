package com.toolschallenge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_transacao")
public class Transacao {

    @Id
    @Positive
    @NotNull
    @Column(name = "id_transacao")
    private Long transacaoId;

    @NotEmpty
    private String cartao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriado;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizado;

    @Embedded
    private Descricao descricao;
    @Embedded
    private FormaPagamento formaPagamento;

    public Transacao() {}

    public Transacao(String cartao, Descricao descricao, FormaPagamento formaPagamento) {
        this.cartao = cartao;
        this.descricao = descricao;
        this.formaPagamento = formaPagamento;
    }

    public void setTransacaoId(Long transacaoId) {
        this.transacaoId = transacaoId;
    }

    public Long getTransacaoId() {
        return transacaoId;
    }

    public String getCartao() {
        return cartao;
    }

    public void setCartao(String cartao) {
        this.cartao = cartao;
    }

    public Descricao getDescricao() {
        return descricao;
    }

    public void setDescricao(Descricao descricao) {
        this.descricao = descricao;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDateTime getDataCriado() {
        return dataCriado;
    }

    public void setDataCriado(LocalDateTime dataCriado) {
        this.dataCriado = dataCriado;
    }

    public LocalDateTime getDataAtualizado() {
        return dataAtualizado;
    }

    public void setDataAtualizado(LocalDateTime dataAtualizado) {
        this.dataAtualizado = dataAtualizado;
    }
}
