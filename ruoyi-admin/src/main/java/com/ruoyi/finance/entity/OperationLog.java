package com.ruoyi.finance.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@DynamicInsert
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime operationTime;

    @Column(nullable = false, length = 20)
    private String operationType;

    @Column(length = 50)
    private String dataType;

    private Long dataId;

    @Column(length = 500)
    private String content;

    @Column(nullable = false, length = 10)
    private String result;

    @Column(length = 200)
    private String failReason;
}
