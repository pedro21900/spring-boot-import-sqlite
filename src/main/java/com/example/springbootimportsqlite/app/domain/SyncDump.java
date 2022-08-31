package com.example.springbootimportsqlite.app.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SyncDump {

    private String database;

    private Long version;

    private Boolean encrypted;

    private String mode;

    private List<SyncDumpItem> tables;

}
