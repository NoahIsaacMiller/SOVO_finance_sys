package com.ruoyi.finance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ruoyi.finance.enums.IncomeExpenseType;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 财务科目实体类
 * 用于记录财务系统中的科目信息（如现金、银行存款、主营业务收入等）
 */
@Entity
@Data
@DynamicInsert // 动态插入，只插入非空字段
@DynamicUpdate // 动态更新，只更新修改过的字段
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true) // 非空约束，长度限制
    private String name;

    @Column(nullable = false)
    private String type;

    private Boolean deleted = Boolean.FALSE;

    /**
     * 创建时间
     */
    @Column(updatable = false) // 禁止更新
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 预处理方法：插入前自动设置创建时间
     */
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 预处理方法：更新前自动更新时间
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
