package com.example.springbootimportsqlite.core.repository;

import com.example.springbootimportsqlite.core.repository.datafilter.config.SummaryOptionsConfig;
import com.example.springbootimportsqlite.core.repository.datafilter.model.ListEx;
import com.example.springbootimportsqlite.core.repository.datafilter.model.PageEx;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryOptions;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface com os métodos padrão para todos os Repositories da aplicação.
 *
 * @param <T>  Entidade JPA.
 * @param <ID> Id da entidade T.
 */
@NoRepositoryBean
public interface StandardRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Consulta os registros da entidade T com possibilidade de adição de predicados (Specification) e informações de sumarização.
     *
     * @param specification  Specification aplicados a consulta.
     * @param summaryOptions Informações de sumarização.
     * @return Lista estendida com os registros de T e informações de sumarização.
     * @see SummaryOptionsConfig
     */
    ListEx<T> findAll(Specification<T> specification, SummaryOptions summaryOptions);

    /**
     * Consulta os registros da entidade T com possibilidade de adição de predicados (Specification), paginação e informações de sumarização.
     *
     * @param specification  Specification aplicados a consulta.
     * @param pageable       Informações de paginação
     * @param summaryOptions Informações de sumarização.
     * @return Paginação estendida com os registros de T e informações de sumarização.
     */
    PageEx<T> findAll(Specification<T> specification, Pageable pageable, SummaryOptions summaryOptions);

    /**
     * Consulta os registros da entidade T com possibilidade de adição de predicados (Specification), ordenação e informações de sumarização.
     *
     * @param specification  Specification aplicados a consulta.
     * @param sort           Informações de ordenação.
     * @param summaryOptions Informações de sumarização.
     * @return Lista estendida com os registros de T e informações de sumarização.
     */
    ListEx<T> findAll(Specification<T> specification, Sort sort, SummaryOptions summaryOptions);

    /**
     * Realiza uma operação de sumarização com suporte a Specification.
     *
     * @param specification  Informações de Specification da consulta.
     * @param summaryOptions Lista com a requisição de operações de sumarização.
     * @return Lista com o(s) resultado(s) da operações de sumarização.
     * @see SummaryOptionsConfig
     */
    List<SummaryResult> summarize(Specification<T> specification, SummaryOptions summaryOptions);

    /**
     * Realiza uma operação de atualização parcial da entidade.
     *
     * @param id               Id da entidade
     * @param entityAttributes Mapa com os atributos da entidade T que sofrerão atualização.
     */
    T patch(ID id, Map<String, Object> entityAttributes);
}
