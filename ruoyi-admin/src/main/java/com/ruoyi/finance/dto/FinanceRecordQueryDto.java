package com.ruoyi.finance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel(description = "财务记录查询参数")
public class FinanceRecordQueryDto {

    @ApiModelProperty(value = "财务记录ID（精确匹配）", example = "1001")
    private Long financeRecordId;

    @ApiModelProperty(value = "科目ID（精确匹配）", example = "2001")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称（模糊匹配）", example = "银行存款")
    @Length(max = 100, message = "科目名称不能超过100个字符")
    private String subjectName;

    @ApiModelProperty(value = "科目类型（精确匹配）", example = "1")
    private Integer subjectType;

    @ApiModelProperty(value = "是否包含已删除的记录", example = "false")
    private Boolean includeDeleted = false;

    @ApiModelProperty(value = "是否包含已删除的科目", example = "false")
    private Boolean includeDeletedSubject = false;

    @ApiModelProperty(value = "最小金额（大于等于）", example = "100.00")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "最大金额（小于等于）", example = "10000.00")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "开始日期（大于等于）", example = "2023-01-01")
    private LocalDate beginDate;

    @ApiModelProperty(value = "结束日期（小于等于）", example = "2023-12-31")
    private LocalDate endDate;

    @ApiModelProperty(value = "排序字段（支持多字段，逗号分隔）", example = "recordDate,amount")
    @Pattern(regexp = "^[a-zA-Z0-9.,_]+$", message = "排序字段格式非法")
    private String sortField;

    @ApiModelProperty(value = "排序方向（支持多方向，逗号分隔，asc/desc）", example = "desc,asc")
    @Pattern(regexp = "^(asc|desc|,)+$", message = "排序方向只能是asc或desc")
    private String sortDirection;

    @ApiModelProperty(value = "页码（从1开始）", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;
}