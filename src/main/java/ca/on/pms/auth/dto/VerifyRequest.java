package ca.on.pms.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
    @NotBlank String email,
    @NotBlank String code
) {}