package woowatech8.openmission.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Comment;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.form.CommentForm;
import woowatech8.openmission.service.AnswerService;
import woowatech8.openmission.service.CommentService;
import woowatech8.openmission.service.QuestionService;
import woowatech8.openmission.service.UserService;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {

    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;

    // 댓글 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createAnswerComment(
            @PathVariable("id") Integer answerId,
            @Valid CommentForm commentForm,
            BindingResult bindingResult,
            Principal principal,
            @RequestParam(value = "parentId", required = false) Integer parentId
    ){

        Answer answer = answerService.getAnswer(answerId);
        SiteUser user = userService.getUser(principal.getName());

        if (bindingResult.hasErrors()){
            return String.format("redirect:/question/detail/%d#answer_%d",
                    answer.getQuestion().getId(), answer.getId());
        }

        Comment parent = null;
        if (parentId != null) {
            parent = commentService.getComment(parentId);
        }

        commentService.createOnAnswer(answer, commentForm.getContent(), user, parent);

        return String.format(
                "redirect:/question/detail/%d?openCommentFor=%d#answer_%d",
                answer.getQuestion().getId(),
                answer.getId(),
                answer.getId()
        );

    }

    // 댓글 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm,
                                @PathVariable("id") Integer id,
                                Principal principal
                                ){

        Comment comment = commentService.getComment(id);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        commentForm.setContent(comment.getContent());
        return "comment_form"; //
    }

    // 댓글 수정 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                @PathVariable("id") Integer id,
                                Principal principal
                                ) {

        if (bindingResult.hasErrors()) {
            return "comment_form";
        }

        Comment comment = commentService.getComment(id);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        commentService.modify(comment, commentForm.getContent());

        return String.format("redirect:/question/detail/%d?openCommentFor=%d#answer_%d",
                comment.getAnswer().getQuestion().getId(),
                comment.getAnswer().getId(),
                comment.getAnswer().getId());
    }

    // 댓글 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(@PathVariable("id") Integer id, Principal principal) {

        Comment comment = commentService.getComment(id);

        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        Answer answer = comment.getAnswer();
        Integer questionId = comment.getAnswer().getQuestion().getId();
        Integer answerId = comment.getAnswer().getId();
        Comment parent = comment.getParent();

        // 삭제 전에 현재 이 답변에 달린 전체 댓글 수를 알아둔다
        int currentCount = answer.getCommentList() != null ? answer.getCommentList().size() : 0;

        commentService.delete(comment);

        // 1) parent 가 있는 대댓글을 삭제한 경우
        if (parent != null) {
            // -> 댓글 섹션은 계속 열린 상태로 유지
            return String.format(
                    "redirect:/question/detail/%d?openCommentFor=%d#answer_%d",
                    questionId,
                    answerId,
                    answerId
            );
        }

        // 2) parent 가 없는 최상위 댓글인 경우
        //    - 삭제 전 댓글이 1개 뿐이었다면 -> 삭제 후에는 0개가 됨
        //    - 그냥 답변 카드 위치로만 돌아가자
        if (currentCount == 1) {
            return String.format(
                    "redirect:/question/detail/%d#answer_%d",
                    questionId,
                    answerId
            );
        }

        // 3) 그 외 (최상위 댓글이지만 다른 댓글들이 아직 남아있음)
        //    -> 댓글 섹션은 계속 열어둠
        return String.format(
                "redirect:/question/detail/%d?openCommentFor=%d#answer_%d",
                questionId,
                answerId,
                answerId
        );
    }

    // 댓글 추천 (토글)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String commentVote(@PathVariable("id") Integer id, Principal principal) {
        Comment comment = commentService.getComment(id);
        SiteUser siteUser = userService.getUser(principal.getName());

        commentService.vote(comment, siteUser);

        return String.format("redirect:/question/detail/%d?openCommentFor=%d#answer_%d",
                comment.getAnswer().getQuestion().getId(),
                comment.getAnswer().getId(),
                comment.getAnswer().getId());
    }

}
