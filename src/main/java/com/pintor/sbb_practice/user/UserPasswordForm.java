package com.pintor.sbb_practice.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordForm {
    @NotEmpty(message = "현재 비밀번호는 필수항목입니다.")
    private String presentPW;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String newPW;

    @NotEmpty(message = "새 비밀번호 확인은 필수항목입니다")
    private String newPW2;
}
