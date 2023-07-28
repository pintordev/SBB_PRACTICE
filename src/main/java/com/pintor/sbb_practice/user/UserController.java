package com.pintor.sbb_practice.user;

import com.pintor.sbb_practice.DataNotFoundException;
import com.pintor.sbb_practice.answer.Answer;
import com.pintor.sbb_practice.answer.AnswerService;
import com.pintor.sbb_practice.comment.Comment;
import com.pintor.sbb_practice.comment.CommentService;
import com.pintor.sbb_practice.question.Question;
import com.pintor.sbb_practice.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final UserService userService;
    private final UserEmailService userEmailService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        // 비밀번호와 비밀번호 확인에 입력한 문자열이 서로 다르면 다시 입력 하도록
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "입력한 비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        if (!this.userService.confirmCertificationCode(userCreateForm.getInputCode(), userCreateForm.getGenCode())) {
            bindingResult.rejectValue("inputCode", "codeInCorrect",
                    "입력한 인증번호가 일치하지 않습니다.");
            return "signup_form";
        }

        userService.create(userCreateForm.getUsername(), userCreateForm.getPassword1(), userCreateForm.getEmail());

        return "redirect:/user/login";
    }

    @PostMapping("/signup/emailConfirm")
    @ResponseBody
    public String emailConfirm(@RequestParam("email") String email) {
        String genCode = this.userService.genConfirmCode(8);
        System.out.println(genCode);
        this.userEmailService.mailSend(email, "이메일 인증", genCode);
        return this.userService.getEmailConfirmCode(genCode);
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/log/loginDate")
    public String loginDate(Principal principal) {
        SiteUser user = this.userService.getUser(principal.getName());
        this.userService.updateLoginDate(user);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal, @RequestParam(value = "anchor", defaultValue = "") String anchor, @RequestParam(value = "questionPage", defaultValue = "0") int questionPage, @RequestParam(value = "answerPage", defaultValue = "0") int answerPage, @RequestParam(value = "commentPage", defaultValue = "0") int commentPage) {
        SiteUser user = this.userService.getUser(principal.getName());
        Page<Question> questionPaging = this.questionService.getListByUser(questionPage, user);
        Page<Answer> answerPaging = this.answerService.getListByUser(answerPage, user);
        Page<Comment> commentPaging = this.commentService.getListByUser(commentPage, user);
        model.addAttribute("user", user);
        model.addAttribute("questionPaging", questionPaging);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("commentPaging", commentPaging);
        model.addAttribute("anchor", anchor);
        return "mypage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/password")
    public String modifyPassword(UserPasswordForm userPasswordForm) {
        return "modify_password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/password")
    public String modifyPassword(@Valid UserPasswordForm userPasswordForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "modify_password_form";
        }

        SiteUser user = this.userService.getUser(principal.getName());
        if (!this.userService.confirmPassword(userPasswordForm.getPresentPW(), user)) {
            bindingResult.rejectValue("presentPW", "passwordInCorrect",
                    "현재 비밀번호를 바르게 입력해주세요.");
            return "modify_password_form";
        }

        // 비밀번호와 비밀번호 확인에 입력한 문자열이 서로 다르면 다시 입력 하도록
        if (!userPasswordForm.getNewPW().equals(userPasswordForm.getNewPW2())) {
            bindingResult.rejectValue("newPW2", "passwordInCorrect",
                    "입력한 비밀번호가 일치하지 않습니다.");
            return "modify_password_form";
        }

        userService.modifyPassword(userPasswordForm.getNewPW(), user);

        return "redirect:/user/logout";
    }

    @GetMapping("/find/username")
    public String findUsername() {
        return "find_username_form";
    }

    @PostMapping("/find/username")
    @ResponseBody
    public String findUsername(@RequestParam("email") String email) {
        return this.userService.getUserByEmail(email).getUsername();
    }

    @GetMapping("/find/password")
    public String findPassword() {
        return "find_password_form";
    }

    @PostMapping("/find/password")
    @ResponseBody
    public String findPassword(@RequestParam("username") String username, @RequestParam("email") String email) {
        SiteUser user = this.userService.getUserByUsernameAndEmail(username, email);

        if (user == null) {
            throw new DataNotFoundException("siteuser not found");
        }

        String genPassword = this.userService.getTemporalPassword(16);
        System.out.println(genPassword);
        this.userEmailService.mailSend(email, "비밀번호 찾기", genPassword);
        this.userService.modifyPassword(genPassword, user);

        return "";
    }
}
