package com.mitwpu.cseconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClubRequest {
    @NotBlank(message = "Club name is required")
    @Size(max = 100)
    private String name;

    private String description;
    private String category;
    private String logoUrl;
    private Integer foundedYear;
    private String facultyAdvisor;
}
