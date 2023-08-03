package com.pintor.sbb_practice;

import com.pintor.sbb_practice.question.Question;
import com.pintor.sbb_practice.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class CommonUtil {

    private QuestionService questionService;

    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public String timeDifference(LocalDateTime oldDate) {
        Duration diff = Duration.between(oldDate, LocalDateTime.now());
        if (diff.getSeconds() < 60) return "방금 전";
        if (diff.getSeconds() < 60 * 60) return diff.toMinutes() + "분 전";
        if (diff.getSeconds() < 60 * 60 * 24) return diff.toHours() + "시간 전";
        if (diff.getSeconds() < 60 * 60 * 24 * 7) return diff.toDays() + "일 전";
        return oldDate.format(DateTimeFormatter.ofPattern("yy.MM.dd"));
    }

    public Integer getAnswerCommentCount(Integer id) {
        Question question = this.questionService.getQuestion(id);
        Integer count = 0;
        count += question.getAnswerList().size();
        for (int i = 0; i < question.getAnswerList().size(); i++) {
            count += question.getAnswerList().get(i).getCommentList().size();
        }
        return count;
    }
}
