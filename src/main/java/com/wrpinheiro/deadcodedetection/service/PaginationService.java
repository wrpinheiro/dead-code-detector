package com.wrpinheiro.deadcodedetection.service;

import com.wrpinheiro.deadcodedetection.exceptions.PaginationException;
import com.wrpinheiro.deadcodedetection.model.Paginator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * A service that helps to make pagination.
 *
 * @author wrpinheiro
 */
@Service
public class PaginationService {
    public <T> Paginator<T> getPage(List<T> items, Integer page, Integer pageSize) throws PaginationException {

        if (page < 1 || pageSize < 1) {
            throw new PaginationException("page and pageSize must be equal or greater than 1");
        }

        int totalPages = (items.size() / pageSize);
        if (items.size() % pageSize > 0)
            totalPages++;

        int initialIdxPage = Math.max(0, (page - 1) * pageSize);
        int finalExclusiveIdxPage = initialIdxPage + Math.min(pageSize, items.size());

        Paginator paginator = new Paginator();
        if (initialIdxPage < items.size()) {
            paginator.setData(items.subList(initialIdxPage, finalExclusiveIdxPage));
        } else {
            paginator.setData(Collections.emptyList());
        }
        paginator.setPage(page);
        paginator.setPageSize(pageSize);
        paginator.setTotalPages(totalPages);

        return paginator;
    }
}
