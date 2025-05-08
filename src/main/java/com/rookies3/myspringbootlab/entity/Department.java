package com.rookies3.myspringbootlab.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;

    @OneToMany
    private List<Student> students;
}
