package com.example.sbb.example.preparation.question;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.category.Category;
import com.example.sbb.example.preparation.user.SiteUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    private Set<SiteUser> voter;

    // 글 작성 시 리스트로 선택하도록 해야 됨
    // 일단 카테고리만 만들고 디폴트는 "질문"
    @ManyToOne
    private Category category;

    @Column
    private Integer hit;
}
