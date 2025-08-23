package com.ruoyi.finance.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.finance.dto.FinanceRecordCreateRequest;
import com.ruoyi.finance.dto.FinanceRecordQueryDto;
import com.ruoyi.finance.dto.FinanceRecordResponse;
import com.ruoyi.finance.dto.FinanceRecordUpdateRequest;
import com.ruoyi.finance.entity.FinanceRecord;
import com.ruoyi.finance.entity.Subject;
import com.ruoyi.finance.converter.FinanceRecordMapper;
import com.ruoyi.finance.repository.FinanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceRecordService {
    @Autowired
    private FinanceRecordMapper financeRecordMapper;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FinanceRecordRepository financeRecordRepository;

    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "amount", "recordDate", "createTime", "updateTime",
            "subject.id", "subject.name", "subject.type", "subject.createTime"
    );

    private static final String DEFAULT_SORT_FIELD = "recordDate";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    public FinanceRecord getById(Long recordId, Boolean includeDeleted) throws Exception {
        FinanceRecord record = includeDeleted ?
                financeRecordRepository.findById(recordId).orElse(null) :
                financeRecordRepository.findByIdAndDeletedFalse(recordId);

        if (record == null) {
            throw new ServiceException("流水记录id<" + recordId + ">不存在");
        }
        return record;
    }

    public FinanceRecordResponse getFinanceRecordResponseById(Long recordId, Boolean includeDeleted) throws Exception {
        FinanceRecord record = includeDeleted ?
                financeRecordRepository.findById(recordId).orElse(null) :
                financeRecordRepository.findByIdAndDeletedFalse(recordId);

        if (record == null) {
            throw new ServiceException("流水记录id<" + recordId + ">不存在");
        }
        return financeRecordMapper.toFinanceRecordResponse(record);
    }

    /**
     * 动态条件查询财务记录（带排序和分页）
     */
    public Page<FinanceRecordResponse> dynamicQuery(FinanceRecordQueryDto dto, int pageNum, int pageSize) {
        // 构建动态查询条件
        Specification<FinanceRecord> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getFinanceRecordId() != null) {
                predicates.add(cb.equal(root.get("id"), dto.getFinanceRecordId()));
            }

            Join<FinanceRecord, Subject> subjectJoin = root.join("subject", JoinType.INNER);

            if (dto.getSubjectId() != null) {
                predicates.add(cb.equal(subjectJoin.get("id"), dto.getSubjectId()));
            }

            if (dto.getSubjectType() != null) {
                predicates.add(cb.equal(subjectJoin.get("type"), dto.getSubjectType()));
            }

            if (dto.getSubjectName() != null && !dto.getSubjectName().isEmpty()) {
                predicates.add(cb.like(subjectJoin.get("name"), "%" + dto.getSubjectName() + "%"));
            }

            if (dto.getIncludeDeleted() == null || !dto.getIncludeDeleted()) {
                predicates.add(cb.equal(root.get("deleted"), false));
            }

            if (dto.getIncludeDeletedSubject() == null || !dto.getIncludeDeletedSubject()) {
                predicates.add(cb.equal(subjectJoin.get("deleted"), false));
            }

            if (dto.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), dto.getMinAmount()));
            }

            if (dto.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), dto.getMaxAmount()));
            }

            if (dto.getBeginDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("recordDate"), dto.getBeginDate()));
            }

            if (dto.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("recordDate"), dto.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 构建排序条件
        Sort sort = buildSort(dto.getSortField(), dto.getSortDirection());

        // 构建分页参数 (注意：Spring Data JPA的页码是从0开始的)
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        // 执行分页查询
        Page<FinanceRecord> recordPage = financeRecordRepository.findAll(specification, pageable);

        // 转换为响应DTO并保持分页信息
        return recordPage.map(record -> financeRecordMapper.toFinanceRecordResponse(record));
    }

    /**
     * 动态条件查询财务记录（带排序，返回全部结果）
     */
    public List<FinanceRecordResponse> dynamicQuery(FinanceRecordQueryDto dto) {
        // 调用分页方法，获取全部结果
        return dynamicQuery(dto, 1, Integer.MAX_VALUE).getContent();
    }

    private Sort buildSort(String sortFieldParam, String sortDirParam) {
        List<String> sortFields = (sortFieldParam == null || sortFieldParam.trim().isEmpty())
                ? Arrays.asList(DEFAULT_SORT_FIELD)
                : Arrays.stream(sortFieldParam.split(","))
                .map(field -> {
                    switch (field.trim()) {
                        case "subjectName": return "subject.name";
                        case "subjectType": return "subject.type";
                        case "subjectId": return "subject.id";
                        default: return field.trim();
                    }
                })
                .collect(Collectors.toList());

        List<Sort.Direction> directions = new ArrayList<>();
        if (sortDirParam == null || sortDirParam.trim().isEmpty()) {
            sortFields.forEach(field -> directions.add(DEFAULT_SORT_DIRECTION));
        } else {
            String[] dirArray = sortDirParam.split(",");
            for (int i = 0; i < sortFields.size(); i++) {
                directions.add(i < dirArray.length && "asc".equalsIgnoreCase(dirArray[i].trim())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC);
            }
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortFields.size(); i++) {
            String field = sortFields.get(i);
            Sort.Direction direction = directions.get(i);

            if (!ALLOWED_SORT_FIELDS.contains(field)) {
                field = DEFAULT_SORT_FIELD;
                direction = DEFAULT_SORT_DIRECTION;
            }

            orders.add(new Sort.Order(direction, field));
        }

        return Sort.by(orders);
    }

    public FinanceRecordResponse create(FinanceRecordCreateRequest createRequest) throws Exception {
        if (createRequest == null) {
            throw new ServiceException("dto为null");
        }
        if (!subjectService.existById(createRequest.getSubjectId())) {
            throw new ServiceException("subject id不存在");
        }

        if (createRequest.getRecordDate().isAfter(LocalDate.now())) {
            throw new ServiceException("流水时间不能超过当下");
        }
        FinanceRecord record = financeRecordMapper.toFinanceRecord(createRequest);
        record.setSubject(subjectService.getSubjectById(createRequest.getSubjectId(), false));
        record.setSubjectId(createRequest.getSubjectId());
        financeRecordRepository.save(record);
        return financeRecordMapper.toFinanceRecordResponse(record);
    }

    public FinanceRecordResponse update(FinanceRecordUpdateRequest dto) throws Exception {
        if (dto == null) {
            throw new ServiceException("dto为null");
        }

        if (!financeRecordRepository.existsById(dto.getId())) {
            throw new ServiceException("finance record id不存在");
        }

        if (!subjectService.existById(dto.getSubjectId())) {
            throw new ServiceException("subject id不存在");
        }

        if (dto.getRecordDate().isAfter(LocalDate.now())) {
            throw new ServiceException("流水时间不能超过当下");
        }
        FinanceRecord record = financeRecordMapper.toFinanceRecord(dto);
        record.setSubject(subjectService.getSubjectById(dto.getSubjectId(), false));
        record.setSubjectId(dto.getSubjectId());
        financeRecordRepository.save(record);
        return financeRecordMapper.toFinanceRecordResponse(record);
    }

    public FinanceRecord delete(Long id) throws Exception {
        FinanceRecord record = getById(id, false);
        record.setDeleted(true);
        return financeRecordRepository.save(record);
    }
}
