package woowatech8.openmission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import woowatech8.openmission.entity.Answer;
import woowatech8.openmission.entity.Question;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.exception.DataNotFoundException;
import woowatech8.openmission.repository.AnswerRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author){
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id){
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public Page<Answer> getAnswerPage(Question question, int page, String sortCode) {
        int pageSize = 5; // 한 페이지에 댓글을 몇개 보여줄 것인가

        if("vote".equals(sortCode)) {
            //추천순 정력
            Pageable pageable = PageRequest.of(page, pageSize);
            return answerRepository.findByQuestionOrderByVoteCountDesc(question, pageable);
        }

        // 기본 : 최신순 정렬
        Pageable pageable = PageRequest.of(
                page,
                pageSize,
                Sort.by(Sort.Order.desc("createDate"))
        );
        return answerRepository.findByQuestion(question, pageable);
    }


    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser){
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }




}
