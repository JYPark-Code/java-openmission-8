package woowatech8.openmission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowatech8.openmission.dto.UserSummary;
import woowatech8.openmission.entity.SiteUser;
import woowatech8.openmission.exception.DataNotFoundException;
import woowatech8.openmission.repository.AnswerRepository;
import woowatech8.openmission.repository.CommentRepository;
import woowatech8.openmission.repository.QuestionRepository;
import woowatech8.openmission.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "!@#$%^&*-_=+?";

    private static final int TEMP_PASSWORD_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateDate(LocalDateTime.now());
        this.userRepository.save(user);

        return user;

    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()){
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteUser not found");
        }
    }

    public UserSummary getUserSummary(SiteUser user) {
        long questionCount = questionRepository.countByAuthor(user);
        long answerCount = answerRepository.countByAuthor(user);
        long commentCount = commentRepository.countByAuthor(user);

        long votedQuestionCount = questionRepository.countByAuthor(user);
        long votedAnswerCount = answerRepository.countByVoter(user);

        return new UserSummary(
                questionCount,
                answerCount,
                commentCount,
                votedQuestionCount,
                votedAnswerCount
        );
    }

    // 이메일 & 자기소개 업데이트
    @Transactional
    public void updateProfile(SiteUser user, String email, String bio) {
        user.setEmail(email);
        user.setBio(bio);
        userRepository.save(user);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(SiteUser user, String rawNewPassword) {
        user.setPassword(passwordEncoder.encode(rawNewPassword));
        userRepository.save(user);
    }

    public Optional<SiteUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int idx = random.nextInt(TEMP_PASSWORD_CHARS.length());
            sb.append(TEMP_PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    public Optional<String> resetPasswordWithTemp(String email) {
        Optional<SiteUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        SiteUser user = optionalUser.get();
        String tempPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        return Optional.of(tempPassword); // 화면에 보여줄 용
    }
}
