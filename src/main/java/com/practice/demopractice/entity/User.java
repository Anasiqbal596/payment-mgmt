package com.practice.demopractice.entity;


import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Data // Marks this class as a JPA entity (i.e., a table)
@Table(name = "users") // Optional: sets the table name in the DB
@NoArgsConstructor // Generates no-args constructor (required by JPA)
@AllArgsConstructor // Generates constructor with all fields
@Component
@Entity
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;



}
