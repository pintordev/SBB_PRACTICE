package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.DataNotFoundException;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.user.SiteUser;
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
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Page<Answer> getList(Question question, int page, String sortKeyWord) {
        // order by createDate desc;
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 조건을 저장할 수 있는 Sort.Order 객체로 구성된 List 선언
        // sortKeyWord가 createDate이면 기존 함수 그대로 쓰고..
        // 그게 아니면 키워드로 직접 쿼리 날리는 걸로
        if (sortKeyWord.equals("voter")) {
            Pageable pageable = PageRequest.of(page, 10);
            return this.answerRepository.findAllByQuestionSortByVoterCount(question.getId(), pageable);
        } else if (sortKeyWord.equals("commentList")) {
            Pageable pageable = PageRequest.of(page, 10);
            return this.answerRepository.findAllByQuestionSortByCommentCount(question.getId(), pageable);
        } else {
            sorts.add(Sort.Order.desc("createDate")); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
            Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
            return this.answerRepository.findAllByQuestion(question, pageable);
        }


    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> oa = this.answerRepository.findById(id);
        if (oa.isPresent()) {
            return oa.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser voter) {
        answer.getVoter().add(voter);
        this.answerRepository.save(answer);
    }
}
