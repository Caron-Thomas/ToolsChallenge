package com.toolschallenge.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

public abstract class TransacaoSort {
    private static final List<String> SORT_LIST = Arrays.asList(
            "valor",
            "dataHora",
            "estabelecimento"
    );

    public static Pageable ajustaTransacaoSort(Pageable pageable) {
        Sort sortInicial = pageable.getSort();
        Sort sortAjustada = Sort.unsorted();

        for (Sort.Order order : sortInicial) {
            String atributoSort = order.getProperty();


            if (SORT_LIST.contains(atributoSort)) {
                String atributoSortAjustado = "descricao." + atributoSort;

                sortAjustada = sortAjustada.and(Sort.by(order.getDirection(), atributoSortAjustado));
            } else {
                sortAjustada = sortAjustada.and(Sort.by(order.getDirection(), atributoSort));
            }
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortAjustada
        );
    }
}