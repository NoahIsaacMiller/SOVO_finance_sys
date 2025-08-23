package com.ruoyi.finance.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinanceRecordUpdateRequest {
    @NotNull
    private Long id;
    @NotNull
    private LocalDate recordDate;
    @NotNull
    @Min(0)
    private BigDecimal amount;
    @Size(min = 0, max = 200)
    private String remark;
    @NotNull
    private Long subjectId;
}
