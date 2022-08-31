package com.example.springbootimportsqlite.core.repository.datafilter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe com informações de requisição de uma operação de sumarização.
 */
@Setter
@Getter
@EqualsAndHashCode(of = {"dataField", "operation"})
@ToString(of = {"dataField", "operation"})
public class SummaryRequest {

    /**
     * Nome do atributo que sofrerá a operação de sumarização.
     */
    private String dataField;

    /**
     * Tipo de sumarização a ser realizada.
     *
     * @see SummaryOperation
     */
    private SummaryOperation operation;


}