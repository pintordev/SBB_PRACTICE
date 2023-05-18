package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.DataNotFoundException;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.user.SiteUser;
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
        if (!sortKeyWord.equals("createDate")) sorts.add(Sort.Order.desc(sortKeyWord));
        sorts.add(Sort.Order.desc(sortKeyWord)); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
        return this.answerRepository.findByQuestion(question, pageable);
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
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
