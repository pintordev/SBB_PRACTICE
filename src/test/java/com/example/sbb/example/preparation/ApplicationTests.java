package com.example.sbb.example.preparation;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.answer.AnswerRepository;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionRepository;
import com.example.sbb.example.preparation.question.QuestionService;
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
	private QuestionService questionService;

	@Autowired
	private AnswerRepository answerRepository;

	@Test
	void testJpa() {

	}

}
