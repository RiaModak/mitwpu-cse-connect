package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubmitAchievementRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Date of achievement is required")
    @PastOrPresent(message = "Achievement date cannot be in the future")
    private LocalDate dateOfAchievement;

    private String proofExternalUrl;
    private String issuingOrganization;
}
