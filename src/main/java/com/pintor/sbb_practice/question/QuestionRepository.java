package com.pintor.sbb_practice.question;

import com.pintor.sbb_practice.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> specification, Pageable pageable);

    @Query(value = "select "
            + "distinct q.*, count(qv.question_id) as voter_count "
            + "from question q "
            + "left outer join question_voter qv on q.id = qv.question_id "
            + "group by q.id, qv.question_id "
            + "order by voter_count desc, q.create_date desc "
            , countQuery = "select count(*) from question"
            , nativeQuery = true)
    List<Question> findTop10SortByVoterCount();

    Page<Question> findAllByAuthor(SiteUser user, Pageable pageable);
}
