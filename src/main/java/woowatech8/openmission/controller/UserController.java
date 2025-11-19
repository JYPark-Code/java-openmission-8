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

    // 비밀번호 변경 POST (일단 TODO용 뼈대)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password")
    public String changePassword(Principal principal,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                                 RedirectAttributes redirectAttributes) {
        // TODO: 현재 비밀번호 검증 (PasswordEncoder.matches)
        // TODO: newPassword == newPasswordConfirm 검사
        // TODO: userService.changePassword(user, newPassword);

        redirectAttributes.addFlashAttribute("passwordChangeMessage",
                "비밀번호 변경 기능은 추후 업데이트 예정입니다.");
        return "redirect:/user/mypage";
    }
}
