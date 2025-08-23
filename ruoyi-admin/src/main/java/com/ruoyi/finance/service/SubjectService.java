package com.ruoyi.finance.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.finance.dto.SubjectCreateRequest;
import com.ruoyi.finance.dto.SubjectUpdateRequest;
import com.ruoyi.finance.entity.Subject;
import com.ruoyi.finance.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;


    public Subject getSubjectById(Long id, Boolean includeDeleted) throws Exception {
        if (includeDeleted) return subjectRepository.findById(id).orElseThrow(
                () -> new ServiceException("科目Id <" + id + ">不存在")
        );
        return subjectRepository.findByIdAndDeletedFalse(id);
    }

    public List<Subject> getSubjects(boolean includeDeleted) {
        if (includeDeleted) return subjectRepository.findAll();
        return subjectRepository.findByDeletedFalse();
    }

    public Subject deleteSubjectById(Long id) throws Exception {
        Subject subject = getSubjectById(id, false);
        subject.setDeleted(true);
       return subjectRepository.save(subject);
    }

    private Subject updateSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public boolean existById(Long id) {
        return subjectRepository.existsById(id);
    }

    public boolean existByName(String name) {
        return subjectRepository.existsByName(name);
    }

    public Subject createSubject(SubjectCreateRequest subjectCreateRequest) {
        Subject newSubject = new Subject();
        newSubject.setName(subjectCreateRequest.getName());
        newSubject.setType(subjectCreateRequest.getType());
        return subjectRepository.save(newSubject);
    }

    public Subject updateSubject(SubjectUpdateRequest subjectUpdateRequest) throws Exception {
        Subject subject = getSubjectById(subjectUpdateRequest.getId(), false);
        return subjectRepository.save(subject);
    }
}
