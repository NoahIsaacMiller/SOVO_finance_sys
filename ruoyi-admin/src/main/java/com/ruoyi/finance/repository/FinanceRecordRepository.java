package com.ruoyi.finance.repository;

import com.ruoyi.finance.dto.FinanceRecordResponse;
import com.ruoyi.finance.entity.FinanceRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Long>,
        JpaSpecificationExecutor<FinanceRecord> {
    FinanceRecord getByIdAndDeletedFalse(Long recordId);

    List<FinanceRecord> findBySubjectId(Long subjectId);

    List<FinanceRecord> findByDeletedFalse();

    List<FinanceRecord> findBySubjectIdAndDeletedFalse(Long subjectId);

    List<FinanceRecord> findAll(Specification<FinanceRecord> specification, Sort sort);

    FinanceRecord findByIdAndDeletedFalse(Long recordId);
}
