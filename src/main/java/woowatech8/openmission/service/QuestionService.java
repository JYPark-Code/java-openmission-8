package woowatech8.openmission.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.exception.DataNotFoundException;
import woowatech8.openmission.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public List<Question> getList(){
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public Question getQuestionAndIncreaseView(Integer id) {
        Question question = getQuestion(id);      // 같은 트랜잭션 안에서 조회
        questionRepository.increaseViewCount(id); // JPQL update (트랜잭션 안에서 실행됨)

        // 반환되는 question 객체에도 반영해 두고 싶다면 (optional)
//        question.setViewCount(question.getViewCount() + 1);

        return question;
    }

    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
//        return this.questionRepository.findAll(spec, pageable); // specification 으로 검색
        return this.questionRepository.findAllByKeyword(kw, pageable); // @Query로 검색.
    }

    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser){
        Set<SiteUser> voters = question.getVoter();

        if (voters.contains(siteUser)) {
            // 이미 추천한 경우 → 추천 취소
            voters.remove(siteUser);
        } else {
            // 아직 추천 안 한 경우 → 추천 등록
            voters.add(siteUser);
        }

        this.questionRepository.save(question);
    }


}
