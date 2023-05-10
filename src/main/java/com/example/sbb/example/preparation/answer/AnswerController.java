package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping(value = "/create/{id}") // form 태그 method="post"로 요청되므로
    public String createAnswer(Model model, @PathVariable("id") Integer id, @RequestParam String content) { // 요청 시 들어가는 데이터는 @RequestParam 으로
        // 답변을 저장할 질문 객체 불러옴
        Question question = this.questionService.getQuestion(id);
        // 답변 저장
        this.answerService.create(question, content);
        // 답변 저장 완료 후, 기존에 있었던 질문 상세 페이지로 리턴
        return String.format("redirect:/question/detail/%s", question.getId());
    }
}
