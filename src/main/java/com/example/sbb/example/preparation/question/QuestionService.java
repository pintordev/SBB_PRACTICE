package com.example.sbb.example.preparation.question;

import com.example.sbb.example.preparation.DataNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return this.questionRepository.findAll();
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
}
