package com.example.springbootimportsqlite.app.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.springbootimportsqlite.app.domain.Cultura;
import com.example.springbootimportsqlite.app.domain.Schema;
import com.example.springbootimportsqlite.app.repository.CulturaRepository;
import com.example.springbootimportsqlite.core.utils.StringToTimestampConverter;
import lombok.AllArgsConstructor;
import org.apache.commons.text.CaseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springbootimportsqlite.app.domain.SyncDump;
import com.example.springbootimportsqlite.app.domain.SyncDumpItem;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class SyncDumpService {
    private final CulturaRepository culturaRepository;

    private List<Schema> createSchema() {

        List<Schema> schemas = new ArrayList<>();

        schemas.add(new Schema("id", "INTEGER PRIMARY KEY NOT NULL"));
        schemas.add(new Schema("nome", "TEXT NOT NULL"));
        schemas.add(new Schema("sql_deleted", "BOOLEAN DEFAULT 0 CHECK (sql_deleted IN (0, 1))"));
        schemas.add(new Schema("last_modified", "INTEGER DEFAULT (strftime('%s', 'now'))"));

        return schemas;
    }

    private List<Object[]> getValues() throws IllegalAccessException {
        List<Object[]> values = new ArrayList<>();
        Field[] objectFields = Cultura.class.getDeclaredFields();
        Integer classFieldLength = objectFields.length;

        for (Cultura cultura : culturaRepository.findAll()) {
            Object[] objects = new Object[classFieldLength];
            for (int i = 0; i < classFieldLength; i++) {
                objectFields[i].setAccessible(true);
                objects[i] = objectFields[i].get(cultura);
            }
            values.add(objects);
        }

        return values;
    }

    public SyncDump exportTables() throws IllegalAccessException {

        SyncDump syncDump = new SyncDump();
        SyncDumpItem syncDumpItem = new SyncDumpItem();

        List<SyncDumpItem> syncDumpItems = new ArrayList<>();

        List<Schema> schemas = createSchema();

        List<Object[]> values = getValues();

        syncDumpItem.setName("cultura");

        syncDumpItem.setSchema(schemas);

        syncDumpItem.setValues(values);

        syncDumpItems.add(syncDumpItem);

        syncDump.setDatabase("siapec3-pe-db");
        syncDump.setVersion(1L);
        syncDump.setEncrypted(false);
        syncDump.setMode("partial");
        syncDump.setTables(syncDumpItems);

        return syncDump;
    }

    public void createOrUpdate(SyncDump syncDump) {
        ModelMapper mapper = new ModelMapper();

        List<Map<String, Object>> culturas = new ArrayList<>();
        for (SyncDumpItem syncDumpItem : syncDump.getTables()) {
            for (Integer i = 0; i < syncDumpItem.getValues().size(); i++) {
                Map<String, Object> cultura = new HashMap<>();
                for (Integer j = 0; j < syncDumpItem.getValues().get(i).length; j++) {
                    String nameColumnCamelCase="";
                    if(Objects.nonNull(syncDumpItem)){
                        nameColumnCamelCase= CaseUtils.toCamelCase(syncDumpItem.getSchema().get(j).getColumn(), false, new char[]{'_'});}
                    if (syncDumpItem.getSchema().get(j).getColumn().equals("last_modified")) {
                       // Timestamp timestamp=Timestamp.from(Instant.ofEpochMilli(Long.parseLong(syncDumpItem.getValues().get(i)[j].toString())));
                        cultura.put(nameColumnCamelCase, syncDumpItem.getValues().get(i)[j]);
                    } else cultura.put(nameColumnCamelCase, syncDumpItem.getValues().get(i)[j]);

                }
                culturas.add(cultura);
            }
        }
        //mapper.addConverter(new StringToTimestampConverter());
        List<Cultura> culturaList = culturas.stream()
                .map(cultura -> mapper.map(cultura, Cultura.class))
                .collect(Collectors.toList());
        culturaList.forEach(System.out::println);
        culturaRepository.saveAll(culturaList);
    }

    Integer parseDateToInt(String value) {
        System.out.println(value);
        if (Objects.nonNull(value)) {
            return Integer.parseInt(value);
        } else return null;
    }
}
