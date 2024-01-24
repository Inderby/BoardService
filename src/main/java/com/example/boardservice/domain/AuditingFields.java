package com.example.boardservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class) // META data에 관한 Auditing 기능 또한 여기서 받아줄 수 있다.
@MappedSuperclass
public class AuditingFields {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //파싱에 대한 포맷터를 정해주는 에노테이션
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; //생성일시

    @CreatedBy
    @Column(length = 100, nullable = false, updatable = false)
    private String createdBy; //생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //파싱에 대한 포맷터를 정해주는 에노테이션
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    @Column(length = 100, nullable = false)
    private String modifiedBy; //수정자
}
