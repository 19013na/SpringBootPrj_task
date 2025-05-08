package com.rookies3.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String studentNumber;

    @ManyToOne
    private Department department;

}
