package com.ruoyi.finance.converter;

import com.ruoyi.finance.dto.FinanceRecordCreateRequest;
import com.ruoyi.finance.dto.FinanceRecordResponse;
import com.ruoyi.finance.entity.FinanceRecord;
import org.mapstruct.Mapper;

/**
 * MapStruct 转换接口（自动生成实现类）
 */
@Mapper(componentModel = "spring") // 声明为Spring组件，可注入使用
public interface FinanceRecordMapper {

    FinanceRecordResponse toFinanceRecordResponse(FinanceRecord financeRecord);
    FinanceRecord toFinanceRecord(FinanceRecordCreateRequest financeRecordCreateRequest);
}
