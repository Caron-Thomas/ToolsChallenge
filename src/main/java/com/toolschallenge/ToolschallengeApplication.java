package com.toolschallenge;

import com.toolschallenge.dto.TransacaoDTO;
import com.toolschallenge.enuns.StatusDoPagamento;
import com.toolschallenge.enuns.TipoDePagamento;
import com.toolschallenge.model.Descricao;
import com.toolschallenge.model.FormaPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class ToolschallengeApplication {

    private static final Logger log = LoggerFactory.getLogger(ToolschallengeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ToolschallengeApplication.class, args);
	}

    @Bean
    CommandLineRunner runner() {
        return args -> {
            TransacaoDTO t = new TransacaoDTO(1L,"1234 **** **** 1234", new Descricao(
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
            log.info("Transaçao: "+t);
        };
    }
}
