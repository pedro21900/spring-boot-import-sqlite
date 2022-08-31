package com.example.springbootimportsqlite.app.domain;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Cultura {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CULTURA")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "SEQ_CULTURA", sequenceName = "SEQ_CULTURA")
    private Long id;

    private String nome;

    private Boolean sqlDeleted;

    private Integer lastModified;

    // @OneToMany(fetch = FetchType.LAZY,
    // cascade = {CascadeType.ALL},
    // orphanRemoval = true)
    // @JoinColumn(name = "CULTURA_ID")
    // private Set<Variedade> variedade;
    
}
