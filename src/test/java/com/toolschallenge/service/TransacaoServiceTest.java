package com.toolschallenge.service;

import com.toolschallenge.dto.TransacaoDTO;
import com.toolschallenge.enuns.StatusDoPagamento;
import com.toolschallenge.enuns.TipoDePagamento;
import com.toolschallenge.exceptions.ProblemaComunicacaoException;
import com.toolschallenge.exceptions.TransacaoJaExisteException;
import com.toolschallenge.exceptions.TransacaoNotFoundException;
import com.toolschallenge.model.Descricao;
import com.toolschallenge.model.FormaPagamento;
import com.toolschallenge.model.Transacao;
import com.toolschallenge.repositories.TransacaoRepository;
import com.toolschallenge.utils.SimulaServicoExternoAutorizacao;
import com.toolschallenge.utils.TransacaoWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {
    @Mock
    private TransacaoRepository repository;

    @Mock
    private SimulaServicoExternoAutorizacao servicoExternoAutorizacao;

    @InjectMocks
    private TransacaoService transacaoService;

    private Transacao transacaoAutorizada;
    private TransacaoDTO transacaoDtoAutorizada;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        Descricao descricao = new Descricao(
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "Loja Teste",
                null,
                null,
                StatusDoPagamento.AUTORIZADO
        );

        FormaPagamento formaPagamento = new FormaPagamento(TipoDePagamento.AVISTA, 1);

        this.transacaoAutorizada = new Transacao();
        this.transacaoAutorizada.setTransacaoId(1L);
        this.transacaoAutorizada.setCartao("1234 **** 4321");
        this.transacaoAutorizada.setDescricao(descricao);
        this.transacaoAutorizada.setFormaPagamento(formaPagamento);
        this.transacaoAutorizada.setDataCriado(LocalDateTime.now());

        this.transacaoDtoAutorizada = new TransacaoDTO(
                1L,
                "1234 **** 4321",
                descricao,
                formaPagamento
        );

        this.pageable = PageRequest.of(0, 5, Sort.by("id").descending());
    }

    @Test
    void deveriaRetornarTransacoesPaginadas() {
        List<Transacao> transacoes = Collections.singletonList(transacaoAutorizada);
        Page<Transacao> paginaMock = new PageImpl<>(transacoes, pageable, 1);
        when(repository.buscaTransacoesPaginado(any(Pageable.class))).thenReturn(paginaMock);

        Page<TransacaoWrapper> resultado = transacaoService.buscaTodas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(transacaoAutorizada.getTransacaoId(), resultado.getContent().get(0).transacao().id());

        verify(repository, times(1)).buscaTransacoesPaginado(any(Pageable.class));
    }

    @Test
    void deveriaRetornarTransacaoExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(transacaoAutorizada));

        TransacaoWrapper resultado = transacaoService.buscaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.transacao().id());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deveriaLancarTransacaoNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TransacaoNotFoundException.class, () -> {
            transacaoService.buscaPorId(99L);
        });

        verify(repository, times(1)).findById(99L);
    }

    @Test
    void deveriaCriarComStatusAutorizado() {
        Map.Entry<Long, String> sucesso = new AbstractMap.SimpleEntry<>(1234L, "AUTORIZADO");

        when(repository.existsById(anyLong())).thenReturn(false);
        when(servicoExternoAutorizacao.simularConsultaAprovacao()).thenReturn(sucesso);
        when(repository.save(any(Transacao.class))).thenReturn(transacaoAutorizada);

        TransacaoWrapper resultado = transacaoService.criaTransacao(transacaoDtoAutorizada);

        assertEquals(StatusDoPagamento.AUTORIZADO, resultado.transacao().descricao().getStatusDoPagamento());
        assertEquals(1234L, resultado.transacao().descricao().getCodigoAutorizacao());
        assertNotNull(resultado.transacao().descricao().getNsu());

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).save(any(Transacao.class));
        verify(servicoExternoAutorizacao, times(1)).simularConsultaAprovacao();
    }

    @Test
    void deveriaLancarTransacaoJaExisteException() {
        when(repository.existsById(1L)).thenReturn(true);

        assertThrows(TransacaoJaExisteException.class, () -> {
            transacaoService.criaTransacao(transacaoDtoAutorizada);
        });

        verify(repository, times(1)).existsById(1L);
    }

    @Test
    void deveriaLancarProblemaComunicacaoException() {
        when(repository.existsById(anyLong())).thenReturn(false);
        when(servicoExternoAutorizacao.simularConsultaAprovacao()).thenThrow(new ConnectorStartFailedException(443));

        assertThrows(ProblemaComunicacaoException.class, () -> {
            transacaoService.criaTransacao(transacaoDtoAutorizada);
        });

        verify(repository, times(1)).existsById(1L);
    }

    @Test
    void deveriaCriarEstornoComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(transacaoAutorizada));
        when(repository.save(any(Transacao.class))).thenReturn(transacaoAutorizada);

        TransacaoWrapper resultado = transacaoService.criaTransacaoEstorno(1L);

        assertEquals(StatusDoPagamento.CANCELADO, resultado.transacao().descricao().getStatusDoPagamento());
        assertNotNull(transacaoAutorizada.getDataAtualizado());

        verify(repository).save(transacaoAutorizada);
        assertEquals(StatusDoPagamento.CANCELADO, transacaoAutorizada.getDescricao().getStatusDoPagamento());
    }
}