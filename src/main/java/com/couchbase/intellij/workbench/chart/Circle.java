package com.couchbase.intellij.workbench.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Circle {

    private Double radius;
    private Double[] center;
}
