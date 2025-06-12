package com.toy.toyback.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdCheckResponse {
    private boolean available;   // true : 사용 가능, false : 중복
}
