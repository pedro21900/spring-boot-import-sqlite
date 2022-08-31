package com.example.springbootimportsqlite.app.repository;

import com.example.springbootimportsqlite.app.domain.Cultura;

import com.example.springbootimportsqlite.core.repository.StandardRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CulturaRepository extends StandardRepository<Cultura,Long> {
}
