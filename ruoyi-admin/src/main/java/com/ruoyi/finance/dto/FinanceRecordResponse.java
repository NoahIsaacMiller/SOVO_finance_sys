package com.ruoyi.finance.dto;

import com.ruoyi.finance.entity.Subject;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FinanceRecordResponse {
    private Long id;
    private Subject subject;
    private LocalDate recordDate;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
