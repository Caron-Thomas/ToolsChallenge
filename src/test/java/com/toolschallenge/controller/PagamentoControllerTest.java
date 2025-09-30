package com.toolschallenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolschallenge.dto.TransacaoDTO;
import com.toolschallenge.enuns.StatusDoPagamento;
import com.toolschallenge.enuns.TipoDePagamento;
import com.toolschallenge.exceptions.ProblemaComunicacaoException;
import com.toolschallenge.exceptions.TransacaoJaExisteException;
import com.toolschallenge.exceptions.TransacaoNotFoundException;
import com.toolschallenge.exceptions.TransacaoNotSupportedException;
import com.toolschallenge.model.Descricao;
import com.toolschallenge.model.FormaPagamento;
import com.toolschallenge.service.TransacaoService;
import com.toolschallenge.utils.TransacaoWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TransacaoService transacaoService;

    private final List<TransacaoWrapper> transacoes = new ArrayList<>();

    @BeforeEach
    void setUp(){
        TransacaoDTO t1 = new TransacaoDTO(1L,"1234 **** **** 1234", new Descricao(
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Josefa Lojas",
                123L,
                123L,
                StatusDoPagamento.AUTORIZADO
        ), new FormaPagamento(
                TipoDePagamento.AVISTA,
                1
        ));
        TransacaoDTO t2 = new TransacaoDTO(2L,"1234 **** **** 1234", new Descricao(
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Josefa Lojas",
                123L,
                123L,
                StatusDoPagamento.CANCELADO
        ), new FormaPagamento(
                TipoDePagamento.AVISTA,
                1
        ));
        transacoes.add(new TransacaoWrapper(t1));
        transacoes.add(new TransacaoWrapper(t2));
    }

    @Test
    void deveriaBuscarTodasTransacoes() throws Exception {
        Page<TransacaoWrapper> transacoesPaginadas = new PageImpl<>(this.transacoes);
        when(transacaoService.buscaTodas(any(Pageable.class))).thenReturn(transacoesPaginadas);
        mockMvc.perform(get("/api/v1/pagamentos")
                        .param("page", "0")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(transacaoService).buscaTodas(any(Pageable.class));
    }

    @Test
    void deveriaBuscarUmaPorId() throws Exception {
        TransacaoWrapper transacao = this.transacoes.getFirst();
        when(transacaoService.buscaPorId(anyLong())).thenReturn(transacao);
        mockMvc.perform(get("/api/v1/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.id", is(transacao.transacao().id().toString())))
                .andExpect(jsonPath("$.transacao.descricao.nsu", is(transacao.transacao().descricao().getNsu().toString())))
                .andExpect(jsonPath("$.transacao.formaPagamento.tipo", is(transacao.transacao().formaPagamento().tipo().toString())));
    }

    @Test
    void deveriaRetornarTransacaoNotFoundException() throws Exception {
        when(transacaoService.buscaPorId(anyLong()))
                .thenThrow(new TransacaoNotFoundException(99L));
        mockMvc.perform(get("/api/v1/pagamentos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transação não localizada. ID: 99"));
    }

    @Test
    void deveriaCriaTransacaoEstorno() throws Exception {
        var transacaoAtualizada = this.transacoes.getLast();
        when(transacaoService.criaTransacaoEstorno(anyLong())).thenReturn(transacaoAtualizada);
        mockMvc.perform(put("/api/v1/pagamentos/2/estorno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.id", is("2")))
                .andExpect(jsonPath("$.transacao.descricao.nsu", is("123")))
                .andExpect(jsonPath("$.transacao.formaPagamento.tipo", is("AVISTA")))
                .andExpect(jsonPath("$.transacao.descricao.statusDoPagamento", is("CANCELADO")));
    }

    @Test
    void deveriaCriarNovaTransacao() throws Exception {
        var transacaoCriada = this.transacoes.getFirst();
        when(transacaoService.criaTransacao(any(TransacaoDTO.class))).thenReturn(transacaoCriada);
        mockMvc.perform(post("/api/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoCriada)))
                .andExpect(status().isCreated());
        verify(transacaoService).criaTransacao(any(TransacaoDTO.class));
    }

    @Test
    void deveriaRetornarTransacaoNotSupportedException() throws Exception {
        var transacaoNaoCriada = this.transacoes.getFirst();
        when(transacaoService.criaTransacao(any(TransacaoDTO.class))).thenThrow(new TransacaoNotSupportedException("APRAZO"));
        mockMvc.perform(post("/api/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoNaoCriada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transação não suportada: APRAZO"));
        verify(transacaoService).criaTransacao(any(TransacaoDTO.class));
    }

    @Test
    void deveriaRetornarTransacaoJaExisteException() throws Exception {
        var transacaoNaoCriada = this.transacoes.getFirst();
        when(transacaoService.criaTransacao(any(TransacaoDTO.class))).thenThrow(new TransacaoJaExisteException(transacaoNaoCriada.transacao().id()));
        mockMvc.perform(post("/api/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoNaoCriada)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Transação com ID 1 já foi registrada."));
    }

    @Test
    void deveriaRetornarProblemaComunicacaoException() throws Exception {
        var transacaoNaoCriada = this.transacoes.getFirst();
        when(transacaoService.criaTransacao(any(TransacaoDTO.class))).thenThrow(new ProblemaComunicacaoException());
        mockMvc.perform(post("/api/v1/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoNaoCriada)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço indisponível no momento. Por favor, tente novamente mais tarde."));
        verify(transacaoService).criaTransacao(any(TransacaoDTO.class));
    }
}