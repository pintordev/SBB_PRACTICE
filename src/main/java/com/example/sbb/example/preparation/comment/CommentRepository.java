package com.example.sbb.example.preparation.comment;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByAnswer(Answer answer);

    List<Comment> findTop10ByOrderByCreateDateDesc();

    Page<Comment> findAllByAuthor(SiteUser user, Pageable pageable);
}
