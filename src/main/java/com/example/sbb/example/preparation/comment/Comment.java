package com.example.sbb.example.preparation.comment;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Answer answer;

    @ManyToMany
    private Set<SiteUser> voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> children;
}
