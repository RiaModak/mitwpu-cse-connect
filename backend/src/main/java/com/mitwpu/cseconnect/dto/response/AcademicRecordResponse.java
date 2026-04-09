package com.mitwpu.cseconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicRecordResponse {
    private Long id;
    private String academicYear;
    private Integer semester;
    private BigDecimal sgpa;
    private BigDecimal cgpa;
    private BigDecimal attendancePercent;
    private Integer backlogs;
    private String remarks;
}
