package com.toolschallenge.service;

import com.toolschallenge.dto.TransacaoDTO;
import com.toolschallenge.enuns.StatusDoPagamento;
import com.toolschallenge.exceptions.ProblemaComunicacaoException;
import com.toolschallenge.exceptions.TransacaoJaExisteException;
import com.toolschallenge.exceptions.TransacaoNotFoundException;
import com.toolschallenge.model.Transacao;
import com.toolschallenge.repositories.TransacaoRepository;
import com.toolschallenge.utils.GeradorNumerosRandomicos;
import com.toolschallenge.utils.SimulaServicoExternoAutorizacao;
import com.toolschallenge.utils.TransacaoSort;
import com.toolschallenge.utils.TransacaoWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);
    private final TransacaoRepository repository;
    private final SimulaServicoExternoAutorizacao servicoExternoAutorizacao;

    public TransacaoService(TransacaoRepository repository, SimulaServicoExternoAutorizacao servicoExternoAutorizacao) {
        this.repository = repository;
        this.servicoExternoAutorizacao = servicoExternoAutorizacao;
    }

    public Page<TransacaoWrapper> buscaTodas(Pageable pg) {
        Pageable pgComSortAjustada = TransacaoSort.ajustaTransacaoSort(pg);

        Page<TransacaoWrapper> transacaoWrapperPage = repository.buscaTransacoesPaginado(pgComSortAjustada)
                .map(this::parseToDTO)
                .map(TransacaoWrapper::new);
        return transacaoWrapperPage;
    }

    public TransacaoWrapper buscaPorId(Long id) {
        Optional<Transacao> resultado = findById(id);
        return new TransacaoWrapper(parseToDTO(resultado.get()));
    }

    public TransacaoWrapper criaTransacao(TransacaoDTO dto) {
        log.info("[INFO] Iniciando tentaiva de transação PAGAMENTO id: " + dto.id());

        if(repository.existsById(dto.id())) {
            log.error("[ERRO] Tentativa de Transação duplicada para o ID: {}", dto.id());
            throw new TransacaoJaExisteException(dto.id());
        }

        Transacao transacao = parseToEntity(dto);
        try {
            var resultado = servicoExternoAutorizacao.simularConsultaAprovacao();
            transacao.getDescricao().setStatusDoPagamento(
                    StatusDoPagamento.valueOf(resultado.getValue().toString()));
            transacao.getDescricao().setCodigoAutorizacao(resultado.getKey());
        } catch ( ConnectorStartFailedException e) {
            log.error("[ERRO] Comunicação com serviço de AUTORIZAÇÃO falhou para ID: {}", dto.id());
            throw new ProblemaComunicacaoException();
        }

        transacao.getDescricao().setNsu(GeradorNumerosRandomicos.gerarLong());
        transacao.setDataCriado(LocalDateTime.now());

        repository.save(transacao);
        log.info("[INFO] Transação PAGAMENTO finalizada com sucesso id: " + dto.id());
        return new TransacaoWrapper(parseToDTO(transacao));
    }

    public TransacaoWrapper criaTransacaoEstorno(long id) {
        Transacao transacao = findById(id).get();
        LocalDateTime agora = LocalDateTime.now();
        transacao.getDescricao().setCodigoAutorizacao(GeradorNumerosRandomicos.gerarLong());
        transacao.getDescricao().setStatusDoPagamento(StatusDoPagamento.CANCELADO);
        transacao.setDataAtualizado(agora);

        repository.save(transacao);
        transacao.getDescricao().setDataHora(agora);
        return new TransacaoWrapper(parseToDTO(transacao));
    }

    private Optional<Transacao> findById(long id) {
        Optional<Transacao> transacao = repository.findById(id);

        if(transacao.isEmpty()) {
            throw new TransacaoNotFoundException(id);
        }
        return transacao;
    }

    private Transacao parseToEntity(TransacaoDTO dto) {
        Transacao transacao = new Transacao();
        transacao.setTransacaoId(dto.id());
        transacao.setCartao(dto.cartao());
        transacao.setFormaPagamento(dto.formaPagamento());
        transacao.setDescricao(dto.descricao());

        return transacao;
    }

    private TransacaoDTO parseToDTO(Transacao transacao) {
        TransacaoDTO dto = new TransacaoDTO(
                transacao.getTransacaoId(),
                transacao.getCartao(),
                transacao.getDescricao(),
                transacao.getFormaPagamento()
        );
        return dto;
    }
}
