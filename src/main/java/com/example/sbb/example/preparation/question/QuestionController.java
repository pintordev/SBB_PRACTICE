package com.example.sbb.example.preparation.question;

import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.answer.AnswerForm;
import com.example.sbb.example.preparation.answer.AnswerService;
import com.example.sbb.example.preparation.category.Category;
import com.example.sbb.example.preparation.category.CategoryService;
import com.example.sbb.example.preparation.comment.CommentForm;
import com.example.sbb.example.preparation.user.SiteUser;
import com.example.sbb.example.preparation.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "keyWord", defaultValue = "") String keyWord) {
        Page<Question> paging = this.questionService.getList(page, keyWord);
        model.addAttribute("paging", paging);
        model.addAttribute("keyWord", keyWord);
        return "question_list";
    }

    // 질문 클릭 시 질문 상세 출력 되도록
    @GetMapping("/detail/{id}") // GetMapping에서 변수로 주소를 설정하는 경우
    public String detail(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "sortKeyWord", defaultValue = "createDate") String sortKeyWord, @PathVariable("id") Integer id, AnswerForm answerForm, CommentForm commentForm, HttpServletRequest request, HttpServletResponse response) { // @PathVariable() 로 매개변수를 받아야 함
        Question question;
        if (hitCountJudge(id, request, response)) {
            question = this.questionService.hit(id);
        } else {
            question = this.questionService.getQuestion(id);
        }
        Page<Answer> paging = this.answerService.getList(question, page, sortKeyWord);
        model.addAttribute("question", question);
        model.addAttribute("paging", paging);
        model.addAttribute("sortKeyWord", sortKeyWord);
        return "question_detail";
    }

    // 질문 등록하기 페이지로 이동
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createQuestion(Model model, QuestionForm questionForm) {
        // Model 객체를 사용하지 않아도 binding 된 상태라서 템플릿에서 사용 가능
        // 애플리케이션 재 실행해야 반영됨
        List<Category> categories = this.categoryService.getList();
        model.addAttribute("categories", categories);
        return "question_form";
    }

    // 질문 등록하기 페이지에서 등록이 되었을 때, post 요청 시
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createQuestion(Model model, @Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) { // validation 통과 못할 경우
            List<Category> categories = this.categoryService.getList();
            model.addAttribute("categories", categories);
            return "question_form"; // 질문 입력 페이지로
        }
        // validation 통과할 경우
        SiteUser author = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), author, questionForm.getCategory()); // Form 으로 들어온 데이터로 질문 생성
        return "redirect:/question/list"; // 질문 저장 후 원래 페이지인 질문 목록 페이지로 redirect
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyQuestion(Model model, QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        List<Category> categories = this.categoryService.getList();
        model.addAttribute("categories", categories);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyQuestion(Model model, @Valid QuestionForm questionForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = this.categoryService.getList();
            model.addAttribute("categories", categories);
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteQuestion(@PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser voter = this.userService.getUser(principal.getName());
        if (question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인이 작성한 질문은 추천할 수 없습니다.");
        }
        this.questionService.vote(question, voter);
        return String.format("redirect:/question/detail/%s", id);
    }

    private boolean hitCountJudge(Integer id, HttpServletRequest request, HttpServletResponse response) {
        // 요청 이전 url을 확인해서 제대로 된 게시물 접근인지 확인
        String refer = request.getHeader("REFERER");

        if (refer == null) return false;

        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String referUri = refer.replaceFirst(path, "");
        System.out.println(referUri);

        // 게시판에서 접근한 경우가 아니면 reject
        if (!referUri.equals("/question/list") && !referUri.equals("/sbb") && !referUri.startsWith("/user/mypage")) return false;

        Cookie oldCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("questionHit")) {
                    oldCookie = cookie;
                }
            }
        }

        // 관련 쿠기가 있다면
        if (oldCookie != null) {
            // 해당 쿠키가 해당 게시물 id를 조회할 때 생성된 쿠기인지 판단
            if (!oldCookie.getValue().contains("[" + id + "]")) {
                // 아니라면 해당 게시물 id를 조회한 결과를 쿠기에 저장
                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(30); // 지속시간 30초
                response.addCookie(oldCookie); // 쿠키를 브라우저에 저장
                return true;
            }
            // 맞다면 reject
            return false;
        } else {
            // 쿠키가 없다면 새로 생성해서 해당 게시물 id를 조회한 결과를 쿠기에 저장
            Cookie newCookie = new Cookie("questionHit","[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(30); // 지속시간 30초
            response.addCookie(newCookie); // 쿠키를 브라우저에 저장
            return true;
        }
    }
}
