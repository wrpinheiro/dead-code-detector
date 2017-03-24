package com.aurea.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;

import static com.aurea.deadcodedetection.model.CodeSmellType.DEAD_CODE;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Data
@Builder
public class CodeSmell {
    private String filename;
    private Integer fromLine;
    private Integer toLine;
    private CodeSmellType codeSmellType = DEAD_CODE;
}
