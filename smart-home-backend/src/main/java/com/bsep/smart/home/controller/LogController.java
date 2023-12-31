package com.bsep.smart.home.controller;

import com.bsep.smart.home.converter.LogConverter;
import com.bsep.smart.home.dto.request.users.PageRequest;
import com.bsep.smart.home.dto.response.LogResponse;
import com.bsep.smart.home.dto.response.PageResponse;
import com.bsep.smart.home.model.Log;
import com.bsep.smart.home.model.Permission;
import com.bsep.smart.home.security.HasAnyPermission;
import com.bsep.smart.home.services.logs.GetAllLogsForProperty;
import com.bsep.smart.home.services.logs.SearchLogs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final SearchLogs searchLogs;
    @GetMapping("/search/{propertyId}")
    @HasAnyPermission({Permission.PROPERTY_MANIPULATION})
    public PageResponse<LogResponse> getLogs(@Valid final PageRequest pageRequest,
                                             @RequestParam(required = false) String search,
                                             @PathVariable UUID propertyId) {
        return searchLogs.execute(LogConverter.toLogPageInfo(pageRequest.getPageNumber(), pageRequest.getPageSize(), search, propertyId));
    }


}
