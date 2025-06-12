package com.toy.toyback.login.dto;

import java.time.LocalDateTime;
import com.toy.toyback.login.entity.UserEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "비밀번호는 문자와 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,3}$",
            message = "이메일 도메인은 최대 3자리여야 합니다 (예: .com, .net)"
    )
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(
            regexp = "^0\\d{1,10}$",
            message = "전화번호는 0으로 시작하고 숫자만 포함해야 합니다."
    )
    private String phoneNumber;

    private Boolean used;

    public UserEntity toEntity() {
        return new UserEntity(
            this.userId,
            this.password,
            this.name,
            this.email,
            this.phoneNumber,
            this.used,
            LocalDateTime.now(), // 생성일
            LocalDateTime.now()  // 수정일
        );
    }
}
