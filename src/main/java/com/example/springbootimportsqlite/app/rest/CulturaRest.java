package com.example.springbootimportsqlite.app.rest;

import com.example.springbootimportsqlite.app.domain.Cultura;
import com.example.springbootimportsqlite.app.domain.SyncDump;
import com.example.springbootimportsqlite.app.service.SyncDumpService;
import com.example.springbootimportsqlite.core.template.StandardRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cultura")
public class CulturaRest extends StandardRest<Cultura,Long> {

}

