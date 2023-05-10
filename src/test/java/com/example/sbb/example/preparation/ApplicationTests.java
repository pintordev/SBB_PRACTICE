package com.example.sbb.example.preparation;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.answer.AnswerRepository;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Test
	void testJpa() {
		// id == 2 인 question 객체에 답변 저장
		// 답변 내용 => "네 자동으로 생성됩니다."

		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer answer = new Answer();
		answer.setContent("네 자동으로 생성됩니다");
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(q);
		this.answerRepository.save(answer);
	}

}
