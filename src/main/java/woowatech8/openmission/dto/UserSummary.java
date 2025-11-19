package woowatech8.openmission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSummary {
    private long questionCount;
    private long answerCount;
    private long commentCount;
    private long votedQuestionCount;
    private long votedAnswerCount;
}
