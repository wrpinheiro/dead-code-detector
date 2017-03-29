package com.wrpinheiro.deadcodedetection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
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
