package com.ruoyi.finance.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.finance.dto.SubjectCreateRequest;
import com.ruoyi.finance.dto.SubjectUpdateRequest;
import com.ruoyi.finance.service.SubjectService;
import com.ruoyi.finance.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ApiResponse getSubject(@RequestParam(required = false, defaultValue = "false") Boolean includeDeleted) {
        return ApiResponse.success(subjectService.getSubjects(includeDeleted));
    }

    @Log(title="科目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResponse addSubject(@Valid @RequestBody SubjectCreateRequest subjectCreateRequest) {
        return ApiResponse.success(subjectService.createSubject(subjectCreateRequest));
    }

    @Log(title="科目管理", businessType = BusinessType.UPDATE)
    @PatchMapping
    public ApiResponse updateSubject(@Valid @RequestBody SubjectUpdateRequest subjectUpdateRequest) throws Exception {
        return ApiResponse.success(subjectService.updateSubject(subjectUpdateRequest));
    }

    @Log(title="科目管理", businessType = BusinessType.DELETE)
    @DeleteMapping
    public ApiResponse deleteSubject(@RequestParam(required = true) Long id) throws Exception {
        return ApiResponse.success(subjectService.deleteSubjectById(id));
    }
}
