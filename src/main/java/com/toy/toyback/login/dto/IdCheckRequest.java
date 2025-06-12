package com.toy.toyback.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdCheckRequest {

    @NotBlank
    private String userId;
}
