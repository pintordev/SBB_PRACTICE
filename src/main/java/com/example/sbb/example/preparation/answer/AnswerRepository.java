package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Page<Answer> findAllByQuestion(Question question, Pageable pageable);

    // 나눠 쓰기는 너무 빡세다..
    @Query(value = "select "
            + "distinct a.*, count(av.answer_id) as voter_count "
            + "from answer a "
            + "left outer join answer_voter av on a.id = av.answer_id "
            + "where a.question_id = :question_id "
            + "group by a.id, av.answer_id "
            + "order by voter_count desc, a.create_date desc "
            , countQuery = "select count(*) from Answer"
            , nativeQuery = true)
    Page<Answer> findAllByQuestionSortByVoterCount(@Param("question_id") Integer questionId, Pageable pageable);

    @Query(value = "select "
            + "distinct a.*, count(c.answer_id) as comment_count "
            + "from answer a "
            + "left outer join comment c on a.id = c.answer_id "
            + "where a.question_id = :question_id "
            + "group by a.id, c.answer_id "
            + "order by comment_count desc, a.create_date desc "
            , countQuery = "select count(*) from answer"
            , nativeQuery = true)
    Page<Answer> findAllByQuestionSortByCommentCount(@Param("question_id") Integer questionId, Pageable pageable);
}