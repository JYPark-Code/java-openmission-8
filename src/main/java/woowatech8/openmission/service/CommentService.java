package woowatech8.openmission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Comment;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.exception.DataNotFoundException;
import woowatech8.openmission.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getComment(Integer id) {
        Optional<Comment> oc = commentRepository.findById(id);
        if (oc.isPresent()){
            return oc.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    // 답변에 달리는 댓글, 대댓글
    public Comment createOnAnswer(Answer answer, String content, SiteUser author, Comment parent) {
        Comment c = new Comment();
        c.setAnswer(answer);
        c.setContent(content);
        c.setAuthor(author);
        c.setCreateDate(LocalDateTime.now());
        c.setParent(parent);
        return commentRepository.save(c);
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void delete(Comment comment){
        commentRepository.delete(comment);
    }

    // 추천
    public void vote(Comment comment, SiteUser siteUser) {
        if (comment.getVoter().contains(siteUser)) {
            comment.getVoter().remove(siteUser);    // 토글로 가고 싶으면 이렇게도 가능
        } else {
            comment.getVoter().add(siteUser);
        }
        this.commentRepository.save(comment);
    }
}
