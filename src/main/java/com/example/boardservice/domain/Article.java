package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
@EntityListeners(AuditingEntityListener.class) // 해당 어노테이션을 사용함으로써 entity에 대해서도 auditing 기능을 사용할 수 있게 된다.
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//MySQL의 auto increment는 identity 방식이다.
    private Long id;

    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; //본문

    @Setter private String hashtag; //해시태그

    @OrderBy("id") //id를 기준으로 정렬
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)//mappedBy를 사용하지 않으면 기본 이름을 사용하게 된다(기본 이름 : article_articleComment)
    @ToString.Exclude //ToString 대상에서 제외 처리를 해주지 않는다면 성능 이슈 뿐만 아니라 순환참조가 발생할 수 있기 때문에 이를 방지하기 위해 필수
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; //생성일시
    @CreatedBy @Column(length = 100, nullable = false) private String createdBy; //생성자
    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt; //수정일시
    @LastModifiedBy @Column(length = 100, nullable = false) private String modifiedBy; //수정자

    protected Article() {} //Hibernate 구현체를 사용하는 경우 기본 생성자를 갖고 있어야한다. 최소 protected부터 가능하다

    public Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) { //id라는 유니크한 속성만으로도 동등한 객체인지에 대한 비교가 충분하기 때문에 id만을 사용하여 성능을 높여줌
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null &&  id.equals(article.id);
        //아직 영속화가 되기 전이면 null일 수 있기 때문에 null check와 동시에 아직 영속화 대상에 포함되지 않는 객체들은 같은 대상으로 보지 않겠다는 의미도 갖고 있음.
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
