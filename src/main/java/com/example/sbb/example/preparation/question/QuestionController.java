package com.example.sbb.example.preparation.question;

import com.example.sbb.example.preparation.answer.AnswerForm;
import com.example.sbb.example.preparation.user.SiteUser;
import com.example.sbb.example.preparation.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    // Repository => Service 거쳐가도록 수정
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    // 질문 클릭 시 질문 상세 출력 되도록
    @GetMapping(value = "/detail/{id}") // GetMapping에서 변수로 주소를 설정하는 경우
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) { // @PathVariable() 로 매개변수를 받아야 함
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    // 질문 등록하기 페이지로 이동
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createQuestion(QuestionForm questionForm) {
        // Model 객체를 사용하지 않아도 binding 된 상태라서 템플릿에서 사용 가능
        // 애플리케이션 재 실행해야 반영됨
        return "question_form";
    }

    // 질문 등록하기 페이지에서 등록이 되었을 때, post 요청 시
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) { // validation 통과 못할 경우
            return "question_form"; // 질문 입력 페이지로
        }
        // validation 통과할 경우
        SiteUser author = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), author); // Form 으로 들어온 데이터로 질문 생성
        return "redirect:/question/list"; // 질문 저장 후 원래 페이지인 질문 목록 페이지로 redirect

    }
}
