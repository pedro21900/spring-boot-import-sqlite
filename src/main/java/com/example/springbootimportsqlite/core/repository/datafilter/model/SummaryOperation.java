package com.example.springbootimportsqlite.core.repository.datafilter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Triple;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.Function;

public enum SummaryOperation {
    @JsonProperty("sum") SUM(t -> t.getRight().sum(t.getMiddle().get(t.getLeft()))),
    @JsonProperty("max") MAX(t -> t.getRight().max(t.getMiddle().get(t.getLeft()))),
    @JsonProperty("min") MIN(t -> t.getRight().min(t.getMiddle().get(t.getLeft()))),
    @JsonProperty("count") COUNT(t -> t.getRight().count(t.getMiddle().get(t.getLeft()))),
    @JsonProperty("dcount") DCOUNT(t -> t.getRight().countDistinct(t.getMiddle().get(t.getLeft()))),
    @JsonProperty("avg") AVG(t -> t.getRight().avg(t.getMiddle().get(t.getLeft())));

    @Getter
    Function<Triple<String, Root<?>, CriteriaBuilder>, Expression<? extends Number>> sqlDef;

    SummaryOperation(Function<Triple<String, Root<?>, CriteriaBuilder>, Expression<? extends Number>> sqlDef) {
        this.sqlDef = sqlDef;
    }
}
