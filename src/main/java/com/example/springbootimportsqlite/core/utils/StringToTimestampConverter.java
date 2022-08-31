package com.example.springbootimportsqlite.core.utils;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringToTimestampConverter implements Converter<String, Timestamp> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public Timestamp convert(MappingContext<String, Timestamp> mappingContext) {
        String source = mappingContext.getSource();
        LocalDateTime dateTime = LocalDateTime.parse(source, this.formatter);
        return Timestamp.valueOf(dateTime);
    }
}
