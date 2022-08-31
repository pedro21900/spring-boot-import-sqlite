package com.example.springbootimportsqlite.app.rest;

import com.example.springbootimportsqlite.app.domain.Cultura;
import com.example.springbootimportsqlite.app.domain.SyncDump;
import com.example.springbootimportsqlite.app.service.SyncDumpService;
import com.example.springbootimportsqlite.core.template.StandardRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync-dump")
public class SyncDumpRestRest  {
    @Autowired
    private SyncDumpService syncDumpService;

    @GetMapping
    public SyncDump exportAndCreateDataDb() throws IllegalAccessException {
        return syncDumpService.exportTables();
    }
//    public SyncDump exportAndCreateDataDb(){
//        return syncDumpService.exportTables();
//    }

    @PostMapping
    public void insert(@RequestBody SyncDump syncDump){
        syncDumpService.createOrUpdate(syncDump);
    }
}

