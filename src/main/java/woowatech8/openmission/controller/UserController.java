package woowatech8.openmission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import woowatech8.openmission.dto.UserSummary;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Comment;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.form.UserCreateForm;
import woowatech8.openmission.repository.AnswerRepository;
import woowatech8.openmission.repository.CommentRepository;
import woowatech8.openmission.repository.QuestionRepository;
import woowatech8.openmission.service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(),
                    userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";

    }


    @GetMapping("/login")
    public String login() {
        return "login_form";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        // 현재 로그인한 사용자 이름
        String username = principal.getName();

        // 유저 정보 조회
        SiteUser user = userService.getUser(username);

        // 가입 후 경과일
        long daysSinceJoin = 0L;
        if (user.getCreateDate() != null) {
            daysSinceJoin = ChronoUnit.DAYS.between(user.getCreateDate(), LocalDateTime.now());
        }

        UserSummary summary = userService.getUserSummary(user);

        List<Question> recentQuestions =
                questionRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
        List<Answer> recentAnswers =
                answerRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
        List<Comment> recentComments =
                commentRepository.findTop5ByAuthorOrderByCreateDateDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("daysSinceJoin", daysSinceJoin);
        model.addAttribute("summary", summary);
        model.addAttribute("recentQuestions", recentQuestions);
        model.addAttribute("recentAnswers", recentAnswers);
        model.addAttribute("recentComments", recentComments);

        return "mypage";
    }

    // 프로필 수정 POST
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile")
    public String updateProfile(Principal principal,
                                @RequestParam("email") String email,
                                @RequestParam(value = "bio", required = false) String bio) {
        String username = principal.getName();
        SiteUser user = userService.getUser(username);
        userService.updateProfile(user, email, bio);
        return "redirect:/user/mypage";
    }

    // 비밀번호 변경 POST
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password")
    public String changePassword(Principal principal,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                                 RedirectAttributes redirectAttributes) {

        // 1. 새 비밀번호/확인 비밀번호 일치 여부 체크
        if (!newPassword.equals(newPasswordConfirm)) {
            redirectAttributes.addFlashAttribute("passwordChangeMessage", "새 비밀번호가 서로 일치하지 않습니다.");
            return "redirect:/user/mypage";
        }

        // 2) 최소 길이 등 정책
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("passwordChangeMessage", "새 비밀번호는 8자 이상이어야 합니다.");
            return "redirect:/user/mypage";
        }

        String username = principal.getName();
        SiteUser user = userService.getUser(username);

        userService.changePassword(user, newPassword);

        redirectAttributes.addFlashAttribute("passwordChangeMessage",
                "비밀번호가 변경되었습니다.");
        return "redirect:/user/mypage";
    }

    // 비밀번호 찾기 폼
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    // 이메일 입력 받아 임시 비밀번호 발급
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       Model model) {

        if (email == null || email.isBlank()) {
            model.addAttribute("message", "이메일을 입력해주세요.");
            return "forgot_password";
        }

        Optional<String> tempPasswordOpt = userService.resetPasswordWithTemp(email);

        // 원래는 알려주지 않는게 보안상 맞으나, 과제용으로 넣음.
        if (tempPasswordOpt.isEmpty()) {
            model.addAttribute("message", "해당 이메일로 가입된 계정을 찾을 수 없습니다.");
            return "forgot_password";
        }

        String tempPassword = tempPasswordOpt.get();
        model.addAttribute("message", "임시 비밀번호가 발급되었습니다. 아래 임시 비밀번호로 로그인 후, 마이페이지에서 비밀번호를 변경해주세요.");
        model.addAttribute("tempPassword", tempPassword);

        return "forgot_password";
    }


}
