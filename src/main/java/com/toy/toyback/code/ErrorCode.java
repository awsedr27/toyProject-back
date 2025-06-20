package com.toy.toyback.code;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.event.Level;

public enum ErrorCode {

    /* -------------------------------------------------------- */
    /* 공통 (COMMON)                                            */
    /* -------------------------------------------------------- */
    SUCCESS("COM-0000", HttpStatus.OK, "/errors/common-success", "성공", Level.INFO),
    UNKNOWN("COM-9999", HttpStatus.INTERNAL_SERVER_ERROR, "/errors/unknown", "알 수 없는 오류", Level.ERROR),

    /* -------------------------------------------------------- */
    /* 인증 / 토큰 (AUTH)                                       */
    /* -------------------------------------------------------- */
    AUTH_INVALID  ("AUTH-0001", HttpStatus.UNAUTHORIZED, "/errors/auth-invalid", "로그인 실패", Level.WARN),
    TOKEN_NOT_FOUND("AUTH-0002", HttpStatus.UNAUTHORIZED, "/errors/token-not-found", "리프레시 토큰이 없습니다.", Level.WARN),
    REFRESH_FAIL ("AUTH-0003", HttpStatus.UNAUTHORIZED, "/errors/refresh-fail", "토큰 재발급 실패", Level.WARN),

    /* -------------------------------------------------------- */
    /* 사용자 (USER)                                            */
    /* -------------------------------------------------------- */
    USER_DUPLICATE("USR-0001", HttpStatus.CONFLICT, "/errors/user-duplicate", "ID가 중복됩니다", Level.WARN),
    DB_ERROR      ("USR-0002", HttpStatus.INTERNAL_SERVER_ERROR, "/errors/db-error", "DB 처리 오류", Level.ERROR),

    /* -------------------------------------------------------- */
    /* 필드                                                     */
    /* -------------------------------------------------------- */
    private final String code;            // 비즈니스 식별 코드 (로그·대시보드)
    private final HttpStatus status;      // 매핑되는 HTTP 상태
    private final URI type;               // 문제 문서 URI (RFC 7807 type)
    private final String title;           // 간단 메시지 (i18n 키로 대체 가능)
    private final Level logLevel;         // 기본 로깅 레벨

    ErrorCode(String code, HttpStatus status, String type, String title, Level level) {
        this.code = code;
        this.status = status;
        this.type = URI.create(type);
        this.title = title;
        this.logLevel = level;
    }

    /* -------------------------------------------------------- */
    /* Getter                                                   */
    /* -------------------------------------------------------- */
    public String getCode()      { return code; }
    public HttpStatus getStatus(){ return status; }
    public URI getType()         { return type; }
    public String getTitle()     { return title; }
    public Level getLogLevel()   { return logLevel; }

    /* -------------------------------------------------------- */
    /* static Map for O(1) 조회                                 */
    /* -------------------------------------------------------- */
    private static final Map<String, ErrorCode> CACHE = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(ErrorCode::getCode, e -> e));

    /**
     * 문자열 코드 → ErrorCode 매핑 (대소문자 무시). 없으면 UNKNOWN 반환.
     */
    public static ErrorCode from(String code) {
        if (code == null) return UNKNOWN;
        ErrorCode ec = CACHE.get(code.toUpperCase());
        return ec != null ? ec : UNKNOWN;
    }

    public ProblemDetail toProblemDetail() {
        ProblemDetail body = ProblemDetail.forStatus(this.status);
        body.setType(this.type);
        body.setTitle(this.title);
        body.setProperty("code", this.code);
        return body;
    }
}
