package com.rookies3.myspringbootlab.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publisher")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "establishedDate")
    private LocalDate establishedDate;

    @Column(name = "address")
    private String address;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "publisher",
            cascade = CascadeType.ALL)
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}
