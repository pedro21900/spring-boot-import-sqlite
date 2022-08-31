package com.example.springbootimportsqlite.core.repository.datafilter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Classe de paginação estendida que herda de {@link Page}
 *
 * @param <T> Entidade JPA.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PageEx<T> extends PageImpl<T> implements Page<T> {

    /**
     * Retorna a lista com a sumarização da coluna correspondente.
     */
    private List<SummaryResult> summaries;

    public PageEx(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

}
