package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//MySQL의 auto increment는 identity 방식이다.
    private Long id;

    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; //본문

    @ToString.Exclude
    @JoinTable(
            name = "article_hashtag",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    @OrderBy("createdAt DESC") //댓글 생성 순서대 정렬
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)//mappedBy를 사용하지 않으면 기본 이름을 사용하게 된다(기본 이름 : article_articleComment)
    @ToString.Exclude //ToString 대상에서 제외 처리를 해주지 않는다면 성능 이슈 뿐만 아니라 순환참조가 발생할 수 있기 때문에 이를 방지하기 위해 필수
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId")
    private UserAccount userAccount; //유저 계정 id

    protected Article() {} //Hibernate 구현체를 사용하는 경우 기본 생성자를 갖고 있어야한다. 최소 protected부터 가능하다

    public Article(UserAccount userAccount,String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Article of(UserAccount userAccount, String title, String content) {
        return new Article(userAccount, title, content);
    }

    public void addHashtag(Hashtag hashtag){
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags){
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags(){
        this.getHashtags().clear();
    }

    @Override
    public boolean equals(Object o) { //id라는 유니크한 속성만으로도 동등한 객체인지에 대한 비교가 충분하기 때문에 id만을 사용하여 성능을 높여줌
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return this.getId() != null &&  this.getId().equals(article.id);
        //아직 영속화가 되기 전이면 null일 수 있기 때문에 null check와 동시에 아직 영속화 대상에 포함되지 않는 객체들은 같은 대상으로 보지 않겠다는 의미도 갖고 있음.
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
