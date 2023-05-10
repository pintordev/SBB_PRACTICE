package com.example.sbb.example.preparation.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    // Repository => Service 거쳐가도록 수정
    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);
        return "question_list";
    }

    // 질문 클릭 시 질문 상세 출력 되도록
    @GetMapping(value = "/detail/{id}") // GetMapping에서 변수로 주소를 설정하는 경우
    public String detail(Model model, @PathVariable("id") Integer id) { // @PathVariable() 로 매개변수를 받아야 함
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }
}
