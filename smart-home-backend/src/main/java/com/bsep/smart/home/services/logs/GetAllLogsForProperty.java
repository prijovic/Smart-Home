package com.bsep.smart.home.services.logs;

import com.bsep.smart.home.dto.response.PageResponse;
import com.bsep.smart.home.jpaspecification.PagingUtil;
import com.bsep.smart.home.model.Log;
import com.bsep.smart.home.mongorepository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GetAllLogsForProperty {
    private final LogRepository logRepository;

    @Transactional
    public PageResponse<Log> execute(String propertyId, int pageNumber, int pageSize) {
        final Pageable pageable = PagingUtil.getPageable(pageNumber, pageSize);
        final Page<Log> logPage = logRepository.getLogsByPropertyId(propertyId, pageable);
        return PageResponse.<Log>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .items(new ArrayList<>(logPage.getContent()))
                .totalPages(logPage.getTotalPages())
                .numberOfElements(logPage.getNumberOfElements())
                .totalElements(logPage.getTotalElements())
                .build();
    }
}
