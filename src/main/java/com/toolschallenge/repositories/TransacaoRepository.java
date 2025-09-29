package com.toolschallenge.repositories;

import com.toolschallenge.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT t FROM Transacao t")
    Page<Transacao> buscaTransacoesPaginado(Pageable pg);
}
