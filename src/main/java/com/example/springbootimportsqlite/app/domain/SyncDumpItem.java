package com.example.springbootimportsqlite.app.domain;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncDumpItem {

    private String name;
    
    private List<Schema> schema;

    private List<Object[]> values;
}
