package com.example.springbootimportsqlite.core.template;

import com.example.springbootimportsqlite.core.repository.StandardRepository;
import com.example.springbootimportsqlite.core.repository.datafilter.RSQLParam;

import com.example.springbootimportsqlite.core.repository.datafilter.model.PageEx;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryOptions;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class StandardRest<T, ID extends Serializable> {

    private Class<T> entityClass;

    protected StandardRepository<T, ID> repository;

    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        this.entityClass = (Class<T>) ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).getRawClass();
        Repositories repositories = new Repositories(applicationContext);
        this.repository = (StandardRepository<T, ID>) repositories.getRepositoryFor(entityClass).orElseThrow(() -> new IllegalArgumentException("Repositório não encontrado."));
    }

    @GetMapping
    public ResponseEntity<PageEx<?>> findAll(RSQLParam q, Pageable pageable, SummaryOptions summaryOptions) {
        return ResponseEntity.ok(
            repository.findAll(q.getSpecification(), pageable, summaryOptions)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable ID id) {
        return ResponseEntity.ok(
            repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entidade não encontrada. Id=" + id))
        );
    }

    @GetMapping("/_summarize")
    public ResponseEntity<List<SummaryResult>> summarize(RSQLParam q, SummaryOptions summaryOptions) {
        return ResponseEntity.ok(
            repository.summarize(q.getSpecification(), summaryOptions)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insert(@RequestBody T resource) {
        return ResponseEntity.ok(repository.save(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody T resource) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Entidade não encontrada. Id=" + id);
        return ResponseEntity.ok(repository.save(resource));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ID id) {
        repository.deleteById(id);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<T> patch(@PathVariable ID id,
                                   @RequestBody Map<String, Object> entityAttributes) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Entidade não encontrada. Id=" + id);
        return ResponseEntity.ok(repository.patch(id, entityAttributes));
    }
}
