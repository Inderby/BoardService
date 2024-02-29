package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false, length = 500)private String content; //본문
    @Setter @ManyToOne(optional = false) private Article article; //게시글(ID)

    @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount;//유저 계정(ID)

    @Setter
    @Column(updatable = false)
    private Long parentCommentId; //부모 댓글 아이디 - 기존의 양방향 바인딩이 아닌 단방향 바인딩을 사용

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>(); //자식 댓글들
    public ArticleComment() {}

    private ArticleComment(UserAccount userAccount, Article article, Long parentCommentId, String content) {
        this.userAccount = userAccount;
        this.article = article;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return new ArticleComment(userAccount, article, null, content);
    }

    public void addChildComment(ArticleComment child){
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
