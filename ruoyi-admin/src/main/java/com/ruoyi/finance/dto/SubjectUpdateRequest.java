package com.ruoyi.finance.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubjectUpdateRequest {
    @NotNull
    private Long id;

    private String name;

    private String type;
}
