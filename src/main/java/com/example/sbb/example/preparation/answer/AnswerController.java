package com.example.sbb.example.preparation.answer;

import com.example.sbb.example.preparation.comment.CommentForm;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionService;
import com.example.sbb.example.preparation.user.SiteUser;
import com.example.sbb.example.preparation.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}/{sortKeyWord}/{page}") // form 태그 method="post"로 요청되므로
    public String createAnswer(Model model, @PathVariable("id") Integer id, @PathVariable("sortKeyWord") String sortKeyWord, @PathVariable("page") int page, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) { // 요청 시 들어가는 데이터는 @RequestParam 으로
        // 답변을 저장할 질문 객체 불러옴
        Question question = this.questionService.getQuestion(id);
        SiteUser author = this.userService.getUser(principal.getName());
        Page<Answer> paging = this.answerService.getList(question, page, sortKeyWord);
        // 답변 입력 validation 통과 못했을 경우 question_detail 템플릿을 다시 렌더링
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question); // 질문 상세를 출력해야 하므로 model 객체로 질문을 전달한다
            model.addAttribute("paging", paging);
            model.addAttribute("sortKeyWord", sortKeyWord);
            model.addAttribute("commentForm", new CommentForm());
            return "question_detail";
        }
        // 답변 저장
        Answer answer = this.answerService.create(question, answerForm.getContent(), author);
        // 답변 저장 완료 후, 기존에 있었던 질문 상세 페이지로 리턴
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", answer.getQuestion().getId(), page, answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}/{page}")
    public String modifyAnswer(AnswerForm answerForm, @PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}/{page}")
    public String modifyAnswer(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", answer.getQuestion().getId(), page, answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteAnswer(@PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}/{page}")
    public String voteAnswer(@PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser voter = this.userService.getUser(principal.getName());
        if (answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인이 작성한 댓글은 추천할 수 없습니다.");
        }
        this.answerService.vote(answer, voter);
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", answer.getQuestion().getId(), page, answer.getId());
    }
}
