package com.example.sbb.example.preparation.comment;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.answer.AnswerForm;
import com.example.sbb.example.preparation.answer.AnswerService;
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

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final AnswerService answerService;
    private final CommentService commentService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{answerId}/{commentId}/{sortKeyWord}/{page}")
    public String createComment(Model model, @PathVariable("answerId") Integer answerId, @PathVariable("commentId") Integer commentId, @PathVariable("sortKeyWord") String sortKeyWord, @PathVariable("page") int page, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
        Answer answer = this.answerService.getAnswer(answerId);
        Comment parent = this.commentService.getComment(commentId);
        SiteUser author = this.userService.getUser(principal.getName());
        Page<Answer> paging = this.answerService.getList(answer.getQuestion(), page, sortKeyWord);
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", answer.getQuestion());
            model.addAttribute("paging", paging);
            model.addAttribute("sortKeyWord", sortKeyWord);
            model.addAttribute("answerForm", new AnswerForm());
            model.addAttribute("answerId", answer.getId());
            return "question_detail";
        }
        this.commentService.create(answer, parent, commentForm.getContent(), author);
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", answer.getQuestion().getId(), page, answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}/{page}")
    public String modifyComment(CommentForm commentForm, @PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}/{page}")
    public String modifyComment(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", comment.getAnswer().getQuestion().getId(), page, comment.getAnswer().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s#answer_%s", comment.getAnswer().getQuestion().getId(), comment.getAnswer().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}/{page}")
    public String voteComment(@PathVariable("id") Integer id, @PathVariable("page") int page, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        SiteUser voter = this.userService.getUser(principal.getName());
        if (comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인이 작성한 댓글은 추천할 수 없습니다.");
        }
        this.commentService.vote(comment, voter);
        return String.format("redirect:/question/detail/%s?page=%s#answer_%s", comment.getAnswer().getQuestion().getId(), page, comment.getAnswer().getId());
    }

    @PostMapping("/goto")
    @ResponseBody
    public String getUri(@RequestParam("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        Answer answer = comment.getAnswer();
        Question question = answer.getQuestion();
        int page = this.answerService.getPage(answer.getId(), question);
        return String.format("/question/detail/%s?page=%s#answer_%s", answer.getQuestion().getId(), page, answer.getId());
    }
}
