package com.store.store_bucket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_no")
    private Long logNo;

    @Column(name = "api_path", length = 100)
    private String apiPath;

    @Column(name = "method", length = 10)
    private String method;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "status_code")
    private Integer statusCode;

    @Lob
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ApiLog(String apiPath, String method, String userId, Integer statusCode, String message) {
        this.apiPath = apiPath;
        this.method = method;
        this.userId = userId;
        this.statusCode = statusCode;
        this.message = message;
    }
}
