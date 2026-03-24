package com.bikram.springbootproject.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @ManyToMany(mappedBy = "users")
    private List<Task> tasks;



}
