package com.ruoyi.finance.repository;

import com.ruoyi.finance.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByDeletedFalse();

    Subject findByIdAndDeletedFalse(Long id);

    boolean existsByName(String name);
}
