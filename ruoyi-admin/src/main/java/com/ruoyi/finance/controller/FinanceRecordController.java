package com.ruoyi.finance.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.finance.dto.FinanceRecordCreateRequest;
import com.ruoyi.finance.dto.FinanceRecordQueryDto;
import com.ruoyi.finance.service.FinanceRecordService;
import com.ruoyi.finance.utils.ApiResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 财务记录控制器
 * 处理财务流水相关的HTTP请求
 */
@RestController
@RequestMapping("/api/finance-record")
public class FinanceRecordController {

    @Autowired
    private FinanceRecordService financeRecordService;

    /**
     * 动态查询财务记录
     * @param queryDto 查询条件DTO
     * @return 包含查询结果的响应
     */
    @GetMapping
    public ApiResponse financeRecord(FinanceRecordQueryDto queryDto) {
        // 调用服务层进行动态查询
        Object result = financeRecordService.dynamicQuery(queryDto);
        return ApiResponse.success("查询成功", result);
    }

    /**
     * 添加新的财务记录
     * @param createRequest 财务记录创建请求DTO（包含验证注解）
     * @return 操作结果响应
     */
    @Log(title = "财务流水管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResponse add(@Valid @RequestBody FinanceRecordCreateRequest createRequest) throws Exception {
       return ApiResponse.success(financeRecordService.create(createRequest));
    }
}
