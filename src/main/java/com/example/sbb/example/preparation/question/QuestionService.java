package com.example.sbb.example.preparation.question;

import com.example.sbb.example.preparation.DataNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page) {
        // order by createDate desc;
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 조건을 저장할 수 있는 Sort.Order 객체로 구성된 List 선언
        sorts.add(Sort.Order.desc("createDate")); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
        return this.questionRepository.findAll(pageable);
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

    public void create(String subject, String content) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }
}
