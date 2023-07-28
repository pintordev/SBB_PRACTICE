package com.pintor.sbb_practice;

import com.pintor.sbb_practice.answer.AnswerRepository;
import com.pintor.sbb_practice.question.QuestionRepository;
import com.pintor.sbb_practice.question.QuestionService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
