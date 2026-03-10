package com.bisma.foundation.practice_materi_4_7.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;

import java.util.Date;

public record UserReqDTO(@NotBlank String username,
                         @NotBlank String name,
                         @NotBlank String email,
                         @NotEmpty String password,
                         int age,
                         @Past Date birthDate,
                         Long id) {
}
