package com.example.sbb.example.preparation.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class SiteUser {
    // userName, password, email
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @CreatedDate
    private LocalDateTime createDate;

    private LocalDateTime lastLoginDate;
}
