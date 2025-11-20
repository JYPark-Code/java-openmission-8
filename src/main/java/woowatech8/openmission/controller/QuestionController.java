package woowatech8.openmission.controller;

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
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.form.AnswerForm;
import woowatech8.openmission.form.CommentForm;
import woowatech8.openmission.form.QuestionForm;
import woowatech8.openmission.service.AnswerService;
import woowatech8.openmission.service.QuestionService;
import woowatech8.openmission.service.UserService;

import java.security.Principal;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(
                         Model model,
                         @PathVariable("id") Integer id,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "sort", defaultValue = "latest") String sort,
                         @RequestParam(value = "openAnswerId", required = false) Long openAnswerId,
                         AnswerForm answerForm,
                         CommentForm commentForm,
                         Principal principal

        ){

        Question question = questionService.getQuestionAndIncreaseView(id);
        Page<Answer> paging = answerService.getAnswerPage(question, page, sort);

        model.addAttribute("question", question);
        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);
        model.addAttribute("openAnswerId", openAnswerId);

        // 로그인 유저를 model에 추가
        if (principal != null) {
            SiteUser loginUser = userService.getUser(principal.getName());
            model.addAttribute("loginUser", loginUser);
        }

        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()){
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm,
                                 @PathVariable("id") Integer id, Principal principal){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(questionForm.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id){
        if(bindingResult.hasErrors()){
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal,
                               @PathVariable("id") Integer id,
                               @RequestParam(value = "openAnswerId", required = false) Long openAnswerId
                               ) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);

        if (openAnswerId != null) {
            // 댓글 열린 answer 유지
            return String.format("redirect:/question/detail/%s?openAnswerId=%s", id, openAnswerId);
        }

        return String.format("redirect:/question/detail/%s", id);
    }

}
