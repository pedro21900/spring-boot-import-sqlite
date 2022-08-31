package com.example.springbootimportsqlite.core.repository.config;

import com.example.springbootimportsqlite.SpringBootImportSqliteApplication;
import com.example.springbootimportsqlite.core.repository.impl.StandardRepositoryImpl;
import com.example.springbootimportsqlite.core.repository.StandardRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Habilita a {@link StandardRepository} para uso.
 */
@Configuration
@EnableJpaRepositories(repositoryBaseClass = StandardRepositoryImpl.class, basePackageClasses = SpringBootImportSqliteApplication.class)
public class StandardJpaRepositoryConfig {
}
