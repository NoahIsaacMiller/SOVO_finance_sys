package com.ruoyi.finance.enums;

import java.util.HashMap;
import java.util.Map;

public enum IncomeExpenseType {
    INCOME(1, "收入"),
    EXPENSE(2, "支出");

    private final int code;
    private final String desc;

    // 构造函数
    IncomeExpenseType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 用于从代码转换为枚举的映射
    private static final Map<Integer, IncomeExpenseType> CODE_MAP = new HashMap<>();

    static {
        for (IncomeExpenseType type : values()) {
            CODE_MAP.put(type.code, type);
        }
    }

    // 根据代码获取枚举实例
    public static IncomeExpenseType fromCode(int code) {
        return CODE_MAP.get(code);
    }

    // getter方法
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}