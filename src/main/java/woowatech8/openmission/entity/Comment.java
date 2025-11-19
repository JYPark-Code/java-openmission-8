package woowatech8.openmission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;

    // 어떤 답변에 달린 댓글인지
    @ManyToOne
    private Answer answer;

    // 대댓글 트리용 부모 댓글
    @ManyToOne
    private Comment parent;

    // 자식 댓글들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    // 추천 기능
    @ManyToMany
    Set<SiteUser> voter;

}
