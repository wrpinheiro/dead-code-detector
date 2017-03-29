package com.wrpinheiro.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * A dead code issue (or smell) representation.
 *
 * @author wrpinheiro
 */
@Data
@ToString
@Builder
public class DeadCodeIssue {
    /**
     * The kind of element that this dead code represents, such as:
     * variable, method, class, public class, parameter, etc.
     */
    private String kind;

    /**
     * Reference to the affected element in a format such as: <code>class name.method name.args</code>
     * For example, the ref for the attribute kind filename in this class is: <code>DeadCodeIssue.filename</code>
     */
    private String ref;

    /**
     * The filename containing the dead code.
     */
    private String filename;

    /**
     * The initial line that the dead code appears.
     */
    private Integer fromLine;

    /**
     * The last line that the dead code appears. Note that fromLine can be equal to toLine in the case of a dead
     * code with only one line.
     */
    private Integer toLine;
}
