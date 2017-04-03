package com.wrpinheiro.deadcodedetection.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.wrpinheiro.deadcodedetection.exceptions.PaginationException;
import com.wrpinheiro.deadcodedetection.model.Paginator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test class for pagination service.
 *
 * @author wrpinheiro
 */
public class PaginationServiceTest {
    private PaginationService paginationService;

    @Before
    public void setup() {
        this.paginationService = new PaginationService();
    }

    @Test
    public void must_return_page_size_equals_3_for_collection_size_5_and_page_equals_2()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3", "value 4", "value 5");

        final Paginator<String> page = paginationService.getPage(values, 1, 2);

        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    @Test
    public void must_return_one_element_in_last_page_when_page_size_is_equal_collection_size_minus_one()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3", "value 4", "value 5", "value 6");

        final Paginator<String> page = paginationService.getPage(values, 2, values.size() - 1);

        assertThat(page.getData()).hasSize(1);
    }

    @Test
    public void must_return_all_elements_when_page_size_is_equal_to_collection_size()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3", "value 4", "value 5", "value 6");

        final Paginator<String> page = paginationService.getPage(values, 1, values.size());

        assertThat(page.getData()).hasSize(values.size());
    }

    @Test(expected = PaginationException.class)
    public void must_throw_pagination_exception_when_size_is_zero()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3");

        paginationService.getPage(values, 1, 0);
    }

    @Test(expected = PaginationException.class)
    public void must_throw_pagination_exception_when_page_is_zero()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3");

        paginationService.getPage(values, 0, 10);
    }

    @Test
    public void must_return_an_empty_page_when_page_is_greater_than_last_page()
            throws PaginationException {
        final List<String> values = asList("value 1", "value 2", "value 3", "value 4", "value 5", "value 6");

        final Paginator<String> page = paginationService.getPage(values, 3, 3);

        assertThat(page.getData()).isEmpty();
    }
}
