package woowatech8.openmission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Comment;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 해당 답변의 최상위 댓글(부모가 없는 것들)만
    List<Comment> findByAnswerAndParentIsNullOrderByCreateDateAsc(Answer answer);

    long countByAuthor(SiteUser author);

    List<Comment> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);


}
