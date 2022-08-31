package com.example.springbootimportsqlite.core.repository.datafilter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListEx<T> {

    private List<T> content = new ArrayList<>();

    private List<SummaryResult> summaries = new ArrayList<>();

}
