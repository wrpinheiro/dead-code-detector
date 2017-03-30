package com.wrpinheiro.deadcodedetection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representation of a page of data.
 *
 * @author wrpinheiro
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paginator<T> {
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private List<T> data;
}
