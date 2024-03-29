package com.example.boardservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
public class UserAccount extends AuditingFields {
    @Id @Column(length = 50) private String userId;

    @Setter @Column(nullable = false) private String userPassword;

    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter private String memo;

    @OrderBy("id") //id를 기준으로 정렬
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    @ToString.Exclude //ToString 대상에서 제외 처리를 해주지 않는다면 성능 이슈 뿐만 아니라 순환참조가 발생할 수 있기 때문에 이를 방지하기 위해 필수
    private final Set<Article> articles = new LinkedHashSet<>();

    protected UserAccount() {
    }

    private UserAccount(String userId, String userPassword, String email, String nickname, String memo, String createdBy) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    //이미 인증이 되어 있는 상태에서 유저의 정보가 필요 없을 경우
    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo){
        return new UserAccount(userId, userPassword, email, nickname,memo, null);
    }

    // 인증이 안되어 있는 상태이기 때문에 생성자 정보가 없는 경우
    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo, String createdBy){
        return new UserAccount(userId, userPassword, email, nickname,memo, createdBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount userAccount)) return false;
        return this.getUserId() != null && this.getUserId().equals(userAccount.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }
}
