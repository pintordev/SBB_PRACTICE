package com.pintor.sbb_practice;

import com.pintor.sbb_practice.answer.Answer;
import com.pintor.sbb_practice.answer.AnswerService;
import com.pintor.sbb_practice.comment.Comment;
import com.pintor.sbb_practice.comment.CommentService;
import com.pintor.sbb_practice.question.Question;
import com.pintor.sbb_practice.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    // root URL redircet to /sbb
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        List<Question> questionList = this.questionService.getBestQuestion();
        model.addAttribute("questionList", questionList);
        List<Answer> answerList = this.answerService.getRecentAnswer();
        model.addAttribute("answerList", answerList);
        List<Comment> commentList = this.commentService.getRecentComment();
        model.addAttribute("commentList", commentList);
        return "index";
    }
}
