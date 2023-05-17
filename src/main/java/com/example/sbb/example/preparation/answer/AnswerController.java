package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionService;
import com.example.sbb.example.preparation.user.SiteUser;
import com.example.sbb.example.preparation.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/{id}") // form 태그 method="post"로 요청되므로
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) { // 요청 시 들어가는 데이터는 @RequestParam 으로
        // 답변을 저장할 질문 객체 불러옴
        Question question = this.questionService.getQuestion(id);
        SiteUser author = this.userService.getUser(principal.getName());
        // 답변 입력 validation 통과 못했을 경우 question_detail 템플릿을 다시 렌더링
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question); // 질문 상세를 출력해야 하므로 model 객체로 질문을 전달한다
            return "question_detail";
        }
        // 답변 저장
        this.answerService.create(question, answerForm.getContent(), author);
        // 답변 저장 완료 후, 기존에 있었던 질문 상세 페이지로 리턴
        return String.format("redirect:/question/detail/%s", question.getId());
    }
}
