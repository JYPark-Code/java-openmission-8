package woowatech8.openmission.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    @Query(
            value = """
                select a
                from Answer a
                left join a.voter v
                where a.question = :question
                group by a
                order by count(v) desc, a.createDate desc
                """,
            countQuery = """
                     select count(a)
                     from Answer a
                     where a.question = :question
                     """
    )
    Page<Answer> findByQuestionOrderByVoteCountDesc(
            @Param("question") Question question,
            Pageable pageable
    );

    long countByAuthor(SiteUser author);

    long countByVoter(SiteUser voter);

    List<Answer> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);

}
