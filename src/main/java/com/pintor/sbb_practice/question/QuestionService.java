package com.pintor.sbb_practice.question;

import com.pintor.sbb_practice.DataNotFoundException;
import com.pintor.sbb_practice.answer.Answer;
import com.pintor.sbb_practice.category.CategoryRepository;
import com.pintor.sbb_practice.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    private Specification<Question> search(String keyWord) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> question, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true); // 중복 제거
                Join<Question, SiteUser> questionAuthor = question.join("author", JoinType.LEFT);
                Join<Question, Answer> answer = question.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> answerAuthor = answer.join("author", JoinType.LEFT);
                return criteriaBuilder.or(criteriaBuilder.like(question.get("subject"), "%" + keyWord + "%"),
                        criteriaBuilder.like(question.get("content"), "%" + keyWord + "%"),
                        criteriaBuilder.like(questionAuthor.get("username"), "%" + keyWord + "%"),
                        criteriaBuilder.like(answer.get("content"), "%" + keyWord + "%"),
                        criteriaBuilder.like(answerAuthor.get("username"), "%" + keyWord + "%"));
            }
        };
    }
    public Page<Question> getList(int page, String keyWord) {
        // order by createDate desc;
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 조건을 저장할 수 있는 Sort.Order 객체로 구성된 List 선언
        sorts.add(Sort.Order.desc("createDate")); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
        Specification<Question> specification = search(keyWord);
        return this.questionRepository.findAll(specification, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> oq = this.questionRepository.findById(id);
        if (oq.isPresent()) {
            return oq.get();
        } else {
            // 해당 id를 갖는 질문 객체가 존재하지 않는 경우
            // DataNotFoundException 에러 출력
            throw new DataNotFoundException("question not found");
        }
    }

    public List<Question> getBestQuestion() {
        return this.questionRepository.findTop10SortByVoterCount();
    }

    public Page<Question> getListByUser(int page, SiteUser user) {
        // order by createDate desc;
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 조건을 저장할 수 있는 Sort.Order 객체로 구성된 List 선언
        sorts.add(Sort.Order.desc("createDate")); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
        return this.questionRepository.findAllByAuthor(user, pageable);
    }

    public void create(String subject, String content, SiteUser author, String category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(author);
        question.setCategory(this.categoryRepository.findByType(category)); // 임시로 나중에 게시판 확장하면 변경 예정
        question.setHit(0);
        this.questionRepository.save(question);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser voter) {
        question.getVoter().add(voter);
        this.questionRepository.save(question);
    }

    public Question hit(Integer id) {
        Question question = this.getQuestion(id);
        question.setHit(question.getHit() + 1);
        this.questionRepository.save(question);
        return question;
    }
}
