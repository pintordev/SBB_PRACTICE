package com.example.sbb.example.preparation.comment;

import com.example.sbb.example.preparation.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByAnswer(Answer answer);
}
