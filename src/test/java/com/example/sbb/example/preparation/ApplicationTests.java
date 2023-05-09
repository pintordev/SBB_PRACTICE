package com.example.sbb.example.preparation;

import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void testJpa() {
		// 질문을 subject 로 조회 => "sbb가 무엇인가요?"
		// 조회된 질문의 id가 1인지 조회
	}

}
