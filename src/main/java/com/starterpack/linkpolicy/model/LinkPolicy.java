package com.starterpack.linkpolicy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "link_policy")
public class LinkPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String pattern;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected LinkPolicy() {}

    public LinkPolicy(String pattern, LocalDateTime createdAt) {
        this.pattern = pattern;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


