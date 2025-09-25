package com.starterpack.member.dto;

import com.starterpack.member.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MemberUpdateRequestDto(

        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password,

        @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
        String name,

        String profileImageUrl,

        LocalDate birthDate,

        Gender gender,

        @Size(max = 20)
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-1234-5678)")
        String phoneNumber
) {
        public static final MemberUpdateRequestDto EMPTY_FORM = new MemberUpdateRequestDto(
                null, // email
                null, // password
                null, // name
                null,  // profileImageUrl
                null, // birthDate
                null, // gender
                null // phoneNumber
        );
}
