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
    /**
     * Return the nth sublist of items containing at most pageSize items. The nth sublist is defined by page.
     *
     * @param items the items to be paginated
     * @param page the nth sublist
     * @param pageSize the page size
     * @param <T> the type of the items
     * @return a sublist of item
     * @throws PaginationException when page or pageSize have invalid values
     */
    public <T> Paginator<T> getPage(final List<T> items, final Integer page, final Integer pageSize)
            throws PaginationException {

        if (page < 1 || pageSize < 1) {
            throw new PaginationException("page and pageSize must be equal or greater than 1");
        }

        int totalPages = items.size() / pageSize;
        if (items.size() % pageSize > 0) {
            totalPages++;
        }

        final int initialIdxPage = Math.max(0, (page - 1) * pageSize);
        final int finalExclusiveIdxPage = initialIdxPage + Math.min(pageSize, items.size());

        final Paginator<T> paginator = new Paginator();
        if (initialIdxPage < items.size()) {
            paginator.setData(items.subList(initialIdxPage, finalExclusiveIdxPage));
        } else {
            paginator.setData(Collections.emptyList());
        }
        paginator.setTotalItems(items.size());
        paginator.setPage(page);
        paginator.setPageSize(pageSize);
        paginator.setTotalPages(totalPages);

        return paginator;
    }
}
