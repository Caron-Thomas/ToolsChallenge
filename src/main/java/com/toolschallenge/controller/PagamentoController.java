package com.toolschallenge.controller;

import com.toolschallenge.dto.TransacaoDTO;
import com.toolschallenge.service.TransacaoService;
import com.toolschallenge.utils.TransacaoWrapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagamentos")
public class PagamentoController {

    private final TransacaoService transacaoService;

    public PagamentoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    Page<TransacaoWrapper> buscaTodas(@PageableDefault(sort = "id") Pageable pageable ) {
        return transacaoService.buscaTodas(pageable);
    }

    @GetMapping("/{id}")
    ResponseEntity<TransacaoWrapper> buscaPorId(@PathVariable Long id) {
        TransacaoWrapper transacao = transacaoService.buscaPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(transacao);
    }

    @PutMapping("/{id}/estorno")
    ResponseEntity<TransacaoWrapper> criaTransacaoEstorno(@PathVariable Long id) {
        TransacaoWrapper transacaoAtualizada = transacaoService.criaTransacaoEstorno(id);
        return ResponseEntity.status(HttpStatus.OK).body(transacaoAtualizada);
    }

    @PostMapping("")
    ResponseEntity<TransacaoWrapper> criar(@Valid @RequestBody TransacaoWrapper transacao) {
        TransacaoDTO dto = transacao.transacao();
        TransacaoWrapper transacaoCriada = transacaoService.criaTransacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoCriada);

    }
}
