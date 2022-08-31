package com.example.springbootimportsqlite.core.repository.impl;

import com.example.springbootimportsqlite.core.repository.datafilter.model.ListEx;
import com.example.springbootimportsqlite.core.repository.datafilter.model.PageEx;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryOptions;
import com.example.springbootimportsqlite.core.repository.datafilter.model.SummaryResult;
import com.example.springbootimportsqlite.core.repository.StandardRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class StandardRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements StandardRepository<T, ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final Class<T> entityClass;
    @Autowired
    private ModelMapper mapper;

    public StandardRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
        this.entityClass = entityInformation.getJavaType();
    }

    @Override
    public ListEx<T> findAll(Specification<T> specification, SummaryOptions summaryOptions) {
        log.debug("findAll(specification={}, summaryOptions={})", specification, summaryOptions);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        applySpecification(specification, root, cq, cb);

        TypedQuery<T> q = em.createQuery(cq);
        applyEntityGraph(q, "findAll", Specification.class, SummaryOptions.class);
        ListEx<T> listEx = new ListEx<>();
        listEx.setContent(q.getResultList());
        execAgg(specification, summaryOptions, listEx);
        return listEx;
    }

    @Override
    public PageEx<T> findAll(Specification<T> specification, Pageable pageable, SummaryOptions summaryOptions) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        applySpecification(specification, root, cq, cb);
        applySort(root, cq, cb, pageable.getSort());

        TypedQuery<T> q = em.createQuery(cq);
        applyEntityGraph(q, "findAll", Specification.class, Pageable.class, SummaryOptions.class);
        applyPagination(q, pageable);
        long count = count(entityClass, specification);
        PageEx<T> pageEx = new PageEx<>(
                q.getResultList(),
                pageable,
                count
        );
        execAgg(specification, summaryOptions, pageEx);
        return pageEx;
    }

    //
    @Override
    public ListEx<T> findAll(Specification<T> specification, Sort sort, SummaryOptions summaryOptions) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        applySpecification(specification, root, cq, cb);
        applySort(root, cq, cb, sort);

        TypedQuery<T> q = em.createQuery(cq);
        applyEntityGraph(q, "findAll", Specification.class, Sort.class, SummaryOptions.class);
        ListEx<T> listEx = new ListEx<>();
        listEx.setContent(q.getResultList());
        execAgg(specification, summaryOptions, listEx);
        return listEx;
    }

    @Override
    public List<SummaryResult> summarize(Specification<T> specification, SummaryOptions summaryOptions) {
        log.debug("summarize(specification={}, summaryOptions={})", specification, summaryOptions);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<T> root = cq.from(entityClass);
        Selection[] selections = summaryOptions.getSummaryRequests()
                .stream()
                .map(agg -> agg.getOperation().getSqlDef().apply(Triple.of(agg.getDataField(), root, cb)))
                .toArray(Selection[]::new);
        cq.multiselect(selections);
        applySpecification(specification, root, cq, cb);
        TypedQuery<Tuple> query = em.createQuery(cq);
        Tuple tuple = query.getSingleResult();
        List<SummaryResult> summariesResult = new ArrayList<>();
        for (int i = 0; i < tuple.getElements().size(); i++)
            summariesResult.add(
                    new SummaryResult(
                            summaryOptions.getSummaryRequests().get(i).getDataField(),
                            tuple.get(i),
                            summaryOptions.getSummaryRequests().get(i).getOperation()
                    ));
        return summariesResult;
    }

    protected <X> void applySpecification(Specification<X> specification, Root<X> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (Objects.nonNull(specification) && Objects.nonNull(specification.toPredicate(root, cq, cb))) {
            cq.where(specification.toPredicate(root, cq, cb));
        }
    }

    protected void applyPagination(TypedQuery<?> q, Pageable pageable) {
        if (Objects.nonNull(pageable)) {
            q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            q.setMaxResults(pageable.getPageSize());
        }
    }


    protected void applyEntityGraph(TypedQuery<?> q, String methodName, Class<?>... params) {
        Class<?> repositoryEx = this.getClass().getInterfaces()[0];
        if (Objects.nonNull(repositoryEx)) {
            Method method = MethodUtils.getMatchingMethod(this.getClass().getInterfaces()[0], methodName, params);
            if (Objects.nonNull(method)) {
                log.debug("applyEntityGraph: {}", method);
                if (method.isAnnotationPresent(EntityGraph.class)) {
                    EntityGraph entityGraphAnn = method.getAnnotation(EntityGraph.class);
                    javax.persistence.EntityGraph entityGraph = em.getEntityGraph(entityGraphAnn.value());
                    q.setHint("javax.persistence.loadgraph", entityGraph);
                }
            }
        }
    }

    protected <X> void applySort(Root<X> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Sort sort) {
        log.debug("applySort: {}", sort);
        if (Objects.nonNull(sort)) {
            cq.orderBy(sort
                    .stream()
                    .map(o -> o.isAscending() ? cb.asc(root.get(o.getProperty())) : cb.desc(root.get(o.getProperty())))
                    .toArray(Order[]::new));
        }
    }

    protected void execAgg(Specification<T> specification, SummaryOptions summaryOptions, ListEx<T> listEx) {
        if (Objects.nonNull(summaryOptions.getSummaryRequests()) && !summaryOptions.getSummaryRequests().isEmpty()) {
            List<SummaryResult> summaries = this.summarize(specification, summaryOptions);
            listEx.setSummaries(summaries);
        }
    }

    protected void execAgg(Specification<T> specification, SummaryOptions summaryOptions, PageEx<T> pageEx) {
        if (Objects.nonNull(summaryOptions.getSummaryRequests()) && !summaryOptions.getSummaryRequests().isEmpty()) {
            List<SummaryResult> summaries = this.summarize(specification, summaryOptions);
            pageEx.setSummaries(summaries);
        }
    }

    protected <X> long count(Class<X> countEntityClass, Specification<X> specification) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<X> root = cq.from(countEntityClass);
        cq.select(cb.count(root));
        applySpecification(specification, root, cq, cb);
        TypedQuery<Number> query = em.createQuery(cq);
        return query.getSingleResult().longValue();
    }

    @Override
    @Transactional
    @SneakyThrows
    public T patch(ID id, Map<String, Object> entityAttributes) {
        log.debug("patch(id={}, entity={})", id, entityAttributes);
        T dbEntity = em.find(entityClass, id);
        EntityType<T> entityType = em.getMetamodel().entity(entityClass);
        String idName = entityType.getDeclaredId(id.getClass()).getName();
        log.debug("isId: {}, attribue: {}", idName, idName);
        BeanUtils.populate(dbEntity, entityAttributes);
        return em.merge(dbEntity);
    }
}
