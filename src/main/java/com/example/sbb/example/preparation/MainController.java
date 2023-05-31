package com.example.sbb.example.preparation;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.answer.AnswerService;
import com.example.sbb.example.preparation.comment.Comment;
import com.example.sbb.example.preparation.comment.CommentService;
import com.example.sbb.example.preparation.question.Question;
import com.example.sbb.example.preparation.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
